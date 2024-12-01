package net.nocturne.utils;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.sql.ResultSet;

import net.nocturne.Settings;
import net.nocturne.game.Region;
import net.nocturne.game.World;
import net.nocturne.game.WorldObject;
import net.nocturne.game.WorldTile;
import net.nocturne.game.item.Item;
import net.nocturne.utils.sql.Database;

public final class ObjectSpawns {

	public static final void init() {
		loadSQLObjectSpawns();

	}

	private static final void loadSQLObjectSpawns() {
		Database db = new Database(Settings.MYSQL_HOST, Settings.MYSQL_USER,
				Settings.MYSQL_PASS, "arkscape");

		if (!db.init()) {
			System.err.println("[DATABASE] No connection could be made to the database.");
		}
		int count=0;
		try {
			ResultSet rs = db.executeQuery("SELECT * FROM objectspawns");
			while (rs.next()) {
				int id = rs.getInt("ID");
				int x = rs.getInt("x");
				int y = rs.getInt("y");
				int z = rs.getInt("z");
				WorldTile tile = new WorldTile(x,y,z);
				int region = tile.getRegionId();
				if(rs.getInt("region")==0){
					db.executeUpdate("UPDATE objectspawns SET region="+region+" WHERE ID="+id);
				}

				//addObjectSpawn(objectId, type, rotation,tile.getRegionId(), tile);
				count++;
			}
		} catch (Throwable e) {
			Logger.handle(e);
		}
		Logger.log("ObjectSpawns", "Loaded "+count+" Object Spawns.");
	}

	private static final void packObjectSpawns() {
		Logger.log("ObjectSpawns", "Packing object spawns...");
		if (!(new File("data/map/packedSpawns")).mkdir()) {
			throw new RuntimeException(
					"Couldn\'t create packedSpawns directory.");
		} else {
			try {
				BufferedReader e = new BufferedReader(new FileReader(
						"data/map/unpackedSpawnsList.txt"));

				while (true) {
					String line = e.readLine();
					if (line == null) {
						e.close();
						break;
					}

					if (!line.startsWith("//")) {
						String[] splitedLine = line.split(" - ");
						if (splitedLine.length != 2) {
							e.close();
							throw new RuntimeException(
									"Invalid Object Spawn line: " + line);
						}

						String[] splitedLine2 = splitedLine[0].split(" ");
						String[] splitedLine3 = splitedLine[1].split(" ");
						if (splitedLine2.length != 3
								|| splitedLine3.length != 3) {
							e.close();
							throw new RuntimeException(
									"Invalid Object Spawn line: " + line);
						}

						int objectId = Integer.parseInt(splitedLine2[0]);
						int type = Integer.parseInt(splitedLine2[1]);
						int rotation = Integer.parseInt(splitedLine2[2]);
						WorldTile tile = new WorldTile(
								Integer.parseInt(splitedLine3[0]),
								Integer.parseInt(splitedLine3[1]),
								Integer.parseInt(splitedLine3[2]));
						addObjectSpawn(objectId, type, rotation,
								tile.getRegionId(), tile);
					}
				}
			} catch (Throwable var9) {
				Logger.handle(var9);
			}

		}
	}

	public static final void loadObjectSpawns(int regionId) {
		Database db = new Database(Settings.MYSQL_HOST, Settings.MYSQL_USER,
				Settings.MYSQL_PASS, "arkscape");

		if (!db.init()) {
			System.err.println("[DATABASE] No connection could be made to the database.");
		}
		int count=0;
		Region region = World.getRegion(regionId);
		try {
			ResultSet rs = db.executeQuery("SELECT * FROM objectspawns WHERE region="+regionId);
			while (rs.next()) {
				int objectId = rs.getInt("ID");
				int type = rs.getInt("type");
				int rotation = rs.getInt("rotation");
				int plane = rs.getInt("z");
				int x = rs.getInt("x");
				int y = rs.getInt("y");
				WorldObject object = new WorldObject(objectId, type,
						rotation, x, y, plane);
				region.spawnObject(object, plane, object.getXInRegion(),
						object.getYInRegion(), false);
				count++;
			}
		} catch (Throwable e) {
			Logger.handle(e);
		}
	}

	private static final void addObjectSpawn(int objectId, int type,
			int rotation, int regionId, WorldTile tile) {
		try {
			DataOutputStream e = new DataOutputStream(new FileOutputStream(
					"data/map/packedSpawns/" + regionId + ".os", true));
			e.writeInt(objectId);
			e.writeByte(type);
			e.writeByte(rotation);
			e.writeByte(tile.getPlane());
			e.writeShort(tile.getX());
			e.writeShort(tile.getY());
			e.flush();
			e.close();
		} catch (FileNotFoundException var6) {
			var6.printStackTrace();
		} catch (IOException var7) {
			var7.printStackTrace();
		}

	}

}
