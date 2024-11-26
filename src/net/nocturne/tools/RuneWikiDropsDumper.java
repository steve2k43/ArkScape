package net.nocturne.tools;

import java.io.*;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import net.nocturne.cache.Cache;
import net.nocturne.cache.loaders.ItemDefinitions;
import net.nocturne.cache.loaders.NPCDefinitions;
import net.nocturne.game.npc.Drops;
import net.nocturne.tools.ItemDropsPacker.NPCDrop;
import net.nocturne.utils.NPCDrops;
import net.nocturne.utils.Utils;

public class RuneWikiDropsDumper {

	public static final void main(String[] args) throws IOException {
		Cache.init();
		for (int i = 0; i < Utils.getNPCDefinitionsSize(); i++) {
			NPCDefinitions defs = NPCDefinitions.getNPCDefinitions(i);
			if (!defs.hasAttackOption())
				continue;
			try {
				if (!dumpNPC(i))
					System.out.println("Failed dumping npc: " + i + ", "
							+ NPCDefinitions.getNPCDefinitions(i).name);
				else
					System.out.println("Dumped npc: " + i + ", "
							+ NPCDefinitions.getNPCDefinitions(i).name);
			} catch (java.lang.StringIndexOutOfBoundsException e) {
				e.printStackTrace();
			}
		}
	}

	public static boolean dumpNPC(int npcId) throws IOException {
		NPCDefinitions defs = NPCDefinitions.getNPCDefinitions(npcId);
		String pageName = defs.name;// .concat(" (Daemonheim)");
		if (pageName == null || pageName.equals("null"))
			return true;
		pageName = pageName.replaceAll(" ", "_");
		WebPage page = new WebPage("http://runescape.wikia.com/wiki/"
				+ pageName + "?action=edit");// "http://runescape.wikia.com/wiki/"+
		// pageName);
		try {
			page.load();
		} catch (SocketTimeoutException e) {
			return dumpNPC(npcId);
		} catch (Exception e) {
			System.out.println("Invalid page: " + npcId + ", " + pageName);
			return false;
		}
		/*
		 * int section = getDropsSection(page); pageName +=
		 * "?action=edit&section="+section; page = new
		 * WebPage("http://runescape.wikia.com/wiki/"+ pageName); //
		 * System.out.println("http://runescape.wikia.com/wiki/"+ pageName); try
		 * { page.load(); } catch (SocketTimeoutException e) { return
		 * dumpNPC(npcId); } catch (Exception e) {
		 * System.out.println("Invalid page: " + npcId + ", " + pageName);
		 * return false; }
		 */
		dumpDrops(npcId, page);
		return true;
	}

