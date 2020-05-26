package client;

import constants.Constants;
import gamelogic.Board;
import gamelogic.ClientHumanPlayer;
import utils.Tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;

/**
 * Created by Bouke Willem on 31-1-2015.
 */
public class ClientInputHandler implements Runnable, Observer {
    private static BufferedReader in;
    private static BufferedWriter out;
    private boolean handlingMove;
    private ClientHumanPlayer client;
    private Board board;

    public ClientInputHandler(BufferedWriter o) {
        out = o;
    }


    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        handleClientInput();
    }

    private void handleClientInput() {
        while (true) {
//                String s = (handlingMove) ? "> " + client.getName() + " (" + client.getMark().toString() + ")" + ", what is your choice? 0 to 6, h for help" : "> ";
//                System.out.println(s);


            askForInput("");
//                if(!handlingMove)
//                {
//                    String s = "> ";
//                    askForInput(client, board, s);
//                } else {
//                    String s = "> " + client.getName() + " (" + client.getMark().toString() + ")"
//                            + ", what is your choice? 0 to 6, h for help";
//                    askForInput(client, board, s);
//                }
        }
    }

    public void handleInput() {
        String prompt = "> ";
        String line = readString(prompt);
        sendMessageToServer(line);
    }

    private void sendMessageToServer(String line) {
        try {
            out.write(line);
            out.newLine();
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    private void askForInput(String prompt) {

//        String prompt = "> " + p.getName() + " (" + p.getMark().toString() + ")"
//                + ", what is your choice? 0 to 6, h for help";
        String line = readString(prompt);
        String[] words = line.split(" ");
        if (words.length == 1 && words[0].length() == 1) {
            if (words[0].charAt(0) == 'h') {
                int hint = client.hintAI.determineMove(board, client.getMark());
                System.out.println("Hint: " + hint);
                askForInput(prompt);
            }
        }
        if (words.length == 1 && isInteger(words[0])) {
            int choice = Integer.parseInt(words[0]);
            boolean valid = board.isCol(choice) && board.colHasSpace(choice);
            if (!valid) {
                System.out.println("ERROR: field " + choice
                        + " is no valid choice.");
                askForInput(prompt);
//                valid = board.isCol(choice) && board.colHasSpace(choice);
            }

            String s = Constants.DOMOVE_COMMAND + " " + choice;
            handlingMove = false;
            Tools.easyOut(s, out);
        }
        sendMessageToServer(line);
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
        client = (ClientHumanPlayer) o;
        board = (Board) arg;
        handlingMove = true;
        String s = "> " + client.getName() + " (" + client.getMark().toString() + ")" + ", what is your choice? 0 to 6, h for help";
        System.out.println(s);
    }
}
