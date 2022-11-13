package example3;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Server {
    public static void main(String[] args) throws IOException {
        // Step 1 : Create a socket to listen at port 1234
        DatagramSocket socket = new DatagramSocket(1234);
        byte[] buffer = new byte[65535];


        DatagramPacket packet = null;
        while (true) {

            // Step 2 : create a DatgramPacket to receive the data.
            packet = new DatagramPacket(buffer, buffer.length);

            // Step 3 : revieve the data in byte buffer.
            socket.receive(packet);

            System.out.println("Client:-" + data(buffer));

            // Exit the server if the client sends "bye"
            if (data(buffer).toString().equals("bye")) {
                System.out.println("Client sent bye.....EXITING");
                break;
            }

            // Clear the buffer after every message.
            buffer = new byte[65535];
        }
    }

    // A utility method to convert the byte array
    // data into a string representation.
    public static StringBuilder data(byte[] buffer) {
        if (buffer == null) return null;
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (buffer[i] != 0) {
            sb.append((char) buffer[i]);
            i++;
        }
        return sb;
    }
}
