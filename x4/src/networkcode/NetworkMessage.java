package networkcode;

/**
 * Created by Bouke Willem on 27-1-2015.
 */

//Should this be observable? I don't think so.
public class NetworkMessage {
    private String text;
    private String command;
    private String[] parameters;
    private ClientHandler handler;

    //Flags:
    private boolean isClientMessage;    //Is client or server?
    private boolean extern;             //Internal message (for closing &c) or from elsewhere.
    private boolean dealtWith;          //Is the message handled?

    public NetworkMessage(String in, ClientHandler h) {
        text = in;

        //Message is not dealt with yet.
        dealtWith = false;

        //Handler:
        handler = h;

        //Message came from a client (see parameter), so:
        isClientMessage = true;

        //Parse incoming string into command with parameters:
        String parsed[] = in.split(" ", 2);
        if (parsed.length == 1) {
            //If a single command, just the command
            command = parsed[0];
        }
        if (parsed.length >= 2) {
            //If a command with parameters:
            //Command is first:
            command = parsed[0];
            //Parameters is an array of parameters:
            parameters = parsed[1].split(" ");
        }
    }

    public NetworkMessage(String in) {
        text = in;

        //Message is not dealt with yet.
        dealtWith = false;

        //Message came from a server (see parameter), so:
        isClientMessage = false;

        //Parse incoming string into command with parameters:
        String parsed[] = in.split(" ", 2);
        if (parsed.length == 1) {
            //If a single command, just the command
            command = parsed[0];
        }
        if (parsed.length >= 2) {
            //If a command with parameters:
            //Command is first:
            command = parsed[0];
            //Parameters is an array of parameters:
            parameters = parsed[1].split(" ");
        }
    }

    //@ ensures \result == true;

    public void setDealtWith(boolean dealtWith) {
        this.dealtWith = dealtWith;
    }


    //Here be getters
    public NetworkMessage getMessage() {
        return this;
    }

    public ClientHandler getHandler() {
        return handler;
    }

    public String getCommand() {
        return command;
    }

    public String[] getParameters() {
        return parameters;
    }

    public String getText() {
        return text;
    }

    public boolean isClientMessage() {
        return isClientMessage;
    }

    public boolean isExtern() {
        return extern;
    }

    public boolean isDealtWith() {
        return dealtWith;
    }

}
