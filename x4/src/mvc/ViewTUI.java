package mvc;

import gamelogic.*;

import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;


/**
 * Created by Bouke Willem on 22-1-2015.
 *
 */
public class ViewTUI extends Observable implements ViewInterface, Observer {
    private Board board;

    public ViewTUI(Board b) {
        board = b;
    }

    @Override
    public void start() {

    }

    @Override
    public void showBoard(Board b) {
        String s = "";
        for (int i = 0; i < Board.VER; i++) {
            String row = "";
            for (int j = 0; j < Board.HOR; j++) {
                if (b.getField(i, j) != Mark.EM) {
                    row = row + " " + b.getField(i, j).toString() + " ";
                } else row = row + " " + "  " + " ";

                if (j < Board.HOR - 1) {
                    row = row + "|";
                }
            }
            s = s + row;
            if (i < Board.VER - 1) {
                s = s + "\n";
            }
        }
        s = s + "\n" + Board.NUMBERING + "\n" + "Moves so far: " + b.movesSoFar + "\n";
        System.out.println(s);
    }


    public void doMove(int i) {
        setChanged();
        notifyObservers(i);
    }

    public void handleInput(Player player) {
        String prompt = "> " + player.getName() + " (" + player.getMark().toString() + ")"
                + ", what is your choice? 0 to 6, h for help";
        String line = readString(prompt);
        String[] words = line.split(" ");
        if (words.length == 1 && words[0].length() == 1) {
            if (words[0].charAt(0) == 'h') {
//                int hint = player.hintAI.determineMove(board, player.getMark());
//                System.out.println("Hint: " + hint);
//                handleInput(player);
            }
        }
        if (words.length == 1 && isInteger(words[0])) {
            int choice = Integer.parseInt(words[0]);
            boolean valid = board.isCol(choice) && board.colHasSpace(choice);
            if (!valid) {
                System.out.println("ERROR: field " + choice
                        + " is no valid choice.");
                handleInput(player);
//                valid = board.isCol(choice) && board.colHasSpace(choice);
            }
            doMove(Integer.parseInt(words[0]));
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

    public static boolean isChar(String s) {
        try {
            Character.isLetter(s.charAt(0));
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
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
        if (arg == "Move made") showBoard((Board) o);

        if (arg == "Move made (reset)") showBoard((Board) o);

        if (arg == "No space") {
            //Show that move is invalid
        }

        if (arg == "Full") {
            System.out.println("The board is full. There is a tie!");
        }

        if (arg == "Make a move") {
            //_Shouldn't_ get called when using an ai player.
            Player t;
            t = (Player) o;
            handleInput(t);
        }

        if (arg == Mark.OO || arg == Mark.XX) {
            // Show board and show that there is a winner
//            showBoard(board);
            System.out.println(arg + " has won! That is player " + arg.toString() + ".");
        }
    }
}
