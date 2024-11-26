package net.nocturne.game.player.content;

import net.nocturne.game.player.Player;
import net.nocturne.utils.ScrollMessage;

public class CharacterSettings {

	public static void handleButtons(Player player, int componentId) {
		switch (componentId) {
		case 2:
			StaffList.send(player);
			break;
		case 4:
			//player.getDialogueManager().startDialogue("LadyIthell", 19560);
			ScrollMessage.displayScrollMessageUpdate(player, "serverinfo");
			break;
		case 6:
			//player.getDialogueManager().startDialogue("Ariane", 23009, null);
			player.getDialogueManager().startDialogue("OpenURLPrompt", "website");
			break;
		case 8:
			player.getDialogueManager().startDialogue("OpenURLPrompt", "vote");
			break;
		case 10:
			//player.getDialogueManager().startDialogue("OpenURLPrompt", "");
			ScrollMessage.displayScrollMessageUpdate(player, "donatorinfo");
			break;
		case 12:
			//player.getDialogueManager().startDialogue("TicketDialogue");
			player.getPackets().sendHideIComponent(1082, player.getinternumber(), true);
			player.getPackets().sendGameMessage("Hiding Component: "+player.getinternumber());
			player.setinternumber(player.getinternumber()+1);
			break;
		case 14:
			//ScrollMessage.displayScrollMessageUpdate(player, "donatorinfo");
			player.getDialogueManager().startDialogue("BugDialogue");
			break;
		default:
			player.getPackets().sendGameMessage(
					"This button does not have an action set.");
			break;
		}
	}
}