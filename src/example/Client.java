package example;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args){
        String name = args[0];
        String writeTo = args[1];
        String serverHost = "127.0.0.1";
        int serverPort = 4444;

        try{
            // connect
            Socket socket = new Socket(serverHost, serverPort);

            // setup
            InputStream is = socket.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            OutputStream os = socket.getOutputStream();
            OutputStreamWriter wr = new OutputStreamWriter(os);

            // login
            // login protocol: login:<name>
            wr.write("login:" + name + "\r\n");
            wr.flush();

            // ask for list
            wr.write("list" + "\r\n");
            wr.flush();

            // get list
            String received = br.readLine();
            System.out.println(received);

            // start the client writer
            ClientWriter cw = new ClientWriter(br);
            cw.start();

            // protocol for sending messages
            // <message>:<name-to>:<the message>
            for(int i = 0; i<100; i++) {
                // write to writeTo
                wr.write("message:" + writeTo + ":" + "this is the real message " + i + "\r\n");
                wr.flush();
                try {
                    Thread.sleep(1000);
                } catch(Exception e) {

                }
            }

        } catch(IOException ex){
            System.err.println("Error Connection!");
            ex.printStackTrace();
        }
    }
}