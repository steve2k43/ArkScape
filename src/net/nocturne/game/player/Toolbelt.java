package net.nocturne.game.player;

import java.io.Serializable;

import net.nocturne.game.item.Item;
import net.nocturne.game.player.actions.skills.woodcutting.Woodcutting;

public class Toolbelt implements Serializable {

	private static final long serialVersionUID = -7244573478285647954L;

	private static final int[][] TOOLBELT_ITEMS = new int[][] {
			{ 946 }, // knife 1
			{ 1735 }, // shears 2
			{ 1595 }, // amulet mould 3
			{ 1755 }, // chisel 4
			{ 1599 }, // holy mould 5
			{ 1597 }, // necklake mould 6
			{ 1733 }, // needle 7
			{ 1592 }, // ring mold 8
			{ 5523 }, // tiara mold 9
			{ 13431 }, // crayfish cage 10
			{ 307 }, // fishing road 11
			{ 309 }, // fly fishing road 12
			{ 311 }, // harpoon 13
			{ 301 }, // lobster pot 14
			{ 303 }, // small fishing net 15
			{ 1265 }, // bronze pickaxe 16
			{ 2347 },// hammer 16 bronze hatchet
			{ 1351 }, // hatchet 18 chompy
			{ 590 }, // tinderbox 19
			{ -1 }, //20
			{ 1267, 1269, 1273, 1271, 1275, 15259, 32646 }, // Pickaxes 21
			{ 1349, 1353, 1357, 1355, 1359, 6739, 32645 }, //Hatchets 22
			{ 8794 }, //saw 23
			{ 4 }, //ammo mould 24
			{ 9434 }, // bolt mould 25
			{ 11065 }, // bracelet mould 26
			{ 1785 },  // glassblowing pipe 27
			{ 2976 },  // sickle mould 28
			{ 1594 }, // unholy mould 29
			{ 5343 }, // seed dibber 30
			{ 5325 }, // gardening trowel 31
			{ 5341 }, // rake 32
			{ 5329 }, // secaters 33
			{ 233 }, // pestle and mortar 34
			{ 952 }, // spade 35
			{ 305 }, // big fishing net 36
			{ 975 }, // machete 37
			{ 11323 }, // barbarian rod 38
			{ 2575 }, //watch 39
			{ 2576 }, // chart 40
			{ 13153 }, // chain link mould 41
			{ 10150 }, // noose wand 42
			{19675}, // herbicide 43
			{31188}  // seedicide 44
			}; //34 / 58
	private static final int[][] DUNG_TOOLBELT_ITEMS = new int[][] {
			{ 16295, 16297, 16299, 16301, 16303, 16305, 16307, 16309, 16311,
					16313, 16315 },
			{ 16361, 16363, 16365, 16367, 16369, 16371, 16373, 16375, 16377,
					16379, 16381 }, { 17883 }, { 17678 }, { 17794 }, { 17754 },
			{ 17446 }, { 17444 } };

	private static final int[] VAR_IDS = new int[] { 1102, 1103, 1104, 1105, 1106 };
	private static final int[] DUNG_VAR_IDS = new int[] { 1107 };
	private int[][] items;
	private transient Player player;
	private transient boolean dungeonnering;

	public Toolbelt() {
		items = new int[][] { new int[TOOLBELT_ITEMS.length],
				new int[DUNG_TOOLBELT_ITEMS.length] };
		setDefaultItems();
	}

