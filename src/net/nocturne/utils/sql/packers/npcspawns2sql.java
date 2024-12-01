package net.nocturne.utils.sql.packers;

import net.nocturne.Settings;
import net.nocturne.cache.Cache;
import net.nocturne.game.WorldTile;
import net.nocturne.utils.Logger;
import net.nocturne.utils.Utils;
import net.nocturne.utils.sql.Database;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.PreparedStatement;

public class npcspawns2sql {
    private static final String UNPACKED_PATH = "data/npcs/spawnsList.txt";
    public static void main(String[] args) {
        try {
            Cache.init();
            BufferedReader in = new BufferedReader(new FileReader(UNPACKED_PATH));
            int count = 0;
            Database db = new Database(Settings.MYSQL_HOST, Settings.MYSQL_USER,
                    Settings.MYSQL_PASS, "arkscape");

            if (!db.init()) {
                System.err.println("[" +
                        "DATABASE] No connection could be made to the database.");
            }
            String query = "INSERT INTO npcspawns (ID, x, y, z, bounds, hash) VALUES (?,?,?,?,?, ?)";
            PreparedStatement stmt = db.prepare(query);
            while (true) {
                count++;
                String line = in.readLine();
                if (line == null)
                    break;
                if (line.startsWith("//") || line.startsWith("RSBOT"))
                    continue;
                String[] splitedLine = line.split(" - ", 2);
                if (splitedLine.length != 2) {
                    in.close();
                    throw new RuntimeException("Invalid NPC Spawn line: "
                            + line + " , line number: " + count);
                }
                int npcId = Integer.parseInt(splitedLine[0]);
                if (npcId >= Utils.getNPCDefinitionsSize())
                    continue;
                String[] splitedLine2 = splitedLine[1].split(" ", 5);
                if (splitedLine2.length != 3 && splitedLine2.length != 5) {
                    in.close();
                    throw new RuntimeException("Invalid NPC Spawn line: "
                            + line + " , line number: " + count);
                }
                WorldTile tile = new WorldTile(
                        Integer.parseInt(splitedLine2[0]),
                        Integer.parseInt(splitedLine2[1]),
                        Integer.parseInt(splitedLine2[2]));
                int x = Integer.parseInt(splitedLine2[0]);
                int y = Integer.parseInt(splitedLine2[1]);
                int z = Integer.parseInt(splitedLine2[2]);
                int mapAreaNameHash = -1;
                boolean canBeAttackFromOutOfArea = true;
                if (splitedLine2.length == 5) {
                    mapAreaNameHash = Utils.getNameHash(splitedLine2[3]);
                    canBeAttackFromOutOfArea = Boolean
                            .parseBoolean(splitedLine2[4]);
                }
                int bounds=0;
                if(canBeAttackFromOutOfArea==true)
                    bounds=1;
                if(canBeAttackFromOutOfArea==false)
                    bounds=0;
                stmt.setInt(1, npcId);
                stmt.setInt(2, x);
                stmt.setInt(3, y);
                stmt.setInt(4, z);
                stmt.setInt(5, bounds);
                stmt.setInt(6, mapAreaNameHash);
                stmt.executeUpdate();
                System.out.println("Inserting: "+npcId);
            }
            in.close();
        } catch (Throwable e) {
            Logger.handle(e);
        }

    }

}
