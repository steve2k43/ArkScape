package net.nocturne.utils.sql.packers;

import net.nocturne.Settings;
import net.nocturne.game.npc.Drop;
import net.nocturne.game.npc.Drops;
import net.nocturne.utils.Bugsystem;
import net.nocturne.utils.sql.Database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class npcdropsSQL_BACKUP {
    private static HashMap<Integer, Drops> npcDrops = new HashMap<Integer, Drops>();
        public static String[] getdropstablesql() throws Throwable {
            Connection conn = null;
            Database db = new Database(Settings.MYSQL_HOST, Settings.MYSQL_USER,
                    Settings.MYSQL_PASS, "npcdrops");

            if (!db.init()) {
                System.err.println("[DATABASE] No connection could be made to the database.");
            }
            ResultSet rs = null;
            ResultSet rs1 = null;
            DatabaseMetaData meta = (DatabaseMetaData) db.getMeta();
            rs = meta.getTables(null, null, null, new String[] {
                    "TABLE"
            });
            String Tcountquery = "SELECT count(*) AS TOTALNUMBEROFTABLES FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'npcdrops'";
            rs1=db.executeQuery(Tcountquery);
            int Tcount = 0;
            while (rs1.next())
            {
                Tcount = (rs1.getInt(1));
            }
            int count = 0;
            //System.out.println("All table names are in test database:");
            String[] Tname = new String[Tcount];
            while (rs.next()) {
                String tblName = rs.getString("TABLE_NAME");
                Tname[count]=tblName;

                count++;
            }
            //System.out.println(count + " Rows in set ");
            //System.out.println(Tname[0]);
            rs.close();
            return Tname;
        }

    public static int[][] getdropssql(String table) {
            Boolean exists = false;
        Connection conn = null;
        Database db = new Database(Settings.MYSQL_HOST, Settings.MYSQL_USER,
                Settings.MYSQL_PASS, "npcdrops");

        if (!db.init()) {
            System.err.println("[DATABASE] No connection could be made to the database.");
        }
        DatabaseMetaData dbm = db.getMeta();
        ResultSet tables = null;
// check if "employee" table is there
        try {
           tables = dbm.getTables(null, null, table, null);
            if (tables.next()) {
               //System.out.println("EXISTSTSTSTSTS");
               exists=true;
            }
            else {
                String data[] = table.split("_");
                Bugsystem.addNPCDropBug(Integer.valueOf(data[1]));
            }
        } catch (SQLException e){

        }

        String query = "SELECT * FROM "+table+" ORDER BY ID ASC";
        String sql = "SELECT ID, min, max " +
                "FROM "+table;
        String Tcountquery = "SELECT COUNT(*) AS total FROM "+table;
        int count = 0;
        if(exists) {
            try {
                ResultSet rs1 = db.executeQuery(Tcountquery);

                while (rs1.next()) {
                    count = (rs1.getInt("total"));
                }
            } catch (SQLException e) {
                System.out.println(e);
            }
        } else {

        }

            int i = 0;
            int[][] data = new int[count][4];
            if(count>0) {
            try {
                ResultSet rs = db.executeQuery(query);
                i = 0;
                while (rs.next()) {
                    data[i][0] = (rs.getInt(1));
                    data[i][1] = (rs.getInt(2));
                    data[i][2] = (rs.getInt(3));
                    data[i][3] = (rs.getInt(4));
                    i++;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                //  System.out.println(count);

            }
        }

        return data;
    }
    public static Drops getmydrop(int npcId) {
        List<Drop>[] dList = new ArrayList[Drops.VERY_RARE + 1];
        String npcIDD = "_" + String.valueOf(npcId);
        int[][] rows = npcdropsSQL_BACKUP.getdropssql(npcIDD);
        //System.out.println("Table Name: " + npcIDD);
        int i = 0;
        int length = rows.length;
        //System.out.println("LENGTH OF ROW:" + length);
        boolean raredrops = false;
        while (i < length) {

            if (rows[i][0] == 0) {
                if (rows[i][3] == 1) {
                    raredrops = true;
                } else {
                    raredrops = false;
                }

            } else {
                int itemId = rows[i][0];
                int min = rows[i][1];
                int max = rows[i][2];
                int rarity = rows[i][3];
                if (dList[rarity] == null)
                    dList[rarity] = new ArrayList<Drop>();
                Drop drop = new Drop(itemId, min, max);
                dList[rarity].add(drop);
            }
            i++;
        }
        Drops drops = new Drops(raredrops);
        drops.addDrops(dList);
        String data[] = npcIDD.split("_");
        npcDrops.put(Integer.valueOf(data[1]), drops);
        //System.out.println("LOADING NPC " + Integer.valueOf(data[1]));
        return npcDrops.get(npcId);
    }

}
