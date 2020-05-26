//TODO In lobby, use switch cases. And make handleReady() methods &c.
//TODO suggestion: let lobby observe game for gameover notifications.
//Or else: lobby aan game meegeven en dan game methodes uit game laten aanroepen
//bijv. game observeert board: als game over, dan lobby.endgame etc
//Random++ ai: draai per column veel random games, houd win/loss ratio bij, kies beste column.

package networkcode;

import constants.Constants;
import gamelogic.Board;
import gamelogic.Game;
import gamelogic.Mark;
import utils.States;
import utils.Tools;

import java.util.*;

/**
 * Created by Over on 27.01.2015.
 */
public class Lobby implements Observer {
    public List<ClientHandler> clientHandlers = new LinkedList<>();
    public List<ClientHandler> readyClients = new LinkedList<>();
    private List<Game> gameList = new LinkedList<>();

    private static final Object lock = new Object();


    private void handleReady(ClientHandler handler) {
        synchronized (lock) {
            if (readyClients.isEmpty()) {
                readyClients.add(handler);
                handler.setState(States.READY);
                System.out.println(handler.getName() + " added to list of ready clients");
            } else if (!readyClients.contains(handler)) {
                readyClients.add(handler);
                handler.setState(States.READY);
                System.out.println(handler.getName() + " added to list of ready clients");
                if (readyClients.size() >= 2) {
                    //Make a game between two clients
                    startGame(handler, readyClients.get(0));
                }
            } else {
                Tools.sendError(Constants.ERROR_COMMAND + " " + Constants.ERROR8 + " " + "You are already ready", handler);
            }
        }
    }

    private void startGame(ClientHandler p1, ClientHandler p2) {
        if (p1.getState() == States.READY && p2.getState() == States.READY) {
            ClientHandler player1;
            ClientHandler player2;

            //Determine randomly which player starts.
            Random rn = new Random();
            if (rn.nextInt() % 2 == 0) {
                player1 = p1;
                player2 = p2;
            } else {
                player1 = p2;
                player2 = p1;
            }

            //Make game (with the clients)
            Game newGame = new Game(player1, player2, new Board(), this);

            //Add it to the list
            gameList.add(newGame);

            //Let the game observe the clients. Or do this in the constructor of the game.
            player1.addObserver(newGame);
            player2.addObserver(newGame);

            //Change states to GAME
            player1.setState(States.GAME);
            player2.setState(States.GAME);

            //Send Start_game command to the players
            String s = Constants.START_GAME_COMMAND + " " + player1.getName() + " " + player2.getName();
            NetworkMessage n = new NetworkMessage(s);
            player1.sendMessageToClient(n);
            player2.sendMessageToClient(n);

            //Remove them from the ready list
            readyClients.remove(player1);
            readyClients.remove(player2);

            //Stop observing them, let the Game handle that:
            player1.deleteObserver(this);
            player2.deleteObserver(this);

            //Start the game
            newGame.start();
        }
    }

    public void addHandler(ClientHandler handler) {
        clientHandlers.add(handler);
        handler.addObserver(this);
        System.out.println("Added " + handler.getName() + " to list.");
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof NetworkMessage) {
            NetworkMessage msg = (NetworkMessage) arg;
            String command = msg.getCommand();
            switch (command) {
                case Constants.READY_COMMAND:
                    handleReady(msg.getHandler());
                    break;
                case Constants.GAME_END_COMMAND:
//                    //Be sure to
                    handleEndGame((Game)arg); //Depending on tie or winner (probably doesn't matter to lobby).

                    break;
                case Constants.DOMOVE_COMMAND:
                    if (msg.getHandler().getState() != States.GAME) {
                        String s = Constants.ERROR_COMMAND + " " + Constants.ERROR7 + " " + "You are not in a game, you can't make a move.";
                        NetworkMessage n = new NetworkMessage(s);
                        msg.getHandler().sendMessageToClient(n);
                    }
                    break;
                default:
                    break;
            }
        } else {
            System.out.println("Not a networkMessage.");
        }
    }

    public void handleEndGame(Game g) {
        //Remove game from list and do a lot more, see protocol &c
        g.clients[0].setState(States.LOBBY);
        g.clients[1].setState(States.LOBBY);
        g.clients[0].addObserver(this);
        g.clients[1].addObserver(this);
        g.deleteObserver(this);
        g.clients[0].deleteObserver(g);
        g.clients[1].deleteObserver(g);
        g.deleteObserver(g.clients[0]);
        g.deleteObserver(g.clients[1]);
        g.reset();
        gameList.remove(g);
        System.out.println("Game over!");
    }
}
