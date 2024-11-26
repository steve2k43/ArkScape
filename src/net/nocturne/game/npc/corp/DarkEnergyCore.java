package net.nocturne.game.npc.corp;

import java.util.ArrayList;

import net.nocturne.game.*;
import net.nocturne.game.EffectsManager.EffectType;
import net.nocturne.game.Hit.HitLook;
import net.nocturne.game.npc.NPC;
import net.nocturne.game.player.Player;
import net.nocturne.utils.Utils;

@SuppressWarnings("serial")
public class DarkEnergyCore extends NPC {

	private CorporealBeast beast;
	private Entity target;
	private WorldTile tile;

	DarkEnergyCore(CorporealBeast beast) {
		super(8127, beast.getMiddleWorldTile(), -1, true, true);
		this.beast = beast;
		tile = new WorldTile(beast);
		getTile(beast);
		setNPCHidden(true);
		delay = Utils.projectileTimeToCycles(World.sendProjectileNew(this,
				tile, 1828, 40, 0, 0, 0.7, 20, 0).getEndTime()) - 1;
		setNextFaceWorldTile(tile);
	}

	private void getTile(CorporealBeast beast) {

		for (int i = 0; i < 10; i++) {
			WorldTile t = new WorldTile(beast, 10);
			if (World.isTileFree(t.getPlane(), t.getX(), t.getY(), 1)) {
				tile = t;
				break;
			}
		}
	}

	private int delay;

	@Override
	public void processNPC() {
		if (isDead() || hasFinished())
			return;
		if (delay > 0) {
			delay--;
			return;
		}
		processTargetChange();
	}

	private void processTargetChange() {
		if (getId() == 8127) {
			if (delay == -1) {
				setNPCHidden(true);
				delay = Utils.projectileTimeToCycles(World.sendProjectileNew(
						this, tile, 1828, 30, 0, 0, 0.7, 20, 0).getEndTime()) - 1;
				return;
			}
			if (target == null || !withinDistance(target, 1)) {
				target = getNextTarget();
				if (target == null)
					return;
				setNextAnimation(new Animation(10393));
				tile = new WorldTile(target, 1);
				setNextFaceWorldTile(tile);
				applyCoreEffect();
				delay = -1;
				return;
			}
		} else {
			setNPCHidden(false);
			setNextWorldTile(tile);
			delay = 3;
		}
	}

	private void setNPCHidden(boolean hidden) {
		if (getId() == (hidden ? 1957 : 8127))
			return;
		setNextNPCTransformation(hidden ? 1957 : 8127);
	}

	private Entity getNextTarget() {
		ArrayList<Entity> possibleTarget = beast.getPossibleTargets();
		if (possibleTarget.isEmpty()) {
			finish();
			beast.removeDarkEnergyCore();
			return null;
		}
		return possibleTarget.get(Utils.random(possibleTarget.size()));
	}

	private void applyCoreEffect() {
		int damage = Utils.random(500) + 150;
		target.applyHit(new Hit(beast, damage, HitLook.MAGIC_DAMAGE));
		beast.heal(damage);
		delay = getEffectsManager().hasActiveEffect(EffectType.POISON) ? 10 : 3;
		if (target instanceof Player) {
			Player player = (Player) target;
			player.getPackets()
					.sendGameMessage(
							"The dark core creature steals some life from you for its master.",
							true);
		}
	}

	@Override
	public void sendDeath(Entity source) {
		super.sendDeath(source);
		beast.removeDarkEnergyCore();
	}
}
