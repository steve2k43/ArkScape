package net.nocturne.game.player.dialogues.impl;

import net.nocturne.game.player.dialogues.Dialogue;
import net.nocturne.utils.ShopsHandler;

public class IronmanShops extends Dialogue {

	private int npcId = 945;

	@Override
	public void start() {
		sendNPCDialogue(
				npcId,
				HAPPY,
				"Hello, can i help?");
	}


	@Override
	public void run(int interfaceId, int componentId, int slotId) {
		if (stage == -1) {
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE,
					"What is there to do around here?",
					"I'm alright thank you.");
			stage = 0;
		} else if (stage == 0) {
			if (componentId == OPTION_1) {
				sendNPCDialogue(npcId, NORMAL, "I don't know runescape, so do what the fuck you want.");
				stage = 1;
			} else {
				sendPlayerDialogue(NORMAL, "I don't need any help thank you.");
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
