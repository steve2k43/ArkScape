package net.nocturne.tools;

import java.io.*;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import net.nocturne.Settings;
import net.nocturne.cache.Cache;
import net.nocturne.cache.loaders.NPCDefinitions;
import net.nocturne.utils.Utils;
import net.nocturne.utils.sql.Database;

import javax.swing.plaf.nimbus.State;

public class ItemDropsPacker {

	static class NPCDrop {

		private int itemId, minAmount, maxAmount, rarity;
		private String dropchance;

		NPCDrop(int itemId, int minAmount, int maxAmount, int rarity, String dropchance) {
			if (itemId == 617)
				itemId = 995;
			if (itemId == 2513)
				itemId = 3140;
			this.itemId = itemId;
			this.minAmount = minAmount;
			this.maxAmount = maxAmount;
			this.rarity = rarity;
			this.dropchance = dropchance;
		}

		int getMinAmount() {
			return minAmount;
		}

		int getMaxAmount() {
			return maxAmount;
		}

		public int getItemId() {
			return itemId;
		}

		public int getRarity() {
			return rarity;
		}

		public String getDropchance() {
			return dropchance;
		}
	}

	public static void main(String[] args) {
		for (int i = 0; i < args.length; i++) {
			System.out.println(args[i]);
		}
		Database db = new Database(Settings.MYSQL_HOST, Settings.MYSQL_USER,
				Settings.MYSQL_PASS, "npcdrops");

		if (!db.init()) {
			System.err.println("[DATABASE] No connection could be made to the database.");
		}
		Connection conn = db.connect();
		File directory=new File("data/npcs/drops");
		int fileCount=directory.list().length;
		System.out.println("File Count:"+fileCount);
		int marker = -1;
		int poop = 0;
		try {
			Cache.init();
			//DataOutputStream out = new DataOutputStream(new FileOutputStream(
			//		"data/npcs/packedDrops.d"));
			for (int npcId = 0; npcId < Utils.getNPCDefinitionsSize(); npcId++) {
				marker = npcId;
				File file = new File("data/npcs/drops/" + npcId + ".txt");


				if (file.exists()) {
					int rc=0;
					poop++;
					System.out.println("Progress: "+poop+"/"+fileCount+ " NPCid: "+npcId);
					BufferedReader reader = new BufferedReader(new FileReader(
							file));
					boolean rareDropTable;
					rareDropTable = reader.readLine().contains("true");
					List<NPCDrop> drops = new ArrayList<>();
					while (true) {
						String line = reader.readLine();
						if (line == null)
							break;
						String[] l = line.split(", ");
						int id = Integer.parseInt(l[0]);
						if (id == -1)
							continue;
						if (NPCDefinitions.getNPCDefinitions(npcId).name
								.equalsIgnoreCase("skeleton") && id == 532)
							continue;
						if (NPCDefinitions.getNPCDefinitions(npcId).name
								.equalsIgnoreCase("terror dog") && id == 526)
							continue;
						if (id >= 30318 && id <= 30321)
							continue;
						Double dc =0.0;
						//System.out.println("l3 = "+l[3]);
						String dcvalue = l[3];
						String splitted[] = dcvalue.split("/");
						double a = Double.parseDouble(splitted[0]);
						double b = Double.parseDouble(splitted[1]);
						dc = a/b;
						if(dc*100 == 100){
							rc=1;
						}
						if(dc*100 <100 && 1/dc*100 >= 6.25){
							rc=2;
						}
						if(dc*100 <6.25 && 1/dc*100 >= 3){
							rc=3;
						}
						if(dc*100 <3 && 1/dc*100 >= 1.25){
							rc=4;
						}
						if(dc*100 < 1.25){
							rc=5;
						}
						//System.out.println("DC: "+rc);
						//Double rarity = Double.parseDouble(l[3]);
						//System.out.println("adding drop -  ID: "+id+" min: "+l[1]+" max: "+l[2]+" rarity: "+rc+ " Dropchance: "+dcvalue);
						drops.add(new NPCDrop(id, Integer.parseInt(l[1]),
								Integer.parseInt(l[2]), rc, dcvalue));
					}
					reader.close();
					try{
						Statement stmt = conn.createStatement();
						stmt.executeUpdate("DROP TABLE IF EXISTS _"+npcId);
						stmt.executeUpdate("CREATE TABLE _"+npcId+" (ID INTEGER, min INTEGER, max INTEGER,rarity INTEGER, dropchance char(30))");
						if(rareDropTable){
							stmt.executeUpdate("INSERT INTO _"+npcId+" (ID, min, max, rarity, dropchance) VALUES (0,0,0,1,'0')");
						}

					} catch(SQLException e){
						System.err.println(e);
					}
					//out.writeShort(npcId);
					//out.writeBoolean(rareDropTable);
					//out.writeByte(drops.size());
					for (NPCDrop drop : drops) {
						try{
							String sql = "INSERT INTO _"+npcId+" (ID, min, max, rarity, dropchance) VALUES ("+drop.getItemId()+","+drop.getMinAmount()+","
							+drop.getMaxAmount()+","+(drop.getRarity()-1)+",'"+drop.getDropchance()+"')";
							//System.out.println(sql);
						Statement stmt = conn.createStatement();
						stmt.executeUpdate(sql);
						} catch (SQLException e){
							System.err.println(e);
						}
						//out.writeShort(drop.getItemId());
						//out.writeShort(drop.getMinAmount());
						//out.writeShort(drop.getMaxAmount());
						//out.writeByte(drop.getRarity());
					}
				}
			}
			//out.flush();
			//out.close();
		} catch (NumberFormatException | IOException e) {
			System.out.println("ERROR AT NPC: " + marker);
			e.printStackTrace();
		}
	}
}