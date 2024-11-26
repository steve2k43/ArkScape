package net.nocturne.game.player.dialogues.impl;

import net.nocturne.game.WorldTile;
import net.nocturne.game.item.Item;
import net.nocturne.game.player.Equipment;
import net.nocturne.game.player.actions.skills.magic.Magic;
import net.nocturne.game.player.dialogues.Dialogue;

public class PharaohSceptre extends Dialogue {

	/**
	 * @author: miles M
	 */

	private int itemId;

	@Override
	public void start() {
		stage = 1;
		itemId = (int) parameters[0];
		sendOptionsDialogue("Which Pyramid do you want to teleport to?",
				"Jalsavrah.", "Jaleustrophos.", "Jaldrocht.",
				"I'm happy where I am actually.");
	}

	@Override
	public void run(int interfaceId, int componentId, int slotId) {
		switch (stage) {
		case -1:
			end();
			break;
		case 1:
			switch (componentId) {
			case OPTION_1:
				teleport(1);
				end();
				break;
			case OPTION_2:
				teleport(2);
				end();
				break;
			case OPTION_3:
				teleport(3);
				end();
				break;
			case OPTION_4:
				end();
				break;
			}
			break;
		}
	}

	public void teleport(int id) {
		switch (id) {
		case 1:
			if (player.getEquipment().containsOneItem(itemId)) {
				player.getEquipment().deleteItem(itemId, 1);
				player.getEquipment().getItems()
						.set(Equipment.SLOT_WEAPON, new Item(itemId + 2, 1));
			} else {
				player.getInventory().deleteItem(itemId, 1);
				player.getInventory().addItem(itemId + 2, 1);
			}
			Magic.sendPyramidTeleport(player, new WorldTile(3293, 2804, 0));
			break;
		case 2:
			if (player.getEquipment().containsOneItem(itemId)) {
				player.getEquipment().deleteItem(itemId, 1);
				player.getEquipment().getItems()
						.set(Equipment.SLOT_WEAPON, new Item(itemId + 2, 1));
			} else {
				player.getInventory().deleteItem(itemId, 1);
				player.getInventory().addItem(itemId + 2, 1);
			}
			Magic.sendPyramidTeleport(player, new WorldTile(3341, 2827, 0));
			break;
		case 3:
			if (player.getEquipment().containsOneItem(itemId)) {
				player.getEquipment().deleteItem(itemId, 1);
				player.getEquipment().getItems()
						.set(Equipment.SLOT_WEAPON, new Item(itemId + 2, 1));
			} else {
				player.getInventory().deleteItem(itemId, 1);
				player.getInventory().addItem(itemId + 2, 1);
			}
			Magic.sendPyramidTeleport(player, new WorldTile(3232, 2897, 0));
			break;
		}
		player.getEquipment().refresh(Equipment.SLOT_WEAPON);
		player.getAppearence().generateAppearenceData();
	}

	@Override
	public void finish() {

	}
}