	public void setDefaultItems() {
		for (int i = 0; i < items.length; i++)
			for (int i2 = 0; i2 < items[i].length; i2++)
				if (items[i][i2] != -1 && !(i == 0 && (i2 == 20 || i2 == 21 //|| *i2==42
				 )))
					items[i][i2] = 1;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public void init() {
		refreshConfigs();
	}

	private int getVarIndex(int i) {
		return i / 28;
	}

	public int getIncremment(int slot) {
		if (!dungeonnering)
			return slot == 20 || slot == 21 ? 4 : 1;
		return slot == 0 ? 5 : slot == 1 ? 4 : 1;
	}

	private void refreshConfigs() {

		int[] varValues = new int[getVars().length];
		int indexIncremment = 0;
		for (int i = 0; i < getItems().length; i++) {
			if (getItems()[i] != 0) {
				int index = getVarIndex(indexIncremment);
				varValues[index] |= getItems()[i] << (indexIncremment - (index * 28));
			}
			indexIncremment += getIncremment(i);
		}
		for (int i = 0; i < getVars().length; i++)
			player.getVarsManager().sendVar(getVars()[i], varValues[i]);
	}

	public int[] getItems() {
		return items[dungeonnering ? 1 : 0];
	}

	public int[] getVars() {
		return dungeonnering ? DUNG_VAR_IDS : VAR_IDS;
	}

	public int[][] getToolbeltItems() {
		return dungeonnering ? DUNG_TOOLBELT_ITEMS : TOOLBELT_ITEMS;
	}

	private int[] getItemSlot(int id) {
		for (int i = 0; i < getToolbeltItems().length; i++)
			for (int i2 = 0; i2 < getToolbeltItems()[i].length; i2++)
				if (getToolbeltItems()[i][i2] == id)
					return new int[] { i, i2 };
		return null;
	}

	public boolean addItem(int invSlot, Item item) {
		System.out.println(item.getDefinitions().getName());
		System.out.println("Inv slot = "+invSlot);
		System.out.println("Toolbelt length = "+TOOLBELT_ITEMS.length);
		Object itemdefs = item.getDefinitions();
		if(item.getDefinitions().name.contains("hatchet")){
			System.out.println("CONTAINS HATCHET");
			int reqlevel = 0;
			if (item.getId()==1351)
				reqlevel=1;
			if (item.getId()==1349)
				reqlevel=5;
			if (item.getId()==1353)
				reqlevel=5;
			if (item.getId()==1361)
				reqlevel=11;
			if (item.getId()==1355)
				reqlevel=21;
			if (item.getId()==1357)
				reqlevel=31;
			if (item.getId()==1359)
				reqlevel=41;
			if (item.getId()==6739)
				reqlevel=61;
			if (item.getId()==13661)
				reqlevel=61;
			if (item.getId()==32645)
				reqlevel=71;


			System.out.println(reqlevel);
			if(player.getSkills().hasLevel(8, reqlevel)){
				System.out.println("HAS REQ LEVEL");
			} else {
				System.out.println("HAS NOT REQ LEVEL");
				return false;
			}

		}
		if(item.getDefinitions().name.contains("hatchet") || item.getDefinitions().name.contains("adze")){
			System.out.println("CONTAINS HATCHET");
			int reqlevel = 0;
			if (item.getId()==1351)
				reqlevel=1;
			if (item.getId()==1349)
				reqlevel=5;
			if (item.getId()==1353)
				reqlevel=5;
			if (item.getId()==1361)
				reqlevel=11;
			if (item.getId()==1355)
				reqlevel=21;
			if (item.getId()==1357)
				reqlevel=31;
			if (item.getId()==1359)
				reqlevel=41;
			if (item.getId()==6739)
				reqlevel=61;
			if (item.getId()==13661)
				reqlevel=61;
			if (item.getId()==32645)
				reqlevel=71;


			System.out.println(reqlevel);
			if(player.getSkills().hasLevel(8, reqlevel)){
				System.out.println("HAS REQ LEVEL");
			} else {
				System.out.println("HAS NOT REQ LEVEL");
				return false;
			}

		}
		if(item.getDefinitions().name.contains("pickaxe") || item.getDefinitions().name.contains("adze")){
			System.out.println("CONTAINS PICKAXE");
			int reqlevel = 0;
			if (item.getId()==1265)
				reqlevel=1;
			if (item.getId()==1267)
				reqlevel=1;
			if (item.getId()==1269)
				reqlevel=6;
			if (item.getId()==1273)
				reqlevel=21;
			if (item.getId()==1271)
				reqlevel=31;
			if (item.getId()==1275)
				reqlevel=41;
			if (item.getId()==15259)
				reqlevel=61;
			if (item.getId()==13661)
				reqlevel=61;
			if (item.getId()==32646)
				reqlevel=71;


			System.out.println(reqlevel);
			if(player.getSkills().hasLevel(13, reqlevel)){
				System.out.println("HAS REQ LEVEL");
			} else {
				System.out.println("HAS NOT REQ LEVEL");
				return false;
			}

		}


		int[] slot = getItemSlot(item.getId());
		System.out.println("Slot[0] = "+slot[0]);
		System.out.println("getItems()[slot0] = "+getItems()[slot[0]]);
		System.out.println("slot[1]+1 = "+slot[1] + 1);
		if (slot == null)
			return false;
		if (getItems()[slot[0]] == slot[1] + 1)
			player.getPackets().sendInventoryMessage(0, invSlot,
					"That is already on your tool belt.");
		else if (getItems()[slot[0]] > slot[1] + 1)
			player.getPackets().sendInventoryMessage(0, invSlot,
					"You have a higher tier tool already in your tool belt.");
		else {
			getItems()[slot[0]] = slot[1] + 1;
			player.getInventory().deleteItem(invSlot, item);
			refreshConfigs();
			player.getPackets().sendGameMessage(
					"You add the " + item.getDefinitions().getName()
							+ " to your tool belt.");
		}
		return true;
	}

	public void switchDungeonneringToolbelt() {
		this.dungeonnering = !dungeonnering;
		player.getPackets().sendCSVarInteger(1725, dungeonnering ? 11 : 1);
		refreshConfigs();
	}

	public boolean containsItem(int id) {
		int[] slot = getItemSlot(id);
		return slot != null && getItems()[slot[0]] == slot[1] + 1;
	}

	public boolean isTool(int id) {
		return getItemSlot(id) != null;
	}
}