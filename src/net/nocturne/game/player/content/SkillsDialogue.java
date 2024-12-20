package net.nocturne.game.player.content;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.nocturne.cache.Cache;
import net.nocturne.cache.loaders.ClientScriptMap;
import net.nocturne.cache.loaders.ItemDefinitions;
import net.nocturne.game.World;
import net.nocturne.game.WorldObject;
import net.nocturne.game.item.Item;
import net.nocturne.game.item.ItemConstants;
import net.nocturne.game.player.InterfaceManager;
import net.nocturne.game.player.Player;
import net.nocturne.game.player.actions.skills.cooking.Cooking;
import net.nocturne.game.player.actions.skills.cooking.Cooking.Cookables;
import net.nocturne.game.player.actions.skills.firemaking.Bonfire;
import net.nocturne.game.player.actions.skills.firemaking.Bonfire.Log;
import net.nocturne.game.player.actions.skills.firemaking.Firemaking;
import net.nocturne.game.player.actions.skills.firemaking.Firemaking.Fire;
import net.nocturne.game.player.actions.skills.fletching.LogsFletching.FletchData;
import net.nocturne.game.player.actions.skills.smithing.Smithing;
import net.nocturne.game.player.content.grandExchange.GrandExchange;
import net.nocturne.utils.ItemExamines;

public final class SkillsDialogue {

	private static final Map<Integer, SkillDialogue> skillDialogues = new HashMap<Integer, SkillDialogue>();

	private static int[] NO_MENU_CATEGORIES = { 9468, 9467, 9056, 7053, 7050,
			7048, 7049, 7046, 7047, 7070, 7069, 7065, 7064, 7063, 7077, 7078,
			7103, 7102, 7096, 7093, 7092, 7106, 8076, 7104, 7105, 7556, 8002,
			6921, 7512, 6972, 6973, 6974, 6975, 6969, 6970, 6971, 6979, 6977,
			6976, 6980, 6986, 6990, 6989, 8858, 8856, 8857, 8854, 8855, 8853,
			6808, 6840, 6830, 6831, 6829, 6868, 6898, 6674, 7278, 7277, 7276,
			7275, 8697, 6770, 6771, 6768, 6769, 6774, 6775, 6772, 6773, 6778,
			6779, 6776, 6777, 6762, 6761, 6767, 6766, 6765, 6764, 11357, 11358,
			11359, 11360, 11361, 11362, 11363, 11364, 11365 };

	private static int[] SKILL_DIALOGUES = { -1, 6691, 966, 5773, 6675, 6780,
			6784, 6789, 6794, 6803, 6809, 6821, 6823, 6832, 6838, 6848, 6852,
			6853, 6861, 6869, 6877, 6879, 6894, 6896, 6919, 6939, 6932, 6933,
			6934, 6935, 6936, 6937, 6938, 6941, 6943, 6945, 6981, 6987, 7004,
			7006, 7008, 7010, 7012, 7042, 7043, 7044, 7045, 7051, 7057, 7059,
			7061, 7079, 7081, 7094, 7113, 7551, 7800, 7812, 9471, 9473, 9474,
			9475, 9476, 9477, 10739, 10740, 10738, 10741, 11385, 11463, 11357,
			11358, 11359, 11360, 11361, 11362, 11363, 11364, 11365 };

	public static class SkillDialogue {

		private int menuCSMapId, menuNamesCSMapId;
		private SkillCategory[] categories;

		private SkillDialogue(int id) {
			menuCSMapId = id;

			if (id == -1) {
				menuNamesCSMapId = -1;
				categories = new SkillCategory[NO_MENU_CATEGORIES.length];
				for (int i = 0; i < categories.length; i++)
					categories[i] = new SkillCategory(NO_MENU_CATEGORIES[i]);
			} else {
				menuNamesCSMapId = id
						+ (menuCSMapId == 6852 || menuCSMapId == 6853
								|| menuCSMapId == 7551 ? 2 : 1);
				ClientScriptMap csmap = ClientScriptMap.getMap(id);
				categories = new SkillCategory[csmap.getSize()];
				for (int i = 0; i < categories.length; i++)
					categories[i] = new SkillCategory(csmap.getIntValue(i));
			}
		}

		public SkillCategory getCategory(int id) {
			for (SkillCategory c : categories)
				if (c.getItemsCSMapId() == id)
					return c;
			return null;
		}

		public SkillCategory[] getCategorys() {
			return categories;
		}
	}

