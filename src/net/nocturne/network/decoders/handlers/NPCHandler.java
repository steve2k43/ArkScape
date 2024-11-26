package net.nocturne.network.decoders.handlers;

import net.nocturne.utils.Bugsystem;
import net.nocturne.Settings;
import net.nocturne.game.Animation;
import net.nocturne.game.EffectsManager.EffectType;
import net.nocturne.game.ForceTalk;
import net.nocturne.game.Hit;
import net.nocturne.game.Hit.HitLook;
import net.nocturne.game.World;
import net.nocturne.game.WorldTile;
import net.nocturne.game.item.Item;
import net.nocturne.game.item.ItemConstants;
import net.nocturne.game.item.actions.Drinkables;
import net.nocturne.game.item.actions.RottenPotato;
import net.nocturne.game.npc.DropTable;
import net.nocturne.game.npc.NPC;
import net.nocturne.game.npc.clans.ClanVexillum;
import net.nocturne.game.npc.familiar.Familiar;
import net.nocturne.game.npc.familiar.impl.Pyrelord;
import net.nocturne.game.npc.others.ConditionalDeath;
import net.nocturne.game.npc.others.FireSpirit;
import net.nocturne.game.npc.others.GraveStone;
import net.nocturne.game.npc.others.LivingRock;
import net.nocturne.game.npc.others.MutatedZygomites;
import net.nocturne.game.npc.others.Pet;
import net.nocturne.game.npc.others.PolyporeCreature;
import net.nocturne.game.npc.others.Strykewyrm;
import net.nocturne.game.npc.others.WildyWyrm;
import net.nocturne.game.npc.pet.LegendaryPetAbilities;
import net.nocturne.game.player.FarmingManager.ProductInfo;
import net.nocturne.game.player.Player;
import net.nocturne.game.player.QuestManager.Quests;
import net.nocturne.game.player.RouteEvent;
import net.nocturne.game.player.Skills;
import net.nocturne.game.player.actions.PlayerCombat;
import net.nocturne.game.player.actions.Rest;
import net.nocturne.game.player.actions.skills.agility.HefinAgility;
import net.nocturne.game.player.actions.skills.crafting.Tanning.TanningData;
import net.nocturne.game.player.actions.skills.divination.Wisp;
import net.nocturne.game.player.actions.skills.dungeoneering.DungeonRewardShop;
import net.nocturne.game.player.actions.skills.dungeoneering.rooms.puzzles.ColouredRecessRoom.Block;
import net.nocturne.game.player.actions.skills.dungeoneering.rooms.puzzles.SlidingTilesRoom;
import net.nocturne.game.player.actions.skills.farming.FarmingStore;
import net.nocturne.game.player.actions.skills.fishing.Fishing;
import net.nocturne.game.player.actions.skills.fishing.Fishing.FishingSpots;
import net.nocturne.game.player.actions.skills.herblore.Herblore.CleanAction;
import net.nocturne.game.player.actions.skills.hunter.FlyingEntityHunter;
import net.nocturne.game.player.actions.skills.magic.Magic;
import net.nocturne.game.player.actions.skills.mining.LivingMineralMining;
import net.nocturne.game.player.actions.skills.mining.MiningBase;
import net.nocturne.game.player.actions.skills.mining.SaltyCrabletineMining;
import net.nocturne.game.player.actions.skills.runecrafting.SiphonActionCreatures;
import net.nocturne.game.player.actions.skills.slayer.Slayer.SlayerMaster;
import net.nocturne.game.player.actions.skills.slayer.SlayerShop;
import net.nocturne.game.player.actions.skills.smithing.SpiritshieldCreating;
import net.nocturne.game.player.actions.skills.summoning.Summoning.Pouch;
import net.nocturne.game.player.actions.skills.thieving.PickPocketAction;
import net.nocturne.game.player.actions.skills.thieving.PickPocketableNPC;
import net.nocturne.game.player.actions.skills.thieving.PrifddinasPickpocketing;
import net.nocturne.game.player.content.AbbysObsticals;
import net.nocturne.game.player.content.CarrierTravel;
import net.nocturne.game.player.content.CarrierTravel.Carrier;
import net.nocturne.game.player.content.GnomeGlider;
import net.nocturne.game.player.content.PlayerLook;
import net.nocturne.game.player.content.RecipeShop;
import net.nocturne.game.player.content.SheepShearing;
import net.nocturne.game.player.content.activities.distractions.PenguinHS;
import net.nocturne.game.player.content.activities.minigames.CastleWars;
import net.nocturne.game.player.content.activities.minigames.Sawmill;
import net.nocturne.game.player.content.activities.minigames.SorceressGarden;
import net.nocturne.game.player.content.activities.minigames.pest.CommendationExchange;
import net.nocturne.game.player.controllers.RuneEssenceController;
import net.nocturne.game.player.dialogues.impl.BoatingDialouge;
import net.nocturne.game.player.dialogues.impl.FremennikShipmaster;
import net.nocturne.game.player.dialogues.impl.cities.taverly.PetShopOwner;
import net.nocturne.stream.InputStream;
import net.nocturne.utils.Logger;
import net.nocturne.utils.NPCExamines;
import net.nocturne.utils.ShopsHandler;
import net.nocturne.utils.Utils;

import java.util.concurrent.atomic.AtomicBoolean;

public class NPCHandler {

	public static void handleExamine(final Player player, InputStream stream) {
		boolean forceRun = stream.readUnsignedByteC() == 1;
		int npcIndex = stream.readUnsignedShortLE128();
		if (forceRun)
			player.setRun(forceRun);
		final NPC npc = World.getNPCs().get(npcIndex);
		if (npc instanceof Pet) {
			Pet pet = (Pet) npc;
			if (pet != player.getPet()) {
				player.getPackets().sendGameMessage("This isn't your pet.");
				return;
			}
			if (pet.getId() == 18349 || pet.getId() == 18346 || pet.getId() == 18627 || pet.getId() == 18625
					|| pet.getId() == 17200) {
				player.setNextAnimation(new Animation(827));
				pet.pickup();
			}
		}
		if (npc == null || npc.hasFinished() || !player.getMapRegionsIds().contains(npc.getRegionId()))
			return;
		player.getPackets().sendEntityMessage(0, 15263739, npc, NPCExamines.getExamine(npc));
		DropTable.getDropTable(player, npc);
		player.getPackets().sendResetMinimapFlag();
		if (Settings.DEBUG) {
			Logger.log("NPCHandler", "examined npc: " + npcIndex + ", " + npc.getId());
			player.getPackets().sendGameMessage("Examine: " + npc.getName() + " (id: " + npc.getId() + "). "
					+ npc.getX() + ", " + npc.getY() + ", " + npc.getPlane() + ". Index: " + npcIndex + ".", true);
		}
	}

