package example;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatServer {

    private static final int portNumber = 4444;

    private int serverPort;
    private HashMap<String, Socket> clients;

    public static void main(String[] args){
        ChatServer server = new ChatServer(portNumber);
        server.startServer();
    }

    public ChatServer(int portNumber){
        this.serverPort = portNumber;
    }

    public HashMap<String, Socket> getClients(){
        return clients;
    }

    public boolean putClient(String name, Socket socket) {
        try {
            clients.put(name, socket);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private void acceptClients(ServerSocket serverSocket){

        System.out.println("server starts port = " + serverSocket.getLocalSocketAddress());
        while(true){
            try{
                Socket socket = serverSocket.accept();
                System.out.println("accepts : " + socket.getRemoteSocketAddress());
                ClientThread client = new ClientThread(this, socket);
                Thread thread = new Thread(client);
                thread.start();
            } catch (IOException ex){
                System.out.println("Accept failed on : " + serverPort);
            }
        }
    }

    private void startServer(){
        clients = new HashMap<>();
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(serverPort);
            acceptClients(serverSocket);
        } catch (IOException e){
            System.err.println("Could not listen on port: "+serverPort);
            System.exit(1);
        }
    }
}