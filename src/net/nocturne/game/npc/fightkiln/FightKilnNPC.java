package net.nocturne.game.npc.fightkiln;

import java.util.ArrayList;
import java.util.List;

import net.nocturne.game.*;
import net.nocturne.game.npc.NPC;
import net.nocturne.game.npc.combat.NPCCombatDefinitions;
import net.nocturne.game.player.Player;
import net.nocturne.game.player.content.activities.minigames.FightKiln;
import net.nocturne.game.tasks.WorldTask;
import net.nocturne.game.tasks.WorldTasksManager;

@SuppressWarnings("serial")
public class FightKilnNPC extends NPC {

	private FightKiln Controller;

	public FightKilnNPC(int id, WorldTile tile, FightKiln Controller) {
		super(id, tile, -1, true, true);
		this.Controller = Controller;
	}

	private int getDeathGfx() {
		switch (getId()) {
		case 15201:
			return 2926;
		case 15202:
			return 2927;
		case 15203:
			return 2957;
		case 15213:
			return 2928;
		case 15214:
			return 2928;
		case 15204:
			return 2928;
		case 15205:
			return 2959;
		case 15206:
			return 2929;
		case 15207:
			return 2929;
		case 15208:
			return 2973;
		case 15211:
			return 2876;
		case 15212:
			return 2876;
		default:
			return 2926;
		}
	}

	@Override
	public void sendDeath(Entity source) {
		final NPCCombatDefinitions defs = getCombatDefinitions();
		resetWalkSteps();
		getCombat().removeTarget();
		setNextAnimation(null);
		Controller.checkCrystal();
		WorldTasksManager.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0) {
					setNextAnimation(new Animation(defs.getDeathEmote()));
					setNextGraphics(new Graphics(getDeathGfx()));
				} else if (loop >= defs.getDeathDelay()) {
					reset();
					finish();
					Controller.removeNPC();
					stop();
				}
				loop++;
			}
		}, 0, 1);
	}

	@Override
	public ArrayList<Entity> getPossibleTargets() {
		ArrayList<Entity> possibleTarget = new ArrayList<Entity>(1);
		List<Integer> playerIndexes = World.getRegion(getRegionId())
				.getPlayerIndexes();
		if (playerIndexes != null) {
			for (int npcIndex : playerIndexes) {
				Player player = World.getPlayers().get(npcIndex);
				if (player == null || player.isDead() || player.hasFinished()
						|| !player.isRunning()
						|| player.getAppearence().isHidden())
					continue;
				possibleTarget.add(player);
			}
		}
		return possibleTarget;
	}

	@Override
	public double getMagePrayerMultiplier() {
		return 0.1;
	}

	@Override
	public double getRangePrayerMultiplier() {
		return 0.1;
	}

	@Override
	public double getMeleePrayerMultiplier() {
		return 0.1;
	}

}