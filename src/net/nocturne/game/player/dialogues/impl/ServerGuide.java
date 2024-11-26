package net.nocturne.game.player.dialogues.impl;

import net.nocturne.Settings;
import net.nocturne.game.World;
import net.nocturne.game.item.ItemIdentifiers;
import net.nocturne.game.player.Player;
import net.nocturne.game.player.dialogues.Dialogue;

public class ServerGuide extends Dialogue {

	private boolean introlong = false;
	private int difficulty = 0;

	@Override
	public void start() {
		stage = 1;
		player.lock();
		sendNPCDialogue(945, HAPPY, "Welcome to <col=55728b>"
				+ Settings.SERVER_NAME + "</col>, " + player.getDisplayName()
				+ ".");
	}

	@Override
	public void run(int interfaceId, int componentId, int slotId) {
		switch (stage) {
			case 0:
				end();
				player.unlock();
				player.getAppearence().generateAppearenceData();
				if (player.isAnIronMan()) {
					World.sendWorldMessage(
							"<col=43dde5>Welcome, " + player.getIronmanTitle(false)
									+ "<col=43dde5>" + player.getDisplayName()
									+ " to the world of ArkScape", false);
				} else {
					World.sendWorldMessage("Welcome, " + player.getDisplayName()
							+ " to the world of ArkScape.", false);
				}
				break;
			case 1:
				sendOptionsDialogue(
						DEFAULT_OPTIONS_TITLE,
						"Give me the basics of <col=55728b>" + Settings.SERVER_NAME,
						"I've never played <col=55728b>" + Settings.SERVER_NAME
								+ " before, please explain it to me!");
				stage = 2;
				break;
			case 2:
				switch (componentId) {
					case OPTION_1:
						sendNPCDialogue(945, HAPPY,
								"Eager to get on with it are we?  Very well.");
						stage = 19;
						break;
					case OPTION_2:
						sendNPCDialogue(945, HAPPY,
								"Oh a noob? Well you're just as welcome! Let's get started.");
						introlong = true;
						stage = 3;
						break;
				}
				break;
			case 3:
				sendNPCDialogue(
						945,
						NORMAL,
						"Welcome to the world of "+Settings.SERVER_NAME+"! a wonderful place, There are some items that can be bought from the shops, located around here. But most useful items need to be obtained.");
				stage = 4;
				break;
			case 4:
				sendNPCDialogue(
						945,
						NORMAL,
						"Things are still in the early stage here, so if you come across a bug please let us know! We are trying to make the experience of "+Settings.SERVER_NAME+" as enjoyable as possible.");
				if (introlong == true) {
					stage = 19; // 6
				} else {
					stage = 19;
				}
				break;
			case 5:
				sendNPCDialogue(945, NORMAL,
						"Our XP rates for Normal players are <col=FFFF000>x"
								+ Settings.XP_RATE + "</col>.");
				stage = 51;
				break;
			case 51:
				sendNPCDialogue(
						945,
						HAPPY,
						"At Nocturne, we're commited to giving you the best <col=FFFF000>Experience</col>., to achieve this, we are constantly improving Nocturne");
				stage = 6;
				break;
			case 6:
				sendNPCDialogue(
						945,
						HAPPY,
						"Any questions? Don't be shy, just ask in ::yell or '/' for friends chat, our more seasoned players & Staff will be more than happy to assist you.");
				stage = 7;
				break;
			case 7:
				sendNPCDialogue(945, QUESTIONS,
						"Now, a very serious question, would you like to look into Ironman mode?");
				stage = 8;
				break;
			case 8:
				sendOptionsDialogue(
						"Would you like to be an Ironman?",
						"No, I just want the regular Experience",
						"I fancy the challenge of being an <img=11><col=5F6169>Ironman</col>!",
						"I came here for the Grind! <img=13><col=A30920>Hardcore Ironman</col> me or DIE!");
				stage = 9;
				break;
			case 9:
				switch (componentId) {
					case OPTION_1:
						sendNPCDialogue(945, LAUGHING, "That's fine, have fun!");
						stage = 19;
						break;
					case OPTION_2:
						sendNPCDialogue(
								945,
								HAPPY,
								"Daring Choice! I admire that, I must warn you its not for the feint Hearted. don't expect to max anytime soon!");
						stage = 10;
						break;
					case OPTION_3:
						sendNPCDialogue(945, SCARED,
								"Clearly you're not one to mess with! Very Well.");
						stage = 16;
						break;
				}
				break;
			case 10:
				sendNPCDialogue(
						945,
						NORMAL,
						"You will not be able to use the grand exchange, trade, dungeoneer with a party, enter other players' house, and some more. You are on your own!");
				stage = 11;
				break;
			case 11:
				sendNPCDialogue(
						945,
						NORMAL,
						"Basically you will be playing as a single player. Do you still want to be an <img=11><col=5F6169>Ironman</col>?");
				stage = 12;
				break;
			case 12:
				sendOptionsDialogue("Would you like to be an Ironman?",
						"No, I want to play casually!",
						"I fancy the Challenge! <img=11><col=5F6169>Ironman</col> me!.");
				stage = 13;
				break;
			case 13:
				switch (componentId) {
					case OPTION_1:
						sendNPCDialogue(945, HAPPY, "That's fine, have fun!");
						stage = 19;
						break;
					case OPTION_2:
						sendNPCDialogue(
								945,
								HAPPY,
								"I like your attitude, you'll go far!, you are now an <img=11><col=5F6169>Ironman</col>. Have fun!");
						player.setIronman(true);
						player.setHardcoreIronMan(false);
						stage = 19;
						break;
				}
				break;
			case 15:
				sendNPCDialogue(
						945,
						NORMAL,
						"You will not be able to use the grand exchange, trade, dungeoneer with a party, enter other players' house, and some more.");
				stage = 16;
				break;
			case 16:
				sendNPCDialogue(
						945,
						NORMAL,
						"Basically you will be playing as a single player. Do you still want to be a <img=13><col=A30920>Hardcore Ironman</col>?");
				stage = 17;
				break;
			case 17:
				sendOptionsDialogue("Would you like to be an Ironman?",
						"....actually now that you mention it.",
						"*Draw Sword Demanding HC Ironman Status*");
				stage = 18;
				break;
			case 18:
				switch (componentId) {
					case OPTION_1:
						sendNPCDialogue(945, HAPPY, "That's fine, have fun!");
					case OPTION_2:
						sendNPCDialogue(
								945,
								SCARED,
								"The Gods help your Enemies!, you are now a <img=13><col=A30920>Hardcore Ironman</col>, Have fun!");
						player.setHardcoreIronMan(true);
						player.setIronman(false);
						break;
				}
				stage = 19;
				break;
			case 19:
				sendOptionsDialogue(
						"Please choose a difficulty",
						"EASY MODE - 50 x EXP (x1 drop chance)",
						"NORMAL MODE - 10 x EXP (x1.1 drop chance)", "EXTREME MODE - 3 x EXP (x1.3 drop chance)");
				stage = 20;
				break;
			case 20:
				switch (componentId) {

					case OPTION_1:
						difficulty = 1;
						stage = 30;
						break;
					case OPTION_2:
						difficulty = 2;
						stage = 30;
						break;
					case OPTION_3:
						difficulty = 3;
						stage = 30;
						break;
				}
			case 30:
				System.out.println("DIFFICULTY SET TO: "+difficulty);
				player.setDifficulty(difficulty);
				player.setTitleColor("green");
				sendNPCDialogue(945, HAPPY,
						"You will find some useful tools i have added to your toolbelt. You can use these to help gather supplies around this area.");
				stage = 31;
				break;
			case 31:
				sendNPCDialogue(945, HAPPY,
						"I suggest heading to the mine to make yourself some weapons and armour, or alternatively you can fletch a bow and arrow using the local trees!");
				stage = 0;
				break;
		}
	}

	@Override
	public void finish() {

	}
}