package example2;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class S_Class {
    int pt;
    ServerSocket ss = null;
    Socket socket = null;
    ExecutorService es = null;
    int clientcount = 0;

    public static void main(String[] args) throws IOException {
        S_Class sObject = new S_Class(5000);
        sObject.startServer();
    }

    S_Class(int pt) {
        this.pt = pt;
        es = Executors.newFixedThreadPool(5);
    }

    public void startServer() throws IOException {

        ss = new ServerSocket(5000);
        System.out.println("S_Class Started....");
        System.out.println("To break the connection send BYE....");
        while (true) {
            socket = ss.accept();
            clientcount++;
            ServerThread st = new ServerThread(socket, clientcount, this);
            es.execute(st);
        }

    }

    private static class ServerThread implements Runnable {

        S_Class server = null;
        Socket client = null;
        BufferedReader s1;
        PrintStream s2;
        Scanner sc = new Scanner(System.in);
        int id;
        String s;

        ServerThread(Socket client, int count, S_Class server) throws IOException {

            this.client = client;
            this.server = server;
            this.id = count;
            System.out.println("Connection established with client " + id);

            s1 = new BufferedReader(new InputStreamReader(client.getInputStream()));
            s2 = new PrintStream(client.getOutputStream());

        }

        @Override
        public void run() {
            int x = 1;
            try {
                while (true) {
                    s = s1.readLine();

                    System.out.print("Client(" + id + ") : " + s + "\n");
                    System.out.print("S_Class : ");
                    s = sc.nextLine();
                    if (s.equalsIgnoreCase("bye")) {
                        s2.println("BYE");
                        x = 0;
                        System.out.println("Connection Ended....");
                        break;
                    }
                    s2.println(s);
                }

                s1.close();
                client.close();
                s2.close();
                if (x == 0) {
                    System.out.println("*****Closing*****");
                    System.exit(0);
                }
            } catch (IOException e) {
                System.out.println("Error : " + e);
            }
        }
    }
}
