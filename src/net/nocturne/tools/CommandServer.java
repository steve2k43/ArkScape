package net.nocturne.tools;

import net.nocturne.Engine;
import net.nocturne.game.World;
import net.nocturne.game.player.Player;
import net.nocturne.login.Login;
import net.nocturne.login.account.Account;
import net.nocturne.utils.LoginFilesManager;
import net.nocturne.utils.SerializableFilesManager;
import sun.tools.jar.CommandLine;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class CommandServer implements Serializable{
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
                    Account account = null;
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
                                    if(msg.equals("shutdown")) {
                                        toClient.println("Sending Shutdown Command");
                                        System.out.println("Shutting Down Server");
                                        Engine.shutdown(0, true, false);
                                    }else if(msg.equals("restart")){
                                        toClient.println("Sending Restart Command");
                                        System.out.println("Restarting Server");
                                    }else if(msg.equals("online")){
                                        toClient.println("Players Online: "+ World.getPlayers().size());
                                    }else if(msg.equals("update")){
                                        toClient.println("Sending Update Command");
                                        System.out.println("Updating Server");
                                        Engine.shutdown(10, false,true);
                                    }else if(msg.equals("kick")) {
                                        toClient.println("Please select a player to kick");
                                        Stage = 3;
                                        break;
                                    }else if(msg.equals("getpassword")){
                                        toClient.println("Please select a player to retrieve their password");
                                        Stage=4;
                                    }else if(msg.equals("setpassword")){
                                        toClient.println("Please select a player to set their password");
                                        Stage=5;
                                    }else {
                                        System.out.println(msg);
                                        toClient.println("Unknown Command");
                                    }
                                    break;
                                case 3:
                                    Stage= 2;
                                    msg = fromClient.readLine();
                                    String x= msg;
                                    Boolean poo=false;
                                    try{
                                        poo=World.getPlayer(x).isOnline();
                                    }catch (Throwable e){
                                        toClient.println(x+" Isn't online, or does not exist");
                                        break;
                                    }
                                    if(poo){
                                        toClient.println("Kicking "+x);
                                        World.getPlayer(x).logout(false);
                                    }
                                    break;
                                case 4:
                                    Stage = 2;
                                    msg = fromClient.readLine();
                                    String xx= msg;
                                    Boolean exists=false;
                                    try{
                                        String filePathString = "data/accounts/"+xx+".acc";
                                        File f = new File(filePathString);
                                        if(f.exists() && !f.isDirectory()) {
                                            account = (Account) SerializableFilesManager.loadSerializedFile(f);
                                            toClient.println("Password for "+xx+" is: "+account.getPassword());
                                            account=null;
                                            break;
                                        }
                                        else{
                                            toClient.println(xx+" does not exist");
                                            break;
                                        }
                                    }catch (Throwable e){
                                        toClient.println(xx+" does not exist");
                                        break;
                                    }
                                case 5:
                                    Stage = 2;
                                    msg = fromClient.readLine();
                                    try{
                                        String filePathString = "data/accounts/"+msg+".acc";
                                        File f = new File(filePathString);
                                        if(f.exists() && !f.isDirectory()) {
                                            Stage=6;
                                            //account=(Account) SerializableFilesManager.loadSerializedFile(f);
                                            account = Login.forceLoadAccount(msg);
                                            toClient.println("Please choose a new password for "+msg);
                                            break;
                                        }
                                        else{
                                            toClient.println(msg+" does not exist");
                                            break;
                                        }

                                    }catch (Throwable e){
                                        toClient.println(msg+" does not exist");
                                        break;
                                    }
                                case 6:
                                    Stage = 2;
                                    msg = fromClient.readLine();
                                    try{
                                        String newpass = msg;
                                        account.setPassword(newpass);

                                        LoginFilesManager.saveAccount(account);

                                        toClient.println("Set password for "+account.getUsername()+" to "+newpass);
                                        account=null;
                                        break;
                                    }catch (Throwable e){
                                        toClient.println(msg+" does not exist");
                                        break;
                                    }


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
        CommandServer srv = new CommandServer();
        try {
            srv.run();
        } catch (Throwable e){
            System.out.println(e);
        }
    }
    public static void init() {
        CommandServer srv = new CommandServer();
        srv.run();
    }
}
