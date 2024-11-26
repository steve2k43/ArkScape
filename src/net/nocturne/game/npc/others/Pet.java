package net.nocturne.game.npc.others;

import net.nocturne.game.Graphics;
import net.nocturne.game.World;
import net.nocturne.game.WorldTile;
import net.nocturne.game.npc.NPC;
import net.nocturne.game.npc.familiar.Familiar;
import net.nocturne.game.npc.pet.LegendaryPetAbilities;
import net.nocturne.game.npc.pet.LegendaryPetBoB;
import net.nocturne.game.npc.pet.PetDetails;
import net.nocturne.game.npc.pet.Pets;
import net.nocturne.game.player.Player;
import net.nocturne.game.player.Skills;
import net.nocturne.utils.Utils;

/**
 * Represents a pet.
 * 
 * @author Emperor
 * 
 */
public final class Pet extends NPC {

	/**
	 * The serial UID.
	 */
	private static final long serialVersionUID = -2848843157767889742L;

	/**
	 * The owner.
	 */
	private final Player owner;

	/**
	 * The "near" directions.
	 */
	private final int[][] checkNearDirs;

	/**
	 * The item id.
	 */
	private final int itemId;

	/**
	 * The pet details.
	 */
	private final PetDetails details;

	/**
	 * The growth rate of the pet.
	 */
	private double growthRate;

	public LegendaryPetBoB bob;
	public boolean isLegendaryPet;
	public long petLifeSaver = 0;
	public long petHighAlchemy = 0;
	public long petItemRepair = 0;
	public long petItemBank = 0;
	public long petItemForge = 0;
	public long petItemForageReset = 0;
	public long petNPCExecute = 0;
	public boolean petVamp = false;
	public long petVampReset = 0;
	public int forageCount = 0;
	public boolean foraging = false;
	public long BoBTime = 0;
	public boolean activeBoB = false;

	public long getPetItemForageReset() {
		return petItemForageReset;
	}

	private void setPetItemForageReset(long time) {
		this.petItemForageReset = time;
		owner.petItemForageReset = time;
	}

	public boolean isForging() {
		return foraging;
	}

	public void setForaging(boolean value) {
		foraging = value;
	}

	public long getPetItemForge() {
		return petItemForge;
	}

	public long getPetLifeSaver() {
		return petLifeSaver;
	}

	public long getPetHighAlchemy() {
		return petHighAlchemy;
	}

	public long getPetNPCExecute() {
		return petNPCExecute;
	}

	public boolean isVamp() {
		return petVamp;
	}

	public void setVamp(boolean value) {
		this.petVamp = value;
		owner.petVamp = value;
	}

	public long getPetItemRepair() {
		return petItemRepair;
	}

	public long getPetItemBank() {
		return petItemBank;
	}

	public void setPetVampReset(Player player, long time) {
		player.petVampReset = time;
		this.petVampReset = time;
	}

	public void setPetLifeSaver(Player player, long time) {
		player.petLifeSaver = time;
		this.petLifeSaver = time;
	}

	public void setPetHighAlchemy(Player player, long time) {
		player.petHighAlchemy = time;
		this.petHighAlchemy = time;
	}

	public long getPetVampReset() {
		return petVampReset;
	}

	public void setPetItemRepair(Player player, long time) {
		player.petItemRepair = time;
		this.petItemRepair = time;
	}

	public void setPetNPCExecute(Player player, long time) {
		player.petNPCExecute = time;
		this.petNPCExecute = time;
	}

	public void setPetItemBank(Player player, long time) {
		player.petItemBank = time;
		this.petItemBank = time;
	}

	public void setPetItemForge(Player player, long time) {
		player.petItemForge = time;
		this.petItemForge = time;
	}

	public Pets getPetType() {
		return pet;
	}

	/**
	 * The pets type.
	 */
	private final Pets pet;

	public static transient int MAX_LEVEL = 25;

	public static final transient int DRAGON_WOLF = 16738,
			WARBORN_BEHEMOTH = 17791, RORY_THE_REINDEER = 18819,
			BLOOD_POUNCER = 16677, SKY_POUNDER = 16674,
			PROTOTYPE_COLOSSUS = 17785, BLAZE_HOUND = 16671, TEST_PET = 28683;

