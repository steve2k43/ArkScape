package net.nocturne.tools;

import net.nocturne.Engine;
import net.nocturne.game.World;
import net.nocturne.login.Login;
import net.nocturne.login.account.Account;
import net.nocturne.utils.Logger;
import net.nocturne.utils.LoginFilesManager;
import net.nocturne.utils.SerializableFilesManager;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class CommandServer2 implements Serializable{
    public static void main(String argv[]) throws Exception
    {
        String clientSentence;
        String capitalizedSentence;
        ServerSocket welcomeSocket = new ServerSocket(6789);

        while(true)
        {
            Socket connectionSocket = welcomeSocket.accept();
            BufferedReader inFromClient =
                    new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
            clientSentence=null;
            try {
                clientSentence = inFromClient.readLine();
            } catch (NullPointerException e){
                System.out.println("NULL RECEIVED");
                connectionSocket.close();
                break;
            }
            if(clientSentence==null)
                continue;
            System.out.println("Received: " + clientSentence);
            int Stage=0;
            String[] parts = clientSentence.split("::");
            if(parts.length>2) {
                System.out.println("Command: " + parts[0]);
                System.out.println("Arg 1: " + parts[1]);
                System.out.println("Arg 2: " + parts[2]);
            } else {
                System.out.println("Wrong Params");
                connectionSocket.close();
                continue;
            }
            if(parts[0].equals("login")) {
                Stage = 1;
                System.out.println("Going to S1");
            }
            switch (Stage){
                case 1:
                    Boolean Sucess=false;
                    String filePathString = "data/accounts/"+parts[1]+".acc";
                    File f = new File(filePathString);
                    if(f.exists() && !f.isDirectory()) {
                        System.out.println("Account Exists");
                        Account account = (Account) SerializableFilesManager.loadSerializedFile(f);
                        String pw = account.getPassword();
                        if(pw.equals(parts[2])) {
                            outToClient.writeBytes("login" + '\n');
                            try {
                                //test.givebond(parts[1]);
                            }catch (Throwable e){

                            }
                            break;
                        }
                        else {
                            outToClient.writeBytes("Incorrect Password" + '\n');
                            break;
                        }
                    }
                    else{
                        outToClient.writeBytes(parts[1]+" does not exist"+'\n');
                        break;
                    }

            }

            connectionSocket.close(); //this line was part of the solution
        }
    }
}
