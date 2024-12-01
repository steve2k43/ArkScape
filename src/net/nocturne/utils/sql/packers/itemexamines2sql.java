package net.nocturne.utils.sql.packers;

import net.nocturne.Settings;
import net.nocturne.utils.sql.Database;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

public class itemexamines2sql {
    private final static HashMap<Integer, String> itemExamines = new HashMap<Integer, String>();
    private final static String UNPACKED_PATH = "data/items/unpackedExamines.txt";
    public static void main(String[] args) {
        try {
            Database db = new Database(Settings.MYSQL_HOST, Settings.MYSQL_USER,
                    Settings.MYSQL_PASS, "arkscape");

            if (!db.init()) {
                System.err.println("[DATABASE] No connection could be made to the database.");
            }
            Connection conn = db.connect();
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("DROP TABLE IF EXISTS itemexamines");
            String query = "CREATE TABLE itemexamines (ID INTEGER, text Varchar(255))";
            stmt.executeUpdate(query);
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

                String text = splitedLine[1];
                if(text.contains("'")){
                    text = text.replace("'","''");
                }
                String q = "INSERT INTO itemexamines (ID, text) VALUES ("+itemId+",'"+text+"')";
                System.out.println(q);
                stmt.executeUpdate(q);
            }

            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }
}
