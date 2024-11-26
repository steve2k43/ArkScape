package net.nocturne.game.player.controllers;

import java.util.ArrayList;

import net.nocturne.Settings;
import net.nocturne.game.Entity;
import net.nocturne.game.WorldObject;
import net.nocturne.game.WorldTile;
import net.nocturne.game.item.Item;
import net.nocturne.game.npc.NPC;
import net.nocturne.game.player.content.FriendsChat;
import net.nocturne.utils.Utils;

public class NewPlayerController extends Controller {
	private static ArrayList<String> ips = new ArrayList<>( );

	private NPC target;
	private int stage = 0;

	@Override
	public void start() {
		stage = 0;
		player.setNextWorldTile(Settings.HOME_LOCATION);
		player.getMusicsManager().forcePlayMusic(89);
		player.getInterfaceManager().setRootInterface(1507, false);
	}

	@Override
	public boolean processButtonClick(int interfaceId, int componentId,
			int slotId, int slotId2, int packetId) {
		if (interfaceId == 1507) {
			if (componentId == 8 || componentId == 5) {
				if (player.isInLegacyCombatMode() != (componentId == 8))
					player.switchLegacyModeOld();
				player.getInterfaceManager().setRootInterface(548, false);
			}
			return false;
		} else if (interfaceId == 548 && componentId == 4) {
		}
		return true;
	}

	@Override
	public void process() {

	}

	@Override
	public boolean processObjectClick1(WorldObject object) {
		return false;
	}

	@Override
	public boolean processObjectClick2(WorldObject object) {
		return false;
	}

	@Override
	public boolean processObjectClick3(WorldObject object) {
		return false;
	}

	@Override
	public boolean processNPCClick1(NPC npc) {
		if (stage == 4 && npc == target) {
			stage = 5;
		}
		return false;
	}

	/*
	 * return remove Controller
	 */
	@Override
	public boolean login() {
		start();
		return false;
	}

	/*
	 * return remove Controller
	 */
	@Override
	public boolean logout() {
		return false;
	}

	@Override
	public boolean processMagicTeleport(WorldTile toTile) {
		return false;
	}

	@Override
	public boolean keepCombating(Entity target) {
		return false;
	}

	@Override
	public boolean canAttack(Entity target) {
		return false;
	}

	@Override
	public boolean canHit(Entity target) {
		return false;
	}

	@Override
	public boolean processItemTeleport(WorldTile toTile) {
		return false;
	}

	@Override
	public boolean processObjectTeleport(WorldTile toTile) {
		return false;
	}

	@Override
	public void forceClose() {
		player.getHintIconsManager().removeUnsavedHintIcon();
		player.getMusicsManager().reset();
		if (Settings.HOSTED)
			FriendsChat.requestJoin(player,
					Utils.formatPlayerNameForDisplay("help"));
		String ip = player.getSession() != null ? player.getSession().getIP()
				: null;
		if (ip != null && !ips.contains(ip)) {
			ips.add(ip);
			player.getInventory().addItem(new Item(995, 3001337));
			player.getInventory().addItem(new Item(386, 100));
		} else
			player.getPackets()
					.sendGameMessage(
							"You have already received starting items on another account.");
		player.getDialogueManager()
				.startDialogue(
						"SimpleNPCMessage",
						23009,
						"You are currently under new player protection, this will last for 1 hour. As a result, you have been blessed with Double EXP rates, but you will be unable to interact with other players until the protection has run out.");
		player.getInventory().addItem(new Item(1333, 1));
		player.getInventory().addItem(new Item(1323, 1));
		player.getInventory().addItem(new Item(1153, 1));
		player.getInventory().addItem(new Item(1115, 1));
		player.getInventory().addItem(new Item(1067, 1));
		player.getInventory().addItem(new Item(1540, 1));
		player.getInventory().addItem(new Item(1712, 1));
		player.getInventory().addItem(new Item(2552, 1));
		player.getInventory().addItem(new Item(3853, 1));
		player.getInventory().addItem(new Item(15707, 1));
		player.getInventory().addItem(new Item(1856, 1));
	}
}
