package chat.udp.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.*;

public class Server {
    private int port;
    private DatagramSocket socket;
    byte[] buffer;
    private Map<String, UserInfo> users;

    public Server(int port) throws SocketException {
        this.port = port;
        this.socket = new DatagramSocket(port);
        this.buffer = new byte[4096];
        this.users = new HashMap<>();
    }

    public static void main(String[] args) {
        int port = 6464;

        try {
            Server server = new Server(port);
            server.execute();
        } catch (SocketException ex) {
            System.out.println("[ERROR]: Socket error: " + ex.getMessage());
        }
    }

    public void execute() {
        try {
            System.out.println("[INFO]: Server is listening on port " + port);
            while (true) {
                // Receive new request
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                socket.receive(request);

                // Convert request to string message
                String message = convertBytesToString(request.getData());
                if (!message.startsWith("[")) {
                    // Register new user
                    UserInfo userInfo = new UserInfo(request.getAddress(), request.getPort());
                    this.users.put(message, userInfo);

                    // Notify all for the newly joined user
                    String infoMessage = "[INFO]: " + message + " has joined the chat";
                    System.out.println(infoMessage);
                    broadcast(infoMessage, message);

                    // Send a list of connected users to the newly connected user
                    listUsers(message);
                } else {
                    // Broadcast the message to all connected users
                    String username = getUsernameFromMessage(message);
                    broadcast(message, username);

                    // If user types "list" send a list of all connected users
                    if (message.toLowerCase().contains("list")) {
                        listUsers(username);
                    }

                    // If user types "bye" remove data from users and notify others
                    if (message.toLowerCase().contains("bye")) {
                        users.remove(username);
                        String infoMessage = "[INFO]: " + username + " has left the chat";
                        System.out.println(infoMessage);
                        broadcast(infoMessage, username);
                    }
                }
                // Clear the buffer after every message
                clearBuffer();
            }
        } catch (IOException e) {
            System.out.println("[ERROR]: Error in Server: " + e.getMessage());
        }
    }

    private static String getUsernameFromMessage(String message) {
        String[] temp1 = message.split("\\[");
        String[] temp2 = temp1[1].split("\\]");
        String excludeUsername = temp2[0];
        return excludeUsername;
    }

    private void broadcast(String message, String excludeUsername) {
        for (Map.Entry<String, UserInfo> user : users.entrySet()) {
            if (!user.getKey().equals(excludeUsername)) {
                try {
                    InetAddress address = user.getValue().getAddress();
                    int port = user.getValue().getPort();
                    byte[] buffer = message.getBytes();
                    DatagramPacket response = new DatagramPacket(buffer, buffer.length, address, port);
                    socket.send(response);
                } catch (IOException e) {
                    System.out.println("[ERROR]: Broadcast error: " + e.getMessage());
                }
            }
        }
    }

    private void listUsers(String toUsername) {
        StringBuilder sb = new StringBuilder();
        sb.append("[INFO]: Connected users: you (").append(toUsername).append("), ");
        for (Map.Entry<String, UserInfo> user : users.entrySet()) {
            if (!user.getKey().equals(toUsername)) {
                sb.append(user.getKey()).append(", ");
            }
        }
        sb.delete(sb.length() - 2, sb.length());
        try {
            byte[] buf = sb.toString().getBytes();
            InetAddress address = users.get(toUsername).getAddress();
            int port = users.get(toUsername).getPort();
            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
            socket.send(packet);
        } catch (IOException e) {
            System.out.println("[ERROR]: Error listing connected users: " + e.getMessage());
        }
    }

    private void clearBuffer() {
        this.buffer = new byte[4096];
    }

    // A utility method to convert the byte array
    // data into a string representation.
    private static String convertBytesToString(byte[] buffer) {
        if (buffer == null) return null;
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (buffer[i] != 0) {
            sb.append((char) buffer[i]);
            i++;
        }
        return sb.toString();
    }
}
