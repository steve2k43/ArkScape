package net.nocturne.utils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import net.nocturne.cache.loaders.NPCDefinitions;
import net.nocturne.game.npc.combat.NPCCombatDefinitions;
import net.nocturne.utils.sql.Database;

public final class NPCCombatDefinitionsL {

	public final static HashMap<Integer, NPCCombatDefinitions> npcCombatDefinitions = new HashMap<Integer, NPCCombatDefinitions>();
	public final static HashMap<String, Integer> existingDefinitions = new HashMap<String, Integer>();
	public final static NPCCombatDefinitions DEFAULT_DEFINITION = new NPCCombatDefinitions(
			1, -1, -1, -1, 33, -1, -1, 0.2, true, false, false, false, 2);
	private static final String PACKED_PATH = "data/npcs/packedCombatDefinitions.ncd";

	public static void init() {
		//if (new File(PACKED_PATH).exists())
			//loadPackedNPCCombatDefinitions();
//		else
			loadUnpackedNPCCombatDefinitions();
	}

	public static NPCCombatDefinitions getNPCCombatDefinitions(int npcId) {
		//System.out.println(npcId);
		NPCCombatDefinitions def = npcCombatDefinitions.get(npcId);
		if (def == null) {
			if (existingDefinitions.containsKey(NPCDefinitions
					.getNPCDefinitions(npcId).getName().toLowerCase()))
				return npcCombatDefinitions.get(existingDefinitions
						.get(NPCDefinitions.getNPCDefinitions(npcId).getName()
								.toLowerCase()));
			return DEFAULT_DEFINITION;
		}
		return def;
	}

	public static NPCCombatDefinitions getNPCCombatDefinitions1(int npcId) {
		NPCDefinitions poopoo = NPCDefinitions.getNPCDefinitions(npcId);
		int lvl = poopoo.combatLevel;
		NPCCombatDefinitions def = npcCombatDefinitions.get(npcId);
		NPCCombatDefinitions def1 = npcCombatDefinitions.get(npcId);

			if (existingDefinitions.containsKey(NPCDefinitions
					.getNPCDefinitions(npcId).getName().toLowerCase()))
				return npcCombatDefinitions.get(existingDefinitions
						.get(NPCDefinitions.getNPCDefinitions(npcId).getName()
								.toLowerCase()));

			Connection conn = null;
			Database db = new Database("arctik.co.uk", "ArkScape",
					"ArkScape", "arkscape");

			if (!db.init()) {
				System.err.println("[" +
						"DATABASE] No connection could be made to the database.");
			}
			String query = "SELECT * FROM combatdefs WHERE ID="+npcId;
			ResultSet rs = db.executeQuery(query);
			try {
				if (rs.next()) {
					int hitpoints = rs.getInt("Hitpoints");
					int attackAnim = rs.getInt("attackAnim");
					int defenceAnim = rs.getInt("defenceAnim");
					int deathAnim = rs.getInt("deathAnim");
					int respawnDelay = rs.getInt("respawnDelay");
					int attackGfx = rs.getInt("attackGfx");
					int attackProjectile = rs.getInt("attackProjectile");
					double xp = rs.getDouble("xp");
					boolean follow = rs.getBoolean("follow");
					boolean poisonImmune = rs.getBoolean("poisonImmune");
					boolean poisonous = rs.getBoolean("poisonous");
					boolean agressivenessType = rs.getBoolean("aggressive");
					int agroRatio = rs.getInt("aggroRatio");
					npcCombatDefinitions.put(npcId, new NPCCombatDefinitions(
							hitpoints, attackAnim, defenceAnim, deathAnim,
							respawnDelay, attackGfx, attackProjectile, xp, follow,
							poisonImmune, poisonous, agressivenessType, agroRatio));
					if (!existingDefinitions.containsKey(NPCDefinitions
							.getNPCDefinitions(npcId).getName().toLowerCase()))
						existingDefinitions.put(
								NPCDefinitions.getNPCDefinitions(npcId).getName()
										.toLowerCase(), npcId);
					//System.out.println("Combat added for NPC "+npcId);
					def = npcCombatDefinitions.get(npcId);
				} else {
					//System.out.println("Combat Def Doesnt Exist for NPC "+npcId);
					npcCombatDefinitions.put(npcId, DEFAULT_DEFINITION);

					def = npcCombatDefinitions.get(npcId);
				}
			}catch (SQLException e){

			}


		if(lvl==0){
			return DEFAULT_DEFINITION;
		}
		if(npcId==17185)
			System.out.println("OOO");
		System.out.println(npcId+ " lvl : " +lvl + " hp: "+npcCombatDefinitions.get(npcId).getHitpoints());
		return def;
	}
	public static String getCallerCallerClassName1() {
		StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
		String callerClassName = null;
		for (int i=1; i<stElements.length; i++) {
			StackTraceElement ste = stElements[i];
			if (!ste.getClassName().equals(KDebug.class.getName())&& ste.getClassName().indexOf("java.lang.Thread")!=0) {
				if (callerClassName==null) {
					callerClassName = ste.getClassName();
				} else if (!callerClassName.equals(ste.getClassName())) {
					return ste.getClassName();
				}
			}
		}
		return null;
	}

