package net.nocturne.tools;// Java code for serialization and deserialization
// of a Java object


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import net.nocturne.tools.test;

public class test3 {
    public void run() {
        try {
            int serverPort = 4020;
            ServerSocket serverSocket = new ServerSocket(serverPort);
            //serverSocket.setSoTimeout(10000);
            System.out.println("Waiting for client on port " + serverSocket.getLocalPort() + "...");
            while(true) {


                Socket server = serverSocket.accept();
                System.out.println("Just connected to " + server.getRemoteSocketAddress());

                PrintWriter toClient =
                        new PrintWriter(server.getOutputStream(),true);
                BufferedReader fromClient =
                        new BufferedReader(
                                new InputStreamReader(server.getInputStream()));
                String line = fromClient.readLine();
                System.out.println("Server received: " + line);
                toClient.println("Thank you for connecting to " + server.getLocalSocketAddress());
                toClient.println("Please Enter Username:");

                    String msg ;
                    int msg1 ;
                    boolean user = false;
                    boolean password = false;
                    boolean login = false;
                    String username=null;


                        try {
                            msg = fromClient.readLine();
                            //msg1 = fromClient.read();
                            //tant que le client est connecté
                            int Stage = 0;
                            if(msg!=null) {
                                while (msg != null) {
                                    switch (Stage) {
                                        case 0:
                                            msg = fromClient.readLine();
                                            if (msg.equals("steve")) {
                                                Stage = 1;
                                                System.out.println(msg);
                                                toClient.println("Please enter password for: " + msg);
                                                username = msg;
                                            } else {
                                                System.out.println(msg);
                                                toClient.println("Incorrect Username. Please try again");
                                            }
                                            break;
                                        case 1:
                                            msg = fromClient.readLine();
                                            if (msg.equals("poopoo")) {
                                                Stage = 2;
                                                System.out.println(msg);
                                                toClient.println("Logged in as: " + username+
                                                        ". Welcome to the console! Please type a command.");
                                            } else {
                                                System.out.println(msg);
                                                toClient.println("Incorrect Password. Please try again");
                                            }
                                            break;
                                        case 2:
                                            Stage=2;
                                            msg = fromClient.readLine();
                                            if(msg.equals("shutdown")){
                                                toClient.println("Sending Shutdown Command");
                                                System.out.println("Shutting Down Server");
                                            } else {
                                                System.out.println(msg);
                                                toClient.println("Unknown Command");
                                            }
                                            break;

                                    }
                                }
                            } else {
                                System.out.println("SLJDFLSDJFS");
                            }
                        } catch (IOException e) {
                            //e.printStackTrace();
                            System.out.println("Client Disconnected from: "+server.getRemoteSocketAddress());
                        }
            }
        }
        catch(UnknownHostException ex) {
            ex.printStackTrace();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        test3 srv = new test3();
        try {
            srv.run();
        } catch (Throwable e){
            System.out.println(e);
        }
    }
}
