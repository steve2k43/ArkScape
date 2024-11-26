package net.nocturne.utils.sql.packers;

import net.nocturne.utils.sql.Database;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;

public class itemdestroys2sql {
    private final static String UNPACKED_PATH = "data/items/unpackedDestroys.txt";
    public static void main(String[] args) {
        Database db = new Database("arctik.co.uk", "ArkScape",
                "ArkScape", "arkscape");

        if (!db.init()) {
            System.err.println("[DATABASE] No connection could be made to the database.");
        }

        try {
            BufferedReader in = new BufferedReader(
                    new FileReader(UNPACKED_PATH));
            while (true) {
                String line = in.readLine();
                if (line == null)
                    break;
                if (line.startsWith("//"))
                    continue;
                line = line.replace("﻿", "");
                String[] splitedLine = line.split(" - ", 2);
                if (splitedLine.length < 2) {
                    in.close();
                    throw new RuntimeException(
                            "Invalid list for item examine line: " + line);
                }
                int itemId = Integer.valueOf(splitedLine[0]);
                if (splitedLine[1].length() > 255)
                    continue;
                int ID = Integer.parseInt(splitedLine[0]);
                String text = splitedLine[1];
                if(text.contains("'")){
                    text = text.replace("'","''");
                }
                String query = "INSERT INTO itemdestroys (ID, text) VALUES ("+ID+",'"+text+"')";
                System.out.println(query);
                db.executeUpdate(query);

            }

            in.close();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
}
