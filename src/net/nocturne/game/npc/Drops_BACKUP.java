package net.nocturne.game.npc;

import net.nocturne.cache.loaders.ItemDefinitions;
import net.nocturne.game.item.ItemIdentifiers;
import net.nocturne.game.player.GamePointManager.GPR;
import net.nocturne.game.player.Player;
import net.nocturne.game.player.content.Combat;
import net.nocturne.game.player.content.activities.events.GlobalEvents;
import net.nocturne.game.player.content.activities.events.GlobalEvents.Event;
import net.nocturne.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class Drops_BACKUP {
	public static final int ALWAYS = 0, COMMON = 1, UNCOMMON = 2, RARE = 3,
			VERY_RARE = 4;
	public static final double vrare = 2.0;
	public static final double rare = 5.0;
	public static final double uncommon = 10.0;
	public static final double common = 30.0;
	public static final double always = 100.0;

	public static final double[] DROP_RATES = { always, common, uncommon, rare, vrare };
	public static final double[] DROP_RATES_NORMAL = { always, common*1.1, uncommon*1.1, rare*1.1, vrare*1.1 };
	public static final double[] DROP_RATES_EXTREME = { always, common*1.3, uncommon*1.3, rare*1.3, vrare*1.3 };

	private static final int[] CHARMS = { 12158, 12159, 12160, 12163 };
	private static final Drop[] RARE_DROP_TABLE = {
			new Drop(1623, 1, 1),
			new Drop(1621, 1, 1),
			new Drop(1619, 1, 1),
			new Drop(1617, 1, 1),
			new Drop(1453, 1, 1),
			new Drop(1462, 1, 1),
			new Drop(987, 1, 1),
			new Drop(985, 1, 1),
			new Drop(995, 250, 1200),
			new Drop(1247, 1, 1),
			new Drop(830, 5, 5),
			new Drop(1201, 1, 1),
			new Drop(1319, 1, 1),
			new Drop(1373, 1, 1),
			new Drop(2366, 1, 1),
			new Drop(1249, 1, 1),
			new Drop(1149, 1, 1),
			new Drop(563, 45, 45),
			new Drop(563, 5, 50),
			new Drop(561, 47, 77),
			new Drop(566, 20, 20),
			new Drop(565, 50, 50),
			new Drop(892, 150, 150),
			new Drop(443, 100, 100),
			new Drop(995, 250, 1200) // again
			,
			new Drop(1215, 1, 1),
			new Drop(892, 150, 500) // again
			, new Drop(9143, 200, 200), new Drop(533, 151, 500),
			new Drop(2999, 25, 250), new Drop(258, 33, 33),
			new Drop(3001, 30, 120), new Drop(270, 10, 100),
			new Drop(454, 150, 7500), new Drop(450, 150, 800),
			new Drop(7937, 100, 14500), new Drop(1441, 25, 35),
			new Drop(1443, 25, 36), new Drop(1444, 1, 1),
			new Drop(372, 125, 1000), new Drop(383, 250, 500),
			new Drop(5321, 3, 3), new Drop(1631, 1, 1), new Drop(1615, 1, 1),
			new Drop(1391, 200, 200), new Drop(574, 1000, 1000),
			new Drop(570, 1000, 1000), new Drop(451, 1, 100),
			new Drop(2362, 1450, 7000), new Drop(2364, 1, 150),
			new Drop(5315, 1, 50), new Drop(5316, 1, 6),
			new Drop(5289, 10, 10), new Drop(5304, 1, 31),
			new Drop(5300, 1, 1), new Drop(1516, 100, 4500),
			new Drop(21620, 4, 4), new Drop(9342, 150, 150),
			new Drop(1216, 50, 50), new Drop(20667, 1, 1),
			new Drop(6686, 250, 250) };

	private boolean acessRareTable;
	private Drop[][] drops;
	private Drop[][] gearRareDrops;

	public Drops_BACKUP(boolean acessRareTable) {
		this.acessRareTable = acessRareTable;
		drops = new Drop[VERY_RARE + 1][];
		gearRareDrops = new Drop[VERY_RARE - RARE + 1][];

	}

	public List<Drop> getAllDrops() {
		List<Drop> d = new ArrayList<Drop>();
		if (drops[ALWAYS] != null) {
			for (Drop drop : drops[ALWAYS])
				d.add(drop);
		}if (drops[COMMON] != null) {
			for (Drop drop : drops[COMMON])
				d.add(drop);
		}
		if (drops[UNCOMMON] != null) {
			for (Drop drop : drops[UNCOMMON])
				d.add(drop);
		}
		if (drops[RARE] != null) {
			for (Drop drop : drops[RARE])
				d.add(drop);
		}
		if (drops[VERY_RARE] != null) {
			for (Drop drop : drops[VERY_RARE])
				d.add(drop);
		}
		return d;
	}
	public boolean isitrare(){
		return acessRareTable;
	}

	public List<Drop> generateDrops(Player killer, double rarity) {
		List<Drop> d = new ArrayList<Drop>();
		boolean ringOfWealth = Combat.hasRingOfWealth(killer);
		if (ringOfWealth)
			rarity *= 1.01;
		if (killer.getGamePointManager().hasGamePointsReward(GPR.BETTER_DROPS))
			rarity *= 1.01;
		if (drops[ALWAYS] != null) {
			for (Drop drop : drops[ALWAYS])
				d.add(drop);
		}
		for (int i = COMMON; i <= VERY_RARE; i++) {
			Drop drop = getDrop(i, rarity, killer.getDifficulty());
			if (drop != null) {
				if (ItemDefinitions.getItemDefinitions(drop.getItemId())
						.getName().toLowerCase().contains("warpriest"))
					continue;
				if (ItemDefinitions.getItemDefinitions(drop.getItemId())
						.getName().toLowerCase().contains("keystone")) {
					d.add(drop);
					d.add(drop);
				}
				if (i >= RARE && ringOfWealth) {
					killer.getPackets()
							.sendGameMessage(
									"<col=ff7000>Your ring of wealth shines more brightly!",
									true);
					ringOfWealth = false;
				}
				d.add(drop);
			}
		}
		if (acessRareTable && Utils.random((int) (300 / rarity)) == 0) {
			Drop drop = getRareDropTable();
			if (drop.getItemId() != 20667 || ringOfWealth)
				d.add(drop);

		}
		System.out.println(rarity);
		return d;
	}

	public void setAcessRareTable(boolean t) {
		acessRareTable = t;
	}

	public void addCharms(List<Drop> d, int size) {
		if (GlobalEvents.isActiveEvent(Event.QUAD_CHARMS))
			size *= 4;
		if (!d.isEmpty() && Utils.random(8 / size) == 0)
			d.add(new Drop(CHARMS[Utils.random(CHARMS.length)], 1, size));
	}

	public void addCharms(Player player, List<Drop> d, int size) {
		if (!d.isEmpty() && Utils.random(8 / size) == 0) {
			int id = CHARMS[Utils.random(CHARMS.length)];
			if (player.getInventory().containsItem(
					ItemIdentifiers.CHARMING_IMP, 1)
					|| player.getEquipment().containsOneItem(
							ItemIdentifiers.CHARMING_IMP)) {
				if (player.getInventory().getFreeSlots() < 2) {
					player.getPackets()
							.sendGameMessage(
									"Your charming imp fails to retrieve the charms, they are on the ground.",
									true);
					d.add(new Drop(id, 1, size));
					return;
				}
				player.getPackets().sendGameMessage(
						"Your charming imp found some charms.", true);
				player.getInventory().addItem(id, size);
			} else
				d.add(new Drop(id, 1, size));
		}
	}

	public Drop getRareDropTable() {
		if (Utils.random(5) >= 3)
			return RARE_DROP_TABLE[Utils.random(RARE_DROP_TABLE.length)];
		else
			return Utils.random(1, 2) == 1 ? new Drop(985, 1, 1) : new Drop(
					987, 1, 1);
	}

	public Drop[] getDrops(int rarity) {
		return drops[rarity];
	}

	public Drop[] getDrops() {
		int totalLength = 0;
		for (Drop[] arr : drops)
			totalLength += arr.length;
		Drop[] totalDrops = new Drop[totalLength];
		for (Drop[] arr : drops)
			totalDrops = Stream.concat(Arrays.stream(totalDrops),
					Arrays.stream(arr)).toArray(Drop[]::new);
		return totalDrops;
	}

	public Drop getDrop(int rarity, double e, int difficulty) {
		double[] DROP_RATE =DROP_RATES;
		if(difficulty == 1){ DROP_RATE = DROP_RATES; }
		if(difficulty == 2){ DROP_RATE = DROP_RATES_NORMAL; }
		if(difficulty == 3){ DROP_RATE = DROP_RATES_EXTREME; }
		if (rarity >= RARE) {
			if (gearRareDrops[rarity - RARE] != null
					&& gearRareDrops[rarity - RARE].length != 0
					&& Math.random() * 100 <= (DROP_RATE[rarity] * e))
				return gearRareDrops[rarity - RARE][Utils
						.random(gearRareDrops[rarity - RARE].length)];
		}

		if (drops[rarity] != null && drops[rarity].length != 0
				&& Math.random() * 100 <= (DROP_RATE[rarity] * e)) {
			System.out.println("DROP CHANCE: "+DROP_RATE[rarity]*e+"%");
			return drops[rarity][Utils.random(drops[rarity].length)];
		}

		return null;
	}

	public static boolean countsAsGear(int id) {
		return id == 13754 || id == 11286 || id == 21369 || id == 13746
				|| id == 13748 || id == 13750 || id == 13752;
	}

	public void addDrops(List<Drop>[] dList) {
		for (int i = 0; i < dList.length; i++) {
			if (dList[i] == null)
				continue;
			if (i >= RARE) {
				ArrayList<Drop> cleanedGear = new ArrayList<Drop>();
				for (Drop drop : dList[i].toArray(new Drop[dList[i].size()])) {
					if (countsAsGear(drop.getItemId())
							|| ItemDefinitions.getItemDefinitions(
									drop.getItemId()).isWearItem()) {
						cleanedGear.add(drop);
						dList[i].remove(drop);
					}
				}
				if (cleanedGear.size() > 0)
					gearRareDrops[i - RARE] = cleanedGear
							.toArray(new Drop[cleanedGear.size()]);
			}
			drops[i] = dList[i].toArray(new Drop[dList[i].size()]);
		}
	}
}