	private static final int SKILL_VAR = 1168, CATEGORY_VAR = 1169,
			PRODUCT_VAR = 1170, MAX_QUANTITY_VAR_BIT = 1002,
			CURRENT_QUANTITY_VAR_BIT = 1003;

	public static class SkillCategory {

		private int itemsCSMapId;
		private int categorySpriteId;
		private int[] items;

		private SkillCategory(int id) {
			setItemsCSMapId(id);
			categorySpriteId = ClientScriptMap.getMap(6817).getIntValue(id);
			ClientScriptMap csmap = ClientScriptMap.getMap(id);
			setItems(new int[csmap.getSize()]);
			for (int i = 0; i < getItems().length; i++)
				getItems()[i] = csmap.getIntValue(i);
		}

		public int[] getItems() {
			return items;
		}

		public void setItems(int[] items) {
			this.items = items;
		}

		public int getItemsCSMapId() {
			return itemsCSMapId;
		}

		public void setItemsCSMapId(int itemsCSMapId) {
			this.itemsCSMapId = itemsCSMapId;
		}
	}

	public static SkillDialogue getSkillDialogue(int id) {
		SkillDialogue d = skillDialogues.get(id);
		if (d == null)
			skillDialogues.put(id, d = new SkillDialogue(id));
		return d;
	}

	public static enum CategoryTypes {

		COOKING(14145), CRAFTING(14149), FARMING(14153), FIREMAKING(14157), FLETCHING(
				14161), HERBLORE(14165), MAGIC(14169), RUNECRAFTING(14173), SMITHING(
				14177), SUMMOMING(14181), WOODCUTTING(14185), REWARDS(14193), WATER(
				14197), DIVINATION(20390);

		private int spriteId;

		private CategoryTypes(int spriteId) {
			this.spriteId = spriteId;
		}
	}

	// 1169 var
	public static void main(String[] args) throws IOException {
		Cache.init();
		/*
		 * for(SmeltingBar s : Smelting.SmeltingBar.values())
		 * System.out.println(findSkillDialogue(s.getItemsRequired()[0].getId(),
		 * s.getSkillType() == Skills.CRAFTING ? CategoryTypes.CRAFTING :
		 * CategoryTypes.SMITHING, 0, false));
		 */
		System.out.println(findSkillDialogueByProduce(48));
	}

	/*
	 * not recommended since material repeats so it doesnt always get correctly
	 */
	public static SkillDialogue findSkillDialogueByMaterial(int materialId,
			CategoryTypes type, int index, boolean skipNoMenu) {
		int idx = 0;
		for (int i = skipNoMenu ? 1 : 0; i < SKILL_DIALOGUES.length; i++) {
			SkillDialogue d = getSkillDialogue(SKILL_DIALOGUES[i]);
			SkillCategory c = getCategoryWithMaterial(d, materialId);
			if (c == null
					|| (type != null && c.categorySpriteId != type.spriteId))
				continue;
			// System.out.println(d.menuCSMapId+", "+c.getItemsCSMapId()+",
			// "+c.categorySpriteId+", "+materialId);
			if (idx++ == index)
				return d;
		}
		return null;
	}

	/*
	 * 100% safe. produce is only one for each dialogue
	 */
	public static SkillDialogue findSkillDialogueByProduce(int produceId) {
		for (int i = 0; i < SKILL_DIALOGUES.length; i++) {
			SkillDialogue d = getSkillDialogue(SKILL_DIALOGUES[i]);
			for (SkillCategory c : d.categories) {
				for (int id : c.getItems())
					if (produceId == id) {
						System.out.println("here you go " + i + ": "
								+ d.menuCSMapId + ", " + c.itemsCSMapId);
						return d;
					}
			}
		}
		return null;
	}

	private static SkillCategory getCategoryWithProduce(SkillDialogue sd,
			int produceId) {
		for (SkillCategory sc : sd.categories) {
			for (int id : sc.getItems()) {
				if (produceId == id)
					return sc;
			}
		}
		return null;
	}

