package net.nocturne.tools;// Java code for serialization and deserialization
// of a Java object


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class test2 {


    static void init() throws Exception {
        int port = 6666;
        ServerSocket serverSocket;
        Socket clientSocket;
        PrintWriter out;
        BufferedReader in;
        serverSocket = new ServerSocket(port);
        clientSocket = new Socket();
        System.out.println("Connected");
        clientSocket = serverSocket.accept();
        System.out.println("Client Connected From "+clientSocket.getInetAddress());
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        out.println("Welcome User");
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        String inputLine;

        while ((inputLine = in.readLine()) != null) {
            if (".".equals(inputLine)) {
                out.println("good bye");
                break;
            }
            out.println(inputLine);
            System.out.println(inputLine);
        }
        System.out.println("Connected");
    }
}