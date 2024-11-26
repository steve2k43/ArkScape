package net.nocturne.game.player.dialogues.impl;

import net.nocturne.game.npc.NPC;
import net.nocturne.game.player.content.activities.minigames.SorceressGarden;
import net.nocturne.game.player.dialogues.Dialogue;

public class SorceressGardenNPCs extends Dialogue {

	public NPC npc;

	@Override
	public void start() {
		npc = (NPC) parameters[0];
		sendPlayerDialogue(
				9827,
				((npc.getId() != 5532 && npc.getId() == 5563) ? "Hey kitty!"
						: "Hey apprentice, do you want to try out your teleport skills again?"));
	}

	@Override
	public void run(int interfaceId, int componentId, int slotId) {
		if (stage == -1) {
			stage = 0;
			if (npc.getId() == 5563) {
				sendNPCDialogue(npc.getId(), 9827, "Hiss!");
				finish();
				return;
			} else if (npc.getId() == 5532) {
				sendNPCDialogue(npc.getId(), 9827,
						"Okay, here goes - and remember, to return just drink from the fountain.");
			}
		} else if (stage == 0) {
			stage = 1;
			if (npc.getId() == 5532) {
				SorceressGarden.teleportToSorceressGardenNPC(npc, player);
			}
			end();
		}
	}

	@Override
	public void finish() {

	}
}