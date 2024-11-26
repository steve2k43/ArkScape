package net.nocturne.game.npc.combat.impl;

import net.nocturne.game.*;
import net.nocturne.game.npc.NPC;
import net.nocturne.game.npc.combat.CombatScript;
import net.nocturne.game.npc.combat.NPCCombatDefinitions;
import net.nocturne.utils.Utils;

public class InfernalMage extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Infernal Mage" };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		NPCCombatDefinitions defs = npc.getCombatDefinitions();
		npc.setNextAnimation(new Animation(defs.getAttackEmote()));
		Projectile projectile = World.sendProjectileNew(npc, target,
				defs.getAttackProjectile(), 30, 30, 50, 2, Utils.random(5), 5);
		delayHit(
				npc,
				Utils.projectileTimeToCycles(projectile.getEndTime()) - 1,
				target,
				getMagicHit(npc,
						getMaxHit(npc, NPCCombatDefinitions.MAGE, target)));
		target.setNextGraphics(new Graphics(2739, projectile.getEndTime(), 100));
		return npc.getAttackSpeed();
	}
}
