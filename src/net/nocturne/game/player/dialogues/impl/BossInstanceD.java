package net.nocturne.game.player.dialogues.impl;

import net.nocturne.game.TemporaryAtributtes.Key;
import net.nocturne.game.item.Item;
import net.nocturne.game.item.ItemIdentifiers;
import net.nocturne.game.map.bossInstance.BossInstance;
import net.nocturne.game.map.bossInstance.BossInstanceHandler;
import net.nocturne.game.map.bossInstance.BossInstanceHandler.Boss;
import net.nocturne.game.map.bossInstance.InstanceSettings;
import net.nocturne.game.player.Player;
import net.nocturne.game.player.dialogues.Dialogue;
import net.nocturne.utils.Utils;

public class BossInstanceD extends Dialogue {

	private static Boss boss;

	@Override
	public void start() {
		boss = (Boss) parameters[0];
		if (boss.hasPublicVersion())
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Enter an encounter.",
					"Start/Join custom encounter.");
		else
			sendCustomEncounter();
	}

	public static void handleButtons(Player player, int componentId) {
		switch (componentId) {
		case 53:
			player.setLastBossInstanceSettings(new InstanceSettings(boss));
			InstanceSettings settings = player.getLastBossInstanceSettings();
			if (settings == null)
				return;
			settings.setMaxPlayers(settings.getBoss().getMaxPlayers());
			settings.setMinCombat(1);
			settings.setSpawnSpeed(BossInstance.FASTEST);
			settings.setProtection(BossInstance.FFA);
			if (settings == null)
				return;
			if (boss == Boss.Evil_Chicken) {
				player.getPackets().sendGameMessage(
						"The Evil Chicken instance is under development.");
				return;
			}
			int initialCost = settings.getBoss().getInitialCost();
			if (player.getDonationManager().isHeroicDonator())
				initialCost *= 0.4;
			else if (player.getDonationManager().isDemonicDonator())
				initialCost *= 0.5;
			else if (player.getDonationManager().isAngelicDonator())
				initialCost *= 0.6;
			else if (player.getDonationManager().isDivineDonator())
				initialCost *= 0.7;
			else if (player.getDonationManager().isSupremeDonator())
				initialCost *= 0.8;
			else if (player.getDonationManager().isLegendaryDonator())
				initialCost *= 0.9;
			if (player.getInventory().getCoinsAmount() < initialCost) {
				player.getPackets().sendGameMessage(
						"You don't have enough coins to start this battle.");
				player.setLastBossInstanceSettings(null);
				return;
			}
			if (initialCost > 0 && boss != Boss.Rise_Of_The_Six)
				player.getInventory().removeItemMoneyPouch(
						new Item(995, initialCost));
			settings.setCreationTime(Utils.currentTimeMillis());
			BossInstanceHandler.createInstance(player, settings);
			break;
		case 145:
			player.getInterfaceManager().removeCentralInterface();
			player.getTemporaryAttributtes().put(Key.JOIN_BOSS_INSTANCE, boss);
			player.getPackets().sendInputNameScript(
					"Enter the name of a player in a battle you wish to join.");
			break;
		case 159:
			player.getInterfaceManager().removeCentralInterface();
			String key = player.getLastBossInstanceKey();
			if (key == null) {
				player.getPackets().sendGameMessage(
						"You do not have a battle to rejoin.");
				return;
			}
			if (BossInstanceHandler.findInstance(boss, key) == null) {

				if (key.equals(player.getUsername())
						&& player.getLastBossInstanceSettings() != null
						&& player.getLastBossInstanceSettings().getBoss() == boss
						&& player.getLastBossInstanceSettings()
								.hasTimeRemaining()) {
					// if the instance is null, and its my own player, use
					// the settings to recreate it
					BossInstanceHandler.createInstance(player,
							player.getLastBossInstanceSettings());
					return;
				}
				player.getPackets().sendGameMessage(
						"You do not have a battle to rejoin.");
				return;
			}
			BossInstanceHandler.joinInstance(player, boss, key, false);
			break;
		case 150:
			InstanceSettings.switchPractiseMode(player);
			break;
		}
	}

	private void sendCustomEncounter() {
		stage = 0;
		player.getInterfaceManager().sendCentralInterface(1591);
		player.getVarsManager().sendVar(5144, boss.getStructure());
		player.getPackets().sendExecuteScript(8178);

		player.getInterfaceManager().removeInterface(1188);
		// sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Start.", "Join.",
		// "Rejoin.");
	}

	@Override
	public void run(int interfaceId, int componentId, int slotId) {
		switch (stage) {
		case -1:
			switch (componentId) {
			case OPTION_1:
				BossInstanceHandler.joinInstance(player, boss, "", false);
				end();
				break;
			default:
				sendCustomEncounter();
				break;
			}
			break;
		case 0:
			switch (componentId) {
			case OPTION_1:
				stage = 1;
				// player.getInterfaceManager().sendCentralInterface(1591);
				if (boss == Boss.Rise_Of_The_Six
						&& !player.getInventory().containsItem(
								ItemIdentifiers.BARROWS_TOTEM, 1)) {
					player.getDialogueManager()
							.startDialogue("SimpleMessage",
									"You must have a Barrows Totem in order to start an encounter.");
					return;
				} else {
					player.setLastBossInstanceSettings(new InstanceSettings(
							boss));
					sendOptionsDialogue("Enable practice mode?", "Yes.", "No.");
				}
				break;
			case OPTION_2:
				end();
				player.getTemporaryAttributtes().put(Key.JOIN_BOSS_INSTANCE,
						boss);
				player.getPackets()
						.sendInputNameScript(
								"Enter the name of a player in a battle you wish to join.");
				break;
			default:
				String key = player.getLastBossInstanceKey();
				if (key == null) {
					stage = -2;
					sendDialogue("You do not have a battle to rejoin.");
					return;
				}
				if (BossInstanceHandler.findInstance(boss, key) == null) {

					if (key.equals(player.getUsername())
							&& player.getLastBossInstanceSettings() != null
							&& player.getLastBossInstanceSettings().getBoss() == boss
							&& player.getLastBossInstanceSettings()
									.hasTimeRemaining()) {
						end();
						// if the instance is null, and its my own player, use
						// the settings to recreate it
						BossInstanceHandler.createInstance(player,
								player.getLastBossInstanceSettings());
						return;
					}

					stage = -2;
					sendDialogue("You do not have a battle to rejoin.");
					return;
				}
				end();
				BossInstanceHandler.joinInstance(player, boss, key, false);
				// You do not have a battle to rejoin.
				break;
			}
			break;
		case 1:
			if (player.getLastBossInstanceSettings() == null) {
				end();
				return;
			}
			player.getLastBossInstanceSettings().setPracticeMode(
					componentId == OPTION_1);
			if (boss == Boss.Rise_Of_The_Six)
				player.getInventory().removeItemMoneyPouch(
						new Item(ItemIdentifiers.BARROWS_TOTEM, 1));
			if (boss.isHasHM()) {
				stage = 2;
				sendOptionsDialogue("Enable hard mode?", "Yes.", "No.");
			} else
				sendSelectMaxPlayers();
			break;
		case 2:
			if (player.getLastBossInstanceSettings() == null) {
				end();
				return;
			}
			player.getLastBossInstanceSettings().setHardMode(
					componentId == OPTION_1);
			sendSelectMaxPlayers();
			break;
		case 50:
			end();
			break;
		default:
			end();
			break;
		}
	}

	public void sendSelectMaxPlayers() {
		end();
		InstanceSettings settings = player.getLastBossInstanceSettings();
		if (settings == null)
			return;
		settings.setMaxPlayers(settings.getBoss().getMaxPlayers());
		settings.setMinCombat(1);
		settings.setSpawnSpeed(BossInstance.FASTEST);
		settings.setProtection(BossInstance.FFA);
		startInstance();

		// player.getTemporaryAttributtes().put(Key.JOIN_BOSS_INSTANCE, boss);
		// player.getPackets().sendInputNameScript("Enter the minimum combat
		// level.");
	}

	public void startInstance() {
		InstanceSettings settings = player.getLastBossInstanceSettings();
		if (settings == null)
			return;
		if (boss == Boss.Evil_Chicken) {
			player.getPackets().sendGameMessage(
					"The Evil Chicken instance is under development.");
			return;
		}
		int initialCost = settings.getBoss().getInitialCost();
		if (player.getDonationManager().isHeroicDonator())
			initialCost *= 0.4;
		else if (player.getDonationManager().isDemonicDonator())
			initialCost *= 0.5;
		else if (player.getDonationManager().isAngelicDonator())
			initialCost *= 0.6;
		else if (player.getDonationManager().isDivineDonator())
			initialCost *= 0.7;
		else if (player.getDonationManager().isSupremeDonator())
			initialCost *= 0.8;
		else if (player.getDonationManager().isLegendaryDonator())
			initialCost *= 0.9;
		if (player.getInventory().getCoinsAmount() < initialCost) {
			player.getPackets().sendGameMessage(
					"You don't have enough coins to start this battle.");
			player.setLastBossInstanceSettings(null);
			return;
		}
		if (initialCost > 0)
			player.getInventory().removeItemMoneyPouch(
					new Item(995, initialCost));
		settings.setCreationTime(Utils.currentTimeMillis());
		BossInstanceHandler.createInstance(player, settings);
	}

	@Override
	public void finish() {

	}
}