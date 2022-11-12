package chat.tcp.server;

import java.io.*;
import java.net.Socket;

public class UserThread extends Thread {
    private Socket socket;
    private ChatServer server;
    private PrintWriter writer;

    public UserThread(Socket socket, ChatServer server) {
        this.socket = socket;
        this.server = server;
    }

    public void run() {
        try {
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true);

            printUsers();

            String userName = reader.readLine();
            server.addUserName(userName);

            String serverMessage = userName + " has joined the chat";
            server.broadcast(serverMessage, this);

            String clientMessage;

            while (true) {
                clientMessage = reader.readLine();
                serverMessage = "[" + userName + "]: " + clientMessage;
                server.broadcast(serverMessage, this);
                if (clientMessage.equalsIgnoreCase("bye")) {
                    break;
                }
                if (clientMessage.equalsIgnoreCase("list")) {
                    printUsers();
                }
            }

            server.removeUser(userName, this);
            socket.close();

            serverMessage = userName + " has left the chat";
            server.broadcast(serverMessage, this);
        } catch (IOException ex) {
            System.out.println("Error in UserThread: " + ex.getMessage());
        }
    }

    /**
     * Sends a list of connected users to the newly connected user.
     */
    void printUsers() {
        if (server.hasUsers()) {
            writer.println("Connected users: " + server.getUserNames());
        } else {
            writer.println("No other users are connected");
        }
    }

    /**
     * Sends a message to the client.
     */
    void sendMessage(String message) {
        writer.println(message);
    }
}