	/*
	 * -1 = playermade fire, -2 ingame cook fire.
	 */
	public static boolean selectTool(Player player, int materialId) {
		List<ToolReference> tools = new ArrayList<ToolReference>();

		if (materialId < 0) {
			tools.add(new ToolReference(25637, Cooking.getCook(player)));
			if (materialId == -2)
				tools.add(new ToolReference(24291, Bonfire.Log.LOG));
		} else {
			FletchData fletch = FletchData.getFletchItem(materialId);
			if (fletch != null)
				tools.add(new ToolReference(946, fletch));
			Fire firemake = Firemaking.getFire(materialId);
			if (firemake != null)
				tools.add(new ToolReference(590, firemake));
			Log log = Bonfire.getLog(materialId);
			if (log != null)
				tools.add(new ToolReference(24291, log));
			/*
			 * Gem gem = GemCutting.getGem(materialId); if (gem != null)
			 * tools.add(new ToolReference(1755, gem));
			 */
		}
		if (tools.size() == 0)
			return false;
		if (tools.size() == 1)
			tools.get(0).select(player);
		else {
			player.getPackets().sendHideIComponent(1371, 20, false);
			player.getDialogueManager().startDialogue(
					"ChooseAToolD",
					"What do you want to use on the "
							+ (materialId == -1 ? "fire" : ItemDefinitions
									.getItemDefinitions(materialId).getName())
							+ "?",
					tools.toArray(new ToolReference[tools.size()]));
		}
		return true;

	}

	public static void backToSelect(Player player) {
		ItemDefinitions defs = ItemDefinitions.getItemDefinitions(player
				.getVarsManager().getValue(PRODUCT_VAR));
		int materialId = defs.getCSOpcode(2655);
		if (materialId != 0)
			selectTool(player, materialId);
	}

	public static class ToolReference {

		private int toolId;
		private Object target;

		private ToolReference(int toolId, Object target) {
			this.toolId = toolId;
			this.target = target;
		}

		public int getToolId() {
			return toolId;
		}

		public WorldObject findFire(Player player) {
			for (int x = -1; x <= 1; x++) {
				for (int y = -1; y <= 1; y++) {
					WorldObject o = World.getObjectWithType(
							player.transform(x, y, 0), 10);
					if (o != null && o.getDefinitions().name.equals("Fire"))
						return o;
				}
			}
			return null;

		}

		public void select(Player player) {
			if (target instanceof FletchData) {
				player.getDialogueManager().startDialogue("LogsFletchingD",
						target);
			} else if (target instanceof Cookables) {
				WorldObject fire = findFire(player);
				if (fire == null)
					return;
				player.faceObject(fire);
				player.getDialogueManager().startDialogue("CookingD", target,
						fire);
			} else if (target instanceof Log) {
				WorldObject fire = findFire(player);
				if (fire == null)
					return;
				player.faceObject(fire);
				Bonfire.addLogs(player, fire);
			} else if (target instanceof Fire) {
				player.getActionManager().setAction(
						new Firemaking((Fire) target));
			} // else if (target instanceof Gem)
				// GemCutting.cut(player, (Gem) target);
		}
	}
	
	public static void sendProgressBar(Player player, int produceId, int maxQuantity, int experience){
		player.getInterfaceManager().setWindowInterface(440, 1251);
		player.getVarsManager().sendVar(1175, produceId);
		player.getPackets().sendCSVarInteger(2228, maxQuantity); //max
		player.getPackets().sendCSVarInteger(2229, maxQuantity);
		player.getPackets().sendCSVarInteger(2227, 4);
	}

	public static void sendSkillDialogueByMaterial(Player player, int material,
			CategoryTypes type, int index, boolean skipNoMenu) {
		SkillDialogue d = findSkillDialogueByMaterial(material, type, index,
				skipNoMenu);
		if (d == null)
			return;
		sendSkillDialogue(player, d, getCategoryWithMaterial(d, material));
	}

	public static void sendSkillDialogueByProduce(Player player, int produceId) {
		SkillDialogue d = findSkillDialogueByProduce(produceId);
		if (d == null)
			return;
		sendSkillDialogue(player, d, getCategoryWithProduce(d, produceId));
	}