	public static void handleOption1(final Player player, final InputStream stream) {
		AtomicBoolean bug = new AtomicBoolean(false);
		boolean forceRun = stream.readUnsignedByteC() == 1;
		int npcIndex = stream.readUnsignedShortLE128();
		final NPC npc = World.getNPCs().get(npcIndex);
		if (npc == null || npc.isCantInteract() || npc.isDead() || npc.hasFinished()
				|| !player.getMapRegionsIds().contains(npc.getRegionId()) || player.isLocked())
			return;
		player.stopAll();
		if (forceRun)
			player.setRun(forceRun);
		if (npc.getId() == 17161) {
			npc.faceEntity(player);
			player.getDialogueManager().startDialogue("VoragoChallenge");
		}
		if (npc.getId() != 13634) {
			npc.faceEntity(player);
		}
		if (npc.getId() == 6362 || npc.getId() == 4296 || npc.getDefinitions().name.toLowerCase().contains("banker")
				&& npc.getDefinitions().hasOption("bank")) {
			player.setRouteEvent(new RouteEvent(npc, () -> {
				player.faceEntity(npc);
				if (!player.withinDistance(npc, 2))
					return;
				npc.faceEntity(player);
				player.getDialogueManager().startDialogue("ChooseBank");
			}, true));
			return;
		}
		if (npc.getDefinitions().name.toLowerCase().contains("tool leprechaun") || npc.getId() == 20110) {
			player.getDialogueManager().startDialogue("SimpleNPCMessage", npc.getId(), "Hello.");
		}
		if (npc.getDefinitions().name.toLowerCase().contains("hefin monk")) {
			ShopsHandler.openShop(player, 199);
		}
		if (npc.getId() == 23458 || npc.getId() == 23457 || npc.getId() == 23459 || npc.getId() == 23460) {
			PickPocketableNPC pocket = PickPocketableNPC.get(npc.getId());
			player.getActionManager().setAction(new PickPocketAction(npc, pocket));
		}
		if (npc.getId() == 9462 || npc.getId() == 9464 || npc.getId() == 9466)
			Strykewyrm.handleStomping(player, npc);
		if (npc.getId() == 4250) {
			player.setRouteEvent(new RouteEvent(npc, () -> {
				player.faceEntity(npc);
				if (!player.withinDistance(npc, 2))
					return;
				npc.faceEntity(player);
				player.getDialogueManager().startDialogue("SawmillOperator", npc.getId());
			}, true));
			return;
		}
		for (SlayerMaster master : SlayerMaster.values()) {
			if (npc.getId() == master.getNPCId()) {
				player.getDialogueManager().startDialogue("SlayerMasterD", master.getNPCId(), master);
			}
		}
		if (npc instanceof Pet) {
			Pet pet = (Pet) npc;
			if (pet != player.getPet()) {
				player.getPackets().sendGameMessage("This isn't your pet.");
				return;
			}
			player.setNextAnimation(new Animation(827));
			pet.pickup();
		}
		if (npc.getDefinitions().name.toLowerCase().equals("grand exchange clerk")) {
			player.setRouteEvent(new RouteEvent(npc, () -> {
				player.faceEntity(npc);
				if (!player.withinDistance(npc, 2))
					return;
				npc.faceEntity(player);
				player.getGeManager().openGrandExchange();
			}, true));
			return;
		}
		if (SlidingTilesRoom.handleSlidingBlock(player, npc))
			return;
		if (SiphonActionCreatures.siphon(player, npc))
			return;
		if (npc.getId() == 733) {
			if (player.getTreasureTrailsManager().useNPC(npc))
				return;
		}
		player.setRouteEvent(new RouteEvent(npc, () -> {
			npc.resetWalkSteps();
			player.faceEntity(npc);
			if (!player.getControllerManager().processNPCClick1(npc))
				return;
			if (npc instanceof Wisp) {
				Wisp wisp = (Wisp) npc;
				wisp.harvest(player);
			}
			switch (npc.getId()) {
			case 14256:
				player.getActionManager().setAction(new PlayerCombat(npc));
				break;
			case 961:
				npc.setNextForceTalk(new ForceTalk("I can heal you with the power of MAGIC!"));
				break;
			case 17552:
				if (!player.isZamorak)
					player.getDialogueManager().startDialogue("QuaterMaster", npc.getId());
				else
					player.getPackets().sendGameMessage("You are not allied with Saradomin.");
				break;
			case 17553:
				if (player.isZamorak)
					player.getDialogueManager().startDialogue("QuaterMaster", npc.getId());
				else
					player.getPackets().sendGameMessage("You are not allied with Zamorak.");
				break;
			case 3404:
				player.getDialogueManager().startDialogue("TeplinMacaganD", npc.getId(), 1);
				break;
			case 14364: // greegreeshop
				ShopsHandler.openShop(player, 215);
				break;
			case 945:
				player.getDialogueManager().startDialogue("IronmanShops");
				break;
			case 590: // Adventuring supplies
				ShopsHandler.openShop(player, 29);
				break;
			case 18615: // DZ shop
				ShopsHandler.openShop(player, 221);
				break;
			case 8819:
				ShopsHandler.openShop(player, 222);
				break;
			case 20593:
				ShopsHandler.openShop(player, 223);
				break;
			case 22595:
				ShopsHandler.openShop(player, 224);
				break;
			case 784:
				player.getDialogueManager().startDialogue("RetroRonnie");
				break;
			case 21726:
				ShopsHandler.openShop(player, 225);
				break;
			case 21553:
				ShopsHandler.openShop(player, 226);
				break;
			}
			FishingSpots spot = FishingSpots.forId(npc.getId() | 1 << 24);
			if (spot != null) {
				player.getActionManager().setAction(new Fishing(spot, npc));
				return; // its a spot, they wont face us
			}
			if (npc.getId() == 23374) {
				player.getActionManager().setAction(new SaltyCrabletineMining(npc));
				return;
			}
			if (npc.getId() >= 8837 && npc.getId() <= 8839) {
				player.getActionManager().setAction(new LivingMineralMining((LivingRock) npc));
				return;
			}
			if (npc instanceof GraveStone) {
				GraveStone grave = (GraveStone) npc;
				grave.sendGraveInscription(player);
				return;
			}
			if (npc.getId() == 20275) {
				HefinAgility.lightCreature(player, 1);
			}
			if (npc.getId() == 20274) {
				HefinAgility.merge(player);
			} else if (PenguinHS.isPenguin(player, npc))
				return;
			if (npc.getId() == 20282) {
				if (!player.getInventory().containsItem(32625, 1) && !player.getBank().containsItem(32625)
						&& player.canReceiveSeed < Utils.currentTimeMillis()
						&& player.getSkills().getLevel(Skills.SMITHING) >= 90) {
					player.getDialogueManager().startDialogue("LadyIthellD");
					player.canReceiveSeed = Utils.currentTimeMillis() + (1000 * 60 * 60 * 24 * 7);
					return;
				} else {
					player.getDialogueManager().startDialogue("LadyIthellNormalD");
					return;
				}

			}
			/*
			 * Home dialogues
			 */
			switch (npc.getId()) {
			case 13697: // Troll Invasion
				player.getDialogueManager().startDialogue("CaptainJute", npc.getId(), 1);
				break;
			case 17065: // Warbands rewards
				player.getDialogueManager().startDialogue("Quercus", npc.getId(), 1);
				break;
			case 22942:
				player.getPackets().sendHideIComponent(1776, 10, false);
				player.getPackets().sendHideIComponent(1776, 11, false);
				player.getInterfaceManager().setBackgroundInterface(false, 1776);
				break;
			case 20312: // Ithell
			case 20313:
			case 20314:
			case 20315:
			case 20316: // Amlodd
			case 20317:
			case 20318:
			case 20319:
			case 20320: // Hefin
			case 20321:
			case 20322:
			case 20323:
			case 20324: // Meilyr
			case 20325:
			case 20326:
			case 20327:
			case 20113: // Iorwerth
			case 20114:
			case 20115:
			case 20116:
			case 20125: // Trahaearn
			case 20126:
			case 20127:
			case 20128:
			case 20121: // Cadarn
			case 20122:
			case 20123:
			case 20124:
			case 20117: // Crwys
			case 20118:
			case 20119:
			case 20120:
				player.getActionManager().setAction(new PrifddinasPickpocketing(npc));
				break;
			case 20272:
				player.getDialogueManager().startDialogue("LadyHefin", npc.getId());
				break;
			case 4476:
				player.getDialogueManager().startDialogue("GurdianMummy", npc.getId(), 1);
				break;
			case 3705:
				player.getDialogueManager().startDialogue("Max", npc.getId());
				break;
			case 20309:
				player.getDialogueManager().startDialogue("SimpleNPCMessage", npc.getId(), "Hello, I sell planks.");
				break;
			case 16973:
				player.getDialogueManager().startDialogue("GuthixianHighDruid", npc, 1);
				break;
			case 13790:
				player.getDialogueManager().startDialogue("LumberJack", npc.getId());
				break;
			case 4247:
			case 6715:
			case 20105:
				player.getDialogueManager().startDialogue("EstateAgentD", npc.getId());
				break;
			case 16946:
				player.getDialogueManager().startDialogue("SimpleNPCMessage", npc.getId(),
						"Guthix may teach us as much in death as in life. I feel we will need this knowledge as we face the new age.");
				break;
			case 16944:
				player.getDialogueManager().startDialogue("SimpleNPCMessage", npc.getId(),
						"It calms me to be here. Guthix may be dead, but this shrine is a symbol of our unrelenting faith to him.<br>Thought he is gone, his teachings and followers remain.");
				break;
			// donator zone
			case 14237:
				player.getDialogueManager().startDialogue("Teshmezon", npc.getId());
				break;
			// Challenge Mistress Fara
			case 15780:
				player.getDialogueManager().startDialogue("ChallengeMistressFara", npc.getId(), 1);
				break;
			// Rapture
			case 13955:
				player.getDialogueManager().startDialogue("Rapture", npc.getId());
				break;
			// Ariane (Teleports)
			case 19727:
			case 23009:
				player.getDialogueManager().startDialogue("Ariane", npc.getId(), npc);
				break;
			// Orlando Smith (Tools)
			case 23021:
				player.getDialogueManager().startDialogue("OrlandoSmith", npc.getId());
				break;
			// Fisherman (Fishing Supplies)
			case 219:
				player.getDialogueManager().startDialogue("Fisherman", npc.getId());
				break;
			case 22959:
				player.getDialogueManager().startDialogue("Ozan", npc.getId());
				break;
			// Arianwyn (Equipment Shops)
			case 23419:
				player.getDialogueManager().startDialogue("Arianwyn", npc.getId());
				break;
			// Jatix (Herblore Shops)
			case 587:
				player.getDialogueManager().startDialogue("Jatix", npc.getId());
				break;
			// Diango (Vote Shops)
			case 19921:
				player.getDialogueManager().startDialogue("Diango", npc.getId());
				break;
			// Lady Ithell (Settings Options)
			case 6530:
			case 19560:
				player.getDialogueManager().startDialogue("LadyIthell", npc.getId());
				break;
			/*
			 * Crafting Guilds
			 */
			// Long Hair Master Crafter
			case 2733:
				player.getDialogueManager().startDialogue("FirstMasterCrafter", npc.getId());
				break;
			// Short Hair Master Crafter
			case 2732:
				player.getDialogueManager().startDialogue("SecondMasterCrafter", npc.getId());
				break;
			// Main Master Crafter
			case 805:
				player.getDialogueManager().startDialogue("MainMasterCrafter", npc.getId());
				break;
			/*
			 * Challenge
			 */
			case 19726:
				player.getDialogueManager().startDialogue("SkillerMax", npc.getId());
				break;

			/*
			 * Luthas
			 */
			case 379:
				player.getDialogueManager().startDialogue("Luthas", npc.getId());
				break;
			/*
			 * Customs officer
			 */
			case 380:
				player.getDialogueManager().startDialogue("CustomsOfficer", npc.getId());
				break;

			/*
			 * Zambo
			 */
			case 568:
				player.getDialogueManager().startDialogue("Zambo", npc.getId());
				break;

			/*
			 * Town Crier
			 */
			case 6137:
				player.getDialogueManager().startDialogue("TownCrier", npc.getId());
				break;
			/*
			 * Kaylee
			 */
			case 3217:
				player.getDialogueManager().startDialogue("Kaylee", npc.getId());
				break;
			/*
			 * Flynn
			 */
			case 580:
				player.getDialogueManager().startDialogue("Flynn", npc.getId());
				break;
			/*
			 * Cassie
			 */
			case 577:
				player.getDialogueManager().startDialogue("Cassie", npc.getId());
				break;
			/*
			 * Sir Tiffy Cashien
			 */
			case 2290:
				player.getDialogueManager().startDialogue("SirTiffyCashien", npc.getId());
				break;
			/*
			 * Ikis Krum
			 */
			case 12237:
				player.getDialogueManager().startDialogue("IkisKrum", npc.getId());
				break;
			/*
			 * Party Pete
			 */
			case 659:
				player.getDialogueManager().startDialogue("PartyPete", npc.getId());
				break;
			/*
			 * Aksel
			 */
			case 6663:
				player.getDialogueManager().startDialogue("Aksel", npc.getId());
				break;
			/*
			 * Herald of Falador
			 */
			case 13939:
				player.getDialogueManager().startDialogue("HeraldOfFalador", npc.getId());
				break;

			/*
			 * Silif
			 */
			case 370:
				player.getDialogueManager().startDialogue("Silif", npc.getId());
				break;

			/*
			 * Sir Vyvin
			 */
			case 605:
				player.getDialogueManager().startDialogue("SirVyvin", npc.getId());
				break;

			/*
			 * Doric
			 */
			case 284:
				player.getDialogueManager().startDialogue("Doric", npc.getId());
				break;
			/*
			 * Wyson the Gardener
			 */
			case 39:
				player.getDialogueManager().startDialogue("Wyson");
				break;
			/*
			 * Draynor Village NPC'S
			 */
			// Bank Guard
			case 2574:
				player.getDialogueManager().startDialogue("BankGuard", npc.getId());
				break;
			// Wise Old Man
			case 3820:
				player.getDialogueManager().startDialogue("WiseOldMan", npc.getId());
				break;
			case 755:
				player.getDialogueManager().startDialogue("Morgan", 0);
				break;
			/*
			 * Ned
			 */
			case 918:
				player.getDialogueManager().startDialogue("Ned", npc.getId());
				break;
			/*
			 * Joe
			 */
			case 916:
				player.getDialogueManager().startDialogue("Joe", 0);
				break;
			/*
			 * Leela
			 */
			case 915:
				player.getDialogueManager().startDialogue("Leela", 0);
				break;
			/*
			 * Lottery
			 */
			case 19910:
			case 6138:
				player.getDialogueManager().startDialogue("LotteryDialogue", npc);
				break;
			/*
			 * Aggie
			 */
			case 922:
				player.getDialogueManager().startDialogue("Aggie", 0);
				break;
			/*
			 * Master Garderner
			 */
			case 3299:
				player.getDialogueManager().startDialogue("MartinTheMasterGardener", npc.getId());
				break;
			/*
			 * Lady keli
			 */
			case 919:
				player.getDialogueManager().startDialogue("LadyKeli", 0);
				break;
			/*
			 * Fortunato
			 */
			case 3671:
				player.getDialogueManager().startDialogue("Fortunato", npc.getId());
				break;
			/*
			 * Olivia
			 */
			case 2233:
				player.getDialogueManager().startDialogue("Olivia", npc.getId());
				break;

			/*
			 * Wizard's Tower NPC'S
			 */
			case 4585:
				player.getDialogueManager().startDialogue("ProfessorOnglewip", npc.getId());
				break;

			/*
			 * Dragontooth teleporter
			 */
			case 1704:
				if (player.getX() >= 3700 && player.getX() <= 3703) {
					player.setNextWorldTile(new WorldTile(3791, 3559, 0));
				} else {
					player.setNextWorldTile(new WorldTile(3702, 3487, 0));
				}
				break;
			// Charlie The Tramp
			case 641:
				player.getDialogueManager().startDialogue("CharlieTheTramp", npc.getId());
				break;
			// Varrock Sword Shop
			case 552:
				player.getDialogueManager().startDialogue("SwordShopKeeper", npc.getId());
				break;
			// Varrock Sword Shop
			case 551:
				player.getDialogueManager().startDialogue("SwordShopKeeper", npc.getId());
				break;
			// Dr Harlow
			case 756:
				player.getDialogueManager().startDialogue("DrHarlow", npc.getId());
				break;
			// Tramp
			case 11:
				player.getDialogueManager().startDialogue("Tramp", npc.getId());
				break;
			// Aubury
			case 5913:
				player.getDialogueManager().startDialogue("Aubury", npc);
				break;
			// Guard
			case 368:
				player.getDialogueManager().startDialogue("Guard", npc.getId());
				break;
			// Da Vinci
			case 337:
				player.getDialogueManager().startDialogue("DaVinci", npc.getId());
				break;
			// Chancey
			case 339:
				player.getDialogueManager().startDialogue("Chancey", npc.getId());
				break;
			// South Eastern Bartender
			case 732:
				player.getDialogueManager().startDialogue("SouthEastBartender", npc.getId());
				break;
			// Hops
			case 341:
				player.getDialogueManager().startDialogue("Hops", npc.getId());
				break;
			// Guidor's Wife
			case 342:
				player.getDialogueManager().startDialogue("GuidorsWife", npc.getId());
				break;
			// Town Crier
			case 6135:
				player.getDialogueManager().startDialogue("TownCrier", npc.getId());
				break;
			// Horvik
			case 549:
				player.getDialogueManager().startDialogue("Horvik", npc.getId());
				break;
			// Baraek
			case 547:
				player.getDialogueManager().startDialogue("Baraek", npc.getId());
				break;
			// Xuan
			case 13727:
				player.getDialogueManager().startDialogue("Xuan", npc.getId(), 1);
				break;
			// Zaff
			case 546:
				player.getDialogueManager().startDialogue("Zaff", npc.getId());
				break;
			// Iffie
			case 2778:
				player.getDialogueManager().startDialogue("Iffie", npc.getId());
				break;
			// Thessalia
			case 548:
				player.getDialogueManager().startDialogue("Thessalia", npc.getId());
				break;
			// Penguin HS
			case 8693:
				player.getDialogueManager().startDialogue("PenguinRewards");
				break;
			// Apothercary
			case 638:
				player.getDialogueManager().startDialogue("Apothecary", npc.getId());
				break;
			// Varrock Tanner
			case 2320:
				player.getDialogueManager().startDialogue("VarrockTanner", npc.getId());
				break;
			// Sani
			case 4905:
				player.getDialogueManager().startDialogue("Sani", npc.getId());
				break;
			// Blue Moon Inn Cook
			case 5910:
				player.getDialogueManager().startDialogue("BlueMoonCook", npc.getId());
				break;
			// Lowe
			case 550:
				player.getDialogueManager().startDialogue("Lowe", npc.getId());
				break;
			// Benny
			case 5925:
				player.getDialogueManager().startDialogue("Benny", npc.getId());
				break;
			// Sir Prysin
			case 9359:
				player.getDialogueManager().startDialogue("SirPrysin", npc.getId());
				break;
			// Clive
			case 8174:
				player.getDialogueManager().startDialogue("Clive", npc.getId());
				break;
			// Reldo
			case 647:
				player.getDialogueManager().startDialogue("Reldo", npc.getId());
				break;
			// King Roald
			case 648:
				player.getDialogueManager().startDialogue("KingRoald", npc.getId());
				break;
			// Curator Haig Halen
			case 646:
				player.getDialogueManager().startDialogue("CuratorHaigHalen", npc.getId());
				break;
			// Teacher & Pupil
			case 5947:
				player.getDialogueManager().startDialogue("TeacherAndPupil", npc.getId());
				break;
			// Schoolboy
			case 5946:
				player.getDialogueManager().startDialogue("Schoolboy", npc.getId());
				break;
			// Information Clerk
			case 5938:
				player.getDialogueManager().startDialogue("MuseumClerk", npc.getId());
				break;
			// Father Lawrence
			case 640:
				player.getDialogueManager().startDialogue("FatherLawrence", npc.getId());
				break;
			// Schoolgirl
			case 5984:
				player.getDialogueManager().startDialogue("Schoolgirl", npc.getId());
				break;
			case 523:
				player.getDialogueManager().startDialogue("GeneralStore", npc.getId(), 1);
				break;
			case 522:
				player.getDialogueManager().startDialogue("GeneralStore", npc.getId(), 1);
				break;
			// Pet Shop Owner
			case 6893:
				player.getDialogueManager().startDialogue("PetShopOwner", npc.getId());
				break;
			// Pikkupstix
			case 6988:
				player.getDialogueManager().startDialogue("Pikkupstix", npc.getId());
				break;
			// Magestix
			case 14866:
				player.getDialogueManager().startDialogue("Magestix", npc.getId());
				break;
			// Jacquelyn Manslaughter
			case 14868:
				player.getDialogueManager().startDialogue("JacquelynManslaughter", npc.getId());
				break;
			// Foppish Pierre
			case 15086:
				player.getDialogueManager().startDialogue("FoppishPierre", npc.getId());
				break;
			// Scalectrix
			case 15055:
				player.getDialogueManager().startDialogue("Scalectrix", npc.getId());
				break;
			// Kaqemeex
			case 455:
				player.getDialogueManager().startDialogue("Kaqemeex", npc.getId());
				break;
			// Poletax
			case 14854:
				player.getDialogueManager().startDialogue("Poletax", npc.getId());
				break;
			// Sanfew
			case 454:
				player.getDialogueManager().startDialogue("Sanfew", npc.getId());
				break;
			// Alfred Stonemason
			case 14862:
				player.getDialogueManager().startDialogue("AlfredStonemason", npc.getId());
				break;
			// Pompous Merchant
			case 14911:
				player.getDialogueManager().startDialogue("PompousMerchant", npc.getId());
				break;
			// Nails Newton
			case 15085:
				player.getDialogueManager().startDialogue("NailsNewton", npc.getId());
				break;
			// Head Farmer
			case 14860:
				player.getDialogueManager().startDialogue("HeadFarmerJones", npc.getId());
				break;
			// Jack Oval
			case 14877:
				player.getDialogueManager().startDialogue("JackOval", npc.getId());
				break;
			// Tobias Bronzearms
			case 14870:
				player.getDialogueManager().startDialogue("TobiasBronzearms", npc.getId());
				break;
			// Martin Steelweaver
			case 14874:
				player.getDialogueManager().startDialogue("MartinSteelweaver", npc.getId());
				break;
			// Brian
			case 1860:
				player.getDialogueManager().startDialogue("RimmingtonBrian", npc.getId());
				break;
			// Rommik
			case 585:
				player.getDialogueManager().startDialogue("Rommik", npc.getId());
				break;
			// Chemsit
			case 367:
				player.getDialogueManager().startDialogue("Chemist", npc.getId());
				break;

			/*
			 * Lumbridge Sage
			 */
			case 2244:
				player.getDialogueManager().startDialogue("LumbridgeSage", npc.getId());
				break;
			/*
			 * Doomsayer
			 */
			case 3777:
				player.getDialogueManager().startDialogue("Doomsayer", npc.getId());
				break;
			/*
			 * Xenia
			 */
			case 9633:
				player.getDialogueManager().startDialogue("Xenia", npc.getId());
				break;
			/*
			 * Donnie
			 */
			case 2238:
				player.getDialogueManager().startDialogue("Donie", npc.getId());
				break;
			/*
			 * Bob
			 */
			case 519:
			case 9711:
				player.getDialogueManager().startDialogue("Bob", npc.getId());
				break;
			/*
			 * Gillie Groats
			 */
			case 3807:
				player.getDialogueManager().startDialogue("GillieGroats", npc.getId());
				break;
			/*
			 * Seth Groats
			 */
			case 452:
				player.getDialogueManager().startDialogue("SethGroats", npc.getId());
				break;
			/*
			 * Lactopher
			 */
			case 7870:
				player.getDialogueManager().startDialogue("Lactopher", npc.getId());
				break;
			/*
			 * Victoria
			 */
			case 7872:
				player.getDialogueManager().startDialogue("Victoria", npc.getId());
				break;
			/*
			 * Duke Horacio
			 */
			case 741:
				player.getDialogueManager().startDialogue("DukeHoracio", 0);
				break;
			/*
			 * Wizard Mizgog
			 */
			case 706:
				player.getDialogueManager().startDialogue("WizardMizgog", 0);
				break;

			/*
			 * Wilfred
			 */
			case 4906:
				player.getDialogueManager().startDialogue("Wilfred", npc.getId());
				break;
			/*
			 * Fred the Farmer
			 */
			case 758:
				player.getDialogueManager().startDialogue("FredTheFarmer", npc.getId());
				break;
			/*
			 * Guardsman Dante
			 */
			case 7885:
				player.getDialogueManager().startDialogue("GuardsmanDante", npc.getId());
				break;
			/*
			 * Guardsman DeShawn
			 */
			case 7886:
				player.getDialogueManager().startDialogue("GuardsmanDeShawn", npc.getId());
				break;
			/*
			 * Guardsman Brawn
			 */
			case 7887:
				player.getDialogueManager().startDialogue("GuardsmanBrawn", npc.getId());
				break;
			/*
			 * Guardsman Pazel
			 */
			case 7889:
				player.getDialogueManager().startDialogue("GuardsmanPazel", npc.getId());
				break;
			/*
			 * Guardsman Peale
			 */
			case 7890:
				player.getDialogueManager().startDialogue("GuardsmanPeale", npc.getId());
				break;
			/*
			 * Hans
			 */
			case 0:
				player.getDialogueManager().startDialogue("Hans", npc.getId());
				break;
			/*
			 * Father Aereck
			 */
			case 7937:
				player.getDialogueManager().startDialogue("FatherAereck", npc.getId());
				break;
			/*
			 * Barfy Bill
			 */
			case 3331:
				player.getDialogueManager().startDialogue("BarfyBill", npc.getId());
				break;
			/*
			 * Tarquin
			 */
			case 3328:
				player.getDialogueManager().startDialogue("Tarquin", npc.getId());
				break;
			/*
			 * Apprentice Smith
			 */
			case 4904:
				player.getDialogueManager().startDialogue("ApprenticeSmith", npc.getId());
				break;
			/*
			 * Father Urhney
			 */
			case 458:
				player.getDialogueManager().startDialogue("FatherUrhney", npc.getId());
				break;
			/*
			 * Fremennik Warrior
			 */
			case 9709:
				player.getDialogueManager().startDialogue("FremennikWarrior", npc.getId());
				break;
			/*
			 * Herald Of Lumbridge
			 */
			case 13937:
				player.getDialogueManager().startDialogue("HeraldOfLumbridge", npc.getId());
				break;
			/*
			 * Gee
			 */
			case 2237:
				player.getDialogueManager().startDialogue("Gee", npc.getId());
				break;
			case 4707:
				player.getDialogueManager().startDialogue("MagicInstructor");
				break;
			case 705:
				player.getDialogueManager().startDialogue("MeleeInstructor");
				break;
			case 1861:
				player.getDialogueManager().startDialogue("RangeInstructor");
				break;
			case 278:
				player.getDialogueManager().startDialogue("LumbridgeCook", 0);
				break;
			case 520:
				player.getDialogueManager().startDialogue("GeneralStore", npc.getId(), 1);
				break;
			case 521:
				player.getDialogueManager().startDialogue("GeneralStore", npc.getId(), 1);
				break;
			// Ahab
			case 2692:
				player.getDialogueManager().startDialogue("Ahab", npc.getId());
				break;
			// Jack Seagull
			case 2690:
				player.getDialogueManager().startDialogue("JackSeagull", npc.getId());
				break;
			// Longbow Ben
			case 2691:
				player.getDialogueManager().startDialogue("LongBowBen", npc.getId());
				break;
			// Stanky Morgan
			case 6667:
				player.getDialogueManager().startDialogue("StankyMorgan", npc.getId());
				break;
			// Bard Roberts
			case 3509:
				player.stopAll();
				player.getActionManager().setAction(new Rest(true));
				break;
			// Bartender
			case 734:
				player.getDialogueManager().startDialogue("PortSarimBartender", npc.getId());
				break;
			// Gerrant
			case 558:
				player.getDialogueManager().startDialogue("Gerrant", npc.getId());
				break;
			// Wydin
			case 557:
				player.getDialogueManager().startDialogue("Wydin", 0);
				break;
			// Brian
			case 559:
				player.getDialogueManager().startDialogue("PortSarimBrian", npc.getId());
				break;
			// Betty
			case 583:
				player.getDialogueManager().startDialogue("Betty", npc.getId());
				break;
			// Grum
			case 556:
				player.getDialogueManager().startDialogue("Grum", npc.getId());
				break;
			// Thurgo
			case 604:
				player.getDialogueManager().startDialogue("Thurgo", npc.getId());
				break;
			// Redbeard frank
			case 375:
				player.getDialogueManager().startDialogue("RedbeardFrank", 0);
				break;

			/*
			 * Captain Dalbur
			 */
			case 3809:
				player.getDialogueManager().startDialogue("CaptainDalbur", npc.getId());
				break;

			/*
			 * Gem Trader
			 */
			case 540:
				player.getDialogueManager().startDialogue("GemTrader", npc.getId());
				break;
			/*
			 * Silk Trader
			 */
			case 539:
				player.getDialogueManager().startDialogue("SilkTrader", npc.getId());
				break;
			/*
			 * Faruq
			 */
			case 9159:
				player.getDialogueManager().startDialogue("Faruq", npc.getId());
				break;
			/*
			 * Toll Guards
			 */
			case 925:
				player.getDialogueManager().startDialogue("AlKharidToll", npc.getId());
				break;

			/*
			 * Osman
			 */
			case 5282:
				player.getDialogueManager().startDialogue("Osman", 0);
				break;
			/*
			 * Prince Ali
			 */
			case 920:
				player.getDialogueManager().startDialogue("Prince", 0);
				break;

			/*
			 * Hassan
			 */
			case 923:
				player.getDialogueManager().startDialogue("Hassan", 0);
				break;
			/*
			 * Shop Assistant
			 */
			case 524:
				player.getDialogueManager().startDialogue("AlkharidShopAssistant");
				break;
			/*
			 * Shop Keeper
			 */
			case 525:
				player.getDialogueManager().startDialogue("AlkharidShopKeeper", npc.getId());
				break;
			/*
			 * Zeke
			 */
			case 541:
				player.getDialogueManager().startDialogue("Zeke", npc.getId());
				break;
			/*
			 * Louie Legs
			 */
			case 542:
				player.getDialogueManager().startDialogue("LouieLegs", npc.getId());
				break;
			/*
			 * Karim
			 */
			case 543:
				player.getDialogueManager().startDialogue("Karim", npc.getId());
				break;
			/*
			 * Ranael
			 */
			case 544:
				player.getDialogueManager().startDialogue("Ranael", npc.getId());
				break;
			/*
			 * Dommik
			 */
			case 545:
				player.getDialogueManager().startDialogue("Dommik", npc.getId());
				break;
			/*
			 * Ollie the Camel
			 */
			case 2810:
			case 2811:
			case 2812:
			case 2813:
				player.getDialogueManager().startDialogue("AlkharidCamel", npc.getId());
				break;

			case 22984:
				ShopsHandler.openShop(player, 200);
				break;
				case 15567:
					player.getDialogueManager().startDialogue("Fisherman1", npc.getId());
					break;
				default:
					bug.set(true);
			}
			npc.handleMovement();
			if (npc.getId() == 18204) {
				npc.finish();
				player.getSkills().addXp(Skills.HUNTER, player.getSkills().getLevel(Skills.HUNTER) * 10);
				player.getInventory().addItem(new Item(29293, 1));
				bug.set(false);
			}
			if (npc.getId() == 2619 || npc.getId() == 13455 || npc.getId() == 2617 || npc.getId() == 2618
					|| npc.getId() == 15194) {
				player.getDialogueManager().startDialogue("ChooseBank");
				bug.set(false);
			}
			if (npc instanceof Familiar) {
				Familiar familiar = (Familiar) npc;
				if (player.getFamiliar() != familiar) {
					player.getPackets().sendGameMessage("That isn't your familiar.");
					return;
				} else if (familiar.getDefinitions().hasOption("store") || npc.getDefinitions().hasOption("withdraw")) {
					player.getFamiliar().store();
				} else if (familiar.getDefinitions().hasOption("interact")) {
					Object[] paramaters = new Object[2];
					Pouch pouch = player.getFamiliar().getPouch();
					if (pouch == Pouch.SPIRIT_GRAAHK) {
						paramaters[0] = "Karamja's Hunter Area";
						paramaters[1] = new WorldTile(2787, 3000, 0);
					} else if (pouch == Pouch.SPIRIT_KYATT) {
						paramaters[0] = "Piscatorius Hunter Area";
						paramaters[1] = new WorldTile(2339, 3636, 0);
					} else if (pouch == Pouch.SPIRIT_LARUPIA) {
						paramaters[0] = "Feldip Hills Hunter Area";
						paramaters[1] = new WorldTile(2557, 2913, 0);
					} else if (pouch == Pouch.ARCTIC_BEAR) {
						paramaters[0] = "Rellekka Hills Hunter Area";
						paramaters[1] = new WorldTile(2721, 3779, 0);
					} else if (pouch == Pouch.LAVA_TITAN) {
						paramaters[0] = "Lava Maze - *Deep Wilderness*";
						paramaters[1] = new WorldTile(3028, 3840, 0);
					} else {
						player.getPackets().sendGameMessage("Your familiar has nothing to say.");
						return;
					}
					player.getDialogueManager().startDialogue("FamiliarInspection", paramaters[0], paramaters[1]);
				}
				bug.set(false);
				return;
			}
			npc.faceEntity(player);
			if (player.getTreasureTrailsManager().useNPC(npc))
				return;
			Object[] shipAttributes = BoatingDialouge.getBoatForShip(npc.getId());
			if (shipAttributes != null)
				player.getDialogueManager().startDialogue("BoatingDialouge", npc.getId());
			else if (npc.getId() == 3810){
				GnomeGlider.sendInterface(player);bug.set(false);}
			else if (npc.getId() == 3809){
				GnomeGlider.sendInterface(player);bug.set(false);}
			else if (npc.getId() == 3812){
				GnomeGlider.sendInterface(player);bug.set(false);}
			else if (npc.getId() == 1800){
				GnomeGlider.sendInterface(player);bug.set(false);}
			else if (npc.getId() == 3811){
				GnomeGlider.sendInterface(player);bug.set(false);}
			else if (npc.getId() == 20299){
				GnomeGlider.sendInterface(player);bug.set(false);}
			else if (npc.getId() == 3811){
				GnomeGlider.sendInterface(player);bug.set(false);}
			else if (npc.getId() == 3709){
				player.getDialogueManager().startDialogue("MrEx", npc.getId());bug.set(false);}
			else if (npc.getId() == 5141){
				player.getDialogueManager().startDialogue("UgiDialouge", npc);bug.set(false);}
			else if (npc.getId() == 15513 || npc.getId() >= 11303 && npc.getId() <= 11307){
				player.getDialogueManager().startDialogue("ServantDialogue", npc.getId());bug.set(false);}
			else if (npc.getId() == 2290){
				player.getDialogueManager().startDialogue("SirTiffyCashien", npc.getId());bug.set(false);}
			else if (npc.getId() == 4511){
				player.getDialogueManager().startDialogue("Oneiromancer", npc.getId());bug.set(false);}
			else if (npc.getId() == 8171 || npc.getId() == 8172){
				player.getDialogueManager().startDialogue("Dimintheis", npc.getId());bug.set(false);}
			else if (npc.getId() == 8266){
				player.getDialogueManager().startDialogue("Ghommel");bug.set(false);}
			else if (npc.getId() == 5530){
				player.getDialogueManager().startDialogue("MawnisBurowger");bug.set(false);}
			else if (npc.getId() == 5532){
				player.getDialogueManager().startDialogue("SorceressGardenNPCs", npc);bug.set(false);}
			else if (npc.getId() == 5563){
				player.getDialogueManager().startDialogue("SorceressGardenNPCs", npc);bug.set(false);}
			else if (npc.getId() == 6892 || npc.getId() == 6893){
				player.getDialogueManager().startDialogue("PetShopOwner", npc.getId());bug.set(false);}
			else if (npc.getId() == 780){
				player.getDialogueManager().startDialogue("Gertrude", npc.getId());bug.set(false);}
			else if (npc.getId() == 15907){
				player.getDialogueManager().startDialogue("OsmanDialogue", npc.getId());bug.set(false);}
			else if (npc.getId() == 837){
				player.getDialogueManager().startDialogue("ShantyGuardD", npc.getId());bug.set(false);}
			else if (npc.getId() == 9712){
				player.getDialogueManager().startDialogue("DungeoneeringTutor");bug.set(false);}
			else if (npc.getId() == 836){
				player.getDialogueManager().startDialogue("ShantyD");bug.set(false);}
			else if (npc.getId() == 2301){
				player.getDialogueManager().startDialogue("ShantyMonkeyD");bug.set(false);}
			else if (npc.getId() == 5915){
				player.getDialogueManager().startDialogue("Scribe", npc.getId(), 20709);bug.set(false);}
			else if (npc.getId() == 14872){
				player.getDialogueManager().startDialogue("MiladeDeathD");bug.set(false);}
			else if (npc.getId() == 1526){
				player.getDialogueManager().startDialogue("Lanthus", npc.getId());bug.set(false);}
			else if (npc.getId() == 13633){
				player.getDialogueManager().startDialogue("Scribe", npc.getId(), 20708);bug.set(false);}
			else if (npc.getId() == 2291){
				player.getDialogueManager().startDialogue("RugMerchantD", false, 0);bug.set(false);}
			else if (npc.getId() == 171){
				player.getDialogueManager().startDialogue("Brimstail", npc);bug.set(false);}
			else if (npc.getId() == 250){
				player.getDialogueManager().startDialogue("LadyOfTheLake");bug.set(false);}
			else if (npc.getId() == 6524){
				player.getDialogueManager().startDialogue("BobBarterD");bug.set(false);}
			else if (npc.getId() == 8091){
				player.getDialogueManager().startDialogue("StarSpriteD");bug.set(false);}
			else if (npc.getId() == 2824 || npc.getId() == 1041 || npc.getId() == 20281){
				player.getDialogueManager().startDialogue("TanningD", npc.getId(), TanningData.LEATHER);bug.set(false);}
			else if (npc.getId() == 17143) {
				player.getDialogueManager().startDialogue("Ocellus", npc.getId(), (byte) 1);bug.set(false);
			} else if (npc.getId() == 15451 && npc instanceof FireSpirit) {
				FireSpirit spirit = (FireSpirit) npc;
				spirit.giveReward(player);
				bug.set(false);
				return;
			} else if (npc.getId() == 1204 || npc.getId() == 1206 || npc.getId() == 4649) {
				boolean onDuty = Utils.random(2) == 0;
				player.getDialogueManager().startDialogue("SimpleNPCMessage", npc.getId(),
						onDuty ? "I'm on duty, this isn't the time to be talking to strangers"
								: "It isn't safe here, its best for you to leave now...");
				player.getPackets().sendGameMessage("After all I've been through I think I can handle myself...");
				bug.set(false);
			} else if (npc.getId() == 398 || npc.getId() == 399){
				player.getDialogueManager().startDialogue("SimpleNPCMessage", npc.getId(),
						"Welcome. I hope you enjoy your time in the Legends' Guild.");bug.set(false);}
			else if (npc.getId() >= 1 && npc.getId() <= 6 || npc.getId() >= 7875 && npc.getId() <= 7884){
				player.getDialogueManager().startDialogue("Man", npc.getId());bug.set(false);}
			else if (npc.getId() == 13634) {
				ClanVexillum.openPlayerClanDetails(player);bug.set(false);
			} else if (npc.getId() == 198){
				player.getDialogueManager().startDialogue("Guildmaster", npc.getId());bug.set(false);}
			else if (npc.getId() == 11509){
				player.getDialogueManager().startDialogue("CommodreTyr", npc.getId());bug.set(false);}
			else if (npc.getId() == 2417 || npc.getId() == 20629){
				WildyWyrm.handleInspect(player, npc);bug.set(false);}
			else if (npc.getId() == 1208){
				player.getDialogueManager().startDialogue("QuartsMaster");bug.set(false);}
			else if (npc.getId() == 9707){
				player.getDialogueManager().startDialogue("FremennikShipmaster", npc.getId(), true);bug.set(false);}
			else if (npc.getId() == 9708){
				player.getDialogueManager().startDialogue("FremennikShipmaster", npc.getId(), false);bug.set(false);}
			else if (npc.getId() == 456){
				player.getDialogueManager().startDialogue("FatherAereck", npc.getId());bug.set(false);}
			else if (npc.getId() == 1793){
				player.getDialogueManager().startDialogue("CraftingMaster", npc.getId());bug.set(false);}
			else if (npc.getId() == 3344 || npc.getId() == 3345){
				MutatedZygomites.transform(player, npc);bug.set(false);}
			else if (npc.getId() == 5585){
				player.getDialogueManager().startDialogue("SkillAlchemist");bug.set(false);}
			else if (npc.getId() == 562){ // 107
				player.getDialogueManager().startDialogue("CandleMaker");bug.set(false);}
			else if (npc.getId() == 576){
				player.getDialogueManager().startDialogue("Harry");bug.set(false);}
			else if (npc.getId() == 563){
				player.getDialogueManager().startDialogue("Arhein");bug.set(false);}
			else if (npc.getId() == 575){
				player.getDialogueManager().startDialogue("Hickton");bug.set(false);}
			else if (npc.getId() == 2305){
				player.getDialogueManager().startDialogue("Vannesa");bug.set(false);}
			else if (npc.getId() == 8527){ // nomad capes
				player.getDialogueManager().startDialogue("SimpleNPCMessage", 8527,
						player.getQuestManager().completedQuest(Quests.NOMADS_REQUIEM)
								? "Right click me to see my rewards brother!"
								: "You must kill nomad inside the tent there to see my rewards.");bug.set(false);}
			else if (npc.getId() == 6539){
				player.getDialogueManager().startDialogue("Nastroth", npc.getId());bug.set(false);}
			else if (npc.getId() == 6537){
				player.getDialogueManager().startDialogue("Mandrith_Nastroth", npc.getId());bug.set(false);}
			else if (npc.getId() == 2676){
				player.getDialogueManager().startDialogue("MakeOverMage", npc.getId(), 0);bug.set(false);}
			else if (npc.getId() == 598){
				player.getDialogueManager().startDialogue("Hairdresser", npc.getId());bug.set(false);}
			else if (npc.getId() == 548){
				player.getDialogueManager().startDialogue("Thessalia", npc.getId());bug.set(false);}
			else if (npc.getId() == 659){
				player.getDialogueManager().startDialogue("PartyPete", npc.getId());bug.set(false);}
			else if (npc.getId() == 5915){
				player.getDialogueManager().startDialogue("Scribe", npc.getId(), 20709);bug.set(false);}
			else if (npc.getId() == 13633){
				player.getDialogueManager().startDialogue("Scribe", npc.getId(), 20708);bug.set(false);}
			else if (npc.getId() == 579){
				player.getDialogueManager().startDialogue("DrogoDwarf", npc.getId());bug.set(false);}
			else if (npc.getId() == 3799) {// void general store
				player.getDialogueManager().startDialogue("GeneralStore", npc.getId(), 122);bug.set(false);}
			else if (npc.getId() == 471){ // tree gnome village general store
				player.getDialogueManager().startDialogue("GeneralStore", npc.getId(), 124);bug.set(false);}
			else if (npc.getId() == 582){ // dwarves general store
				player.getDialogueManager().startDialogue("GeneralStore", npc.getId(), 31);bug.set(false);}
			else if (npc.getId() == 1917){
				player.getDialogueManager().startDialogue("GeneralStore", npc.getId(), 106);bug.set(false);}
			else if (npc.getId() == 932){
				player.getDialogueManager().startDialogue("GeneralStore", npc.getId(), 113);bug.set(false);}
			else if (npc.getId() == 1040){
				player.getDialogueManager().startDialogue("GeneralStore", npc.getId(), 81);bug.set(false);}
			else if (npc.getId() == 526){ // donator zone
				player.getDialogueManager().startDialogue("GeneralStore", npc.getId(), 1);bug.set(false);}
			else if (npc.getId() == 209){ // cannon shop
				player.getDialogueManager().startDialogue("Nulodion", npc.getId());bug.set(false);}
			else if (npc.getId() == 15099){
				player.getDialogueManager().startDialogue("Freda");bug.set(false);}
			else if (npc.getId() == 4358){
				player.getDialogueManager().startDialogue("CaveyDavey");bug.set(false);}
			else if (npc.getId() == 15087){
				player.getDialogueManager().startDialogue("DeathPlatueSoldier");bug.set(false);}
			else if (npc.getId() == 1334){
				player.getDialogueManager().startDialogue("Jossik", npc.getId());bug.set(false);}
			else if (npc.getId() == 904){
				player.getDialogueManager().startDialogue("ChamberGaurdian", npc.getId());bug.set(false);}
			else if (npc.getId() == 15193){
				player.getDialogueManager().startDialogue("KilnCapeTrade", npc.getId());bug.set(false);}
			else if (npc.getId() == 2259){
				player.getDialogueManager().startDialogue("MageOfZamorak");bug.set(false);}
			else if (npc.getId() == 903){
				player.getDialogueManager().startDialogue("Lundail", npc.getId());bug.set(false);}
			else if (npc.getId() == 905){
				player.getDialogueManager().startDialogue("Kolodion", npc.getId());bug.set(false);}
			else if (npc.getId() == 528 || npc.getId() == 529){ // edge
				player.getDialogueManager().startDialogue("GeneralStore", npc.getId(), 1);bug.set(false);}
			else if (npc.getId() == 522 || npc.getId() == 523) {// varrock
				player.getDialogueManager().startDialogue("GeneralStore", npc.getId(), 8);bug.set(false);}
			else if (npc.getId() == 520 || npc.getId() == 521){ // lumbridge
				player.getDialogueManager().startDialogue("GeneralStore", npc.getId(), 1);bug.set(false);}
			else if (npc.getId() == 2825 || npc.getId() == 2826){
				player.getDialogueManager().startDialogue("PiratePete", npc);bug.set(false);}
			else if (npc.getId() == 1301){
				player.getDialogueManager().startDialogue("Yrsa");bug.set(false);}
			else if (npc.getId() == 594){
				player.getDialogueManager().startDialogue("Nurmof", npc.getId());bug.set(false);}
			else if (npc.getId() == 665){
				player.getDialogueManager().startDialogue("BootDwarf", npc.getId());bug.set(false);}
			else if (npc.getId() == 5913) {// Aubury
				player.getDialogueManager().startDialogue("Aubury", npc);bug.set(false);}
			else if (npc.getId() == 382 || npc.getId() == 3294 || npc.getId() == 4316){
				player.getDialogueManager().startDialogue("MiningGuildDwarf", npc.getId(), false);bug.set(false);}
			else if (npc.getId() == 3295){
				player.getDialogueManager().startDialogue("MiningGuildDwarf", npc.getId(), true);bug.set(false);}
			else if (npc.getId() == 537){
				player.getDialogueManager().startDialogue("Scavvo", npc.getId());bug.set(false);}
			else if (npc.getId() == 536){
				player.getDialogueManager().startDialogue("GeneralStore", npc.getId(), 17);bug.set(false);}
			else if (npc.getId() == 2617){
				player.getDialogueManager().startDialogue("TzHaarMejJal", npc.getId());bug.set(false);}
			else if (npc.getId() == 3802 || npc.getId() == 6140 || npc.getId() == 6141){
				player.getDialogueManager().startDialogue("LanderSquire", npc.getId());bug.set(false);}
			else if (npc.getId() == 2618){
				player.getDialogueManager().startDialogue("TzHaarMejKah", npc.getId());bug.set(false);}
			else if (npc.getId() == 1595){
				player.getDialogueManager().startDialogue("SaniBoch", false);bug.set(false);}
			else if (npc.getId() == 15149){
				player.getDialogueManager().startDialogue("MasterOfFear", 0);bug.set(false);}
			else if (npc.getId() == 11519){
				player.getDialogueManager().startDialogue("Mariah");bug.set(false);}
			else if (npc.getId() == 11516){
				player.getDialogueManager().startDialogue("TerryGord");bug.set(false);}
			else if (npc.getId() == 11517){
				player.getDialogueManager().startDialogue("MrsGord");bug.set(false);}
			else if (npc.getId() == 3797){
				player.getDialogueManager().startDialogue("RepairSquire");bug.set(false);}
			else if (npc.getId() == 3790 || npc.getId() == 3791 || npc.getId() == 3792){
				player.getDialogueManager().startDialogue("VoidKnightExchange", npc.getId());bug.set(false);}
			else if (npc.getId() == 5445){
				player.getDialogueManager().startDialogue("SimpleNPCMessage", npc.getId(), "Penguinn rules!");bug.set(false);}
			else if (npc.getName().toLowerCase().contains("impling")) {
				FlyingEntityHunter.captureFlyingEntity(player, npc);bug.set(false);
			} else if (npc.getId() == 7065 || npc.getId() == 7066) {
				player.getDialogueManager().startDialogue("DefaultTradeDialouge", npc.getId(), stream);bug.set(false);
			} else {
				switch (npc.getDefinitions().name.toLowerCase()) {
				case "void knight":
					player.getDialogueManager().startDialogue("VoidKnightExchange", npc.getId());bug.set(false);
					break;
				case "master crafter":
					player.getDialogueManager().startDialogue("SimpleNPCMessage", npc.getId(),
							"Welcome to the crafting guild.");bug.set(false);
					break;
				case "sheep":
					SheepShearing.shearAttempt(player, npc);bug.set(false);
					break;
				case "hefin musician":
				case "musician":
				case "drummer":
					if (player.isResting()) {
						player.stopAll();
						return;
					}
					if (player.getEmotesManager().isDoingEmote()) {
						player.getPackets().sendGameMessage("You can't rest while perfoming an emote.");
						return;
					}
					if (player.isLocked()) {
						player.getPackets().sendGameMessage("You can't rest while perfoming an action.");
						return;
					}
					player.stopAll();
					player.getActionManager().setAction(new Rest(true));
					bug.set(false);
					break;
				case "lucille":
					if (!Settings.underDevelopment(player)) {
						player.getDialogueManager().startDialogue("Lucille");bug.set(false);
					}
					break;
				default:
					 player.getPackets().sendGameMessage("Nothing interesting happens.");
					if (Settings.DEBUG) {
						System.out.println("cliked 1 at npc[" + npc.getIndex() + "] id : " + npc.getId() + ", "
								+ npc.getX() + ", " + npc.getY() + ", " + npc.getPlane());
						player.getPackets().sendGameMessage("Click 1: " + npc.getName() + " (id: " + npc.getId() + ") "
								+ npc.getX() + ", " + npc.getY() + ", " + npc.getPlane() + ".", true);
					}
					bug.set(true);
					break;
				}
			}
			if(bug.get()) {
				Bugsystem.addNPCBug(1, npc.getName(), npc.getId(), npc.getX(), npc.getY(), npc.getPlane());
			}

		}));
	}

