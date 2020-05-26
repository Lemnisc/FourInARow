package gamelogic;

import java.io.BufferedWriter;
import java.util.Observable;

/**
 * Created by Bouke Willem on 27-1-2015.
 */
public class ServerPlayer extends Player {
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

    /**
     * Makes a move on the board. <br>
     *
     * @param board the current board
     */
    @Override
    public void makeMove(Board board) {

    }

    /**
     * Makes a move on the board. <br>
     *
     * @param board the current board
     */
    @Override
    public void makeMove(Board board, int column) {
        int keuze = column;
        board.movesSoFar.add(keuze);
        board.setInCol(keuze, getMark());
    }

    public ServerPlayer(String name, Mark m) {
        super(name, m);
    }
}
