package example;

import java.io.BufferedReader;

public class ClientWriter extends Thread {
    BufferedReader br;
    public ClientWriter(BufferedReader br) {
        this.br = br;
    }

    public void run() {
        try {
            while(true) {
                String received = br.readLine();
                // protocol
                // incoming message from another client message:<from>:<the message>
                System.out.println("Received: " + received);
            }
        } catch (Exception e) {

        }
    }
}
