package gamelogic;

import ai.*;
import client.Client;
import constants.Constants;
import networkcode.NetworkMessage;
import utils.States;
import utils.Tools;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.util.Observable;

/**
 * Created by Bouke Willem on 28-1-2015.
 */
public class ClientAIPlayer extends Player {

    private Strategy strategy;

    public ClientAIPlayer(String n, Mark m, Strategy s) {
        super(n, m);
        strategy = s;
    }

    /**
     * Determines the field for the next move.
     *
     * @param board the current game board
     * @return the player's choice
     */
    @Override
    public int determineMove(Board board) {
        return strategy.determineMove(board, this.getMark());
    }

    /**
     * Makes a move on the board. <br>
     *
     * @param b the current board
     */
    @Override
    public void makeMove(Board b) {
        int keuze = determineMove(b);
        b.movesSoFar.add(keuze);
        b.setInCol(keuze, getMark());
    }

    @Override
    public void makeMove(Board b, int column) {
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
                default:
                    break;
            }
        }
    }

    public void handleSendingMove(Board b, BufferedWriter out) {
        int choice = determineMove(b);
        String s = Constants.DOMOVE_COMMAND + " " + choice;
        Tools.easyOut(s, out);
    }
}