	// recommend using the other one for skills. this one is manual
	public static void sendSkillDialogue(final Player player, SkillDialogue sd,
			SkillCategory sc) {
		if (sc == null)
			sc = sd.categories[0];
		int maxslot=27;

		player.getVarsManager().sendVar(SKILL_VAR, sd.menuCSMapId);
		setCategory(player, sc);
		player.getPackets().sendCSVarInteger(2222, sd.menuNamesCSMapId);
		player.getInterfaceManager().sendCentralInterface(1370);
		player.getInterfaceManager().setInterface(true, 1370, 62, 1371);
		player.getPackets().sendIComponentSettings(1371, 62, 0,
				sd.categories.length, 2);
		player.getPackets().sendIComponentSettings(1371, 44, 0, 500, 2);
		player.getPackets().sendIComponentSettings(1371, 36, 0, 1, 2359296);
		player.getPackets().sendIComponentSettings(1371, 143, 0, maxslot, 2);
		player.setCloseInterfacesEvent(new Runnable() {
			@Override
			public void run() {
				player.getVarsManager().sendVar(SKILL_VAR, -1);
				player.getVarsManager().sendVar(CATEGORY_VAR, -1);
				player.getVarsManager().sendVar(PRODUCT_VAR, -1);
				//player.getVarsManager().sendVarBit(MAX_QUANTITY_VAR_BIT, 0);
				player.getVarsManager().sendVarBit(CURRENT_QUANTITY_VAR_BIT, 0);
			}
		});

	}

	private static SkillCategory getCategoryWithMaterial(SkillDialogue sd,
			int materialId) {
		for (SkillCategory sc : sd.categories) {
			for (int itemId : sc.getItems()) {
				ItemDefinitions defs = ItemDefinitions
						.getItemDefinitions(itemId);
				if (defs.getCSOpcode(2655) == materialId)
					return sc;
			}
		}
		return null;
	}

	public static void setCategoryByIndex(Player player, int index) {
		int skill = player.getVarsManager().getValue(SKILL_VAR);
		if (skill == 0)
			return;
		SkillDialogue sd = getSkillDialogue(skill);
		if (sd == null || index >= sd.categories.length)
			return;
		setCategory(player, sd.categories[index]);
	}

	private static void setCategory(Player player, SkillCategory sc) {
		player.getVarsManager().sendVar(CATEGORY_VAR, sc.getItemsCSMapId());
		int bestProduct = -1;
		l: for (int i = sc.getItems().length - 1; i >= 0; i--) { // best product
																	// for lvl
																	// with
																	// material
			ItemDefinitions defs = ItemDefinitions.getItemDefinitions(sc
					.getItems()[i]);
			int skillId = ClientScriptMap.getMap(681).getIntValue(
					defs.getCSOpcode(2640));
			int level = defs.getCSOpcode(2645);
			int multiplierQuantity = defs.getCSOpcode(2653);
			if ((skillId == -1 || player.getSkills().getLevel(skillId) >= level)) {
				for (int i2 = 0; i2 < 3; i2++) {
					int material = defs.getCSOpcode(2655 + i2);
					int amount = defs.getCSOpcode(2665 + i2);
					if (material != 0
							&& !(player.getInventory().containsItem(material,
									amount
											* (multiplierQuantity == 0 ? 1
													: multiplierQuantity))))
						continue l;
				}
				bestProduct = i;
				break;
			}
		}
		if (bestProduct == -1) { // best product for lvl if no mat
			bestProduct = 0;
			for (int i = sc.getItems().length - 1; i >= 0; i--) {
				ItemDefinitions defs = ItemDefinitions.getItemDefinitions(sc
						.getItems()[i]);
				int skillId = ClientScriptMap.getMap(681).getIntValue(
						defs.getCSOpcode(2640));
				int level = defs.getCSOpcode(2645);
				if (skillId == -1
						|| player.getSkills().getLevel(skillId) >= level) {
					bestProduct = i;
					break;
				}
			}
		}
		setProduct(player, sc.getItems()[bestProduct]);
	}

	public static void setProductByIndex(Player player, int index) {
		int skill = player.getVarsManager().getValue(SKILL_VAR);
		if (skill == 0)
			return;

		SkillCategory sc = getSkillDialogue(skill).getCategory(
				player.getVarsManager().getValue(CATEGORY_VAR));
		if (sc == null || index >= sc.getItems().length)
			return;

		setProduct(player, sc.getItems()[index]);
	}

	private static void setProduct(Player player, int result) {
		player.getVarsManager().sendVar(PRODUCT_VAR, result);
		setMaxQuantity(player, result);
		Item item = new Item(result, 1);
		player.getPackets()
				.sendCSVarString(2391, ItemExamines.getExamine(item));
		boolean tradeable = ItemConstants.isTradeable(item);
		player.getPackets().sendCSVarInteger(2223, tradeable ? 1 : 0);
		if (tradeable)
			player.getPackets().sendCSVarInteger(2224,
					GrandExchange.getPrice(result));

	}

