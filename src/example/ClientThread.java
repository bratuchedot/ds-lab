package example;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;

public class ClientThread implements Runnable {
    private Socket socket;
    private PrintWriter clientOut;
    private ChatServer server;
    BufferedReader br;
    OutputStreamWriter wr;

    public ClientThread(ChatServer server, Socket socket){
        this.server = server;
        this.socket = socket;

        // setup
        try {
            InputStream is = socket.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            br = new BufferedReader(isr);
            OutputStream os = socket.getOutputStream();
            wr = new OutputStreamWriter(os);
        } catch (Exception e) {

        }
    }

    private PrintWriter getWriter(){
        return clientOut;
    }

    @Override
    public void run() {
        try{
            String name = "";
            // start protocol loop
            while(true) {
                String command = br.readLine();
                System.out.println("Server received: " + command);
                String[] commandSeparated = command.split("[:]");

                // login
                if(commandSeparated[0].equals("login")) {
                    name = commandSeparated[1];
                    server.putClient(commandSeparated[1], socket);
                } else if (commandSeparated[0].equals("list")) {
                    HashMap<String, Socket> clients = server.getClients();
                    Set<String> clientList = clients.keySet();
                    String response = "";
                    for (String clientOne : clientList) {
                        response = response + clientOne + ",";
                    }
                    response = response + "\r\n";

                    wr.write(response);
                    wr.flush();
                } else if (commandSeparated[0].equals("message")) {
                    String to = commandSeparated[1];
                    String theMessage = commandSeparated[2];

                    // get the client
                    Socket toSocket = server.getClients().get(to);
                    if (toSocket != null) {
                        OutputStream tos = toSocket.getOutputStream();
                        OutputStreamWriter twr = new OutputStreamWriter(tos);
                        twr.write("message:" + name + ":" + theMessage + "\r\n");
                        twr.flush();
                    } else {
                        System.out.println("Client " + to + " does not exist");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}