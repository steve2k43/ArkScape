package net.nocturne.game.player.dialogues.impl;

import net.nocturne.game.player.dialogues.Dialogue;
import net.nocturne.utils.ShopsHandler;

public class Vannesa extends Dialogue {

	private int npcId = 2305;

	@Override
	public void start() {
		sendNPCDialogue(npcId, NORMAL, "Hello. How can I help you?");
	}

	@Override
	public void run(int interfaceId, int componentId, int slotId) {
		if (stage == -1) {
			this.sendOptionsDialogue(DEFAULT_OPTIONS_TITLE,
					"What are you selling?",
					"Can you give me any Farming advice?",
					"I'm okay, thank you.");
			stage = 0;
		} else if (stage == 0) {
			if (componentId == OPTION_1) {
				ShopsHandler.openShop(player, 188);
				end();
			} else if (componentId == OPTION_2) {
				sendPlayerDialogue(NORMAL,
						"Can you give me any Farming advice?");
				stage = 2;
			} else {
				sendPlayerDialogue(NORMAL, "I'm okay, thank you.");
				stage = 1;
			}
		} else if (stage == 1) {
			end();
		} else if (stage == 2) {
			sendNPCDialogue(npcId, NORMAL, "Yes - Ask a gardener.");
			stage = 1;
		}
	}

	@Override
	public void finish() {

	}
}
