package net.nocturne.game.player.dialogues.impl;

import net.nocturne.game.item.Item;
import net.nocturne.game.player.dialogues.Dialogue;

public class GamfriedShield extends Dialogue {

	int npcId = 4287;

	@Override
	public void start() {
		sendPlayerDialogue(NORMAL, "May I have a shield please?");
	}

	@Override
	public void run(int interfaceId, int componentId, int slotId) {
		stage++;
		if (stage == 0)
			sendNPCDialogue(npcId, NORMAL, "Of course!");
		else if (stage == 1) {
			sendEntityDialogue(IS_ITEM, 8856, -1,
					"The dwarf hands you a large shield.");
			player.getInventory().addItem(new Item(8856, 1));
		} else if (stage == 2)
			end();
	}

	@Override
	public void finish() {
	}
}
