package net.nocturne.game.player.content.activities.ports.missions;

import net.nocturne.game.World;
import net.nocturne.game.WorldTile;
import net.nocturne.game.item.Item;
import net.nocturne.game.player.Player;
import net.nocturne.game.player.content.activities.ports.missions.MissionConstants.MissionState;
import net.nocturne.game.player.content.activities.ports.missions.MissionConstants.MissionType;

/**
 * 
 * @author Frostbite<Abstract>
 * @contact<skype;frostbitersps><email;frostbitersps@gmail.com>
 */

public abstract class Mission {

	protected MissionState state = MissionState.NOT_STARTED;
	protected Player player;
	protected int stage = 0;

	public Mission(Player player) {
		this.player = player;
	}

	public abstract String getMissionName();

	public abstract int getTotalLevelRequirement();

	public abstract MissionType getMissionType();

	public abstract int getExperienceRateMultiplier();

	public abstract MissionReward[] getPossibleRewards();

	public void sendReward() {
		for (MissionReward reward : getPossibleRewards()) {
			switch (reward.getType()) {
			case EXPERIENCE:
				player.getSkills().addXp(
						(Integer) reward.getData()[0],
						(Integer) reward.getData()[1]
								* getExperienceRateMultiplier());
				break;
			case ITEM:
				Item item = (Item) reward.getData()[0];
				if (player.getInventory().getFreeSlots() >= 1) {
					player.getInventory().addItem(item);
				} else {
					World.addGroundItem(item, new WorldTile(player));
				}
				break;
			default:
				break;
			}
		}
		stage = 0;
		state = MissionState.NOT_STARTED;
	}

	public int getStage() {
		return stage;
	}

	public void setStage(int stage) {
		this.stage = stage;
	}

	public MissionState getState() {
		return state;
	}

	public void setState(MissionState state) {
		this.state = state;
	}

}
