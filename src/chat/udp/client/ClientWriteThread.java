package chat.udp.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class ClientWriteThread extends Thread {
    private DatagramSocket socket;
    private Client client;
    private InetAddress address;
    int port;
    private byte[] buffer;

    public ClientWriteThread(DatagramSocket socket, Client client, InetAddress address, int port) {
        this.socket = socket;
        this.client = client;
        this.address = address;
        this.port = port;
        this.buffer = new byte[65535];
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("[INFO]: Enter your name: ");
        String userName = scanner.nextLine();
        client.setUserName(userName);

        // Login to the server (send username, address and port)
        try {
            buffer = userName.getBytes();
            DatagramPacket loginRequest = new DatagramPacket(buffer, buffer.length, address, port);
            socket.send(loginRequest);
        } catch (IOException e) {
            System.out.println("[ERROR]: Error writing to server: " + e.getMessage());
        }

        String message;
        while (true) {
            try {
                String userNameInBrackets = "[" + userName + "]: ";
                System.out.print(userNameInBrackets);
                message = scanner.nextLine();
                message = userNameInBrackets.concat(message);
                buffer = message.getBytes();
                DatagramPacket request = new DatagramPacket(buffer, buffer.length, address, port);
                socket.send(request);
                if (message.toLowerCase().contains("bye")) {
                    System.out.println("[INFO]: You have left the chat");
                    break;
                }
                clearBuffer();
            } catch (IOException e) {
                System.out.println("[ERROR]: Error writing to server: " + e.getMessage());
            }
        }
    }

    private void clearBuffer() {
        this.buffer = new byte[65535];
    }
}