	public static boolean readDrop(String data, List<NPCDrop> drops) {
		try {
			int itemId = -1;
			int minQuantity = 1;
			int maxQuantity = 1;
			int rarity = 1; // commum
			boolean noted = false;
			// System.out.println(data);
			String[] sp = data.split("#");
			for (int i = 0; i < sp.length; i += 2) {
				String s = sp[i];
				if (s.equals("CName") || s.equals("Name")) {
					String name = sp[i + 1];
					itemId = getItemId(name);
					if (itemId == -1) {
						System.out.println("Unnkown item: " + name);
						return false;
					}
				} else if (s.equals("CQuantity") || s.equals("Quantity")) {
					String quantity = sp[i + 1];
					if (quantity.contains(" (noted)")) {
						noted = true;
						quantity = quantity.replace((" (noted)"), "");
					}
					if (quantity.contains(" noted)")) {
						noted = true;
						quantity = quantity.replace((" noted)"), "");
					}
					if (quantity.contains("(noted)")) {
						noted = true;
						quantity = quantity.replace(("(noted)"), "");
					}
					if (quantity.contains("(Noted)")) {
						noted = true;
						quantity = quantity.replace(("(Noted)"), "");
					}
					if (quantity.contains("noted")) {
						noted = true;
						quantity = quantity.replace(("noted"), "");
					}
					quantity = quantity.replace(" ", "");
					String[] sp2 = quantity.split("-");
					if (sp2[0].equals("Unknown") || sp2[0].equals("")
							|| sp2[0].equals("?"))
						sp2[0] = "1";
					else if (sp2[0].equals("40(/un)"))
						sp2[0] = "40";
					minQuantity = Integer.valueOf(sp2[0]);
					try {
						maxQuantity = Integer.valueOf(sp2[sp2.length - 1]);
					} catch (Throwable e) {
						maxQuantity = minQuantity;
					}
				} else if (s.equals("CRarity") || s.equals("Rarity")) {
					// System.out.println(Arrays.toString(sp));
					String r = sp[i + 1];
					rarity = getRarity(r);
					if (rarity == -1)
						return false;
				}
			}
			if (noted) {
				itemId = ItemDefinitions.getItemDefinitions(itemId).certId;
				if (itemId == -1)
					return false;
			}
			//return drops.add(new NPCDrop(itemId, minQuantity, maxQuantity,
			//		rarity));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static void dumpDrops(int id, WebPage page) {
		try {
			boolean acessToRareTable = false;
			List<NPCDrop> drops = new ArrayList<NPCDrop>();
			for (String line : page.getLines()) {
				// itemdrop
				if (line.contains("{{DropsLine")) {
					// System.out.println(line);
					String data = line.replace("{{DropsLine|", "")
							.replace("}}", "").replace("|", "#")
							.replace("=", "#").replace(", ", "-")
							.replace("%27", "").replace(" or ", "-")
							.replace(",", "-").replace(" ", "")
							.replace("&lt;sub>&lt;/sub>", "");
					// System.out.println(data);
					try {
						if (!readDrop(data, drops))
							continue;
					} catch (ArrayIndexOutOfBoundsException e) {
						System.out.println(e);
					}
				} else if (line.contains("BDropsLine")
						&& line.contains("CName") && line.contains("CQuantity")
						&& line.contains("CRarity")) {
					String data = line.substring(line.indexOf("BDropsLine"));
					data = data.substring(0,
							data.indexOf("\" data-rte-instance=\""));
					data = data.replace("%20", " ").replace("%7D%7D%22%7D", "")
							.replace("%3D", "#").replace("%7", "#")
							.replace("_", "").replace("%2C", "-")
							.replace("%27", "").replace("%28", "(")
							.replace("%29", ")").replace("- ", "-")
							.replace(" -", "-").replace("BDropsLine#", "")
							.replace("-%27%273-7 times%27%27", "")
							.replace(" or ", "-").replace("%2B%2B", "")
							.replace("1unnotedotherwise", "1")
							.replace("%3F", "1").replace("(unnoted)", "")
							.replace("(un", "").replace("%2B", "")
							.replace("(/un)", "");
					// System.out.println(data);
					try {
						if (!readDrop(data, drops))
							continue;
					} catch (ArrayIndexOutOfBoundsException e) {
						System.out.println(e);
					}
				} else if (line.toLowerCase().contains("rare drop table")) {
					acessToRareTable = true;
					// System.out.println(line);
				}
			}
			if (acessToRareTable == false && drops.size() == 0)
				return;
			System.out.println(drops.size() + ", " + acessToRareTable);
			File file = new File("./data/npcs/drops/" + id + ".txt");
			try {
				BufferedWriter writer = new BufferedWriter(new FileWriter(file));
				writer.write("RareDropTable=" + acessToRareTable);
				writer.newLine();
				writer.flush();
				for (NPCDrop drop : drops) {
					double dropchance=0.0;
					if(drop.getRarity()==1){
						dropchance = 1;
					}
					if(drop.getRarity()==2){
						dropchance = Math.floor(1/Drops.common);
					}
					if(drop.getRarity()==3){
						dropchance = Math.floor(1/Drops.uncommon);
					}
					if(drop.getRarity()==4){
						dropchance = Math.floor(1/Drops.rare);
					}
					if(drop.getRarity()==5){
						dropchance = Math.floor(1/Drops.vrare);
					}
					writer.write(drop.getItemId() + ", " + drop.getMinAmount()
							+ ", " + drop.getMaxAmount() + ", "
							+ drop.getRarity());
					writer.newLine();
					writer.flush();
				}
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static int getRarity(String rarity) {
		rarity = rarity.toLowerCase();
		if (rarity.contains("always"))
			return 0;
		if (rarity.contains("uncommon"))
			return 2;
		if (rarity.contains("random") || rarity.contains("varies")
				|| rarity.contains("common") || rarity.contains("unknown"))
			return 1;
		if (rarity.contains("very rare"))
			return 4;
		if (rarity.contains("rare"))
			return 3;
		System.out.println("Unnkown rarity: " + rarity);
		return -1; // Common
	}

	public static int getItemId(String name) {
		name = name.replace(" (General Graardor)", "");
		name = name.replace(" (blue)", "");
		name = name.replace(" (black)", "");
		name = name.replace("Chefs hat", "Chef's hat");
		name = name.replace("(top)", "top");
		name = name.replace("(bottom)", "bottom");
		name = name.replace("Clue scroll{{!", "");
		name = name.replace(" (light)", "");
		name = name.replace(" (dark)", "");
		for (int itemId = 0; itemId < Utils.getItemDefinitionsSize(); itemId++) {
			ItemDefinitions item = ItemDefinitions.getItemDefinitions(itemId);
			if (item.isNoted() || item.isLended())
				continue;
			if (item.getName().equalsIgnoreCase(name))
				return itemId;
		}
		return -1;
	}

	public static int getDropsSection(WebPage page) {
		for (String line : page.getLines()) {
			if (line.contains("section=")
					&& line.contains("\" title=\"Edit Drops section\"")) {
				String c = line.substring(line.indexOf("section=") + 8,
						line.indexOf("\" title=\"Edit Drops section\""));
				return Integer.valueOf(c);
			}
		}
		System.out.println("-1");
		return -1;
	}
}
