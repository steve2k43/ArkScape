package net.nocturne.game.player.dialogues.impl;

import net.nocturne.game.player.content.TicketSystem;
import net.nocturne.game.player.content.TicketSystem.TicketEntry;
import net.nocturne.game.player.dialogues.Dialogue;

public class BugDialogue extends Dialogue {

	private String reason;
	private String BUG;

	@Override
	public void start() {
		sendDialogue("Welcome to the Bug Reporting System. Abuse / Spam of this system will result in jailtime, or a ban.");
	}

	@Override
	public void run(int interfaceId, int componentId, int slotId) {
		switch (stage) {
			case -2:
				end();
				break;
			case -1:
				stage = 0;
				sendOptionsDialogue("Please select an option.",
						"Submit Bug", "Cancel");
				break;
			case 0:

				switch (componentId) {
					case OPTION_1:
						stage = -2;
						player.getPackets().sendInputLongTextScript("Please write a description of the bug.");
						player.getTemporaryAttributtes().put("bug", Boolean.TRUE);
						System.out.println("you submitted bug" +player.getTemporaryAttributtes().get("bug"));
						end();
						break;

					case OPTION_2:
						stage = -2;
						end();
						break;

				}
				break;
		}
	}
	@Override
	public void finish() {

	}
}
