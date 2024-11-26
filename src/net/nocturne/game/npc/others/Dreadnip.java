package net.nocturne.game.npc.others;

import net.nocturne.game.WorldTile;
import net.nocturne.game.npc.NPC;
import net.nocturne.game.player.Player;

@SuppressWarnings("serial")
public class Dreadnip extends NPC {

	private static final String[] DREADNIP_MESSAGES = {
			"Your dreadnip gave up as you were too far away.",
			"Your dreadnip served its purpose and fled." };

	private Player target;
	private int ticks;

	public Dreadnip(Player target, WorldTile tile, int mapAreaNameHash,
			boolean canBeAttackFromOutOfArea) {
		super(14416, tile, mapAreaNameHash, canBeAttackFromOutOfArea, true);
		this.target = target;
		setForceFollowClose(true);
	}

	@Override
	public void processNPC() {
		super.processNPC();
		ticks++;
		boolean isDistanced = !withinDistance(target, 10);
		if (target.hasFinished() || isDistanced
				|| target.getAttackedBy() == null
				|| target.getAttackedBy().isDead() || ticks >= 33) {
			target.getPackets().sendGameMessage(
					DREADNIP_MESSAGES[isDistanced ? 0 : 1]);
			finish();
		}
	}

	public Player getOwner() {
		return target;
	}

	public int getTicks() {
		return ticks;
	}
}