	/**
	 * Constructs a new {@code Pet} {@code Object}.
	 * 
	 * @param id
	 *            The NPC id.
	 * @param itemId
	 *            The item id.
	 * @param owner
	 *            The owner.
	 * @param tile
	 *            The world tile.
	 */
	public Pet(int id, int itemId, Player owner, WorldTile tile,
			PetDetails details) {
		super(id, tile, -1, false);
		this.owner = owner;
		this.itemId = itemId;
		this.checkNearDirs = Utils.getCoordOffsetsNear(super.getSize());
		this.details = details;
		this.pet = Pets.forId(itemId);
		setRun(true);
		this.isLegendaryPet = checkLegendPet();
		if (isLegendaryPet) {
			BoBTime = 0;
			this.setRun(true);
			activeBoB = false;
			foraging = false;
			petVamp = false;
			petVampReset = owner.petVampReset;
			forageCount = owner.petForageItems;
			petNPCExecute = owner.petNPCExecute;
			petLifeSaver = owner.petLifeSaver;
			petHighAlchemy = owner.petHighAlchemy;
			petItemRepair = owner.petItemRepair;
			petItemBank = owner.petItemBank;

		}
		if (pet == Pets.TROLL_BABY
				&& owner.getPetManager().getTrollBabyName() != null) {
			setName(owner.getPetManager().getTrollBabyName());
		}
		sendMainConfigurations();
	}

	private boolean checkLegendPet() {
		return pet == Pets.BLOODPOUNCER || pet == Pets.SKYPOUNCER
				|| pet == Pets.BLAZEHOUND || pet == Pets.DRAGON_WOLF
				|| pet == Pets.WARBORN_BEHEMOTH
				|| pet == Pets.PROTOTYPE_COLOSSUS
				|| pet == Pets.RORY_THE_REINDEER || pet == Pets.SHADOW_DRAKE;
	}

	@Override
	public void processNPC() {
		unlockOrb();
		if (pet == Pets.TROLL_BABY || pet.getFood().length > 0) {
			details.updateHunger(0.025);
			owner.getVarsManager().sendVarBit(4286, (int) details.getHunger());
		}
		if (isLegendaryPet && activeBoB) {
			// BoB timer has run out
			if (LegendaryPetAbilities.getRemainingTime(7200, BoBTime) < 0) {
				owner.getPackets().sendGameMessage(
						"Your pets BoB Timer has run out.");
				bob.dropBob();
				bob = null;
				BoBTime = 0;
				activeBoB = false;
			}
		}
		if (isLegendaryPet && isVamp()
				&& !LegendaryPetAbilities.onCoolDown(3600, getPetVampReset())) {
			setVamp(false);
			setPetVampReset(owner, Utils.currentTimeMillis());
			owner.getPackets().sendGameMessage(
					"Your pet Vampyrism ability has ended.");
		}
		if (isLegendaryPet && foraging
				&& !LegendaryPetAbilities.onCoolDown(7200, getPetItemForge())) {
			if (LegendaryPetAbilities.petForge(owner, this)) {
				setPetItemForge(owner, Utils.currentTimeMillis());
				if (forageCount >= 5) {
					setPetItemForageReset(Utils.currentTimeMillis());
					owner.getPackets()
							.sendGameMessage(
									"Your pet is tired from gathering item, you will have to wait awhile to have your pet forage for items again.");
					forageCount = 0;
					owner.petForageItems = 0;
					foraging = false;
				}
			}
		}
		if (details.getHunger() >= 90.0 && details.getHunger() < 90.025) {
			owner.getPackets()
					.sendGameMessage(
							"<col=ff0000>Your pet is starving, feed it before it runs off.</col>");
		} else if (details.getHunger() == 100.0) {
			owner.getPetManager().setNpcId(-1);
			owner.getPetManager().setItemId(-1);
			owner.setPet(null);
			owner.getPetManager().removeDetails(itemId);
			owner.getPackets().sendGameMessage(
					"Your pet has ran away to find some food!");
			switchOrb(false);
			owner.getInterfaceManager().removeFamiliarInterface();
			owner.getPackets().sendIComponentSettings(747, 17, 0, 0, 0);
			finish();
			return;
		}
		if (growthRate > 0.000) {
			details.updateGrowth(growthRate);
			owner.getVarsManager().sendVarBit(4285, (int) details.getGrowth());
			if (details.getGrowth() == 100.0) {
				growNextStage();
			}
		}
		if (!withinDistance(owner, 12)) {
			call();
			return;
		}
		sendFollow();
	}

