package net.nocturne.game.player.dialogues.impl.skills.fletching;

import net.nocturne.cache.loaders.ItemDefinitions;
import net.nocturne.game.player.actions.skills.fletching.LogsFletching;
import net.nocturne.game.player.actions.skills.fletching.LogsFletching.FletchData;
import net.nocturne.game.player.content.SkillsDialogue;
import net.nocturne.game.player.content.SkillsDialogue.SkillDialogueResult;
import net.nocturne.game.player.dialogues.Dialogue;

public class LogsFletchingD extends Dialogue {

	@Override
	public void start() {
		FletchData fletch = (FletchData) parameters[0];
		SkillsDialogue.sendSkillDialogueByProduce(player,
				fletch.getProducedBow());
	}

	@Override
	public void run(int interfaceId, int componentId, int slotId) {
		SkillDialogueResult result = SkillsDialogue.getResult(player);
		int quant =	ItemDefinitions.getItemDefinitions(result.getProduce()).getCSOpcode(2653);
		int id = result.getProduce();
		if (id>=34672 && id<=34680)
			id=52;
		FletchData fletch = FletchData.getBarByProduce(id,quant);
		System.out.println(fletch);
		System.out.println(id);
		System.out.println(quant);
		player.getActionManager().setAction(
				new LogsFletching(fletch, id, result
						.getQuantity()));
		player.getTemporaryAttributtes().remove("quantity");
		player.getTemporaryAttributtes().put("quantity", result.getQuantity());
		System.out.println("TRIED TO FLETCH: "+result.getQuantity());
		end();
	}

	@Override
	public void finish() {
	}
}