package net.nocturne.game.player.content.activities.jobs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.nocturne.game.player.Player;
import net.nocturne.game.player.content.activities.jobs.impl.bananaplantation.BananaPlantation;

public class JobManager implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5144508508885593395L;
	private Player player;
	private List<Job> jobs = new ArrayList<Job>(Jobs.values().length);

	public JobManager(Player player) {
		this.setPlayer(player);
		init();
	}

	public void add(Jobs jobs, Job job) {
		getJobs().add(jobs.index(), job);
	}

	public Job get(Jobs key) {
		return getJobs().get(key.index());
	}

	public JobManager init() {
		add(Jobs.BANANA_PLANTATION, new BananaPlantation(player));
		return this;
	}

	public List<Job> getJobs() {
		return jobs;
	}

	public JobManager process() {
		return this;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}
}
