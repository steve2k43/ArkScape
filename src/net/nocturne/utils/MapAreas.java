package net.nocturne.utils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import net.nocturne.game.WorldTile;
import net.nocturne.utils.sql.Database;

public final class MapAreas {

	private final static HashMap<Integer, int[]> mapAreas = new HashMap<Integer, int[]>();
	private final static String PACKED_PATH = "data/map/packedMapAreas.ma";

	public static final void init() {
		loadSQLMapAreas();
	}

	private static void loadSQLMapAreas() {
		Database db = new Database("arctik.co.uk", "ArkScape",
				"ArkScape", "arkscape");

		if (!db.init()) {
			System.err.println("[DATABASE] No connection could be made to the database.");
		}
		int count=0;
		try {
			ResultSet rs = db.executeQuery("SELECT * FROM mapareas");
			while(rs.next()) {
				String areaName = rs.getString("areaname");
				int areaNameHash = Utils.getNameHash(areaName);
				int[] coordsList = new int[5];
				coordsList[0] = rs.getInt("plane");
				coordsList[1] = rs.getInt("minx");
				coordsList[2] = rs.getInt("maxx");
				coordsList[3] = rs.getInt("miny");
				coordsList[4] = rs.getInt("maxy");
				mapAreas.put(areaNameHash, coordsList);
				count++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		Logger.log("MapAreas", "Loaded "+count+" Map Areas.");
	}

	public static final boolean isAtArea(String areaName, WorldTile tile) {
		return isAtArea(Utils.getNameHash(areaName), tile);
	}

	public static final boolean isAtArea(int areaNameHash, WorldTile tile) {
		int[] coordsList = mapAreas.get(areaNameHash);
		if (coordsList == null)
			return false;
		int index = 0;
		while (index < coordsList.length) {
			if (tile.getPlane() == coordsList[index]
					&& tile.getX() >= coordsList[index + 1]
					&& tile.getX() <= coordsList[index + 2]
					&& tile.getY() >= coordsList[index + 3]
					&& tile.getY() <= coordsList[index + 4])
				return true;
			index += 5;
		}
		return false;
	}

	public static final void removeArea(int areaNameHash) {
		mapAreas.remove(areaNameHash);
	}

	public static final void addArea(int areaNameHash, int[] coordsList) {
		mapAreas.put(areaNameHash, coordsList);
	}

	private static void loadUnpackedMapAreas() {
		Logger.log("MapAreas", "Packing map areas...");
		try {
			BufferedReader in = new BufferedReader(new FileReader(
					"data/map/unpackedMapAreas.txt"));
			DataOutputStream out = new DataOutputStream(new FileOutputStream(
					PACKED_PATH));
			while (true) {
				String line = in.readLine();
				if (line == null)
					break;
				if (line.startsWith("//"))
					continue;
				String[] splitedLine = line.split(" - ", 2);
				String areaName = splitedLine[0];
				String[] splitedCoords = splitedLine[1].split(" ");
				int[] coordsList = new int[splitedCoords.length];
				if (coordsList.length < 5) {
					in.close();
					out.close();
					throw new RuntimeException("Invalid list for area line: "
							+ line);
				}
				for (int i = 0; i < coordsList.length; i++)
					coordsList[i] = Integer.parseInt(splitedCoords[i]);
				int areaNameHash = Utils.getNameHash(areaName);
				if (mapAreas.containsKey(areaNameHash))
					continue;
				out.writeInt(areaNameHash);
				out.writeByte(coordsList.length);
				for (int i = 0; i < coordsList.length; i++)
					out.writeShort(coordsList[i]);
				mapAreas.put(areaNameHash, coordsList);
			}
			in.close();
			out.flush();
			out.close();
		} catch (Throwable e) {
			Logger.handle(e);
		}
	}

	private static void loadPackedMapAreas() {
		try {
			RandomAccessFile in = new RandomAccessFile(PACKED_PATH, "r");
			FileChannel channel = in.getChannel();
			ByteBuffer buffer = channel.map(MapMode.READ_ONLY, 0,
					channel.size());
			while (buffer.hasRemaining()) {
				int areaNameHash = buffer.getInt();
				int[] coordsList = new int[buffer.get() & 0xff];
				for (int i = 0; i < coordsList.length; i++)
					coordsList[i] = buffer.getShort() & 0xffff;
				mapAreas.put(areaNameHash, coordsList);
			}
			channel.close();
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private MapAreas() {

	}
}
