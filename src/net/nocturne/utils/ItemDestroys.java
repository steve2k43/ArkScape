package net.nocturne.utils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import net.nocturne.Settings;
import net.nocturne.game.item.Item;
import net.nocturne.utils.sql.Database;

public class ItemDestroys {

	private final static HashMap<Integer, String> itemDestroys = new HashMap<Integer, String>();
	private final static String PACKED_PATH = "data/items/packedDestroys.d";
	private final static String UNPACKED_PATH = "data/items/unpackedDestroys.txt";

	public static final void init() {
		loadSQLItemDestroys();
	}

	public static final String getDestroy(Item item) {
		String examine = itemDestroys.get(item.getId());
		if (examine != null)
			return examine;
		return "You can reclaim this item from the place you found it.";
	}

	private static void loadPackedItemDestroys() {
		try {
			RandomAccessFile in = new RandomAccessFile(PACKED_PATH, "r");
			FileChannel channel = in.getChannel();
			ByteBuffer buffer = channel.map(MapMode.READ_ONLY, 0,
					channel.size());
			while (buffer.hasRemaining())
				itemDestroys.put(buffer.getShort() & 0xffff,
						readAlexString(buffer));
			channel.close();
			in.close();
		} catch (Throwable e) {
			Logger.handle(e);
		}
	}

	private static void loadSQLItemDestroys() {
		Database db = new Database(Settings.MYSQL_HOST, Settings.MYSQL_USER,
				Settings.MYSQL_PASS, "arkscape");

		if (!db.init()) {
			System.err.println("[DATABASE] No connection could be made to the database.");
		}
		int count = 0;
		try {
			Connection conn = db.connect();
			Statement stmt = conn.createStatement();
			String query = "SELECT * FROM itemdestroys";
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()){
				int itemId = rs.getInt("ID");
				String weight = rs.getString("text");
				itemDestroys.put(itemId, weight);
				count++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		Logger.log("ItemDestroys", "Loaded "+count+" Item Destroys.");
	}
	private static void loadUnpackedItemDestroys1() {
		Logger.log("ItemExamines", "Packing item examines...");
		try {
			BufferedReader in = new BufferedReader(
					new FileReader(UNPACKED_PATH));
			DataOutputStream out = new DataOutputStream(new FileOutputStream(
					PACKED_PATH));
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
				int itemId = Integer.valueOf(splitedLine[0]);
				if (splitedLine[1].length() > 255)
					continue;
				out.writeShort(itemId);
				writeAlexString(out, splitedLine[1]);
				itemDestroys.put(itemId, splitedLine[1]);
			}

			in.close();
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	public static String readAlexString(ByteBuffer buffer) {
		int count = buffer.get() & 0xff;
		byte[] bytes = new byte[count];
		buffer.get(bytes, 0, count);
		return new String(bytes);
	}

	public static void writeAlexString(DataOutputStream out, String string)
			throws IOException {
		byte[] bytes = string.getBytes();
		out.writeByte(bytes.length);
		out.write(bytes);
	}
}
