package server;


import constants.Constants;
import networkcode.ClientHandler;
import networkcode.Lobby;
import networkcode.NetworkMessage;
import utils.Tools;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by over on 22/01/2015.
 */
public class Server extends Thread implements Observer {
    private Lobby lobby;
    // Map: Key = Name, Value = Client
    private Map<String, ClientHandler> clientHandlerMap = new HashMap<>();
    private int port;

    // The server has to be started with a port on which it will listen.
    public Server(int port) {
        this.port = port;
        lobby = new Lobby();
    }

//    // This function creates a map wherein a playername is the key and its corresponding handler is the value
//    public void mapAdder(networkMessage message)
//    {
//        clientHandlerMap.put(message.getParameters()[0], message.getHandler());
//    }

    public void handleJoin(NetworkMessage msg) {
        ClientHandler handler = msg.getHandler();
        if (!handler.isAccepted()) {
            if (msg.getParameters() != null && msg.getParameters().length >= 2) {
                String name = msg.getParameters()[0];
                int group = -1;
                try {
                    group = Integer.parseInt(msg.getParameters()[1]);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    System.out.println("ERROR: " + msg.getParameters()[1] + " is not an integer");

                    //MAY send an error command to the client. Error 009, invalid parameter error.
                    //Something like
                    Tools.sendError(Constants.ERROR_COMMAND + " " + Constants.ERROR9 + " " + "Invalid group parameter", handler);

                }
                if (group > 0 && group <= 99) {
                    if (!clientHandlerMap.keySet().contains(name)) {
                        System.out.println(name + " has joined the game.");
                        clientHandlerMap.put(name, handler);
                        handler.setAccepted();
                        handler.setName(name);
                        handler.setGroup(group);
                        lobby.addHandler(handler);
                    } else {
                        System.out.println("User " + name + " already exists.");
                        Tools.sendError(Constants.ERROR_COMMAND + " " + Constants.ERROR4 + " " + "Username already exists", handler);
                    }
                } else {
                    Tools.sendError(Constants.ERROR_COMMAND + " " + Constants.ERROR9 + " " + "Invalid group number", handler);
                }
            } else {
                Tools.sendError(Constants.ERROR_COMMAND + " " + Constants.ERROR9 + " " + "Invalid parameters", handler);
            }
        } else {
            Tools.sendError(Constants.ERROR_COMMAND + " " + Constants.ERROR8 + " " + "You are already a player", handler);
        }
    }

    public void run() {
        try {
            ServerSocket ssock = new ServerSocket(port, 0, InetAddress.getByName("0.0.0.0"));

            while (true) {
                Socket sock = ssock.accept();
                System.out.println("Client opened a socket from " + sock.getInetAddress() + " !");
                ClientHandler handler = new ClientHandler(sock);
                handler.addObserver(this);
                new Thread(handler).start();
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println(Constants.SERVERUSAGE);
            System.exit(0);
        }

        String portString = args[0];
        int port = 0;

        // parse portnumber
        try {
            port = Integer.parseInt(portString);
        } catch (NumberFormatException e) {
            System.out.println(Constants.SERVERUSAGE);
            System.out.println("ERROR: port " + portString + " is not an integer");
            System.exit(0);
        }

        // And start the server
        Server f = new Server(port);
        System.out.println("Server starting.");
        f.start();
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof NetworkMessage) {
            NetworkMessage msg = (NetworkMessage) arg;
            if (msg.getCommand().equals(Constants.JOIN_COMMAND)) {
//                mapAdder(msg);
                handleJoin(msg);
            }
        } else if (arg == "Socket broken") {
            ClientHandler c = (ClientHandler) o;
            System.out.println("Connection to " + c.getName() + " was broken.");
            lobby.clientHandlers.remove(c);
            lobby.readyClients.remove(c);
        }
    }
}
