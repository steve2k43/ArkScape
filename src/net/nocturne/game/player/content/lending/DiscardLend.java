package net.nocturne.game.player.content.lending;

import net.nocturne.game.player.dialogues.Dialogue;

public class DiscardLend extends Dialogue {

	@Override
	public void start() {
		// TODO Auto-generated method stub

	}

	@Override
	public void run(int interfaceId, int componentId, int slotId)
			throws ClassNotFoundException {
		// TODO Auto-generated method stub

	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

	/*
	 * private Lend lend;
	 * 
	 * @Override public void start() { lend = (Lend) parameters[0];
	 * player.getInterfaceManager().sendDialogueInterface(1189);
	 * player.getPackets().sendItemOnIComponent(1189, 1, lend.getItem().getId(),
	 * 1); int hours = LendingManager.getHoursLeft(lend.getTime()); int minutes
	 * = LendingManager.getMinutesLeft(lend.getTime());
	 * player.getPackets().sendIComponentText( 1189, 4,
	 * "<col=00007f>~ Loan expires in " + (hours > 0 ? hours + " hour" + (hours
	 * > 1 ? "s " : "") : "") + "" + (minutes > 0 ? minutes + " minute" +
	 * (minutes > 1 ? "s" : "") : "") + " ~</col><br>" +
	 * "If you discard this item, it will disappear.<br>" +
	 * "You won't be able to pick it up again."); }
	 * 
	 * @Override public void run(int interfaceId, int componentId, int slotId) throws
	 * ClassNotFoundException, IOException { if (stage == -1) { if (componentId
	 * == 10) { sendOptionsDialogue("Really discard item?",
	 * "Yes, discard it. I won't need it again.", "No, I'll keep hold of it.");
	 * stage = 1; } } else if (stage == 1) { if (componentId == OPTION_1) {
	 * LendingManager.unLend(lend); end(); } else if (componentId == OPTION_2) {
	 * end(); } } }
	 * 
	 * @Override public void finish() { }
	 */
}