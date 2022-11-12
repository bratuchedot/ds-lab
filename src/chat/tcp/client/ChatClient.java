package chat.tcp.client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * This is the client. You can run multiple instances of
 * this program. Type "bye" to terminate the program.
 */
public class ChatClient {
    private String hostname;
    private int port;
    private String userName;

    public ChatClient(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    public void execute() {
        try {
            Socket socket = new Socket(hostname, port);
            System.out.println("You have successfully connected to the chat server");
            new ReadThread(socket, this).start();
            new WriteThread(socket, this).start();
        } catch (UnknownHostException ex) {
            System.out.println("Server not found: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("I/O error: " + ex.getMessage());
        }
    }

    void setUserName(String userName) {
        this.userName = userName;
    }

    String getUserName() {
        return this.userName;
    }

    public static void main(String[] args) {
//        if (args.length < 2) {
//            System.out.println("Syntax: java ChatServer <hostname> <port-number>");
//            System.exit(0);
//        }
//        String hostname = args[0];
//        int port = Integer.parseInt(args[1]);
        String hostname = "localhost";
        int port = 3535;

        ChatClient client = new ChatClient(hostname, port);
        client.execute();
    }
}
