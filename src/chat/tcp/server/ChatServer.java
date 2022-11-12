package chat.tcp.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

/**
 * This is the server. It needs to be started first
 * in order the app to work.
 */
public class ChatServer {
    private int port;
    private Set<String> userNames;
    private Set<UserThread> userThreads;

    public ChatServer(int port) {
        this.port = port;
        this.userNames = new HashSet<>();
        this.userThreads = new HashSet<>();
    }

    public void execute() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Chat Server is listening on port " + port);
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New user has joined the chat");

                UserThread newUser = new UserThread(socket, this);
                userThreads.add(newUser);
                newUser.start();
            }
        } catch (IOException ex) {
            System.out.println("Error in ChatServer: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
//        if (args.length < 1) {
//            System.out.println("Syntax: java ChatServer <port-number>");
//            System.exit(0);
//        }
//        int port = Integer.parseInt(args[0]);
        int port = 3535;

        ChatServer server = new ChatServer(port);
        server.execute();
    }

    /**
     * Delivers a message from one user to others (broadcasting)
     */
    void broadcast(String message, UserThread excludeUser) {
        for (UserThread aUser : userThreads) {
            if (aUser != excludeUser) {
                aUser.sendMessage(message);
            }
        }
    }

    /**
     * Stores username of the newly connected client.
     */
    void addUserName(String userName) {
        userNames.add(userName);
    }

    /**
     * When a client is disconnected, removes the associated username and UserThread
     */
    void removeUser(String userName, UserThread aUser) {
        boolean removed = userNames.remove(userName);
        if (removed) {
            userThreads.remove(aUser);
            System.out.println("The user " + userName + " has left the chat");
        }
    }

    Set<String> getUserNames() {
        return this.userNames;
    }

    /**
     * Returns true if there are other users connected (not count the currently connected user)
     */
    boolean hasUsers() {
        return !this.userNames.isEmpty();
    }
}
