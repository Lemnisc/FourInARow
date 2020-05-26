package gamelogic;

import java.io.BufferedWriter;
import java.util.Observable;

/**
 * Created by Bouke Willem on 29-1-2015.
 */
public class ClientOtherPlayer extends Player {

    public ClientOtherPlayer(String n, Mark m) {
        super(n, m);
    }

    /**
     * Determines the field for the next move.
     *
     * @param board the current game board
     * @return the player's choice
     */
    @Override
    public int determineMove(Board board) {
        return 0;
    }

    @Override
    public void handleSendingMove(Board board, BufferedWriter out) {

    }

    /**
     * Makes a move on the board. <br>
     *
     * @param b the current board
     */
    @Override
    public void makeMove(Board b) {
    }

    @Override
    public void makeMove(Board b, int column) {
        b.movesSoFar.add(column);
        b.setInCol(column, getMark());
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

    }
}
