package net.nocturne.game.player.actions.skills.divination;

import net.nocturne.game.Animation;
import net.nocturne.game.Entity;
import net.nocturne.game.WorldTile;
import net.nocturne.game.npc.NPC;
import net.nocturne.game.player.Player;
import net.nocturne.game.tasks.WorldTask;
import net.nocturne.game.tasks.WorldTasksManager;
import net.nocturne.utils.Utils;

/**
 * 
 * @author Trenton
 * 
 */
@SuppressWarnings("serial")
public class Wisp extends NPC {

	private int life;
	private WispInfo info;
	private boolean isSpring;
	private boolean usedUp;

	public Wisp(int id, WorldTile tile) {
		super(id, tile);
		this.info = WispInfo.forNpcId(id);
		isSpring = false;
	}

	@Override
	public void spawn() {
		super.spawn();
		setUsedUp(false);
		setSpring(false);
	}

	@Override
	public void sendDeath(Entity source) {
		resetWalkSteps();
		WorldTasksManager.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0) {
					setNextAnimation(new Animation(21203));
				} else if (loop >= 1) {
					setUsedUp(true);
					setNextNPCTransformation(info.getNpcId());
					setRespawnTask();
					stop();
				}
				loop++;
			}
		}, 0, 1);
	}

	@Override
	public void processNPC() {
		super.processNPC();
		if (isSpring()) {
			if (life > 0)
				life--;
			if (life <= 0 && !isDead()) {
				setHitpoints(0);
				sendDeath(this);
			}
		}
	}

	public void harvest(Player player) {
		if (!DivinationHarvest.checkAll(player, info))
			return;
		if (!isSpring() && info != WispInfo.ANCESTRAL
				&& info != WispInfo.NEGATIVE && info != WispInfo.POSITIVE) {
			setNextNPCTransformation(info.getSpringNpcId());
			life = Utils.random(18, 60);
			setLocked(true);
			setSpring(true);
		}
		player.getActionManager().setAction(
				new DivinationHarvest(player, new Object[] { this, info }));
	}

	public boolean isSpring() {
		return this.isSpring;
	}

	public int getLife() {
		return life;
	}

	public void setLife(int life) {
		this.life = life;
	}

	public void setSpring(boolean isSpring) {
		this.isSpring = isSpring;
	}

	public boolean isUsedUp() {
		return usedUp;
	}

	public void setUsedUp(boolean usedUp) {
		this.usedUp = usedUp;
	}
}