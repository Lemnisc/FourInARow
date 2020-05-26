package gamelogic;

import java.io.BufferedWriter;
import java.util.Observable;
import java.util.Observer;

/**
 * Abstract class for keeping a player in the Tic Tac Toe game. Module 2 lab
 * assignment.
 *
 * @author Theo Ruys en Arend Rensink
 * @version $Revision: 1.4 $
 */
public abstract class Player extends Observable implements Observer {

    // -- Instance variables -----------------------------------------

    private String name;
    private Mark mark;

    // -- Constructors -----------------------------------------------

    /*@
       requires theName != null;
       requires theMark == theMark.XX || theMark == theMark.OO;
       ensures this.getName() == theName;
       ensures this.getMark() == theMark;
     */

    /**
     * Creates a new Player object.
     */
    public Player(String theName, Mark theMark) {
        this.name = theName;
        this.mark = theMark;
    }

    // -- Queries ----------------------------------------------------

    /**
     * Returns the name of the player.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the mark of the player.
     */
    public Mark getMark() {
        return mark;
    }

    /*@
       requires board != null & !board.isFull();
       ensures board.isField(\result) & board.isEmptyField(\result);

     */

    /**
     * Determines the field for the next move.
     *
     * @param board the current game board
     * @return the player's choice
     */
    public abstract int determineMove(Board board);

    public abstract void handleSendingMove(Board board, BufferedWriter out);


    // -- Commands ---------------------------------------------------

    /*@
       requires board != null & !board.isFull();
     */

    /**
     * Makes a move on the board. <br>
     *
     * @param board the current board
     */
    public abstract void makeMove(Board board);

    public abstract void makeMove(Board board, int column);


//        This is for the clientplayer:
//        int keuze = determineMove(board);
//        board.movesSoFar.add(keuze);
//        board.setInCol(keuze, getMark());


}
