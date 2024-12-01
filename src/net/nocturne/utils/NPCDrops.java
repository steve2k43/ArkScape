package net.nocturne.utils;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.sql.*;

import net.nocturne.game.npc.Drop;
import net.nocturne.game.npc.Drops;
import net.nocturne.utils.sql.packers.npcdropsSQL;

public class NPCDrops {

	private final static String PACKED_PATH = "data/npcs/packedDrops.d";
	private static HashMap<Integer, Drops> npcDrops = new HashMap<Integer, Drops>();
	private static HashMap<Integer, Drops> npcDrops1 = new HashMap<Integer, Drops>();

	public static final void init() {
		//loadPackedNPCDrops();
	}

	public static Drops getDrops(int npcId) {
		//npcDrops = npcdropsSQL.getmydrop(npcId);

		return npcDrops.get(npcId);
	}

	public static void addDrops(int npcId, Drops drops) {
		npcDrops.put(npcId, drops);
	}

	public class GetAllTables {
		public void main(String[] args) throws SQLException {
			Connection conn = null;
			try {
				try {
					Class.forName("com.mysql.jdbc.Driver");
				} catch (Exception e) {
					System.out.println(e);
				}
				conn = (Connection) DriverManager.getConnection("jdbc:mysql://mysql.arctik.co.uk/npcdrops&allowPublicKeyRetrieval=true", "ArctikServer", "ArctikServer0!0");
				System.out.println("Connection is created succcessfully:");
			} catch (Exception e) {
				System.out.println(e);
			}
			ResultSet rs = null;
			DatabaseMetaData meta = (DatabaseMetaData) conn.getMetaData();
			rs = meta.getTables(null, null, null, new String[] {
					"TABLE"
			});
			int count = 0;
			System.out.println("All table names are in test database:");
			while (rs.next()) {
				String tblName = rs.getString("TABLE_NAME");
				System.out.println(tblName);
				count++;
			}
			System.out.println(count + " Rows in set ");
			conn.close();
		}

	}

	private static void loadPackedNPCDrops() {
		try {
			RandomAccessFile in = new RandomAccessFile(PACKED_PATH, "r");
			FileChannel channel = in.getChannel();
			ByteBuffer buffer = channel.map(MapMode.READ_ONLY, 0,
					channel.size());

			while (buffer.hasRemaining()) {
				int npcId = buffer.getShort() & 0xffff;
				boolean acessRareTable = buffer.get() == 1;
				Drops drops = new Drops(acessRareTable);
				@SuppressWarnings("unchecked")
				List<Drop>[] dList = new ArrayList[Drops.VERY_RARE + 1];
				int size = (buffer.get() & 0xff);
				for (int i = 0; i < size; i++) {
					int itemId = buffer.getShort() & 0xffff;
					int min = buffer.getShort() & 0xffff;
					int max = buffer.getShort() & 0xffff;
					int rarity = buffer.get() & 0xff;
					if (dList[rarity] == null)
						dList[rarity] = new ArrayList<Drop>();
					Drop drop = new Drop(itemId, min, max);
					dList[rarity].add(drop);
				}


				drops.addDrops(dList);
				npcDrops.put(npcId, drops);
			}
			channel.close();
			in.close();
		} catch (Throwable e) {
			Logger.handle(e);
		}
	}

}