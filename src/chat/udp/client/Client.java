package chat.udp.client;

import java.io.IOException;
import java.net.*;

public class Client {
    private String hostname;
    private int port;
    private String userName;

    public Client(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    public void execute() {
        try {
            // Address and port of the server we're trying to connect to
            InetAddress address = InetAddress.getByName("localhost");
            int port = 6464;

            DatagramSocket socket = new DatagramSocket();
            System.out.println("[INFO]: You have successfully connected to the server");
            System.out.println("[INFO]: type \"list\" to list all connected users or");
            System.out.println("[INFO]: type \"bye\" to leave the chat\n");
            new ClientReadThread(socket, this).start();
            new ClientWriteThread(socket, this, address, port).start();
        } catch (UnknownHostException e) {
            System.out.println("[ERROR]: Server not found: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("[ERROR]: I/O error: " + e.getMessage());
        }
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public static void main(String[] args) {
        String hostname = "localhost";
        int port = 4646;
        Client client = new Client(hostname, port);
        client.execute();
    }
}

