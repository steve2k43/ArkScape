package net.nocturne.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import net.nocturne.Settings;
import net.nocturne.game.item.Item;
import net.nocturne.game.player.Player;
import net.nocturne.game.player.content.Shop;
import net.nocturne.utils.sql.Database;

public class ShopsHandler {

	/**
	 * @author: Matrix team
	 * @author: miles M
	 */

	private static final HashMap<Integer, Shop> handledShops = new HashMap<Integer, Shop>();

	private static final String PACKED_PATH = "data/items/packedShops.s";
	private static final String UNPACKED_PATH = "data/items/unpackedShops.txt";

	private static final int SHOP_QUANTITY_RATE = 10;

	private static final String PATH = "data/items/grandExchangeBuyables.txt";

	public static void init() {
		loadSQLshops();
	}

	public static void forceReload() {
		handledShops.clear();
		loadSQLshops();
	}

	private static void loadUnpackedShops() {
		Logger.log("ShopsHandler", "Packing shops...");
		File file = new File(PATH);
		if (file.exists())
			file.renameTo(new File(
					"data/items/grandExchange/grandExchangeBuyables ("
							+ Utils.random(0, 100) + ") - " + getDate()
							+ ".json"));
		try {
			@SuppressWarnings("resource")
			BufferedReader in = new BufferedReader(
					new FileReader(UNPACKED_PATH));
			/*
			 * BufferedWriter writer = new BufferedWriter(new FileWriter(PATH,
			 * true));
			 */
			DataOutputStream out = new DataOutputStream(new FileOutputStream(
					PACKED_PATH));
			while (true) {
				String line = in.readLine();
				if (line == null)
					break;
				if (line.startsWith("//"))
					continue;
				String[] splitedLine = line.split(" - ", 3);
				if (splitedLine.length != 3)
					throw new RuntimeException("Invalid list for shop line: "
							+ line);
				String[] splitedInform = splitedLine[0].split(" ", 4);
				if (splitedInform.length != 4)
					throw new RuntimeException("Invalid list for shop line: "
							+ line);
				String[] splitedItems = splitedLine[2].split(" ");
				int key = Integer.valueOf(splitedInform[0]);
				int money = Integer.valueOf(splitedInform[1]);
				boolean generalStore = Boolean.valueOf(splitedInform[2]);
				boolean ironMan = Boolean.valueOf(splitedInform[3]);
				Item[] items = new Item[splitedItems.length / 2];
				int count = 0;
				for (int i = 0; i < items.length; i++) {
					items[i] = new Item(Integer.valueOf(splitedItems[count++]),
							Integer.valueOf(splitedItems[count++]), 0, true);
				}
				if (key < 1200) {
					for (int i = 0; i < items.length; i++) {
						// handleGeStuff(items[i].getId(), writer);
						int value = items[i].getAmount();
						if (value < Short.MAX_VALUE) // 32k
							items[i].setAmount(value * SHOP_QUANTITY_RATE);
					}
				} else {
					for (int i = 0; i < items.length; i++) {
						if (items[i].getDefinitions().isStackable()
								|| items[i].getDefinitions().isNoted())
							items[i].setAmount(items[i].getId() == 995 ? 2000000000
									: 1000000);
						else
							items[i].setAmount(5000);
					}
				}

				out.writeInt(key);
				writeAlexString(out, splitedLine[1]);
				out.writeShort(money);
				out.writeBoolean(generalStore);
				out.writeBoolean(ironMan);
				out.writeByte(items.length);
				for (Item item : items) {
					out.writeShort(item.getId());
					out.writeInt(item.getAmount());
				}
				addShop(key, new Shop(key, splitedLine[1], money, items,
						generalStore, ironMan));
			}
			in.close();
			out.close();
			// writer.close();
		} catch (Throwable e) {
			Logger.handle(e);
		}
	}

