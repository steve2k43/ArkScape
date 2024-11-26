package net.nocturne.utils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import net.nocturne.game.World;
import net.nocturne.game.WorldTile;
import net.nocturne.game.item.Item;
import net.nocturne.utils.sql.Database;

public final class ItemSpawns {

	public static final void init() {
		loadSQLItemSpawns();
	}

	private static final void loadSQLItemSpawns() {
		Database db = new Database("arctik.co.uk", "ArkScape",
				"ArkScape", "arkscape");

		if (!db.init()) {
			System.err.println("[DATABASE] No connection could be made to the database.");
		}
		int count=0;
		try {
			ResultSet rs = db.executeQuery("SELECT * FROM itemspawns");
			while (rs.next()) {
				int id = rs.getInt("ID");
				int x = rs.getInt("x");
				int y = rs.getInt("y");
				int z = rs.getInt("z");
				WorldTile tile = new WorldTile(x,y,z);
				int region = tile.getRegionId();
				if(rs.getInt("region")==0){
					db.executeUpdate("UPDATE itemspawns SET region="+region+" WHERE ID="+id);
				}

				//addItemSpawn(id, tile.getRegionId(), tile);
				count++;
			}
		} catch (Throwable e) {
			Logger.handle(e);
		}
		Logger.log("ItemSpawns", "Loaded "+count+" Item Spawns.");
	}

	private static final void packItemSpawns() {
		Logger.log("ItemSpawns", "Packing item spawns...");
		if (!new File("data/items/packedSpawns").mkdir())
			throw new RuntimeException(
					"Couldn't create packedSpawns directory.");
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
				WorldTile tile = new WorldTile(
						Integer.parseInt(splitedLine2[0]),
						Integer.parseInt(splitedLine2[1]),
						Integer.parseInt(splitedLine2[2]));
				addItemSpawn(itemId, tile.getRegionId(), tile);
			}
			in.close();
		} catch (Throwable e) {
			Logger.handle(e);
		}
	}

	@SuppressWarnings("deprecation")
	public static final void loadItemSpawns(int regionId) {
		Database db = new Database("arctik.co.uk", "ArkScape",
				"ArkScape", "arkscape");

		if (!db.init()) {
			System.err.println("[DATABASE] No connection could be made to the database.");
		}
		int count=0;
		try {
			ResultSet rs = db.executeQuery("SELECT * FROM itemspawns WHERE region="+regionId);
			while (rs.next()) {
				int id = rs.getInt("ID");
				int x = rs.getInt("x");
				int y = rs.getInt("y");
				int z = rs.getInt("z");
				WorldTile tile = new WorldTile(x,y,z);
				World.addGroundItemForever(new Item(id, 1), new WorldTile(
						x, y, z));
				count++;
			}
		} catch (Throwable e) {
			Logger.handle(e);
		}
	}

	private static final void addItemSpawn(int itemId, int regionId,
			WorldTile tile) {
		Database db = new Database("arctik.co.uk", "ArkScape",
				"ArkScape", "arkscape");

		if (!db.init()) {
			System.err.println("[DATABASE] No connection could be made to the database.");
		}
		int x = tile.getX();
		int y = tile.getY();
		int z = tile.getPlane();
		ResultSet rs = db.executeQuery("SELECT * FROM itemspawns WHERE ID="+itemId);
		try {
			if (rs.next()) {
				int region=rs.getInt("region");
				if(region==0) {
					String query = ("UPDATE itemspawns SET region=" + regionId + " WHERE ID = " + itemId);
					db.executeUpdate(query);
				}
			}
		}catch (SQLException e){

		}
	}

	private ItemSpawns() {
	}
}