	public static void handleOption2(final Player player, InputStream stream) {
		boolean forceRun = stream.readUnsignedByteC() == 1;
		int npcIndex = stream.readUnsignedShortLE128();
		final NPC npc = World.getNPCs().get(npcIndex);
		if (npc == null || npc.isCantInteract() || npc.isDead() || npc.hasFinished()
				|| !player.getMapRegionsIds().contains(npc.getRegionId()) || player.isLocked())
			return;
		player.stopAll();
		if (forceRun)
			player.setRun(forceRun);
		if (npc.getId() == 17161) {
			npc.faceEntity(player);
			player.getDialogueManager().startDialogue("VoragoInstantChallenge");
		}
		if (npc.getId() == 4296 || npc.getDefinitions().name.contains("Banker")
				|| npc.getDefinitions().name.contains("banker")) {
			player.setRouteEvent(new RouteEvent(npc, () -> {
				player.faceEntity(npc);
				if (!player.withinDistance(npc, 2))
					return;
				npc.faceEntity(player);
				player.getDialogueManager().startDialogue("Banker", npc.getId());
			}, true));
			return;
		}
		if (npc.getDefinitions().name.toLowerCase().contains("tool leprechaun") || npc.getId() == 20110) {
			FarmingStore.sendInterface(player);
		}
		if (npc.getId() == 4250) {
			player.setRouteEvent(new RouteEvent(npc, () -> {
				player.faceEntity(npc);
				if (!player.withinDistance(npc, 2))
					return;
				npc.faceEntity(player);
				Sawmill.openPlanksConverter(player);
			}, true));
			return;
		}
		if (npc.getDefinitions().name.toLowerCase().equals("grand exchange clerk")) {
			player.setRouteEvent(new RouteEvent(npc, () -> {
				player.faceEntity(npc);
				if (!player.withinDistance(npc, 2))
					return;
				npc.faceEntity(player);
				player.getDialogueManager().startDialogue("GrandExchangeClerk", npc.getId());
			}, true));
			return;
		}
		player.setRouteEvent(new RouteEvent(npc, () -> {
			npc.resetWalkSteps();
			player.faceEntity(npc);
			if (player.getTreasureTrailsManager().useNPC(npc))
				return;
			FishingSpots spot = FishingSpots.forId(npc.getId() | (2 << 24));
			if (spot != null) {
				player.getActionManager().setAction(new Fishing(spot, npc));
				return;
			}
			npc.faceEntity(player);
			if (!player.getControllerManager().processNPCClick2(npc))
				return;
			if (npc.getDefinitions().options != null && npc.getDefinitions().options[2] != null
					&& npc.getDefinitions().options[2].equalsIgnoreCase("bank")) {
				player.getDialogueManager().startDialogue("ChooseBank");
				return;
			}
			if (npc.getId() == 961) {
				player.getEffectsManager().removeEffect(EffectType.POISON);
				player.setHitpoints(player.getMaxHitpoints());
				return;
			}
			if (npc.getId() == 17143) {
				player.getDialogueManager().startDialogue("Ocellus", npc.getId(), (byte) 2);
				return;
			}
			if (npc.getId() == 599) {
				player.getDialogueManager().startDialogue("MakeOverMage", npc.getId(), 0);
				return;
			}
			if (npc.getId() == 20275) {
				HefinAgility.lightCreature(player, 2);
			} else if (npc.getId() == 15451 && npc instanceof FireSpirit) {
				FireSpirit spirit = (FireSpirit) npc;
				spirit.giveReward(player);
				return;
			}
			switch (npc.getId()) {
				case 836:
					ShopsHandler.openShop(player, 1000);
					break;
			case 2153:
				ShopsHandler.openShop(player, 229);
				break;
			// Challenge Mistress Fara
			case 15780:
				player.getDialogueManager().startDialogue("ChallengeMistressFara", npc.getId(), 2);
				break;
			case 13697: // Troll Invasion
				player.getDialogueManager().startDialogue("CaptainJute", npc.getId(), 50);
				break;
			case 16973:
				player.getDialogueManager().startDialogue("GuthixianHighDruid", npc, 2);
				break;
			case 20309:
				if (player.getSkills().getTotalLevel() <= 1500) {
					player.getDialogueManager().startDialogue("SimpleNPCMessage", npc.getId(),
							"Sorry, but I only sell to players with a total level of at least 1500.");
					return;
				}
				ShopsHandler.openShop(player, 198);
				break;
			case 3404:
				player.getDialogueManager().startDialogue("TeplinMacaganD", npc.getId(), 2);
				break;
			case 20285:
				RecipeShop.sendInterface(player);
				break;
			/*
			 * Diango
			 */
			case 970:
				player.getDialogueManager().startDialogue("DiangoD");
				break;
			case 22942:
				player.getDialogueManager().startDialogue("QuarterMasterGully", npc.getId());
				break;
			case 22984:
				ShopsHandler.openShop(player, 200);
				break;
			case 17065: // Warbands rewards
				player.getDialogueManager().startDialogue("Quercus", npc.getId(), 2);
				break;

			/*
			 * Arhein
			 */
			case 563:
				ShopsHandler.openShop(player, 108);
				break;

			/*
			 * Flynn's Mace Shop
			 */
			case 580:
				ShopsHandler.openShop(player, 44);
				break;

			/*
			 * Cassies Shield Shop
			 */
			case 577:
				ShopsHandler.openShop(player, 45);
				break;
			/*
			 * Wizard In Runespan
			 */
			case 15418:
				player.setNextWorldTile(new WorldTile(3103, 3158, 3));
				player.getControllerManager().forceStop();
				break;
			/*
			 * Hair dresser
			 */
			case 598:
				PlayerLook.openHairdresserSalon(player);
				break;

			/*
			 * Gardener
			 */
			case 1217:
				player.getDialogueManager().startDialogue("Gardener", npc.getId());
				break;

			/*
			 * Make over mage
			 */
			case 2676:
				PlayerLook.openMageMakeOver(player);
				break;

			/*
			 * Gardener
			 */
			case 3234:
				player.getDialogueManager().startDialogue("Gardener", npc.getId());
				break;

			/*
			 * Aggie
			 */
			case 922:
				player.getDialogueManager().startDialogue("Aggie", -3);
				break;

			/*
			 * Diango
			 */
			case 19921:
				player.getDialogueManager().startDialogue("Diango", npc.getId());
				break;

			case 4476:
				player.getDialogueManager().startDialogue("SimpleNPCMessage", npc.getId(),
						"Search the anonymous looking door behind you.");
				break;

			/*
			 * Olivia
			 */
			case 2233:
				ShopsHandler.openShop(player, 99);
				break;

			/*
			 * Dragontooth teleporter
			 */
			case 1704:
				if (player.getX() >= 3700 && player.getX() <= 3703) {
					player.setNextWorldTile(new WorldTile(3791, 3559, 0));
				} else {
					player.setNextWorldTile(new WorldTile(3702, 3487, 0));
				}
				break;

			/*
			 * Sedridor
			 */
			case 300:
				npc.setNextForceTalk(new ForceTalk("Senventior Disthine Molenko!"));
				player.lock(2);
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2909, 4834, 0));
				break;

			/*
			 * Xuan
			 */
			case 13727:
				player.getDialogueManager().startDialogue("Xuan", npc.getId(), 2);
				break;

			/*
			 * Varrock Sword Shop
			 */
			case 552:
				ShopsHandler.openShop(player, 4);
				break;

			case 551:
				ShopsHandler.openShop(player, 4);
				break;
			/*
			 * Horvik
			 */
			case 549:
				ShopsHandler.openShop(player, 8);
				break;
			/*
			 * Zaff
			 */
			case 546:
				ShopsHandler.openShop(player, 3);
				break;
			/*
			 * Thessalia
			 */
			case 548:
				ShopsHandler.openShop(player, 2);
				break;
			/*
			 * Iffie
			 */
			case 2778:
				ShopsHandler.openShop(player, 161);
				break;
			/*
			 * Lowe
			 */
			case 550:
				ShopsHandler.openShop(player, 7);
				break;

			/*
			 * Aubury
			 */
			case 5913:
				ShopsHandler.openShop(player, 6);
				break;

			case 522:
			case 526:
				ShopsHandler.openShop(player, 1);
				break;

			case 523:
				ShopsHandler.openShop(player, 1);
				break;

			// Pet Shop Owner
			case 6893:
				ShopsHandler.openShop(player, 163);
				break;
			// Pikkupstix
			case 6988:
				ShopsHandler.openShop(player, 164);
				break;
			// Jatix
			case 587:
				ShopsHandler.openShop(player, 186);
				break;

			// Brian
			case 1860:
				ShopsHandler.openShop(player, 100);
				break;

			// Rommik
			case 585:
				ShopsHandler.openShop(player, 101);
				break;

			// Bard Roberts
			case 3509:
				player.getDialogueManager().startDialogue("BardRoberts", npc.getId());
				break;
			// Gerrant
			case 558:
				ShopsHandler.openShop(player, 31);
				break;
			// Wydin
			case 557:
				ShopsHandler.openShop(player, 30);
				break;
			// Brian
			case 559:
				ShopsHandler.openShop(player, 33);
				break;
			// Betty
			case 583:
				ShopsHandler.openShop(player, 34);
				break;
			// Grum
			case 556:
				ShopsHandler.openShop(player, 32);
				break;

			case 19727:
				ShopsHandler.openShop(player, 197);
				break;

			/*
			 * Bob's Axes Shop
			 */
			case 519:
				ShopsHandler.openShop(player, 58);
				break;
			/*
			 * Musician
			 */

			case 29:
				player.getDialogueManager().startDialogue("MusicianD", npc.getId());
				break;

			/*
			 * Unknown
			 */
			case 520:
				ShopsHandler.openShop(player, 1);
				break;

			case 22980: // Fish oil
				ShopsHandler.openShop(player, 213);
				break;
			case 22983: // Torts R Us
				ShopsHandler.openShop(player, 214);
				break;
			case 9701: // Food Shop
				ShopsHandler.openShop(player, 200);
				break;

			case 521:
				ShopsHandler.openShop(player, 1);
				break;

			/*
			 * Fremennik Sailor
			 */
			case 9708:
				FremennikShipmaster.sail(player, false);
				break;

			/*
			 * Shop Keeper || Shop Assistant - Alkharid General Store
			 */
			case 524:
			case 529:
			case 528:
			case 525:
				ShopsHandler.openShop(player, 173);
				break;

			/*
			 * Gem Trader Shop
			 */
			case 540:
				ShopsHandler.openShop(player, 35);
				break;

			/*
			 * Zeke
			 */
			case 541:
				ShopsHandler.openShop(player, 36);
				break;

			/*
			 * Louie Legs
			 */
			case 542:
				ShopsHandler.openShop(player, 37);
				break;

			/*
			 * Ranael
			 */
			case 544:
				ShopsHandler.openShop(player, 38);
				break;

			/*
			 * Dommik
			 */
			case 545:
				ShopsHandler.openShop(player, 13);
				break;

			/*
			 * Dealga
			 */
			case 11475:
				ShopsHandler.openShop(player, 185);
				break;

			/*
			 * Captain Dalbur
			 */
			case 3809:
				player.getDialogueManager().startDialogue("CaptainDalbur", npc.getId());
				break;

			/*
			 * Faruq Shop
			 */
			case 9159:
				ShopsHandler.openShop(player, 171);
				break;

			/*
			 * Vanessa
			 */
			case 2305:
				ShopsHandler.openShop(player, 188);
				break;

			case 13634:
				if (player.getUsername().equalsIgnoreCase(ClanVexillum.getClanPlanterUsername(player))) {
					npc.applyHit(new Hit(player, npc.getMaxHitpoints(), HitLook.REFLECTED_DAMAGE));
					player.getInventory().addItem(20709, 1);
					player.getClanManager().getClan().setClanPlanterUsernameToNull();
					System.out.println(player.getClanManager().getClan().getClanPlanterUsername());
				} else
					player.getPackets().sendGameMessage("This is not your vexillum.");
				break;
			}
			PickPocketableNPC pocket = PickPocketableNPC.get(npc.getId());
			if (pocket != null) {
				player.getActionManager().setAction(new PickPocketAction(npc, pocket));
				return;
			} else if (npc instanceof Familiar) {
				Familiar familiar = (Familiar) npc;
				if (player.getFamiliar() != familiar) {
					player.getPackets().sendGameMessage("That isn't your familiar.");
					return;
				} else if (familiar.getDefinitions().hasOption("cure")) {
					if (!player.getEffectsManager().hasActiveEffect(EffectType.POISON)) {
						player.getPackets().sendGameMessage("Your arent poisoned or diseased.");
						return;
					} else {
						player.getFamiliar().drainSpecial(2);
						player.setAntipoisonDelay(200);
					}
				} else if (familiar.getDefinitions().hasOption("interact")) {
					Object[] paramaters = new Object[2];
					Pouch pouch = player.getFamiliar().getPouch();
					if (pouch == Pouch.SPIRIT_GRAAHK) {
						paramaters[0] = "Karamja's Hunter Area";
						paramaters[1] = new WorldTile(2787, 3000, 0);
					} else if (pouch == Pouch.SPIRIT_KYATT) {
						paramaters[0] = "Piscatorius Hunter Area";
						paramaters[1] = new WorldTile(2339, 3636, 0);
					} else if (pouch == Pouch.SPIRIT_LARUPIA) {
						paramaters[0] = "Feldip Hills Hunter Area";
						paramaters[1] = new WorldTile(2557, 2913, 0);
					} else if (pouch == Pouch.ARCTIC_BEAR) {
						paramaters[0] = "Rellekka Hills Hunter Area";
						paramaters[1] = new WorldTile(2721, 3779, 0);
					} else if (pouch == Pouch.LAVA_TITAN) {
						paramaters[0] = "Lava Maze - *Deep Wilderness*";
						paramaters[1] = new WorldTile(3028, 3840, 0);
					} else
						return;
					player.getDialogueManager().startDialogue("FamiliarInspection", paramaters[0], paramaters[1]);
				}
				return;
			} else if (npc instanceof GraveStone) {
				GraveStone grave = (GraveStone) npc;
				grave.repair(player, false);
				return;
			}
			switch (npc.getDefinitions().name.toLowerCase()) {
			case "void knight":
				CommendationExchange.openExchangeShop(player);
				break;
			}
			Object[] shipAttributes = BoatingDialouge.getBoatForShip(npc.getId());
			if (shipAttributes != null) {
				CarrierTravel.sendCarrier(player, (Carrier) shipAttributes[0], (boolean) shipAttributes[1]);
			} else if (npc.getId() == 9707)
				FremennikShipmaster.sail(player, true);
			else if (npc.getId() == 9708)
				FremennikShipmaster.sail(player, false);
			else if (npc.getId() == 3777)
				player.getDoomsayerManager().openDoomsayer();
			else if (SlayerMaster.startInteractionForId(player, npc.getId(), 2)) {
			} else if (npc.getId() == 2619 || npc.getId() == 13455 || npc.getId() == 2617 || npc.getId() == 2618
					|| npc.getId() == 15194)
				player.getDialogueManager().startDialogue("Banker", npc.getId());
			else if ((npc.getId() == 14849 || npc.getId() == 1610) && npc instanceof ConditionalDeath)
				((ConditionalDeath) npc).useHammer(player);
			else if (npc.getId() == 2291)
				player.getDialogueManager().startDialogue("RugMerchantD", true, 0);
			else if (npc.getId() == 9711)
				DungeonRewardShop.openRewardShop(player);
			else if (npc.getId() == 2824 || npc.getId() == 1041 || npc.getId() == 20281)
				player.getDialogueManager().startDialogue("TanningD", npc.getId(), TanningData.LEATHER);
			else if (npc.getId() == 8527) // nomad capes
				if (player.getQuestManager().completedQuest(Quests.NOMADS_REQUIEM))
					ShopsHandler.openShop(player, 51);
				else
					player.getDialogueManager().startDialogue("SimpleNPCMessage", 8527,
							"You must kill nomad inside the tent there to see my rewards.");
			else if (npc.getId() == 1595)
				player.getDialogueManager().startDialogue("SaniBoch", false);
			else if (npc.getId() == 3810)
				GnomeGlider.sendInterface(player);
			else if (npc.getId() == 3809)
				GnomeGlider.sendInterface(player);
			else if (npc.getId() == 3812)
				GnomeGlider.sendInterface(player);
			else if (npc.getId() == 1800)
				GnomeGlider.sendInterface(player);
			else if (npc.getId() == 3811)
				GnomeGlider.sendInterface(player);
			else if (npc.getId() == 20299)
				GnomeGlider.sendInterface(player);
			else if (npc.getId() == 15149)
				player.getDialogueManager().startDialogue("MasterOfFear", 3);
			else if (npc.getId() == 462) // Aubury
				RuneEssenceController.teleport(player, npc);
			else if (npc.getId() == 171) // Brimstail
				RuneEssenceController.teleport(player, npc);
			else if (npc instanceof Pet) {
				if (npc != player.getPet()) {
					player.getPackets().sendGameMessage("This isn't your pet!");
					return;
				}
				Pet pet = player.getPet();
				player.getPackets().sendMessage(99,
						"Pet [id=" + pet.getId() + ", hunger=" + pet.getDetails().getHunger() + ", growth="
								+ pet.getDetails().getGrowth() + ", stage=" + pet.getDetails().getStage() + "].",
						player);
			} else {
				// player.getPackets().sendGameMessage("Nothing
				// interesting
				// happens.");
				if (Settings.DEBUG) {
					System.out.println("cliked 2 at npc id : " + npc.getId() + ", " + npc.getX() + ", " + npc.getY()
							+ ", " + npc.getPlane());
					player.getPackets().sendGameMessage("Click 2: " + npc.getName() + " (id: " + npc.getId() + ") "
							+ npc.getX() + ", " + npc.getY() + ", " + npc.getPlane() + ".", true);
				}
				//Bugsystem.addNPCBug(2, npc.getName(), npc.getId(), npc.getX(), npc.getY(), npc.getPlane());
			}
		}));
	}

	public static void handleOption3(final Player player, InputStream stream) {
		boolean forceRun = stream.readUnsignedByteC() == 1;
		int npcIndex = stream.readUnsignedShortLE128();
		final NPC npc = World.getNPCs().get(npcIndex);
		if (npc == null || npc.isCantInteract() || npc.isDead() || npc.hasFinished()
				|| !player.getMapRegionsIds().contains(npc.getRegionId()) || player.isLocked())
			return;
		player.stopAll(false);
		if (forceRun)
			player.setRun(forceRun);
		if (npc.getId() == 4296 || npc.getDefinitions().name.toLowerCase().contains("banker")) {
			player.setRouteEvent(new RouteEvent(npc, () -> {
				player.faceEntity(npc);
				if (!player.withinDistance(npc, 2))
					return;
				npc.faceEntity(player);
				player.getGeManager().openCollectionBox();
			}, true));
			return;
		}
		if (npc.getDefinitions().name.toLowerCase().contains("tool leprechaun") || npc.getId() == 20110) {
			player.getDialogueManager().startDialogue("SimpleNPCMessage", npc.getId(), "I am not teleporting anyone.");
		}
		if (npc.getId() == 4250) {
			player.setRouteEvent(new RouteEvent(npc, () -> {
				player.faceEntity(npc);
				if (!player.withinDistance(npc, 2))
					return;
				npc.faceEntity(player);
				ShopsHandler.openShop(player, 189);
			}, true));
			return;
		}
		if (npc.getDefinitions().name.toLowerCase().equals("grand exchange clerk")) {
			player.setRouteEvent(new RouteEvent(npc, () -> {
				player.faceEntity(npc);
				if (!player.withinDistance(npc, 2))
					return;
				npc.faceEntity(player);
				player.getDialogueManager().startDialogue("OffersOrHistory", npc.getId());
			}, true));
			return;
		}
		player.setRouteEvent(new RouteEvent(npc, () -> {
			npc.resetWalkSteps();
			if (!player.getControllerManager().processNPCClick3(npc))
				return;
			player.faceEntity(npc);
			if (npc.getDefinitions().options != null && npc.getDefinitions().options[3] != null
					&& npc.getDefinitions().options[3].equalsIgnoreCase("bank")) {
				player.getDialogueManager().startDialogue("ChooseBank");
				return;
			}
			if (npc.getId() >= 8837 && npc.getId() <= 8839) {
				MiningBase.prospect(player, "You examine the remains...",
						"The remains contain traces of living minerals.");
				return;
			}
			if (npc instanceof GraveStone) {
				GraveStone grave = (GraveStone) npc;
				grave.repair(player, true);
				return;
			}
			npc.faceEntity(player);
			if (npc.getId() == 17143) {
				player.getDialogueManager().startDialogue("Ocellus", npc.getId(), (byte) 3);
				return;
			}
			switch (npc.getId()) {
			case 13697: // Troll Invasion
				player.getDialogueManager().startDialogue("CaptainJute", npc.getId(), 51);
				break;
			case 3404:
				player.getDialogueManager().startDialogue("TeplinMacaganD", npc.getId(), 3);
				break;
			case 20309:
				if (player.getSkills().getTotalLevel() <= 1500) {
					player.getDialogueManager().startDialogue("SimpleNPCMessage", npc.getId(),
							"Sorry, but I only sell to players with a total level of at least 1500.");
					return;
				}
				ShopsHandler.openShop(player, 198);
				break;
			case 20285:
				RecipeShop.sendInterface(player);
				break;

			/*
			 * Aubury
			 */
			case 5913:
				ShopsHandler.openShop(player, 6);
				break;

			/*
			 * Zaff
			 */
			case 546:
				ShopsHandler.openShop(player, 10);
				break;

			case 16973:
				player.getDialogueManager().startDialogue("GuthixianHighDruid", npc, 3);
				break;

			/*
			 * Thessalia
			 */
			case 548:
				// PlayerLook.openThessaliasMakeOver(player);
				PlayerLook.openCharacterCustomizing(player, false);
				break;

			case 4476:
				player.getDialogueManager().startDialogue("GurdianMummy", npc.getId(), 2);
				break;

			/*
			 * Max
			 */
			case 3705:
			case 3374:
				if (player.getSkills().getLevel(Skills.ATTACK) < 99 || player.getSkills().getLevel(Skills.STRENGTH) < 99
						|| player.getSkills().getLevel(Skills.DEFENCE) < 99
						|| player.getSkills().getLevel(Skills.RANGED) < 99
						|| player.getSkills().getLevel(Skills.PRAYER) < 99
						|| player.getSkills().getLevel(Skills.MAGIC) < 99
						|| player.getSkills().getLevel(Skills.RUNECRAFTING) < 99
						|| player.getSkills().getLevel(Skills.CONSTRUCTION) < 99
						|| player.getSkills().getLevel(Skills.DUNGEONEERING) < 99
						|| player.getSkills().getLevel(Skills.HITPOINTS) < 99
						|| player.getSkills().getLevel(Skills.AGILITY) < 99
						|| player.getSkills().getLevel(Skills.HERBLORE) < 99
						|| player.getSkills().getLevel(Skills.THIEVING) < 99
						|| player.getSkills().getLevel(Skills.CRAFTING) < 99
						|| player.getSkills().getLevel(Skills.FLETCHING) < 99
						|| player.getSkills().getLevel(Skills.SLAYER) < 99
						|| player.getSkills().getLevel(Skills.HUNTER) < 99
						|| player.getSkills().getLevel(Skills.MINING) < 99
						|| player.getSkills().getLevel(Skills.SMITHING) < 99
						|| player.getSkills().getLevel(Skills.FISHING) < 99
						|| player.getSkills().getLevel(Skills.COOKING) < 99
						|| player.getSkills().getLevel(Skills.FIREMAKING) < 99
						|| player.getSkills().getLevel(Skills.WOODCUTTING) < 99
						|| player.getSkills().getLevel(Skills.FARMING) < 99
						|| player.getSkills().getLevel(Skills.SUMMONING) < 99
						|| player.getSkills().getLevel(Skills.CONSTRUCTION) < 99
						|| player.getSkills().getLevel(Skills.DUNGEONEERING) < 99
						|| player.getSkills().getLevel(Skills.DIVINATION) < 99) {
					// || player.getSkills().getLevel(Skills.INVENTION)
					// <
					// 99
					player.getPackets().sendGameMessage("You need to be maxed to access Max's store.");
					return;
				}

				player.getDialogueManager().startDialogue("MaxCapeShop", npc.getId());

				break;

			/*
			 * Bob Barter
			 */
			case 6529:
				player.getDialogueManager().startDialogue("BobBarterD", npc.getId());
				break;
			case 19921:
				System.out.println(player.getUsername() + " has attempted to use the GTL vote system.");
				break;
			// Magestix
			case 14866:
				ShopsHandler.openShop(player, 162);
				break;
			// Jacquelyn Manslaughter
			case 14868:
				ShopsHandler.openShop(player, 54);
				break;
			// Poletax
			case 14854:
				ShopsHandler.openShop(player, 165);
				break;
			// Head Farmer Jones
			case 14860:
				ShopsHandler.openShop(player, 167);
				break;
			// Tobias Bronzearms
			case 14870:
				ShopsHandler.openShop(player, 169);
				break;
			// Martin Steelweaver
			case 14874:
				ShopsHandler.openShop(player, 170);
				break;
			}
			if (SlayerMaster.startInteractionForId(player, npc.getId(), 3))
				ShopsHandler.openShop(player, 54);
			else if (npc.getId() == 2619)
				player.getGeManager().openCollectionBox();
			else if (npc.getId() >= 4650 && npc.getId() <= 4656 || npc.getId() == 7065 || npc.getId() == 7066) // Trader
				CarrierTravel.openCharterInterface(player);
			else if (npc.getId() == 1526)
				CastleWars.openCastleWarsTicketExchange(player);
			else if (npc.getId() == 14877)
				player.getDialogueManager().startDialogue("TanningD", npc.getId(), TanningData.LEATHER);
			else if (npc.getId() == 548)
				PlayerLook.openCharacterCustomizing(player, false);
			else if (npc.getId() == 1301)
				PlayerLook.openYrsaShop(player);
			else if (npc.getId() == 6892 || npc.getId() == 6893)
				PetShopOwner.sellShards(player);
			else if (npc.getId() == 6524) {
				player.getDialogueManager().startDialogue("SimpleNPCMessage", npc.getId(), "Her ya go chap.");
				Drinkables.decantPotsInv(player);
			} else if (npc.getId() == 2259)
				AbbysObsticals.teleport(player, npc);
			else if (npc.getId() == 4287)
				player.getDialogueManager().startDialogue("GamfriedShield");
			else if (npc.getId() == 359)
				ShopsHandler.openShop(player, 212);
			else if (npc.getId() == 5532)
				SorceressGarden.teleportToSorceressGardenNPC(npc, player);
			else if (npc.getId() == 1334) // Book Shop
				ShopsHandler.openShop(player, 35);
			else if (npc.getId() == 5913) // Aubury
				ShopsHandler.openShop(player, 184);
			// else
			// player.getPackets().sendGameMessage("Nothing interesting
			// happens.");
		}));
		if (Settings.DEBUG) {
			System.out.println("cliked 3 at npc id : " + npc.getId() + ", " + npc.getX() + ", " + npc.getY() + ", "
					+ npc.getPlane());
			player.getPackets().sendGameMessage("Click 3: " + npc.getName() + " (id: " + npc.getId() + ") " + npc.getX()
					+ ", " + npc.getY() + ", " + npc.getPlane() + ".", true);
		}
		Bugsystem.addNPCBug(3, npc.getName(), npc.getId(), npc.getX(), npc.getY(), npc.getPlane());
	}

	public static void handleOption4(final Player player, InputStream stream) {
		boolean forceRun = stream.readUnsignedByteC() == 1;
		int npcIndex = stream.readUnsignedShortLE128();
		final NPC npc = World.getNPCs().get(npcIndex);
		if (npc == null || npc.isCantInteract() || npc.isDead() || npc.hasFinished()
				|| !player.getMapRegionsIds().contains(npc.getRegionId()) || player.isLocked())
			return;
		player.stopAll(false);
		if (forceRun)
			player.setRun(forceRun);
		if (npc.getDefinitions().name.toLowerCase().equals("grand exchange clerk")) {
			player.setRouteEvent(new RouteEvent(npc, () -> {
				player.faceEntity(npc);
				if (!player.withinDistance(npc, 2))
					return;
				npc.faceEntity(player);
				player.getInterfaceManager().openMenu(5, 3);
			}, true));
			return;
		}
		player.setRouteEvent(new RouteEvent(npc, () -> {
			npc.resetWalkSteps();
			if (!player.getControllerManager().processNPCClick4(npc))
				return;
			player.faceEntity(npc);
			if (npc instanceof GraveStone) {
				GraveStone grave = (GraveStone) npc;
				grave.demolish(player);
				return;
			}
			npc.faceEntity(player);
			switch (npc.getId()) {
			// Jack Oval
			case 14877:
				ShopsHandler.openShop(player, 168);
				break;
			case 19921:
				System.out.println(player.getUsername() + " has attempted to use the GTL vote system.");
				break;
			case 3404:
				player.getDialogueManager().startDialogue("TeplinMacaganD", npc.getId(), 4);
				break;
			case 13697: // Troll Invasion
				player.getDialogueManager().startDialogue("CaptainJute", npc.getId(), 18);
				break;
			case 16973:
				player.getDialogueManager().startDialogue("GuthixianHighDruid", npc, 4);
				break;
			/*
			 * Lady Meilyr recipe shop
			 */
			case 20286:
				RecipeShop.sendInterface(player);
				break;
			/*
			 * Max
			 */
			case 3374:
				if (player.getSkills().getLevel(Skills.ATTACK) < 99 || player.getSkills().getLevel(Skills.STRENGTH) < 99
						|| player.getSkills().getLevel(Skills.DEFENCE) < 99
						|| player.getSkills().getLevel(Skills.RANGED) < 99
						|| player.getSkills().getLevel(Skills.PRAYER) < 99
						|| player.getSkills().getLevel(Skills.MAGIC) < 99
						|| player.getSkills().getLevel(Skills.RUNECRAFTING) < 99
						|| player.getSkills().getLevel(Skills.CONSTRUCTION) < 99
						|| player.getSkills().getLevel(Skills.DUNGEONEERING) < 99
						|| player.getSkills().getLevel(Skills.HITPOINTS) < 99
						|| player.getSkills().getLevel(Skills.AGILITY) < 99
						|| player.getSkills().getLevel(Skills.HERBLORE) < 99
						|| player.getSkills().getLevel(Skills.THIEVING) < 99
						|| player.getSkills().getLevel(Skills.CRAFTING) < 99
						|| player.getSkills().getLevel(Skills.FLETCHING) < 99
						|| player.getSkills().getLevel(Skills.SLAYER) < 99
						|| player.getSkills().getLevel(Skills.HUNTER) < 99
						|| player.getSkills().getLevel(Skills.MINING) < 99
						|| player.getSkills().getLevel(Skills.SMITHING) < 99
						|| player.getSkills().getLevel(Skills.FISHING) < 99
						|| player.getSkills().getLevel(Skills.COOKING) < 99
						|| player.getSkills().getLevel(Skills.FIREMAKING) < 99
						|| player.getSkills().getLevel(Skills.WOODCUTTING) < 99
						|| player.getSkills().getLevel(Skills.FARMING) < 99
						|| player.getSkills().getLevel(Skills.SUMMONING) < 99
						|| player.getSkills().getLevel(Skills.CONSTRUCTION) < 99
						|| player.getSkills().getLevel(Skills.DUNGEONEERING) < 99
						|| player.getSkills().getLevel(Skills.DIVINATION) < 99) {
					player.getPackets().sendGameMessage("You need to be maxed to access Max's store.");
					return;
				}
				for (Player players : World.getPlayers()) {
					if (player.isAnIronMan())
						players.getPackets()
								.sendGameMessage("<img=6></img><shad=FF0000> News: " + player.getIronmanTitle(false)
										+ " <shad=FF0000>" + player.getDisplayName()
										+ " has been awarded the Max Cape!");
					else
						players.getPackets().sendGameMessage("<img=6></img><shad=FF0000> News: "
								+ player.getDisplayName() + " has been awarded the Max Cape!");
					ShopsHandler.openShop(player, 52);
				}
				break;

			/*
			 * Aubury
			 */
			case 5913:
				npc.setNextForceTalk(new ForceTalk("Senventior Disthinte Molesko!"));
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2909, 4834, 0));
				break;
			}
			if (npc.getId() == 14866)
				ShopsHandler.openShop(player, 162);
			if (npc.getId() == 14870)
				ShopsHandler.openShop(player, 169);
			else if (npc.getId() == 6070)
				ShopsHandler.openShop(player, 54);
			else if (npc.getId() == 5913) // Aubury
				RuneEssenceController.teleport(player, npc);
			else if (npc.getId() == 14872)
				player.getDialogueManager().startDialogue("KillingQuickD");
			else if (npc.getId() == 5110) // Aleck's Hunter Emporium
				ShopsHandler.openShop(player, 56);
			else if (npc.getId() == 14854) // Poletax's Herblore Shop
				ShopsHandler.openShop(player, 165);
			else if (SlayerMaster.startInteractionForId(player, npc.getId(), 4)) {
				SlayerShop.sendInterface(player);
			}
			// else
			// player.getPackets().sendGameMessage("Nothing interesting
			// happens.");
		}));
		if (Settings.DEBUG) {
			System.out.println("cliked 4 at npc id : " + npc.getId() + ", " + npc.getX() + ", " + npc.getY() + ", "
					+ npc.getPlane());
			player.getPackets().sendGameMessage("Click 4: " + npc.getName() + " (id: " + npc.getId() + ") " + npc.getX()
					+ ", " + npc.getY() + ", " + npc.getPlane() + ".", true);
		}
		Bugsystem.addNPCBug(4, npc.getName(), npc.getId(), npc.getX(), npc.getY(), npc.getPlane());
	}

	public static void handleItemOnNPC(final Player player, final NPC npc, final int slot, final Item item) {
		if (item == null)
			return;
		player.setRouteEvent(new RouteEvent(npc, () -> {
			if (!player.getInventory().containsItem(item.getId(), item.getAmount())) {
				return;
			}
			if (npc == null || npc.isDead() || npc.hasFinished()
					|| !player.getMapRegionsIds().contains(npc.getRegionId()))
				return;
			if (npc instanceof Block) {
				Block block = (Block) npc;
				if (!block.useItem(player, item)) {
					return;
				}
			}
			if (npc.getId() == 20282 && item.getId() == 6739) {
				if (player.getInventory().containsItem(6739, 4000)) {
					player.getInventory().deleteItem(6739, 1);
					player.getInventory().deleteItem(32622, 4000);
					player.getInventory().addItem(32645, 1);
					player.getDialogueManager().startDialogue("SimpleMessage",
							"Lady Ithell has successfully created a crystal hatchet for you.");
					return;
				} else {
					player.getDialogueManager().startDialogue("SimpleMessage",
							"Make sure you have a dragon hatchet and 4.000 harmonic dust with you.");
				}
			}
			if (npc.getId() == 20282 && item.getId() == 15259) {
				if (player.getInventory().containsItem(6739, 4000)) {
					player.getInventory().deleteItem(15259, 1);
					player.getInventory().deleteItem(32622, 4000);
					player.getInventory().addItem(32646, 1);
					player.getDialogueManager().startDialogue("SimpleMessage",
							"Lady Ithell has successfully created a crystal pickaxe for you.");
					return;
				} else {
					player.getDialogueManager().startDialogue("SimpleMessage",
							"Make sure you have a dragon pickaxe and 4.000 harmonic dust with you.");
				}
			}
			if (npc instanceof Pet) {
				player.faceEntity(npc);
				if (player.hasLegendaryPet()) {
					if (player.getPet() != (Pet) npc) {
						player.getPackets().sendGameMessage("This is not your pet!");
						return;
					}
					String itemName = item.getName().toLowerCase();
					if (itemName.contains("pouch")) {
						// legendary pet beast of burden
						switch (itemName) {
						case "spirit terrorbird pouch":
							LegendaryPetAbilities.petAddBoBPouch(player, item, (Pet) npc, 12);
							break;
						case "war tortoise pouch":
							LegendaryPetAbilities.petAddBoBPouch(player, item, (Pet) npc, 18);
							break;
						case "pack yak pouch":
							LegendaryPetAbilities.petAddBoBPouch(player, item, (Pet) npc, 30);
							break;
						default:
							player.getPackets().sendGameMessage("Your pet cant do anything with this summoning pouch");
							break;
						}
					} else {
						player.getDialogueManager().startDialogue("LegendaryPetDialogue", item, (Pet) npc);
					}
				} else {
					player.getPetManager().eat(item.getId(), (Pet) npc);
				}
				return;
			}
			if (npc instanceof Familiar) {
				Familiar familiar = (Familiar) npc;
				if (familiar.getBob() != null) {
					familiar.getBob().addItem(slot, item.getAmount());
				} else if (npc.getId() == 7378 || npc.getId() == 7377) {
					if (Pyrelord.lightLog(familiar, item)) {
					}
				} else if (npc.getId() == 7339 || npc.getId() == 7339) {
					if ((item.getId() >= 1704 && item.getId() <= 1710 && item.getId() % 2 == 0)
							|| (item.getId() >= 10356 && item.getId() <= 10366 && item.getId() % 2 == 0)
							|| (item.getId() == 2572
									|| (item.getId() >= 20653 && item.getId() <= 20657 && item.getId() % 2 != 0))) {
						for (Item item1 : player.getInventory().getItems().getItems()) {
							if (item1 == null)
								continue;
							if (item1.getId() >= 1704 && item1.getId() <= 1710 && item1.getId() % 2 == 0)
								item1.setId(1712);
							else if (item1.getId() >= 10356 && item1.getId() <= 10366 && item1.getId() % 2 == 0)
								item1.setId(10354);
							else if (item1.getId() == 2572
									|| (item1.getId() >= 20653 && item1.getId() <= 20657 && item1.getId() % 2 != 0))
								item1.setId(20659);
						}
						player.getInventory().refresh();
						player.getDialogueManager().startDialogue("ItemMessage",
								"Your ring of wealth and amulet of glory have all been recharged.", 1712);
					}
				}
			} else if (npc instanceof Pet) {
				player.faceEntity(npc);
				player.getPetManager().eat(item.getId(), (Pet) npc);
			} else if (npc instanceof ConditionalDeath)
				((ConditionalDeath) npc).useHammer(player);
			else if (item.getId() == 22444)
				PolyporeCreature.sprinkleOil(player, npc);
			else if (item.getId() == 5733) {
				if (player.getRights() < 2) {
					player.getInventory().deleteItem(5733, 28);
					return;
				}
				RottenPotato.useOnNpc(player, npc);

			} else if (item.getId() == 9050 && npc.getId() == 4476) {
				player.getDialogueManager().startDialogue("GurdianMummy", npc.getId(), 2);
			} else if (npc.getId() == 4476) {
				player.getPackets().sendGameMessage("The Mummy is not interested in this.");
			} else if (item.getId() == SpiritshieldCreating.SPIRIT_SHIELD
					|| item.getId() == SpiritshieldCreating.HOLY_ELIXIR && npc.getId() == 802) {
				SpiritshieldCreating.blessShield(player, false);
			} else if (npc.getId() == 7729 && SpiritshieldCreating.isSigil(item.getId()))
				SpiritshieldCreating.attachShield(player, item, false);
			else if ((npc.getId() == 519 || npc.getId() == 3797) && ItemConstants.repairItem(player,
					player.getInventory().getItems().getThisItemSlot(item), false)) {
			} else if ((npc.getId() == 9711)
					&& ItemConstants.repairItem(player, player.getInventory().getItems().getThisItemSlot(item), true)) {
			} else if ((npc.getId() == 13727) && ItemConstants.handleRecolorItem(player, item))
				return;
			else if (npc.getName().equals("Tool leprechaun")
					&& (CleanAction.getHerb(item.getId()) != null || ProductInfo.isProduct(item))
					&& !item.getDefinitions().isNoted() && item.getDefinitions().getCertId() != -1) {
				int quantity = player.getInventory().getAmountOf(item.getId());
				player.getInventory().deleteItem(item.getId(), quantity);
				player.getInventory().addItem(item.getDefinitions().getCertId(), quantity);
				player.getDialogueManager().startDialogue("ItemMessage",
						"The leprechaun exchanges your items for banknotes.", item.getId());
			} else
				player.getPackets().sendGameMessage("Nothing interesting happens.");
		}));
	}
}