	private static void loadUnpackedNPCCombatDefinitions() {
		int count = 0;

		try {
			Database db = new Database("arctik.co.uk", "ArkScape",
					"ArkScape", "arkscape");

			if (!db.init()) {
				System.err.println("[" +
						"DATABASE] No connection could be made to the database.");
			}
			String query = "SELECT * FROM combatdefs";
			ResultSet rs = db.executeQuery(query);
			while (rs.next()) {
				int id = rs.getInt("ID");
				int hitpoints = rs.getInt("Hitpoints");
				int attackAnim = rs.getInt("attackAnim");
				int defenceAnim = rs.getInt("defenceAnim");
				int deathAnim = rs.getInt("deathAnim");
				int respawnDelay = rs.getInt("respawnDelay");
				int attackGfx = rs.getInt("attackGfx");
				int attackProjectile = rs.getInt("attackProjectile");
				double xp = rs.getDouble("xp");
				boolean follow = rs.getBoolean("follow");
				boolean poisonImmune = rs.getBoolean("poisonImmune");
				boolean poisonous = rs.getBoolean("poisonous");
				boolean agressivenessType = rs.getBoolean("aggressive");
				int agroRatio = rs.getInt("aggroRatio");
				npcCombatDefinitions.put(id, new NPCCombatDefinitions(
						hitpoints, attackAnim, defenceAnim, deathAnim,
						respawnDelay, attackGfx, attackProjectile, xp, follow,
						poisonImmune, poisonous, agressivenessType, agroRatio));
				if (!existingDefinitions.containsKey(NPCDefinitions
						.getNPCDefinitions(id).getName().toLowerCase()))
					existingDefinitions.put(
							NPCDefinitions.getNPCDefinitions(id).getName()
									.toLowerCase(), id);
				count++;
			}
			rs.close();
		} catch (Throwable e) {
			Logger.handle(e);
		}
		Logger.log("NPCCombatDefinitionsL", "Loaded "+count+ " Combat Definitions");
	}

