package net.nocturne.game.player.dialogues.impl;

import net.nocturne.game.player.dialogues.Dialogue;
import net.nocturne.utils.ShopsHandler;

public class Hickton extends Dialogue {

	private int npcId = 575;

	@Override
	public void start() {
		sendNPCDialogue(npcId, NORMAL,
				"Welcome to Hicton's Archery Emporium. Do you want to see my wares?");
	}

	@Override
	public void run(int interfaceId, int componentId, int slotId) {
		if (stage == -1) {
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Yes, please.",
					"No I prefer to bash things close up.");
			stage = 0;
		} else if (stage == 0) {
			if (componentId == OPTION_1) {
				ShopsHandler.openShop(player, 109);
				end();
			} else {
				sendPlayerDialogue(NORMAL,
						"No I prefer to bash things close up.");
				stage = 1;
			}
		} else if (stage == 1) {
			end();
		}
	}

	@Override
	public void finish() {

	}
}
