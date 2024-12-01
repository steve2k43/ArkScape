package net.nocturne.utils;

import net.nocturne.Settings;
import net.nocturne.utils.sql.Database;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public final class MapArchiveKeys {

	private final static HashMap<Integer, int[]> keys = new HashMap<Integer, int[]>();
	private final static String PACKED_PATH = "data/map/archiveKeys/packed.mcx";

	public static final int[] getMapKeys(int regionId) {
		return keys.get(regionId);
	}

	public static void init() {
		//loadSQLKeys();
		loadPackedKeys();
	}
	private static void loadSQLKeys() {
		Database db = new Database(Settings.MYSQL_HOST, Settings.MYSQL_USER,
				Settings.MYSQL_PASS, "arkscape");

		if (!db.init()) {
			System.err.println("[DATABASE] No connection could be made to the database.");
		}
		int count=0;
		try {
			ResultSet rs = db.executeQuery("SELECT * FROM xteas");
			while(rs.next()) {
				int id = rs.getInt("ID");
				int a = rs.getInt("a");
				int b = rs.getInt("b");
				int c = rs.getInt("c");
				int d = rs.getInt("d");
				int[] xteas = new int[4];
				xteas[0]=a;xteas[1]=b;xteas[2]=c;xteas[3]=d;
				keys.put(id, xteas);
				count++;
			}
		} catch (SQLException e){

		}
		Logger.log("ObjectExamines", "Loaded "+count+" Map Keys.");
	}
	private static final void loadPackedKeys() {
		try {
			RandomAccessFile in = new RandomAccessFile(PACKED_PATH, "r");
			FileChannel channel = in.getChannel();
			ByteBuffer buffer = channel.map(MapMode.READ_ONLY, 0,
					channel.size());
			while (buffer.hasRemaining()) {
				int regionId = buffer.getShort() & 0xffff;
				int[] xteas = new int[4];
				for (int index = 0; index < 4; index++)
					xteas[index] = buffer.getInt();
				keys.put(regionId, xteas);
			}
			channel.close();
			in.close();
		} catch (Throwable e) {
			Logger.handle(e);
		}
	}

	public static final void loadUnpackedKeys() {
		Logger.log("MapArchiveKeys", "Packing map containers xteas...");
		try {
			DataOutputStream out = new DataOutputStream(new FileOutputStream(
					PACKED_PATH));
			File unpacked = new File("data/map/archiveKeys/unpacked/");
			File[] xteasFiles = unpacked.listFiles();
			for (File region : xteasFiles) {
				String name = region.getName();
				if (!name.contains(".txt")) {
					region.delete();
					continue;
				}
				int regionId = Short.parseShort(name.replace(".txt", ""));
				if (regionId <= 0) {
					region.delete();
					continue;
				}
				BufferedReader in = new BufferedReader(new FileReader(region));
				out.writeShort(regionId);
				final int[] xteas = new int[4];
				for (int index = 0; index < 4; index++) {
					xteas[index] = Integer.parseInt(in.readLine());
					out.writeInt(xteas[index]);
				}
				keys.put(regionId, xteas);
				in.close();
			}
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private MapArchiveKeys() {

	}

}
