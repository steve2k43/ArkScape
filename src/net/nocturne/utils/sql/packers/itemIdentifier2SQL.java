package net.nocturne.utils.sql.packers;

import net.nocturne.cache.Cache;
import net.nocturne.cache.loaders.NPCDefinitions;
import net.nocturne.game.item.Item;
import net.nocturne.game.player.content.Shop;
import net.nocturne.utils.Logger;
import net.nocturne.utils.Utils;
import net.nocturne.utils.sql.Database;

import java.io.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class itemIdentifier2SQL {
	private static final String PATH = "data/items/itemidentifiers.txt";

	public static final void main(String[] args) throws IOException {
		//Cache.init();
		//Logger.log("ShopsHandler", "Packing shops...");
		//File file = new File(PATH);
		String[] splitedLine;
		try {
			@SuppressWarnings("resource")
			BufferedReader in = new BufferedReader(
					new FileReader(PATH));
			/*
			 * BufferedWriter writer = new BufferedWriter(new FileWriter(PATH,
			 * true));
			 */
			Database db = new Database("arctik.co.uk", "ArkScape",
					"ArkScape", "arkscape");

			if (!db.init()) {
				System.err.println("[DATABASE] No connection could be made to the database.");
				return;
			}
			String query1 = "INSERT INTO itemidentifiers (ID,Name) VALUES (?, ?)";
			PreparedStatement prest = db.prepare(query1);
			while (true) {
				String line = in.readLine();
				if (line == null)
					break;
				if (line.startsWith("//"))
					continue;
				line = line.replace("\t", "");
				splitedLine = line.split(" = ", 2);
				if (splitedLine.length != 2)
					throw new RuntimeException("Invalid list for item line: "
							+ line);

				//String[] splitedInform = splitedLine[0].split(" ", 4);
				System.out.println(splitedLine[0] + " " + splitedLine[1]);




				if (0 == 0) {

					prest.setInt(1, Integer.parseInt(splitedLine[1]));
					prest.setString(2, splitedLine[0]);
					prest.addBatch();

					prest.clearParameters();
				}

				//prest.close();


			}
			int[] results = prest.executeBatch();
		}catch(Throwable e){
			Logger.handle(e);
		}
	}
}