	private static void loadUnpackedNPCCombatDefinitions1() {
		int count = 0;
		Logger.log("NPCCombatDefinitionsL", "Packing npc combat definitions...");
		try {
			DataOutputStream out = new DataOutputStream(new FileOutputStream(
					PACKED_PATH));
			BufferedReader in = new BufferedReader(new FileReader(
					"data/npcs/unpackedCombatDefinitionsList.txt"));
			while (true) {
				String line = in.readLine();
				count++;
				if (line == null)
					break;
				if (line.startsWith("//"))
					continue;
				String[] splitedLine = line.split(" - ", 2);
				if (splitedLine.length != 2) {
					in.close();
					out.close();
					throw new RuntimeException(
							"Invalid NPC Combat Definitions line: " + count
									+ ", " + line);
				}
				int npcId = Integer.parseInt(splitedLine[0]);
				String[] splitedLine2 = splitedLine[1].split(" ", 13);
				if (splitedLine2.length != 13) {
					in.close();
					out.close();
					throw new RuntimeException(
							"Invalid NPC Combat Definitions line: " + count
									+ ", " + line);
				}
				int hitpoints = Integer.parseInt(splitedLine2[0]);
				int attackAnim = Integer.parseInt(splitedLine2[1]);
				int defenceAnim = Integer.parseInt(splitedLine2[2]);
				int deathAnim = Integer.parseInt(splitedLine2[3]);
				int respawnDelay = Integer.parseInt(splitedLine2[4]);
				int attackGfx = Integer.parseInt(splitedLine2[5]);
				int attackProjectile = Integer.parseInt(splitedLine2[6]);
				double xp = Double.parseDouble(splitedLine2[7]);

				boolean follow = Boolean.parseBoolean(splitedLine2[8]);
				boolean poisonImmune = Boolean.parseBoolean(splitedLine2[9]);
				boolean poisonous = Boolean.parseBoolean(splitedLine2[10]);
				boolean agressivenessType = Boolean
						.parseBoolean(splitedLine2[11]);
				int agroRatio = Integer.parseInt(splitedLine2[12]);
				out.writeInt(npcId);
				out.writeInt(hitpoints);
				out.writeInt(attackAnim);
				out.writeInt(defenceAnim);
				out.writeInt(deathAnim);
				out.writeInt(respawnDelay);
				out.writeInt(attackGfx);
				out.writeInt(attackProjectile);
				out.writeDouble(xp);
				out.writeByte(follow ? 1 : 0);
				out.writeByte(poisonImmune ? 1 : 0);
				out.writeByte(poisonous ? 1 : 0);
				out.writeByte(agressivenessType ? 1 : 0);
				out.writeByte(agroRatio);
				npcCombatDefinitions.put(npcId, new NPCCombatDefinitions(
						hitpoints, attackAnim, defenceAnim, deathAnim,
						respawnDelay, attackGfx, attackProjectile, xp, follow,
						poisonImmune, poisonous, agressivenessType, agroRatio));
				if (!existingDefinitions.containsKey(NPCDefinitions
						.getNPCDefinitions(npcId).getName().toLowerCase()))
					existingDefinitions.put(
							NPCDefinitions.getNPCDefinitions(npcId).getName()
									.toLowerCase(), npcId);
			}
			in.close();
			out.close();
		} catch (Throwable e) {
			Logger.handle(e);
		}
	}

	private static void loadPackedNPCCombatDefinitions() {
		try {
			RandomAccessFile in = new RandomAccessFile(PACKED_PATH, "r");
			FileChannel channel = in.getChannel();
			ByteBuffer buffer = channel.map(MapMode.READ_ONLY, 0,
					channel.size());
			while (buffer.hasRemaining()) {
				int npcId = buffer.getInt();
				int hitpoints = buffer.getInt();
				int attackAnim = buffer.getInt();
				int defenceAnim = buffer.getInt();
				int deathAnim = buffer.getInt();
				int respawnDelay = buffer.getInt();
				int attackGfx = buffer.getInt();
				int attackProjectile = buffer.getInt();
				double xp = buffer.getDouble();
				boolean follow = buffer.get() == 1;
				boolean poisonImmune = buffer.get() == 1;
				boolean poisonous = buffer.get() == 1;
				boolean agressivenessType = buffer.get() == 1;
				int agroRatio = buffer.get() & 0xff;
				npcCombatDefinitions.put(npcId, new NPCCombatDefinitions(
						hitpoints, attackAnim, defenceAnim, deathAnim,
						respawnDelay, attackGfx, attackProjectile, xp, follow,
						poisonImmune, poisonous, agressivenessType, agroRatio));
				if (!existingDefinitions.containsKey(NPCDefinitions
						.getNPCDefinitions(npcId).getName().toLowerCase()))
					existingDefinitions.put(
							NPCDefinitions.getNPCDefinitions(npcId).getName()
									.toLowerCase(), npcId);
			}
			channel.close();
			in.close();
		} catch (Throwable e) {
			Logger.handle(e);
		}
	}

	private NPCCombatDefinitionsL() {

	}
}