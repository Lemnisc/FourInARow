package utils;

import networkcode.ClientHandler;
import networkcode.NetworkMessage;

import java.io.BufferedWriter;
import java.io.IOException;

/**
 * Created by Over on 27.01.2015.
 */
public class Tools {

    public static void easyOut(String s, BufferedWriter out) {
        try {
            out.write(s);
            out.newLine();
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendError(String s, ClientHandler h) {
        NetworkMessage err = new NetworkMessage(s);
        h.handleIncoming(err);
    }

}