	/**
	 * Grows into the next stage of this pet (if any).
	 */
	public void growNextStage() {
		if (details.getStage() == 3) {
			return;
		}
		if (pet == null) {
			return;
		}
		int npcId = pet.getNpcId(details.getStage() + 1);
		if (npcId < 1) {
			return;
		}
		details.setStage(details.getStage() + 1);
		int itemId = pet.getItemId(details.getStage());
		if (pet.getNpcId(details.getStage() + 1) > 0) {
			details.updateGrowth(-100.0);
		}
		owner.getPetManager().setItemId(itemId);
		owner.getPetManager().setNpcId(npcId);
		finish();
		Pet newPet = new Pet(npcId, itemId, owner, owner, details);
		newPet.growthRate = growthRate;
		owner.setPet(newPet);
		owner.getPackets().sendGameMessage(
				"<col=ff0000>Your pet has grown larger.</col>");
	}

	/**
	 * Picks up the pet.
	 */
	public void pickup() {
		if (!owner.getInventory().hasFreeSlots()) {
			owner.getPackets().sendGameMessage(
					"You have no inventory space to pick up your pet.");
			return;
		}
		owner.getInventory().addItem(itemId, 1);
		owner.setPet(null);
		owner.getPetManager().setNpcId(-1);
		owner.getPetManager().setItemId(-1);
		switchOrb(false);
		owner.getInterfaceManager().removeFamiliarInterface();
		owner.getPackets().sendIComponentSettings(747, 17, 0, 0, 0);
		finish();
		owner.getInterfaceManager().removeFamiliarInterface();
	}

	/**
	 * Calls the pet.
	 */
	public void call() {
		int size = getSize();
		WorldTile teleTile = null;
		for (int dir = 0; dir < checkNearDirs[0].length; dir++) {
			final WorldTile tile = new WorldTile(new WorldTile(owner.getX()
					+ checkNearDirs[0][dir], owner.getY()
					+ checkNearDirs[1][dir], owner.getPlane()));
			if (World.isTileFree(tile.getPlane(), tile.getX(), tile.getY(),
					size)) {
				teleTile = tile;
				break;
			}
		}
		if (teleTile == null) {
			return;
		}
		setNextWorldTile(teleTile);
	}

	private void sendFollow() {
		if (getLastFaceEntity() != owner.getClientIndex())
			setNextFaceEntity(owner);
		if (isBound() || isStunned())
			return;
		int size = getSize();
		int targetSize = owner.getSize();
		if (Utils.colides(getX(), getY(), size, owner.getX(), owner.getY(),
				targetSize) && !owner.hasWalkSteps()) {
			resetWalkSteps();
			if (!addWalkSteps(owner.getX() + targetSize, getY())) {
				resetWalkSteps();
				if (!addWalkSteps(owner.getX() - size, getY())) {
					resetWalkSteps();
					if (!addWalkSteps(getX(), owner.getY() + targetSize)) {
						resetWalkSteps();
						if (!addWalkSteps(getX(), owner.getY() - size)) {
							return;
						}
					}
				}
			}
			return;
		}
		resetWalkSteps();
		if (!clipedProjectile(owner, true)
				|| !Utils.isOnRange(getX(), getY(), size, owner.getX(),
						owner.getY(), targetSize, 0))
			calcFollow(owner, 2, true, false);
	}

