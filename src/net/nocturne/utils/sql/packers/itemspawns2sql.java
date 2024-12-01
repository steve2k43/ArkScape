package net.nocturne.utils.sql.packers;

import net.nocturne.Settings;
import net.nocturne.game.WorldTile;
import net.nocturne.utils.Logger;
import net.nocturne.utils.sql.Database;

import java.io.BufferedReader;
import java.io.FileReader;

public class itemspawns2sql {
    public static void main(String[] args) {
        Database db = new Database(Settings.MYSQL_HOST, Settings.MYSQL_USER,
                Settings.MYSQL_PASS, "arkscape");

        if (!db.init()) {
            System.err.println("[DATABASE] No connection could be made to the database.");
        }
        Logger.log("ItemSpawns", "Packing item spawns...");
        try {
            BufferedReader in = new BufferedReader(new FileReader(
                    "data/items/unpackedSpawnsList.txt"));
            while (true) {
                String line = in.readLine();
                if (line == null)
                    break;
                if (line.startsWith("//"))
                    continue;
                String[] splitedLine = line.split(" - ", 2);
                if (splitedLine.length != 2) {
                    in.close();
                    throw new RuntimeException("Invalid generated item line: "
                            + line + ", " + splitedLine.length);
                }
                int itemId = Integer.parseInt(splitedLine[0]);

                String[] splitedLine2 = splitedLine[1].split(" ", 3);
                if (splitedLine2.length != 3) {
                    in.close();
                    throw new RuntimeException("Invalid generated item line: "
                            + line);
                }

                int x =  Integer.parseInt(splitedLine2[0]);
                int y =  Integer.parseInt(splitedLine2[1]);
                int z =  Integer.parseInt(splitedLine2[2]);
                WorldTile tile = new WorldTile(
                        Integer.parseInt(splitedLine2[0]),
                        Integer.parseInt(splitedLine2[1]),
                        Integer.parseInt(splitedLine2[2]));
                int region = tile.getRegionId();
                System.out.println(itemId+" "+x+" "+y+" "+z);
                db.executeUpdate("INSERT INTO itemspawns (ID, x, y, z, region) VALUES ("+itemId+","+x+","+y+","+z+","+region+")");
                // addItemSpawn(itemId, tile.getRegionId(), tile);
            }
            in.close();
        } catch (Throwable e) {
            Logger.handle(e);
        }
    }
}