	private static void loadPackedShops() {
		try {
			RandomAccessFile in = new RandomAccessFile(PACKED_PATH, "r");
			FileChannel channel = in.getChannel();
			ByteBuffer buffer = channel.map(MapMode.READ_ONLY, 0,
					channel.size());
			while (buffer.hasRemaining()) {
				int key = buffer.getInt();
				String name = readAlexString(buffer);
				int money = buffer.getShort() & 0xffff;
				boolean generalStore = buffer.get() == 1;
				boolean ironMan = buffer.get() == 1;
				Item[] items = new Item[buffer.get() & 0xff];
				for (int i = 0; i < items.length; i++)
					items[i] = new Item(buffer.getShort() & 0xffff,
							buffer.getInt(), 0, true);
				addShop(key, new Shop(key, name, money, items, generalStore,
						ironMan));

			}
			channel.close();
			in.close();
		} catch (Throwable e) {
			Logger.handle(e);
		}
	}
	private static void loadSQLshops() {
		int count1=0;
		try {
			Database db = new Database(Settings.MYSQL_HOST, Settings.MYSQL_USER,
					Settings.MYSQL_PASS, "arkscape");

			if (!db.init()) {
				System.err.println("[DATABASE] No connection could be made to the database.");
				return;
			}
			String query = "SELECT * FROM shops";

			try {
				ResultSet rs = db.executeQuery(query);
				while(rs.next()) {
					int id = rs.getInt("ID");
					int moneyid=rs.getInt("Money_ID");
					boolean generalstore = rs.getBoolean("General_Store");
					boolean ironman=rs.getBoolean("Iron_Man");
					String storename=rs.getString("Store_Name");

					int i=1;
					int count=0;
					while(i<=20){
						rs.getInt("id"+i);
						if(rs.wasNull()){

						}else {
							count++;
						}

						i++;
					}
					Item[] items = new Item[count];
					i=1;
					while(i<=count){
						items[i-1] = new Item(rs.getInt("id" + i),
								rs.getInt("q" + i), 0, true);
						i++;
					}
					addShop(id, new Shop(id, storename, moneyid, items, generalstore,
							ironman));
					count1++;
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}


		} catch (Throwable e) {
			Logger.handle(e);
		}
		Logger.log("Shops", "Loaded "+count1+" Shops.");
	}

	public static String readAlexString(ByteBuffer buffer) {
		int count = buffer.get() & 0xfff;
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

	public static void restoreShops() {
		for (Shop shop : handledShops.values()) {
			if (shop.isGeneralStore())
				shop.restoreItems(true);
			shop.restoreItems(false);
		}
	}

	public static boolean openShop(Player player, int key) {
		Shop shop = getShop(key);
		if (shop == null) {
			player.getPackets().sendGameMessage(
					"Error, the requested shop '" + shop + "' with id '" + key
							+ "' does not exist.");
			return false;
		} else if (!shop.isIronMan() && player.isAnIronMan()) {
			player.getPackets().sendGameMessage(
					"This shop is restricted to regular players only.");
			return false;
		}
		player.getInterfaceManager().removeCentralInterface();
		shop.addPlayer(player);
		return true;
	}
	public static boolean openShop1(Player player, int key) {
		try {
			Database db = new Database(Settings.MYSQL_HOST, Settings.MYSQL_USER,
					Settings.MYSQL_PASS, "arkscape");

			if (!db.init()) {
				System.err.println("[DATABASE] No connection could be made to the database.");
			}

			try {
				String queryCheck = "SELECT count(*) from shops WHERE ID = ?";
				PreparedStatement ps = db.prepare(queryCheck);
				ps.setInt(1, key);
				final ResultSet resultSet = ps.executeQuery();
				int count1=0;
				if(resultSet.next()) {
					count1 = resultSet.getInt(1);
				}
				if(count1>0) {

					ResultSet rs = db.executeQuery("SELECT * FROM shops WHERE ID=" + key);
					while (rs.next()) {
						int id = rs.getInt("ID");
						int moneyid = rs.getInt("Money_ID");
						boolean generalstore = rs.getBoolean("General_Store");
						boolean ironman = rs.getBoolean("Iron_Man");
						String storename = rs.getString("Store_Name");

						int i = 1;
						int count = 0;
						while (i <= 20) {
							rs.getInt("id" + i);
							if (rs.wasNull()) {

							} else {
								count++;
							}

							i++;

							System.out.println("Count: "+count);	}
						Item[] items = new Item[count];
						i = 1;
						while (i <= count) {
							items[i - 1] = new Item(rs.getInt("id" + i),
									rs.getInt("q" + i), 0, true);
							i++;
						}
						System.out.println(count);
						addShop(id, new Shop(id, storename, moneyid, items, generalstore,
								ironman));
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}


		} catch (Throwable e) {
			Logger.handle(e);
		}
		Shop shop = getShop(key);
		if (shop == null) {
			player.getPackets().sendGameMessage(
					"Error, the requested shop '" + shop + "' with id '" + key
							+ "' does not exist.");
			return false;
		} else if (!shop.isIronMan() && player.isAnIronMan()) {
			player.getPackets().sendGameMessage(
					"This shop is restricted to regular players only.");
			return false;
		}
		player.getInterfaceManager().removeCentralInterface();
		shop.addPlayer(player);
		return true;
	}

	public static Shop getShop(int key) {
		return handledShops.get(key);
	}

	public static void addShop(int key, Shop shop) {
		handledShops.put(key, shop);
	}

	public static String getDate() {
		DateFormat dateFormat = new SimpleDateFormat("MM dd yyyy");
		Date date = new Date();
		String currentDate = dateFormat.format(date);
		date = null;
		dateFormat = null;
		return currentDate;
	}
}