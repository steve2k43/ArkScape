package net.nocturne.utils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import net.nocturne.game.WorldObject;
import net.nocturne.utils.sql.Database;

public class ObjectExamines {

	private final static HashMap<Integer, String> objectExamines = new HashMap<Integer, String>();
	private final static String PACKED_PATH = "data/map/packedExamines.e";
	private final static String UNPACKED_PATH = "data/map/unpackedExamines.txt";

	public static final void init() {
		loadSQLObjectExamines();
	}
	private static void loadSQLObjectExamines() {
		Database db = new Database("arctik.co.uk", "ArkScape",
				"ArkScape", "arkscape");

		if (!db.init()) {
			System.err.println("[DATABASE] No connection could be made to the database.");
		}
		int count=0;
		try {
			ResultSet rs = db.executeQuery("SELECT * FROM objectexamines");
			while(rs.next()) {
				int id = rs.getInt("ID");
				String text = rs.getString("text");
				objectExamines.put(id,text);
				count++;
			}
		} catch (SQLException e){

		}
		Logger.log("ObjectExamines", "Loaded "+count+" Object Examines.");
	}

	public static final String getExamine(WorldObject object) {
		String examine = objectExamines.get(object.getId());
		if (examine != null)
			return examine;
		return "It's a " + object.getDefinitions().name + ".";
	}

	private static void loadPackeddObjectExamines() {
		try {
			RandomAccessFile in = new RandomAccessFile(PACKED_PATH, "r");
			FileChannel channel = in.getChannel();
			ByteBuffer buffer = channel.map(MapMode.READ_ONLY, 0,
					channel.size());
			while (buffer.hasRemaining())
				objectExamines.put(buffer.getInt(), readAlexString(buffer));
			channel.close();
			in.close();
		} catch (Throwable e) {
			Logger.handle(e);
		}
	}

	private static void loadUnpackedObjectExamines() {
		Logger.log("NPCExamines", "Packing object examines...");
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
							"Invalid list for object examine line: " + line);
				}
				int objectId = Integer.valueOf(splitedLine[0]);
				if (splitedLine[1].length() > 255)
					continue;
				out.writeInt(objectId);
				writeAlexString(out, splitedLine[1]);
				objectExamines.put(objectId, splitedLine[1]);
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
