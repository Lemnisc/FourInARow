package gamelogic;

/** TODO: instead of play() we want update():
 * Making a move is:
 *  Finding the current player
 *  If that player notifies us:
 *      Have player.makeMove() just enter that move
 *      (And modify player accordingly)
 * Continue until won:
 *  Send GAME_END to the clients.
 *  (Have the clientHandlers send this back to the lobby
 *  so it can end the game properly.)
 */

import constants.Constants;
import networkcode.ClientHandler;
import networkcode.Lobby;
import networkcode.NetworkMessage;
import utils.Tools;

import java.util.Observable;
import java.util.Observer;

/**
 * Class for maintaining the Four game.
 */
public class Game extends Observable implements Observer {

    // -- Instance variables -----------------------------------------

    public static final int NUMBER_PLAYERS = 2;

    /*@
       private invariant board != null;
     */
    /**
     * The board.
     */
    private Board board;
    private Lobby lobby;

    /*@
       private invariant players.length == NUMBER_PLAYERS;
       private invariant (\forall int i; 0 <= i && i < NUMBER_PLAYERS; players[i] != null);
     */
    /**
     * The 2 players of the game.
     */
    private Player[] players = {null, null};
    public ClientHandler[] clients = {null, null};

    /*@
       private invariant 0 <= current  && current < NUMBER_PLAYERS;
     */
    /**
     * Index of the current player.
     */
    private int current;

    // -- Constructors -----------------------------------------------

    /*@
      requires s0 != null;
      requires s1 != null;
     */

    /**
     * Creates a new Game object.
     *
     * @param s0 the first player
     * @param s1 the second player
     * @param b  the board
     * @param l  the lobby hosting the game
     */
    public Game(ClientHandler s0, ClientHandler s1, Board b, Lobby l) {
        board = b;
        lobby = l;
        b.addObserver(this);
        players = new Player[NUMBER_PLAYERS];
        players[0] = new ServerPlayer(s0.getName(), Mark.XX);
        clients[0] = s0;
        players[1] = new ServerPlayer(s1.getName(), Mark.OO);
        clients[1] = s1;
        current = 0;
    }

    public Game(Player p0, Player p1, Board b) {
        board = b;
        players[0] = p0;
        players[1] = p1;
        current = 0;
    }

    // -- Commands ---------------------------------------------------

    /**
     * Starts the Four game. <br>
     */
    public void start() {
        reset();
        String s = Constants.REQUEST_MOVE_COMMAND + " " + clients[current].getName();
        NetworkMessage msg = new NetworkMessage(s, clients[current]);
        clients[0].sendMessageToClient(msg);
        clients[1].sendMessageToClient(msg);
    }

    public Mark startMonte() {
        playMonte();
        if (board.gameOver()) {
            board.notifyObservers("Full");
        }

        return board.hasWinner;
    }

    private void playMonte() {
        while (!board.gameOver()) {
            players[current].makeMove(board);
            current = (current + 1) % 2;
        }
    }

    /**
     * Resets the game. <br>
     * The board is emptied and player[0] becomes the current player.
     */
    public void reset() {
        current = 0;
        board.reset();
    }


