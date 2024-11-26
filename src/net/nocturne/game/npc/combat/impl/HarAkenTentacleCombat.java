package net.nocturne.game.npc.combat.impl;

import net.nocturne.game.Animation;
import net.nocturne.game.Entity;
import net.nocturne.game.Projectile;
import net.nocturne.game.World;
import net.nocturne.game.npc.NPC;
import net.nocturne.game.npc.combat.CombatScript;
import net.nocturne.game.npc.combat.NPCCombatDefinitions;
import net.nocturne.utils.Utils;

public class HarAkenTentacleCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 15209, 15210 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		int attackStyle = !Utils.isOnRange(npc.getX(), npc.getY(),
				npc.getSize(), target.getX(), target.getY(), target.getSize(),
				0) ? 1 : Utils.random(2);
		switch (attackStyle) {
		case 0:
			npc.setNextAnimation(new Animation(defs.getAttackEmote()));
			delayHit(
					npc,
					0,
					target,
					getMeleeHit(npc,
							getMaxHit(npc, NPCCombatDefinitions.MELEE, target)));
			break;
		case 1:
			npc.setNextAnimation(new Animation(npc.getId() == 15209 ? 16253
					: 16242));
			Projectile projectile = World.sendProjectileNew(npc, target,
					npc.getId() == 15209 ? 3004 : 2922, 140, 35, 35, 5, 16, 0);
			int endTime = Utils.projectileTimeToCycles(projectile.getEndTime()) - 1;
			if (npc.getId() == 15209)
				delayHit(
						npc,
						endTime,
						target,
						getRangedHit(
								npc,
								getMaxHit(npc, NPCCombatDefinitions.RANGE,
										target)));
			else
				delayHit(
						npc,
						endTime,
						target,
						getMagicHit(
								npc,
								getMaxHit(npc, NPCCombatDefinitions.MAGE,
										target)));
			break;
		}
		return npc.getAttackSpeed();
	}
}
