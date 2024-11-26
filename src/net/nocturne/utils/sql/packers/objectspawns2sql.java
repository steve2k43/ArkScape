package net.nocturne.utils.sql.packers;

import net.nocturne.game.WorldTile;
import net.nocturne.utils.Logger;
import net.nocturne.utils.sql.Database;

import java.io.BufferedReader;
import java.io.FileReader;

public class objectspawns2sql {
    public static void main(String[] args) {
        Database db = new Database("arctik.co.uk", "ArkScape",
                "ArkScape", "arkscape");

        if (!db.init()) {
            System.err.println("[DATABASE] No connection could be made to the database.");
        }
        Logger.log("ItemSpawns", "Packing object spawns...");

        try {
            BufferedReader in = new BufferedReader(new FileReader(
                    "data/map/unpackedSpawnsList.txt"));
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
                String[] splitedLine2 = splitedLine[0].split(" ", 3);
                String[] splitedLine3 = splitedLine[1].split(" ", 4);
                if (splitedLine2.length != 3) {
                    in.close();
                    throw new RuntimeException("Invalid generated item 1 line: "
                            + line);
                }
                if (splitedLine3.length < 3) {
                    in.close();
                    throw new RuntimeException("Invalid generated item 2 line: "
                            + line);
                }
                int id =  Integer.parseInt(splitedLine2[0]);
                int type =  Integer.parseInt(splitedLine2[1]);
                int rotation =  Integer.parseInt(splitedLine2[2]);

                int x =  Integer.parseInt(splitedLine3[0]);
                int y =  Integer.parseInt(splitedLine3[1]);
                int z =  Integer.parseInt(splitedLine3[2]);
                int clip;
                if(splitedLine3.length > 3) {
                    clip = Integer.parseInt(splitedLine3[3]);
                }else{
                    clip=0;
                }
                WorldTile tile = new WorldTile(x,y,z);
                int region = tile.getRegionId();
                System.out.println(id+" "+x+" "+y+" "+z);
                db.executeUpdate("INSERT INTO objectspawns (ID, type, rotation, x, y, z, region) VALUES" +
                        " ("+id+","+type+","+rotation+","+x+","+y+","+z+","+region+")");
                // addItemSpawn(itemId, tile.getRegionId(), tile);
            }
            in.close();
        } catch (Throwable e) {
            Logger.handle(e);
        }
    }
}