	/**
	 * Sends the main configurations for the Pet interface (+ summoning orb).
	 */
	public void sendMainConfigurations() {
		switchOrb(true);
		owner.getVarsManager().sendVar(448, itemId);// configures
		owner.getPackets().sendCSVarInteger(1436, 0);
		unlockOrb(); // temporary
	}

	/**
	 * Sends the follower details.
	 */
	public void sendFollowerDetails() {
		owner.getInterfaceManager().setFamiliarInterface(662);
		owner.getPackets().sendHideIComponent(662, 47, false);
		owner.getPackets()
				.sendIComponentText(662, 35, owner.getPet().getName());
		owner.getVarsManager().sendVarBit(4285, (int) details.getGrowth());
		owner.getVarsManager().sendVarBit(4286, (int) details.getHunger());
		owner.getVarsManager().sendVar(1784, owner.getPet().getId());

		owner.getPackets().sendHideIComponent(662, 49, false);

		owner.getPackets().sendHideIComponent(662, 33, true);
		owner.getPackets().sendHideIComponent(662, 37, true);
		owner.getPackets().sendHideIComponent(662, 48, true);
		// owner.getInterfaceManager().setFamiliarInterface(662);
	}

	/**
	 * Switch the Summoning orb state.
	 * 
	 * @param enable
	 *            If the orb should be enabled.
	 */
	public void switchOrb(boolean enable) {
		owner.getVarsManager().sendVar(1174, enable ? getId() : 0);
		if (enable) {
			unlock();
			return;
		}
		lockOrb();
	}

	/**
	 * Unlocks the orb.
	 */
	public void unlockOrb() {
		owner.getPackets().sendHideIComponent(747, 9, false);
		Familiar.sendLeftClickOption(owner);
	}

	/**
	 * Unlocks the interfaces.
	 */
	public void unlock() {
		owner.getPackets().sendHideIComponent(747, 9, false);
	}

	/**
	 * Locks the orb.
	 */
	public void lockOrb() {
		owner.getPackets().sendHideIComponent(747, 9, true);
	}

	/**
	 * Gets the details.
	 * 
	 * @return The details.
	 */
	public PetDetails getDetails() {
		return details;
	}

	/**
	 * Gets the growthRate.
	 * 
	 * @return The growthRate.
	 */
	public double getGrowthRate() {
		return growthRate;
	}

	/**
	 * Sets the growthRate.
	 * 
	 * @param growthRate
	 *            The growthRate to set.
	 */
	public void setGrowthRate(double growthRate) {
		this.growthRate = growthRate;
	}

	/**
	 * Gets the item id of the pet.
	 * 
	 * @return The item id.
	 */
	public int getItemId() {
		return itemId;
	}

	public double addExperience(double experience) {
		if (details.getExperience() > Skills.MAXIMUM_EXP)
			return details.setExperience(Skills.MAXIMUM_EXP);
		int oldLevel = getLevelForXp();
		details.setExperience(details.getExperience() + experience);
		int newLevel = getLevelForXp();
		int levelDiff = newLevel - oldLevel;
		if (details.getLevel() > (MAX_LEVEL - 1))
			details.setLevel((short) MAX_LEVEL);
		if (newLevel > oldLevel) {
			details.setLevel((short) (details.getLevel() + levelDiff));
			if (newLevel == MAX_LEVEL)
				owner.getPackets().sendGameMessage(
						"<col=E86100>Your " + getName()
								+ " has reached it's max level!");
			else
				owner.getPackets().sendGameMessage(
						"<col=E86100>Your " + getName() + " has reached level "
								+ details.getLevel() + "!");
			owner.setNextGraphics(new Graphics(1765));
			setNextGraphics(new Graphics(199));
		}
		return experience;
	}

	public int getLevelForXp() {
		double exp = details.getExperience();
		int points = 0;
		int output = 0;
		for (int lvl = 1; lvl <= MAX_LEVEL; lvl++) {
			points += Math.floor(lvl + (MAX_LEVEL * 50.0)
					* Math.pow((MAX_LEVEL / 5.0), lvl / (MAX_LEVEL / 5.0)));
			output = (int) Math.floor(points / 4);
			if ((output - 1) >= exp)
				return lvl;
		}
		return 25;
	}
}