package net.nocturne.game.player.cutscenes;

import java.util.ArrayList;

import net.nocturne.game.Animation;
import net.nocturne.game.Graphics;
import net.nocturne.game.player.Player;
import net.nocturne.game.player.cutscenes.actions.*;

class HomeCutScene extends Cutscene {

	@Override
	public boolean hiddenMinimap() {
		return false;
	}

	@Override
	public CutsceneAction[] getActions(final Player player) {
		ArrayList<CutsceneAction> actionsList = new ArrayList<>();
		actionsList.add(new ConstructMapAction(603, 488, 3, 3));
		actionsList.add(new MovePlayerAction(10, 0, 1, 127, -1));
		actionsList.add(new LookCameraAction(10, 8, 1000, -1));
		actionsList.add(new PosCameraAction(10, 0, 2000, 3));
		int DRAGONKK = 1;
		actionsList.add(new CreateNPCAction(DRAGONKK, 1302, 10, 6, 1,
				"Dragonkk", 13337, -1));
		actionsList.add(new NPCFaceTileAction(DRAGONKK, 10, 5, -1));
		actionsList.add(new NPCGraphicAction(DRAGONKK, new Graphics(184), 2));
		actionsList.add(new NPCForceTalkAction(DRAGONKK, "....", 3));
		actionsList
				.add(new NPCForceTalkAction(DRAGONKK, "Dragonkk!@!@!@!", -1));
		actionsList
				.add(new NPCAnimationAction(DRAGONKK, new Animation(2108), 3));
		actionsList.add(new NPCFaceTileAction(DRAGONKK, 9, 6, -1));
		actionsList.add(new MovePlayerAction(9, 6, 1, 127, -1));
		actionsList.add(new PlayerFaceTileAction(9, 5, -1));
		actionsList.add(new PlayerAnimationAction(new Animation(2111), -1));
		actionsList.add(new PlayerGraphicAction(new Graphics(184), 1));
		actionsList.add(new DestroyCachedObjectAction(DRAGONKK, 0));
		actionsList.add(new PlayerFaceTileAction(9, 7, 1));
		actionsList.add(new PlayerFaceTileAction(8, 6, 1));
		actionsList.add(new PlayerFaceTileAction(10, 6, 1));
		actionsList.add(new PlayerForceTalkAction("Huh?", 1));
		actionsList.add(new PlayerAnimationAction(new Animation(857), -1));
		actionsList.add(new PlayerForceTalkAction("Where am I?", 3));
		int GUARD1 = 2;
		actionsList.add(new CreateNPCAction(GUARD1, 296, 3, 7, 1, -1));
		int GUARD2 = 3;
		actionsList.add(new CreateNPCAction(GUARD2, 298, 3, 5, 1, -1));
		actionsList.add(new MoveNPCAction(GUARD1, 8, 7, false, 0));
		actionsList.add(new MoveNPCAction(GUARD2, 8, 5, false, 2));
		actionsList.add(new NPCForceTalkAction(GUARD1,
				"You! What are you doing here?", -1));
		actionsList.add(new PlayerFaceTileAction(8, 6, 3));
		actionsList.add(new PlayerForceTalkAction("Idk... Walking??", 2));
		actionsList.add(new NPCForceTalkAction(GUARD1, "You must have slipped",
				1));
		actionsList.add(new NPCForceTalkAction(GUARD1,
				"and hit your head on that tree.", 1));
		actionsList.add(new NPCForceTalkAction(GUARD2, "Does it matter?", 1));
		actionsList.add(new NPCForceTalkAction(GUARD1,
				"Let's just take him to CorruptionX...", 2));
		actionsList.add(new MoveNPCAction(GUARD1, 15, 7, false, -1));
		actionsList.add(new MovePlayerAction(15, 6, false, -1));
		actionsList.add(new MoveNPCAction(GUARD2, 15, 5, false, 0));
		actionsList.add(new PlayerForceTalkAction("What\'s CorruptionX?", 0));
		actionsList.add(new NPCForceTalkAction(GUARD2, "Dammit...", 5));
		actionsList.add(new ConstructMapAction(392, 432, 8, 8));
		actionsList.add(new LookCameraAction(28, 30, 2000, -1));
		actionsList.add(new PosCameraAction(28, 15, 4000, -1));
		actionsList.add(new MovePlayerAction(28, 25, 0,
				Cutscene.TELE_MOVE_TYPE, -1));
		actionsList.add(new PlayerFaceTileAction(28, 26, -1));
		actionsList.add(new MoveNPCAction(GUARD1, 27, 25, 0,
				Cutscene.TELE_MOVE_TYPE, -1));
		actionsList.add(new NPCFaceTileAction(GUARD1, 27, 26, -1));
		actionsList.add(new MoveNPCAction(GUARD2, 29, 25, 0,
				Cutscene.TELE_MOVE_TYPE, -1));
		actionsList.add(new NPCFaceTileAction(GUARD2, 29, 26, 0));
		actionsList.add(new MoveNPCAction(GUARD1, 27, 31, false, -1));
		actionsList.add(new MovePlayerAction(28, 31, false, -1));
		actionsList.add(new MoveNPCAction(GUARD2, 29, 31, false, 6));
		actionsList.add(new NPCGraphicAction(GUARD1, new Graphics(184), -1));
		actionsList.add(new NPCGraphicAction(GUARD2, new Graphics(184), 0));
		actionsList.add(new DestroyCachedObjectAction(GUARD1, -1));
		actionsList.add(new DestroyCachedObjectAction(GUARD2, 2));
		actionsList.add(new PlayerForceTalkAction("Hummm. What now...", 3));
		actionsList
				.add(new CutsceneCodeAction(
						() -> player
								.getDialogueManager()
								.startDialogue("SimpleNPCMessage", 15158,
										"Welcome to Nocturne",
										"If you have any questions make sure to read the guide book in your inventory."),
						-1));
		return actionsList.toArray(new CutsceneAction[actionsList.size()]);
	}
}
