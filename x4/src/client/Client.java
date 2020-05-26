package client;

import ai.MonteCarloStrategy;
import ai.NaiveStrategy;
import constants.Constants;
import gamelogic.*;
import mvc.ViewTUI;
import networkcode.NetworkMessage;
import utils.States;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Observable;

/**
 * Created by Over on 23.01.2015.
 */
public class Client extends Observable {
    private static final String USAGE = "usage: <address> <port> <name> <AI | Human, default = ai>";
    private static BufferedReader in;
    private static BufferedWriter out;
    private static ClientInputHandler clientInputHandler;
    private States serverConnection;

    private enum PlayerType {
        AI, MONTE, HUMAN
    }

    private static PlayerType playerType;

    Player localPlayer;
    ClientOtherPlayer otherPlayer;
    Board board = new Board();

    private static String name;

    public static void main(String[] args) {
        Client c = new Client();
        name = args[2];
        switch (args[3]) {
            case "AI":
                playerType = PlayerType.AI;
                break;
            case "Human":
                playerType = PlayerType.HUMAN;
                break;
            case "Monte":
                playerType = PlayerType.MONTE;
                break;
            default:
                playerType = PlayerType.AI;
                break;
        }
        c.tryToConnect(args);
        c.tryToJoin(name, 27);
        c.tryReady();
        clientInputHandler = new ClientInputHandler(out);
        new Thread(clientInputHandler).start();
        c.handleServerInput();

    }

    //Prepare input from the server for processing
    private void handleServerInput() {
        String msg;
        try {
            while (true) {
                msg = in.readLine();
                while (msg != null && !msg.equals("")) {
                    System.out.println(msg);
                    handleServerIncoming(msg);

                    msg = in.readLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //Handle the message that was input from the server
    private void handleServerIncoming(String msg) {
        NetworkMessage n = new NetworkMessage(msg);
        String command = n.getCommand();
        String[] parameters = n.getParameters();
        switch (command) {
            //Client should handle ACCEPT, START_GAME, REQUEST_MOVE, DONE_MOVE, GAME_END, ERROR.
            case Constants.ACCEPT_COMMAND:
                System.out.println("Server accepted connection.");
                serverConnection = States.ACCEPTED;
                break;
            case Constants.START_GAME_COMMAND:
//                ClientPlayer p1 = new ClientPlayer(parameters[0], Mark.XX);
//                ClientPlayer p2 = new ClientPlayer(parameters[1], Mark.OO);

                ViewTUI view = new ViewTUI(board);
                board.addObserver(view);
                Game game;

                //@requires !parameters[0].equals(parameters[1]);
                if (parameters[0].equals(name)) {
                    switch (playerType) {
                        case AI:
                            NaiveStrategy naive = new NaiveStrategy();
                            localPlayer = new ClientAIPlayer(parameters[0], Mark.XX, naive);
                            break;
                        case HUMAN:
                            localPlayer = new ClientHumanPlayer(parameters[0], Mark.XX);
                            localPlayer.addObserver(clientInputHandler);
                            break;
                        case MONTE:
                            MonteCarloStrategy m = new MonteCarloStrategy(10);
                            localPlayer = new ClientAIPlayer(parameters[0], Mark.XX, m);
                            break;
                        default:
                            break;
                    }
                    otherPlayer = new ClientOtherPlayer(parameters[1], Mark.OO);
                    System.out.println(localPlayer.getName() + " " + otherPlayer.getName());
                    game = new Game(localPlayer, otherPlayer, board);
                    game.reset();
                    this.addObserver(game);
                } else if (parameters[1].equals(name)) {
                    switch (playerType) {
                        case AI:
                            NaiveStrategy naive = new NaiveStrategy();
                            localPlayer = new ClientAIPlayer(parameters[1], Mark.OO, naive);
                            break;
                        case HUMAN:
                            localPlayer = new ClientHumanPlayer(parameters[1], Mark.OO);
                            localPlayer.addObserver(clientInputHandler);
                            break;
                        case MONTE:
                            MonteCarloStrategy m = new MonteCarloStrategy(10);
                            localPlayer = new ClientAIPlayer(parameters[1], Mark.OO, m);
                            break;
                        default:
                            break;
                    }
                    otherPlayer = new ClientOtherPlayer(parameters[0], Mark.XX);
                    game = new Game(otherPlayer, localPlayer, board);
                    this.addObserver(game);
                    game.addObserver(clientInputHandler);
                    game.reset();
                }
                view.showBoard(board);
                break;
            /*
            Basically:
                If a move is requested from this client,
                    send that move to the server.

                If a move is done,
                    add that unconditionally to the game.
                    (because the server is always right)
             */
            case Constants.REQUEST_MOVE_COMMAND:
                if (parameters[0].equals(name)) {
                    //Client selects a play, but doesn't put it on the board immediately.
                    //Instead, it sends it to the server and the client adds the DONE_MOVEs
                    //to the board.
//                    localPlayer.handleSendingMove(board, out);
                    localPlayer.handleSendingMove(board, out);
                }
                break;
            case Constants.DONE_MOVE_COMMAND:
                //Notify the board that a move was made.
                setChanged();
                notifyObservers(n);
                break;
            case Constants.GAME_END_COMMAND:
                String winner = (parameters[0] == null) ? "No one, it was a tie." : parameters[0];
                System.out.println("Game over. Winner: " + winner);
                break;
            case Constants.ERROR_COMMAND:
                System.out.println(parameters[0] + ": " + parameters[1]);
                break;
            default:
                break;
        }
    }


    private void tryToJoin(String j, int g) {
        try {
            String join = "join " + j + " " + g;
            out.write(join);
            out.newLine();
            out.flush();
            serverConnection = States.JOINED;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void tryReady() {
        String ready = "ready_for_game";

        //Try and see if ready-ing works:
        try {
            out.write(ready);
            out.newLine();
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void tryToConnect(String[] args) {
        if (args.length != 4) {
            System.out.println(USAGE);
            System.exit(0);
        }

        InetAddress addr = null;
        int port = 0;
        Socket sock = null;

        //check args[0] - the IP-address
        try {
            addr = InetAddress.getByName(args[0]);
        } catch (UnknownHostException e) {
            System.out.println(USAGE);
            System.out.println("ERROR: host " + args[0] + " unknown");
            System.exit(0);
        }

        // parse args[1] - the port
        try {
            port = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.out.println(USAGE);
            System.out.println("ERROR: port " + args[1] + " is not an integer");
            System.exit(0);
        }


        try {
            sock = new Socket(addr, port);
            serverConnection = States.CONNECTED;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("ERROR: could not create a socket on " + addr
                    + " and port " + port);
        }

        try {
            in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
        } catch (IOException e) {
            System.out.println("ERROR: unable to communicate to server");
            e.printStackTrace();
        } catch (NullPointerException e) {
            System.out.println("ERROR: socket not found.");
            e.printStackTrace();
        }
    }
}