package gamelogic;

import ai.NaiveStrategy;
import constants.Constants;
import utils.Tools;

import java.io.BufferedWriter;
import java.util.Observable;
import java.util.Scanner;

/**
 * Created by Bouke Willem on 1-2-2015.
 */
public class ClientHumanPlayer extends Player {


    public NaiveStrategy hintAI = new NaiveStrategy();


    public ClientHumanPlayer(String name, Mark mark) {
        super(name, mark);
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

    /**
     * Makes a move on the board. <br>
     *
     * @param board the current board
     */
    @Override
    public void makeMove(Board board) {

    }

    @Override
    public void makeMove(Board board, int column) {

    }

    public void handleSendingMove(Board board, BufferedWriter out) {
        setChanged();
        notifyObservers(board);
    }

    private static String readString(String prompt) {
        Scanner in = new Scanner(System.in);
        System.out.print("\n" + prompt);
        if (in.hasNextLine()) {
            return in.nextLine();
        } else {
            return null;
        }
    }

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
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
