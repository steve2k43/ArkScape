package net.nocturne.game.npc.dungeonnering;

import net.nocturne.game.Entity;
import net.nocturne.game.Hit;
import net.nocturne.game.World;
import net.nocturne.game.WorldTile;
import net.nocturne.game.Hit.HitLook;
import net.nocturne.game.npc.NPC;
import net.nocturne.game.player.actions.skills.dungeoneering.DungeonManager;
import net.nocturne.game.player.actions.skills.dungeoneering.RoomReference;

@SuppressWarnings("serial")
public final class AsteaFrostweb extends DungeonBoss {

	private int meleeNPCId;
	private int switchPrayersDelay;
	private int spawnedSpiders;
	private NPC[] spiders;

	public AsteaFrostweb(int id, WorldTile tile, DungeonManager manager,RoomReference reference) {
		super(id, tile, manager, reference);
		spiders = new NPC[6];
		meleeNPCId = id;
		resetSwitchPrayersDelay();
	}

	private void resetSwitchPrayersDelay() {
		switchPrayersDelay = 35; // 35 sec delay
	}

	private void switchPrayers() {
		setNextNPCTransformation(getId() == meleeNPCId + 2 ? meleeNPCId	: getId() + 1);
	}

	@Override
	public void processNPC() {
		super.processNPC();
		if (isDead())
			return;
		if (switchPrayersDelay > 0)
			switchPrayersDelay--;
		else {
			switchPrayers();
			resetSwitchPrayersDelay();
		}
	}

	@Override
	public void sendDeath(Entity source) {
		super.sendDeath(source);
		for (NPC minion : spiders) {
			if (minion == null)
				continue;
			minion.sendDeath(this);
		}
	}

	public void spawnSpider() {
		if (spawnedSpiders >= spiders.length)
			return;

		for (int tryI = 0; tryI < 10; tryI++) {
			WorldTile tile = new WorldTile(this, 2);
			if (World.isTileFree(0, tile.getX(), tile.getY(), 1)) {
				NPC spider = spiders[spawnedSpiders++] = new NPC(64, tile, -1,
						true, true);
				spider.setForceAgressive(true);
				break;
			}
		}
	}

	@Override
	public void handleIngoingHit(final Hit hit) {
		super.handleIngoingHit(hit);
		if (getId() == meleeNPCId) {
			if (hit.getLook() == HitLook.MELEE_DAMAGE)
				hit.setDamage(0);
		} else if (getId() == meleeNPCId + 1) {
			if (hit.getLook() == HitLook.MAGIC_DAMAGE)
				hit.setDamage(0);
		} else if (hit.getLook() == HitLook.RANGE_DAMAGE)
			hit.setDamage(0);
	}

}
