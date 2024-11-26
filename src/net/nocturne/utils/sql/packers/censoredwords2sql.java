package net.nocturne.utils.sql.packers;

import net.nocturne.utils.sql.Database;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

public class censoredwords2sql {
    private final static HashMap<Integer, String> itemExamines = new HashMap<Integer, String>();
    private final static String UNPACKED_PATH = "data/unpackedCensoredWords.txt";
    public static void main(String[] args) {
        try {
            Database db = new Database("arctik.co.uk", "ArkScape",
                    "ArkScape", "arkscape");

            if (!db.init()) {
                System.err.println("[DATABASE] No connection could be made to the database.");
            }
            Connection conn = db.connect();
            Statement stmt = conn.createStatement();
            BufferedReader in = new BufferedReader(
                    new FileReader(UNPACKED_PATH));
            while (true) {
                String line = in.readLine();
                if (line == null)
                    break;
                if (line.startsWith("//"))
                    continue;
                line = line.replace("﻿", "");
                String word = line;
                String q = "INSERT INTO censoredwords (word) VALUES ('"+word+"')";
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
