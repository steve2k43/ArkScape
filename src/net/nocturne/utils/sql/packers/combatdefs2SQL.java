package net.nocturne.utils.sql.packers;

import net.nocturne.Settings;
import net.nocturne.utils.sql.Database;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;

public class combatdefs2SQL {
    private final static HashMap<Integer, String> itemExamines = new HashMap<Integer, String>();
    private final static String UNPACKED_PATH = "data/npcs/unpackedCombatDefinitionsList.txt";

    public static void main(String[] args) {
        try {
            Database db = new Database(Settings.MYSQL_HOST, Settings.MYSQL_USER,
                    Settings.MYSQL_PASS, "arkscape");

            if (!db.init()) {
                System.err.println("[DATABASE] No connection could be made to the database.");
            }
            String q = "INSERT INTO combatdefs (ID, Hitpoints, attackAnim, defenceAnim, deathAnim, " +
                    "respawnDelay, attackGfx, attackProjectile, xp, follow, poisonImmune, poisonous, " +
                    "aggressive, aggroRatio) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            Connection conn = db.connect();
            PreparedStatement stmt = conn.prepareStatement(q);
           // stmt.executeUpdate("DROP TABLE IF EXISTS itemexamines");
           // String query = "CREATE TABLE itemexamines (ID INTEGER, text Varchar(255))";
          //  stmt.executeUpdate(query);

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
                int id = Integer.valueOf(splitedLine[0]);
                String[] splitedLine1 = splitedLine[1].split(" ");
                if (splitedLine[1].length() > 255)
                    continue;
                if (splitedLine1.length < 2) {
                    in.close();
                    throw new RuntimeException(
                            "Invalid list for item examine line: " + line);
                }


                PreparedStatement a;
                int hitpoints = Integer.parseInt(splitedLine1[0]);
                int attackanim = Integer.parseInt(splitedLine1[1]);
                int defenceanim = Integer.parseInt(splitedLine1[2]);
                int deathanim = Integer.parseInt(splitedLine1[3]);
                int respawndelay = Integer.parseInt(splitedLine1[4]);
                int attackgfx = Integer.parseInt(splitedLine1[5]);
                int attackprojectile = Integer.parseInt(splitedLine1[6]);
                double xp = Double.parseDouble(splitedLine1[7]);
                int follow = Boolean.parseBoolean(splitedLine1[8]) ? 1 : 0;
                int pimmune = Boolean.parseBoolean(splitedLine1[9]) ? 1 : 0;
                int posion = Boolean.parseBoolean(splitedLine1[10]) ? 1 : 0;
                int aggressive = Boolean.parseBoolean(splitedLine1[11]) ? 1 : 0;
                int aggroratio = Boolean.parseBoolean(splitedLine1[12]) ? 1 : 0;
                stmt.setInt(1, id);
                stmt.setInt(2, hitpoints);
                stmt.setInt(3, attackanim);
                stmt.setInt(4, defenceanim);
                stmt.setInt(5, deathanim);
                stmt.setInt(6, respawndelay);
                stmt.setInt(7, attackgfx);
                stmt.setInt(8, attackprojectile);
                stmt.setDouble(9, xp);
                stmt.setInt(10, follow);
                stmt.setInt(11, pimmune);
                stmt.setInt(12, posion);
                stmt.setInt(13, aggressive);
                stmt.setInt(14, aggroratio);

                System.out.println(follow);

                stmt.executeUpdate();
            }

            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }
}
