package chat.udp.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class ClientReadThread extends Thread {
    private DatagramSocket socket;
    private Client client;
    private byte[] buffer;

    public ClientReadThread(DatagramSocket socket, Client client) {
        this.socket = socket;
        this.client = client;
        this.buffer = new byte[65535];
    }

    public void run() {
        while (true) {
            try {
                // Receive request with message
                DatagramPacket response = new DatagramPacket(buffer, buffer.length);
                socket.receive(response);

                // Convert the request and print the message
                String message = convertBytesToString(buffer);
                System.out.println("\n" + message);

                // Print the user's name after the message
                if (client.getUserName() != null) {
                    System.out.print("[" + client.getUserName() + "]: ");
                }

                // Clear the buffer after every message.
                clearBuffer();
            } catch (IOException e) {
                System.out.println("[ERROR]: Error reading from the server: " + e.getMessage());
                break;
            }
        }
    }

    private void clearBuffer() {
        this.buffer = new byte[65535];
    }

    // A utility method to convert the byte array
    // data into a string representation.
    public static String convertBytesToString(byte[] buffer) {
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
