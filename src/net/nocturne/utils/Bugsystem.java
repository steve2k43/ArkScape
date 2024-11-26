package net.nocturne.utils;

import com.mysql.jdbc.Statement;
import net.nocturne.cache.loaders.ItemDefinitions;
import net.nocturne.cache.loaders.NPCDefinitions;
import net.nocturne.game.World;
import net.nocturne.game.player.Player;
import net.nocturne.utils.sql.Database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

public class Bugsystem {

    public static void addBug(Player coords, String value, String username) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        int i = 0;
        long unixTime = System.currentTimeMillis() / 1000L;
        i = Math.toIntExact(unixTime);
        System.out.println(i);
        int last_inserted_id = 0;


    try {
        Database db = new Database("arctik.co.uk", "ArkScape",
                "ArkScape", "bugtracker");

        if (!db.init()) {
            System.err.println("[DATABASE] No connection could be made to the database.");
            return;
        }
       /* ResultSet rs;
        String query = "INSERT INTO bugs (submittedby, bug, timestamp, coords) VALUES (?, ?, ?, ?)";
        PreparedStatement prest;
        prest = db.prepare1(query, Statement.RETURN_GENERATED_KEYS);
        prest.setString(1, username);
        prest.setString(2, value);
        prest.setTimestamp(3, timestamp);
        prest.setString(4, String.valueOf(coords));
        prest.executeUpdate();

        rs = prest.getGeneratedKeys();
        if (rs.next()) {
            last_inserted_id = rs.getInt(1);
        }

        */
        ResultSet rs;
        String query = "INSERT INTO mantis_bug_text_table (description, steps_to_reproduce, additional_information) VALUES (?, ?, ?)";
        PreparedStatement prest;
        prest = db.prepare1(query, Statement.RETURN_GENERATED_KEYS);
        prest.setString(1, value);
        prest.setString(2, "N/A");
        prest.setString(3, String.valueOf(coords));
        prest.executeUpdate();

        rs = prest.getGeneratedKeys();
        if (rs.next()) {
            last_inserted_id = rs.getInt(1);
        }
        System.out.println("Record 1 DONE: last id = "+last_inserted_id);
        prest.close();

        query = "INSERT INTO mantis_bug_table (project_id, handler_id, bug_text_id, summary, date_submitted, last_updated) VALUES (?, ?, ?, ?, ?, ?)";
        prest = db.prepare1(query, Statement.RETURN_GENERATED_KEYS);
        prest.setInt(1, 1);
        prest.setInt(2, 0);
        prest.setInt(3, last_inserted_id);
        prest.setString(4, value);
       // prest.setTimestamp(5, timestamp);
        prest.setInt(5, i);
        prest.setInt(6, i);
        prest.executeUpdate();
        rs = prest.getGeneratedKeys();
        if (rs.next()) {
            last_inserted_id = rs.getInt(1);
        }
        System.out.println("Record 2 DONE: last id = "+last_inserted_id);
        prest.close();
        query = "INSERT INTO mantis_custom_field_string_table (field_id, bug_id, value) VALUES (?, ?, ?)";
        prest = db.prepare1(query, Statement.RETURN_GENERATED_KEYS);
        prest.setInt(1, 1);
        prest.setInt(2, last_inserted_id);
        prest.setString(3, username);
        prest.executeUpdate();
        rs = prest.getGeneratedKeys();
        if (rs.next()) {
            last_inserted_id = rs.getInt(1);
        }
        System.out.println("Record 3 DONE: last id = "+last_inserted_id);



      //  query = "INSERT INTO mantis_custom_field_string_table (bug_id, value) VALUES (?, ?)";



        for (Player mod : World.getPlayers()) {
            if (mod == null || mod.hasFinished() || !mod.hasStarted()
                    || (!mod.isSupport()))
                continue;
            mod.getPackets().sendGameMessage("A Bug has been submitted.");
        }
        //db.destroyAll();
        prest.close();
    } catch (Exception e) {
        System.out.println(e);
    }
}

    public static void addNPCBug(int clickoption, String npcname, int npcid, int x, int y, int plane ) {
        try {
            Database db = new Database("arctik.co.uk", "ArkScape",
                    "ArkScape", "arkscape_bugs");

            if (!db.init()) {
                System.err.println("[DATABASE] No connection could be made to the database.");
                return;
            }
            String poop = String.valueOf(clickoption);
            String query = "";
            if(clickoption <= 4) {
                query = "SELECT COUNT(*) AS total FROM clickoption" + poop + " WHERE ID=" + npcid;
            } else if (clickoption == 5) {
                query = "SELECT COUNT(*) AS total FROM npcexamine WHERE ID=" + npcid;
            }

            int count = 0;
            //PreparedStatement prest;
            //prest = db.prepare(query);
            //prest.setInt(1, npcid);
            //rs = prest.executeQuery(query);
            try {
                ResultSet rs = db.executeQuery(query);
                while(rs.next())
                    count=rs.getInt(1);
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                //  System.out.println(count);
            }
            if(count == 0){
                ResultSet rs;
                String query1 = "";
                if(clickoption <= 4) {
                    query1 = "INSERT INTO clickoption"+clickoption+" (ID, npcname, x, y, z) VALUES (?, ?, ?, ?, ?)";
                } else if (clickoption == 5){
                    query1 = "INSERT INTO npcexamine (ID, npcname, x, y, z) VALUES (?, ?, ?, ?, ?)";
                }
                PreparedStatement prest;
                prest = db.prepare(query1);
                prest.setInt(1, npcid);
                prest.setString(2, npcname);
                prest.setInt(3, x);
                prest.setInt(4, y);
                prest.setInt(5, plane);
                prest.executeUpdate();
            }

            //prest.close();


        } catch (Exception e) {
            System.out.println(e);
        }


    }

    public static void addObjectBug(int clickoption, String objectname, int objectid, int x, int y, int plane ) {
        try {
            Database db = new Database("arctik.co.uk", "ArkScape",
                    "ArkScape", "arkscape_bugs");

            if (!db.init()) {
                System.err.println("[DATABASE] No connection could be made to the database.");
                return;
            }
            String poop = String.valueOf(clickoption);
            String query = "";
            if(clickoption <= 5) {
                query = "SELECT COUNT(*) AS total FROM object_clickoption" + poop + " WHERE ID=" + objectid;
            } else if (clickoption == 6) {
                query = "SELECT COUNT(*) AS total FROM objectexamine WHERE ID=" + objectid;
            }

            int count = 0;
            //PreparedStatement prest;
            //prest = db.prepare(query);
            //prest.setInt(1, npcid);
            //rs = prest.executeQuery(query);
            try {
                ResultSet rs = db.executeQuery(query);
                while(rs.next())
                    count=rs.getInt(1);
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                //  System.out.println(count);
            }
            if(count == 0){
                ResultSet rs;
                String query1 = "";
                if(clickoption <= 5) {
                    query1 = "INSERT INTO object_clickoption"+clickoption+" (ID, objectname, x, y, z) VALUES (?, ?, ?, ?, ?)";
                } else if (clickoption == 6){
                    query1 = "INSERT INTO objectexamine (ID, objectname, x, y, z) VALUES (?, ?, ?, ?, ?)";
                }
                PreparedStatement prest;
                prest = db.prepare(query1);
                prest.setInt(1, objectid);
                prest.setString(2, objectname);
                prest.setInt(3, x);
                prest.setInt(4, y);
                prest.setInt(5, plane);
                prest.executeUpdate();
            }

            //prest.close();


        } catch (Exception e) {
            System.out.println(e);
        }


    }

    public static void addNPCDropBug(int npcId) {
        try {
            Database db = new Database("arctik.co.uk", "ArkScape",
                    "ArkScape", "arkscape_bugs");

            if (!db.init()) {
                System.err.println("[DATABASE] No connection could be made to the database.");
                return;
            }
                String query = "SELECT COUNT(*) AS total FROM missingdrops WHERE ID=" + npcId;

            int count = 0;
            //PreparedStatement prest;
            //prest = db.prepare(query);
            //prest.setInt(1, npcid);
            //rs = prest.executeQuery(query);
            try {
                ResultSet rs = db.executeQuery(query);
                while(rs.next())
                    count=rs.getInt(1);
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                //  System.out.println(count);
            }
            if(count == 0){
                ResultSet rs;
                String query1 = "";
                    query1 = "INSERT INTO missingdrops (ID, npcname) VALUES (?, ?)";
                PreparedStatement prest;
                prest = db.prepare(query1);
                prest.setInt(1, npcId);
                prest.setString(2, NPCDefinitions.getNPCDefinitions(npcId).getName());
                prest.executeUpdate();
            }

            //prest.close();


        } catch (Exception e) {
            System.out.println(e);
        }


    }

    public static void addItemExamineBug(int id) {
        try {
            Database db = new Database("arctik.co.uk", "ArkScape",
                    "ArkScape", "arkscape_bugs");

            if (!db.init()) {
                System.err.println("[DATABASE] No connection could be made to the database.");
                return;
            }
            String query = "SELECT COUNT(*) AS total FROM itemexamine WHERE ID=" + id;

            int count = 0;
            //PreparedStatement prest;
            //prest = db.prepare(query);
            //prest.setInt(1, npcid);
            //rs = prest.executeQuery(query);
            try {
                ResultSet rs = db.executeQuery(query);
                while(rs.next())
                    count=rs.getInt(1);
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                //  System.out.println(count);
            }
            if(count == 0){
                ResultSet rs;
                String query1 = "";
                query1 = "INSERT INTO itemexamine (ID, itemname) VALUES (?, ?)";
                PreparedStatement prest;
                prest = db.prepare(query1);
                prest.setInt(1, id);
                prest.setString(2, ItemDefinitions.getItemDefinitions(id).getName());
                prest.executeUpdate();
            }

            //prest.close();


        } catch (Exception e) {
            System.out.println(e);
        }


    }
}