	private static void setMaxQuantity(Player player, int result) {

		ItemDefinitions defs = ItemDefinitions.getItemDefinitions(result);
		int maxQuantity = 0;
		int maxQuantity2 = 0;
		int skillId = ClientScriptMap.getMap(681).getIntValue(
				defs.getCSOpcode(2640));
		int level = defs.getCSOpcode(2645);
		int multiplierQuantity = defs.getCSOpcode(2653);
		//int inv = player.getInventory().getAmountOf(defs.getCSOpcode(2655));
		int primary = defs.getCSOpcode(2655);
		int secondary = defs.getCSOpcode(2656);
		int requiredprimary = defs.getCSOpcode(2665);
		int requiredsecondary = defs.getCSOpcode(2666);
		int amountofprimary = player.getInventory().getAmountOf(primary);
		int amountofsecondary = player.getInventory().getAmountOf(secondary);
		player.getPackets().sendCSVarInteger(2225, amountofsecondary);
		String itemname = defs.getName();
		System.out.println("Primary ID: "+primary);
		System.out.println("Secondary ID: "+secondary);
		System.out.println("Amount of PRIMARY: "+amountofprimary);
		System.out.println("Amount of SECONDARY: "+amountofsecondary);
		System.out.println("Required amount of PRIMARY: "+requiredprimary);
		System.out.println("Required amount of SECONDARY: "+requiredsecondary);
		for( int a = 2780; a > 2400; a--){
			if(defs.getCSOpcode(a) != 0)
			System.out.println("OPCODE "+a+" = "+defs.getCSOpcode(a));
		}
		System.out.println("Item Name: "+itemname);
		if (player.getSkills().getLevel(skillId) >= level) {
			boolean allMatsStackable = true;
			if(requiredsecondary !=0){
				int p = amountofprimary / requiredprimary;
				int s = amountofsecondary / requiredsecondary;
				if(p < s)
					maxQuantity = p;
				if(s < p)
					maxQuantity = s;
				if(s==p)
					maxQuantity = p;
			}else{
				int p = amountofprimary / requiredprimary;
				maxQuantity=p;
			}

			if (allMatsStackable && maxQuantity > 10)
				maxQuantity = 10;
		}
		System.out.println("TEST maxq: "+maxQuantity);
		player.getVarsManager().sendVarBit(MAX_QUANTITY_VAR_BIT, maxQuantity);
		setCurrentQuantity(player, maxQuantity);
		Item item = new Item(result);
	}

	public static void setCurrentQuantity(Player player, boolean increase) {
		//setCurrentQuantity(player,
		//		player.getVarsManager().getBitValue(CURRENT_QUANTITY_VAR_BIT)
		//				+ (increase ? 1 : -1));
		int q = player.getVarsManager().getBitValue(CURRENT_QUANTITY_VAR_BIT);
		int max = player.getVarsManager().getBitValue(MAX_QUANTITY_VAR_BIT);
		//SkillDialogueResult poopoo = getResult(player);
		//int lol = poopoo.quantity;
		System.out.println("CURRENT Q BEFORE: "+q);
		if(increase){
			q++;
			if (q > max)
				q = max;
			else if (q < 1)
				q = 1;
		}else{
			q--;
			if (q > max)
				q = max;
			else if (q < 1)
				q = 1;
		}
		System.out.println("CURRENT Q: "+q);
		player.getVarsManager().sendVarBit(CURRENT_QUANTITY_VAR_BIT, q);
	}

	public static void setCurrentQuantity(Player player, int quantity) {
		int max = player.getVarsManager().getBitValue(MAX_QUANTITY_VAR_BIT);
		if (quantity > max)
			quantity = max;
		else if (quantity < 1)
			quantity = 1;

		System.out.println("CURRENT Q2: "+quantity);
		player.getVarsManager().sendVarBit(CURRENT_QUANTITY_VAR_BIT, quantity);
	}

	public static class SkillDialogueResult {

		private int productId, quantity;

		private SkillDialogueResult(int productId, int quantity) {
			this.productId = productId;
			this.quantity = quantity;
		}

		public int getProduce() {
			return productId;
		}

		public int getQuantity() {
			return quantity;
		}
	}

	public static SkillDialogueResult getResult(Player player) {
		SkillDialogueResult result = new SkillDialogueResult(player
				.getVarsManager().getValue(PRODUCT_VAR), player
				.getVarsManager().getBitValue(CURRENT_QUANTITY_VAR_BIT));
		player.closeInterfaces();
		return result;

	}
}
