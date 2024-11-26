package net.nocturne.game.player.dialogues.impl;

import net.nocturne.game.player.dialogues.Dialogue;

public class Alice extends Dialogue {

	/**
	 * @author: miles M
	 */

	@Override
	public void start() {
		stage = 1;
		sendNPCDialogue(2307, MOCK, "Ey cutey ;)");
	}

	@Override
	public void run(int interfaceId, int componentId, int slotId) {
		switch (stage) {
		case -1:
			end();
			break;
		case 1:
			stage = 2;
			sendNPCDialogue(2307, MOCK, "Ya know Flavius is gay HAHAHA?");
			break;
		case 2:
			stage = -1;
			sendNPCDialogue(2307, MOCK, "And miles... What a handsome guy <3.");
			break;
		}
	}

	@Override
	public void finish() {

	}
}