    /**
     * This method is called whenever the observed object is changed. An
     * application calls an <tt>Observable</tt> object's
     * <code>notifyObservers</code> method to have all the object's
     * observers notified of the change.
     *
     * @param o   the observable object.
     * @param arg an argument passed to the <code>notifyObservers</code>
     */
    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof NetworkMessage) {
            NetworkMessage msg = (NetworkMessage) arg;
            String command = msg.getCommand();
            switch (command) {
                case Constants.DOMOVE_COMMAND:
                    int column;
                    try {
                        column = Integer.parseInt(msg.getParameters()[0]);
                        handleMove(msg.getHandler(), column);
                    } catch (NumberFormatException e) {
                        String s = Constants.ERROR_COMMAND + " " + Constants.ERROR9 + " " + "Parameter is not an integer.";
                        NetworkMessage n = new NetworkMessage(s);
                        msg.getHandler().sendMessageToClient(n);
                        System.out.println(s);
                        e.printStackTrace();
                    }
                    break;

                //This handles moves coming in from the clients.
                case Constants.DONE_MOVE_COMMAND:
                    String[] parameters = msg.getParameters();
                    String name = parameters[0];
                    column = Integer.parseInt(parameters[1]);
                    board.setInCol(column,
                            players[current].getName().equals(name) ? players[0].getMark() : players[1].getMark());
                    board.movesSoFar.add(column);
                    break;
                default:
                    break;
            }
        } else if (arg == "No space") {
            String s = Constants.ERROR_COMMAND + " " + Constants.ERROR2 + " " + "No space in this column.";
            ClientHandler h = clients[current];
            Tools.sendError(s, h);
        } else if (arg == "Move made") {
            System.out.println("Board says: Move was made.");
        } else if (arg == Mark.OO || arg == Mark.XX) {
            System.out.println("Board says: There is a winner. (Handled by other function)");
        } else if (arg == "Socket broken") {
            ClientHandler c = (ClientHandler) o;
            int i = (c.getName().equals(clients[0].getName())) ? 1 : 0;
            board.hasWinner = players[i].getMark();

            ClientHandler winner = clients[i]; //The other player.
            String msg = (board.hasWinner == Mark.OO || board.hasWinner == Mark.XX) ?
                    Constants.GAME_END_COMMAND + " " + winner.getName() :
                    Constants.GAME_END_COMMAND;

            NetworkMessage n = new NetworkMessage(msg);

            clients[0].sendMessageToClient(n.getMessage());
            clients[1].sendMessageToClient(n.getMessage());
            clients[0].deleteObserver(this);
            clients[1].deleteObserver(this);
            reset();
        } else {
            System.out.println("Board said an irrelevant message");
        }
    }

    private void handleMove(ClientHandler h, int i) {
        boolean errorOccured = false;
        if (!board.gameOver()) {
            System.out.println("About to handle a move in column " + i + " by client " + h.getName());
            if (i >= 0 && i < Board.HOR) {
                if (h.getName().equals(players[current].getName())) {
                    players[current].determineMove(board);
                    players[current].makeMove(board, i);
                    System.out.println("It is your move, " + h.getName() + ". You moved in " + i);

                    String s1 = Constants.DONE_MOVE_COMMAND + " " + h.getName() + " " + i;
                    NetworkMessage msg1 = new NetworkMessage(s1);
                    clients[0].sendMessageToClient(msg1);
                    clients[1].sendMessageToClient(msg1);
                } else {
                    String err = Constants.ERROR_COMMAND + " " + Constants.ERROR2 + " " + "Invalid move, it's not your turn.";
                    System.out.println(err);
                    errorOccured = true;
                    Tools.sendError(err, h);
                }
            } else {
                String err = Constants.ERROR_COMMAND + " " + Constants.ERROR2 + " " + "Invalid move, column out of bounds";
                System.out.println(err);
                errorOccured = true;
                Tools.sendError(err, h);
            }
            if (board.gameOver()) {
                ClientHandler winner = clients[current]; //The other player.
                String msg = (board.hasWinner == Mark.OO || board.hasWinner == Mark.XX) ?
                        Constants.GAME_END_COMMAND + " " + winner.getName() :
                        Constants.GAME_END_COMMAND;

                NetworkMessage n = new NetworkMessage(msg);

                clients[0].sendMessageToClient(n.getMessage());
                clients[1].sendMessageToClient(n.getMessage());
                clients[0].deleteObserver(this);
                clients[1].deleteObserver(this);
                lobby.handleEndGame(this);
                reset();
            } else {
//              Send request move to the next player.
                if (!errorOccured) {
                    current = (current + 1) % 2;
                }
                errorOccured = false;
                String s2 = Constants.REQUEST_MOVE_COMMAND + " " + clients[current].getName();
                NetworkMessage msg2 = new NetworkMessage(s2, clients[current]);
                clients[0].sendMessageToClient(msg2);
                clients[1].sendMessageToClient(msg2);
            }
        }
    }
}


