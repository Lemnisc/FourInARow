package networkcode;

import constants.Constants;
import utils.States;
import utils.Tools;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by over on 22/01/2015.
 */
public class ClientHandler extends Observable implements Observer, Runnable {
    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;


    private States state;

    public ClientHandler(Socket socket) throws IOException {
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.socket = socket;
    }

    //Flags:
    private boolean accepted = false;
    private String name;
    private int group;

    public void setAccepted() {
        accepted = true;
        state = States.ACCEPTED;
        String s = Constants.ACCEPT_COMMAND + " " + Constants.OWN_GROUP_NUMBER;
        NetworkMessage n = new NetworkMessage(s);
        sendMessageToClient(n);
    }

    public void sendMessageToClient(NetworkMessage n) {
        Tools.easyOut(n.getText() + "\n", out);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGroup(int group) {
        this.group = group;
    }

    //Getters:

    public boolean isAccepted() {
        return accepted;
    }

    public String getName() {
        return name;
    }

    public int getGroup() {
        return group;
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

        } else {
        }
    }

    public void handleIncoming(NetworkMessage msg) {
        String command = msg.getCommand();
        switch (command) {
            case Constants.ERROR_COMMAND:
                handleError(msg.getParameters());
                break;
        }
    }

    private void handleError(String[] msg) {
        //Slightly convoluted method to render array of words into a single string for output.
        StringBuffer result = new StringBuffer();
        result.append(Constants.ERROR_COMMAND);
        for (String i : msg) {
            result.append(" ");
            result.append(i);
        }
        Tools.easyOut(result.toString(), out);
    }


    @Override
    public void run() {
        String msg;
        try {
            msg = in.readLine();
            while (msg != null) {
                NetworkMessage n = new NetworkMessage(msg, this);
                setChanged();
                notifyObservers(n);
                out.newLine();
                out.flush();
                msg = in.readLine();
            }
        } catch (SocketException e) {
            setChanged();
            System.out.println("Broke socket?");
            notifyObservers("Socket broken");
        } catch (IOException e) {
            // For now, ignore and let thread stop.
            e.printStackTrace();
        }
    }

    public void setState(States state) {
        this.state = state;
    }

    public States getState() {
        return state;
    }
}
