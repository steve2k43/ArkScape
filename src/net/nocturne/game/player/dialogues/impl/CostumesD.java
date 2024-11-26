package net.nocturne.game.player.dialogues.impl;

import net.nocturne.game.player.dialogues.Dialogue;

public class CostumesD extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (int) parameters[0];
		sendNPCDialogue(npcId, DRUNK,
				"I'm no longer selling costumes. I am now rich!!! Sorry, use your interface.");
	}

	int option = 0;

	@Override
	public void run(int interfaceId, int componentId, int slotId) {
		end();

	}

	@Override
	public void finish() {

	}
}
