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

public class ItemExamines {

	private final static HashMap<Integer, String> itemExamines = new HashMap<Integer, String>();
	private final static String PACKED_PATH = "data/items/packedExamines.e";
	private final static String UNPACKED_PATH = "data/items/unpackedExamines.txt";

	public static final void init() {
		//if (new File(PACKED_PATH).exists())
			//loadPackedItemExamines();
		//else
			//loadUnpackedItemExamines();
		loadSQLItemExamines();
	}

	public static final String getExamine(Item item){

		if (item.getAmount() >= 100000)
			return Utils.getFormattedNumber(item.getAmount()) + " x "
					+ item.getDefinitions().getName() + ".";
		if (item.getDefinitions().isNoted())
			return "Swamp this note at any bank for the equivalent item.";

		String examine = itemExamines.get(item.getId());
		if (examine != null)
			return examine;
		Bugsystem.addItemExamineBug(item.getId());
		return "It's an " + item.getDefinitions().getName() + ".";

	}

	private static void loadPackedItemExamines() {
		try {
			RandomAccessFile in = new RandomAccessFile(PACKED_PATH, "r");
			FileChannel channel = in.getChannel();
			ByteBuffer buffer = channel.map(MapMode.READ_ONLY, 0,
					channel.size());
			while (buffer.hasRemaining())
				itemExamines.put(buffer.getShort() & 0xffff,
						readAlexString(buffer));
			channel.close();
			in.close();
		} catch (Throwable e) {
			Logger.handle(e);
		}
	}
	private static void loadSQLItemExamines() {
		Database db = new Database(Settings.MYSQL_HOST, Settings.MYSQL_USER,
				Settings.MYSQL_PASS, "arkscape");

		if (!db.init()) {
			System.err.println("[DATABASE] No connection could be made to the database.");
		}
		int count=0;
		try {
			ResultSet rs = db.executeQuery("SELECT * FROM itemexamines");
			while(rs.next()) {
				int id = rs.getInt("ID");
				String text = rs.getString("text");
				itemExamines.put(id,text);
				count++;
			}
		} catch (SQLException e){

		}
		Logger.log("ItemExamines", "Loaded "+count+" Item Examines.");
	}

	private static void loadUnpackedItemExamines() {
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
				itemExamines.put(itemId, splitedLine[1]);
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
