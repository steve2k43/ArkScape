package net.nocturne.game.npc.others;

import net.nocturne.game.Animation;
import net.nocturne.game.Entity;
import net.nocturne.game.Graphics;
import net.nocturne.game.WorldTile;
import net.nocturne.game.npc.NPC;
import net.nocturne.game.npc.combat.NPCCombatDefinitions;
import net.nocturne.game.tasks.WorldTask;
import net.nocturne.game.tasks.WorldTasksManager;

@SuppressWarnings("serial")
public class BlackDemon extends NPC {

	public BlackDemon(int id, WorldTile tile, int mapAreaNameHash,
			boolean canBeAttackFromOutOfArea, boolean spawned) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
	}

	@Override
	public void sendDeath(final Entity source) {
		final NPCCombatDefinitions defs = getCombatDefinitions();
		resetWalkSteps();
		getCombat().removeTarget();
		setNextAnimation(null);
		if (!isDead())
			setHitpoints(0);
		setNextGraphics(new Graphics(3778, 20, 0));
		final int deathDelay = defs.getDeathDelay();
		WorldTasksManager.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0) {
					setNextAnimation(new Animation(defs.getDeathEmote()));
				} else if (loop >= deathDelay) {
					giveXP();
					drop();
					reset();
					setLocation(getRespawnTile());
					finish();
					if (!isSpawned())
						setRespawnTask();
					stop();
				}
				loop++;
			}
		}, 0, 1);
	}
}
