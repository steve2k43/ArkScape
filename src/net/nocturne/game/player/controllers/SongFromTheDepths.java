package net.nocturne.game.player.controllers;

import java.util.ArrayList;

import net.nocturne.game.Animation;
import net.nocturne.game.WorldObject;
import net.nocturne.game.WorldTile;
import net.nocturne.game.map.MapBuilder;
import net.nocturne.game.npc.NPC;
import net.nocturne.game.player.MusicsManager;
import net.nocturne.game.player.content.FadingScreen;
import net.nocturne.game.tasks.WorldTask;
import net.nocturne.game.tasks.WorldTasksManager;

/**
 * @author Paty
 **/

public class SongFromTheDepths extends Controller {

	private static final WorldTile OUTSIDE = new WorldTile(2975, 3224, 0);// TEMP
																			// for
																			// test

	private int[] boundChuncks;
	private static ArrayList<NPC> npcs = new ArrayList<>();
	private boolean tempvalue = false;

	@Override
	public void start() {
		loadMaze4();
	}

	@Override
	public boolean processObjectClick1(final WorldObject object) {
		final int id = object.getId();

		if (id == 72322) {
			player.lock(6);
			FadingScreen.fade(player, () -> leave(0));
		}

		if (id == 72323) {
			player.lock(6);
			int[] currentChunks = boundChuncks;
			loadMaze6();
			FadingScreen.fade(player,
					() -> {
						leave(1);
						MapBuilder.destroyMap(currentChunks[0],
								currentChunks[1], 8, 8);
						player.setNextWorldTile(getWorldTile(27, 14));
						player.setNextFaceWorldTile(new WorldTile(
								player.getX(), player.getY() + 1, 0));
						player.setForceNextMapLoadRefresh(true);
						player.loadMapRegions();
					});
		}

		if (id == 72326) {
			player.lock(6);
			int[] currentChunks = boundChuncks;
			loadMaze4Return();
			FadingScreen.fade(player,
					() -> {
						leave(1);
						MapBuilder.destroyMap(currentChunks[0],
								currentChunks[1], 8, 8);
						player.setNextWorldTile(getWorldTile(33, 31));
						player.setNextFaceWorldTile(new WorldTile(
								player.getX() - 1, player.getY(), 0));
						player.setForceNextMapLoadRefresh(true);
						player.loadMapRegions();
					});
		}

		if (id == 72327) {
			player.lock(6);
			int[] currentChunks = boundChuncks;
			loadMaze7();
			FadingScreen.fade(player,
					() -> {
						leave(1);
						MapBuilder.destroyMap(currentChunks[0],
								currentChunks[1], 8, 8);
						player.setNextWorldTile(getWorldTile(32, 18));
						player.setNextFaceWorldTile(new WorldTile(
								player.getX(), player.getY() + 1, 0));
						player.setForceNextMapLoadRefresh(true);
						player.loadMapRegions();
					});
		}

		if (id == 72330) {
			player.lock(6);
			int[] currentChunks = boundChuncks;
			loadMaze6Return();
			FadingScreen.fade(player,
					() -> {
						leave(1);
						MapBuilder.destroyMap(currentChunks[0],
								currentChunks[1], 8, 8);
						player.setNextWorldTile(getWorldTile(21, 21));
						player.setNextFaceWorldTile(new WorldTile(
								player.getX(), player.getY() + 1, 0));
						player.setForceNextMapLoadRefresh(true);
						player.loadMapRegions();
					});
		}

		if (id == 72331) {
			player.lock(6);
			int[] currentChunks = boundChuncks;
			loadMaze2();
			FadingScreen.fade(player,
					() -> {
						leave(1);
						MapBuilder.destroyMap(currentChunks[0],
								currentChunks[1], 8, 8);
						player.setNextWorldTile(getWorldTile(19, 19));
						player.setNextFaceWorldTile(new WorldTile(
								player.getX(), player.getY() + 1, 0));
						player.setForceNextMapLoadRefresh(true);
						player.loadMapRegions();
					});
		}

		if (id == 72338) {
			if (object.getRotation() == 3) {
				player.lock(6);
				int[] currentChunks = boundChuncks;
				loadMaze7Return();
				FadingScreen.fade(player, () -> {
					leave(1);
					MapBuilder.destroyMap(currentChunks[0], currentChunks[1],
							8, 8);
					player.setNextWorldTile(getWorldTile(14, 14));
					player.setNextFaceWorldTile(new WorldTile(player.getX(),
							player.getY() + 1, 0));
					player.setForceNextMapLoadRefresh(true);
					player.loadMapRegions();
				});
			} else if (object.getRotation() == 2) {
				player.lock(6);
				int[] currentChunks = boundChuncks;
				loadMaze1Return();
				FadingScreen.fade(player, () -> {
					leave(1);
					MapBuilder.destroyMap(currentChunks[0], currentChunks[1],
							8, 8);
					player.setNextWorldTile(getWorldTile(45, 18));
					player.setNextFaceWorldTile(new WorldTile(player.getX(),
							player.getY() + 1, 0));
					player.setForceNextMapLoadRefresh(true);
					player.loadMapRegions();
				});
			} else {
				player.getPackets().sendGameMessage(
						"ROT: " + object.getRotation());
			}
		}

		if (id == 72339) {
			if (object.getRotation() == 2) {
				player.lock(6);
				int[] currentChunks = boundChuncks;
				loadMaze1();
				FadingScreen.fade(player, () -> {
					leave(1);
					MapBuilder.destroyMap(currentChunks[0], currentChunks[1],
							8, 8);
					player.setNextWorldTile(getWorldTile(25, 14));
					player.setNextFaceWorldTile(new WorldTile(player.getX(),
							player.getY() + 1, 0));
					player.setForceNextMapLoadRefresh(true);
					player.loadMapRegions();
				});
			} else if (object.getRotation() == 0) {
				player.lock(6);
				// player.setNextAnimation(new Animation(?));
				int[] currentChunks = boundChuncks;
				loadDream2();
				FadingScreen.fade(player, () -> {
					leave(1);
					MapBuilder.destroyMap(currentChunks[0], currentChunks[1],
							8, 8);
					player.setNextWorldTile(getWorldTile(25, 14));
					player.setNextFaceWorldTile(new WorldTile(player.getX(),
							player.getY() + 1, 0));
					player.setForceNextMapLoadRefresh(true);
					player.loadMapRegions();
				});
			}
		}

		if (id == 72334) {
			player.lock(6);
			// player.setNextAnimation(new Animation(?));
			int[] currentChunks = boundChuncks;
			loadMaze2Return();
			FadingScreen.fade(player,
					() -> {
						leave(1);
						MapBuilder.destroyMap(currentChunks[0],
								currentChunks[1], 8, 8);
						player.setNextWorldTile(getWorldTile(40, 10));
						player.setNextFaceWorldTile(new WorldTile(
								player.getX(), player.getY() + 1, 0));
						player.setForceNextMapLoadRefresh(true);
						player.loadMapRegions();
					});
		}

		if (id == 72335) {
			player.lock(6);
			// player.setNextAnimation(new Animation(?));
			int[] currentChunks = boundChuncks;
			loadConnect1();
			FadingScreen.fade(player,
					() -> {
						leave(1);
						MapBuilder.destroyMap(currentChunks[0],
								currentChunks[1], 8, 8);
						player.setNextWorldTile(getWorldTile(20, 16));
						player.setNextFaceWorldTile(new WorldTile(
								player.getX(), player.getY() + 1, 0));
						player.setForceNextMapLoadRefresh(true);
						player.loadMapRegions();
					});
		}

		if (id == 72298) {
			player.lock(6);
			// player.setNextAnimation(new Animation(?));
			int[] currentChunks = boundChuncks;
			loadConnect1Return();
			FadingScreen.fade(player,
					() -> {
						leave(1);
						MapBuilder.destroyMap(currentChunks[0],
								currentChunks[1], 8, 8);
						player.setNextWorldTile(getWorldTile(25, 36));
						player.setNextFaceWorldTile(new WorldTile(
								player.getX(), player.getY() + 1, 0));
						player.setForceNextMapLoadRefresh(true);
						player.loadMapRegions();
					});
		}

		if (id == 72311) {
			player.lock(6);
			// player.setNextAnimation(new Animation(?));
			int[] currentChunks = boundChuncks;
			loadDream3();
			FadingScreen.fade(player,
					() -> {
						leave(1);
						MapBuilder.destroyMap(currentChunks[0],
								currentChunks[1], 8, 8);
						player.setNextWorldTile(getWorldTile(23, 15));
						player.setNextFaceWorldTile(new WorldTile(
								player.getX(), player.getY() + 1, 0));
						player.setForceNextMapLoadRefresh(true);
						player.loadMapRegions();
					});
		}

		if (id == 72300) {
			player.lock(6);
			// player.setNextAnimation(new Animation(?));
			int[] currentChunks = boundChuncks;
			loadDream2Return();
			FadingScreen.fade(player,
					() -> {
						leave(1);
						MapBuilder.destroyMap(currentChunks[0],
								currentChunks[1], 8, 8);
						player.setNextWorldTile(getWorldTile(25, 14));
						player.setNextFaceWorldTile(new WorldTile(
								player.getX(), player.getY() + 1, 0));
						player.setForceNextMapLoadRefresh(true);
						player.loadMapRegions();
					});
		}

		// if (id == xxx) {

		// }

		return false;
	}

	@Override
	public boolean sendDeath() {
		player.lock(7);
		player.stopAll();
		WorldTasksManager.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0) {
					player.setNextAnimation(player.getDeathAnimation());
				} else if (loop == 1) {
					player.getPackets()
							.sendGameMessage(
									"You feel the effects of the potion fade and you return to the living realm.");
				} else if (loop == 3) {
					player.reset();
					removeController();
					player.getInterfaceManager().removeFadingInterface();
					player.setNextWorldTile(OUTSIDE);
					player.setNextAnimation(new Animation(-1));
				} else if (loop == 4) {
					player.getMusicsManager().playMusicEffect(
							MusicsManager.DEATH_MUSIC_EFFECT);
					removeNPCs();
					MapBuilder.destroyMap(boundChuncks[0], boundChuncks[1], 8,
							8);
					stop();
				}
				loop++;
			}
		}, 0, 1);
		return false;
	}

	private void loadMaze4() {
		// player.lock();
		player.getPackets().sendGameMessage("Entering Maze 4.");
		player.getInterfaceManager().sendFadingInterface(1280);
		boundChuncks = MapBuilder.findEmptyChunkBound(8, 8);
		// player.ChunckBounds = boundChuncks;
		MapBuilder.copyAllPlanesMap(129, 768, boundChuncks[0], boundChuncks[1],
				8);
		player.setNextWorldTile(getWorldTile(15, 18));
		player.setNextFaceWorldTile(new WorldTile(player.getX(),
				player.getY() + 1, 0));
		// player.unlock();
	}

	private void loadMaze6() {
		// player.lock();
		player.getPackets().sendGameMessage("Entering Maze 6.");
		// MapBuilder.destroyMap(boundChuncks[0], boundChuncks[1], 8, 8);
		boundChuncks = MapBuilder.findEmptyChunkBound(8, 8);
		// player.ChunckBounds = boundChuncks;
		MapBuilder.copyAllPlanesMap(145, 770, boundChuncks[0], boundChuncks[1],
				8);
		// player.setNextWorldTile(getWorldTile(27,14));
		// player.setNextFaceWorldTile(new WorldTile(player.getX(),
		// player.getY()+1, 0));
		// player.setLocation(getWorldTile(27,14));
		// player.unlock();
	}

	private void loadMaze4Return() {
		// player.lock();
		player.getPackets().sendGameMessage("Returning to Maze 4.");
		// MapBuilder.destroyMap(boundChuncks[0], boundChuncks[1], 8, 8);
		boundChuncks = MapBuilder.findEmptyChunkBound(8, 8);
		// player.ChunckBounds = boundChuncks;
		MapBuilder.copyAllPlanesMap(129, 768, boundChuncks[0], boundChuncks[1],
				8);
		// player.unlock();
	}

	private void loadMaze7() {
		// player.lock();
		player.getPackets().sendGameMessage("Entering Maze 7.");
		// MapBuilder.destroyMap(boundChuncks[0], boundChuncks[1], 8, 8);
		boundChuncks = MapBuilder.findEmptyChunkBound(8, 8);
		// player.ChunckBounds = boundChuncks;
		MapBuilder.copyAllPlanesMap(153, 770, boundChuncks[0], boundChuncks[1],
				8);
		// player.unlock();
	}

	private void loadMaze6Return() {
		// player.lock();
		player.getPackets().sendGameMessage("Returning to Maze 6.");
		// MapBuilder.destroyMap(boundChuncks[0], boundChuncks[1], 8, 8);
		boundChuncks = MapBuilder.findEmptyChunkBound(8, 8);
		// player.ChunckBounds = boundChuncks;
		MapBuilder.copyAllPlanesMap(145, 770, boundChuncks[0], boundChuncks[1],
				8);
		// player.unlock();
	}

	private void loadMaze2() {
		// player.lock();
		player.getPackets().sendGameMessage("Entering Maze 2.");
		// MapBuilder.destroyMap(boundChuncks[0], boundChuncks[1], 8, 8);
		boundChuncks = MapBuilder.findEmptyChunkBound(8, 8);
		// player.ChunckBounds = boundChuncks;
		MapBuilder.copyAllPlanesMap(136, 778, boundChuncks[0], boundChuncks[1],
				8);
		// player.unlock();
	}

	private void loadMaze7Return() {
		// player.lock();
		player.getPackets().sendGameMessage("Returning to Maze 7.");
		// MapBuilder.destroyMap(boundChuncks[0], boundChuncks[1], 8, 8);
		boundChuncks = MapBuilder.findEmptyChunkBound(8, 8);
		// player.ChunckBounds = boundChuncks;
		MapBuilder.copyAllPlanesMap(153, 770, boundChuncks[0], boundChuncks[1],
				8);
		// player.unlock();
	}

	private void loadMaze1() {
		// player.lock();
		player.getPackets().sendGameMessage("Entering Maze 1.");
		// MapBuilder.destroyMap(boundChuncks[0], boundChuncks[1], 8, 8);
		boundChuncks = MapBuilder.findEmptyChunkBound(8, 8);
		// player.ChunckBounds = boundChuncks;
		MapBuilder.copyAllPlanesMap(128, 778, boundChuncks[0], boundChuncks[1],
				8);
		// player.unlock();
	}

	private void loadMaze2Return() {
		// player.lock();
		player.getPackets().sendGameMessage("Returning to Maze 2.");
		// MapBuilder.destroyMap(boundChuncks[0], boundChuncks[1], 8, 8);
		boundChuncks = MapBuilder.findEmptyChunkBound(8, 8);
		// player.ChunckBounds = boundChuncks;
		MapBuilder.copyAllPlanesMap(136, 778, boundChuncks[0], boundChuncks[1],
				8);
		// player.unlock();
	}

	private void loadConnect1() {
		// player.lock();
		player.getPackets().sendGameMessage("Entering Connecting Room 1.");
		// MapBuilder.destroyMap(boundChuncks[0], boundChuncks[1], 8, 8);
		boundChuncks = MapBuilder.findEmptyChunkBound(8, 8);
		// player.ChunckBounds = boundChuncks;
		MapBuilder.copyAllPlanesMap(177, 809, boundChuncks[0], boundChuncks[1],
				8);
		// player.unlock();
	}

	private void loadMaze1Return() {
		// player.lock();
		player.getPackets().sendGameMessage("Returning to Maze 1.");
		// MapBuilder.destroyMap(boundChuncks[0], boundChuncks[1], 8, 8);
		boundChuncks = MapBuilder.findEmptyChunkBound(8, 8);
		// player.ChunckBounds = boundChuncks;
		MapBuilder.copyAllPlanesMap(128, 778, boundChuncks[0], boundChuncks[1],
				8);
		// player.unlock();
	}

	private void loadDream2() {
		// player.lock();
		player.getPackets().sendGameMessage("Entering Dream 2.");
		// MapBuilder.destroyMap(boundChuncks[0], boundChuncks[1], 8, 8);
		boundChuncks = MapBuilder.findEmptyChunkBound(8, 8);
		// player.ChunckBounds = boundChuncks;
		MapBuilder.copyAllPlanesMap(137, 761, boundChuncks[0], boundChuncks[1],
				8);
		// player.unlock();
	}

	private void loadConnect1Return() {
		// player.lock();
		player.getPackets().sendGameMessage("Returning to Connecting Room 1.");
		// MapBuilder.destroyMap(boundChuncks[0], boundChuncks[1], 8, 8);
		boundChuncks = MapBuilder.findEmptyChunkBound(8, 8);
		// player.ChunckBounds = boundChuncks;
		MapBuilder.copyAllPlanesMap(177, 809, boundChuncks[0], boundChuncks[1],
				8);
		// player.unlock();
	}

	private void loadDream3() {
		// player.lock();
		player.getPackets().sendGameMessage("Entering Dream 3.");
		// MapBuilder.destroyMap(boundChuncks[0], boundChuncks[1], 8, 8);
		boundChuncks = MapBuilder.findEmptyChunkBound(8, 8);
		// player.ChunckBounds = boundChuncks;
		MapBuilder.copyAllPlanesMap(145, 761, boundChuncks[0], boundChuncks[1],
				8);
		// player.unlock();
	}

	private void loadDream2Return() {
		// player.lock();
		player.getPackets().sendGameMessage("Returning to Dream 2.");
		// MapBuilder.destroyMap(boundChuncks[0], boundChuncks[1], 8, 8);
		boundChuncks = MapBuilder.findEmptyChunkBound(8, 8);
		// player.ChunckBounds = boundChuncks;
		MapBuilder.copyAllPlanesMap(137, 761, boundChuncks[0], boundChuncks[1],
				8);
		// player.unlock();
	}

	@Override
	public void magicTeleported(int type) {
		if (tempvalue) {
			this.forceClose();
		} else {
			tempvalue = true;
		}
	}

	private void removeNPCs() {
		npcs.forEach(NPC::finish);
		npcs.clear();
	}

	private void leave(int type) {
		if (type == 0) {
			player.reset();
			removeController();
			player.getInterfaceManager().removeFadingInterface();
			player.getPackets()
					.sendGameMessage(
							"You feel the effects of the potion fade and you return to the living realm.");
			player.setNextWorldTile(OUTSIDE);
			player.setNextAnimation(new Animation(-1));
			removeNPCs();
			MapBuilder.destroyMap(boundChuncks[0], boundChuncks[1], 8, 8);
		}
		if (type == 1) {
			player.setNextAnimation(new Animation(-1));
			removeNPCs();
		}
	}

	private WorldTile getWorldTile(int mapX, int mapY) {
		return new WorldTile(boundChuncks[0] * 8 + mapX, boundChuncks[1] * 8
				+ mapY, 0);
	}

	@Override
	public boolean logout() {
		leave(0);
		return false;
	}

	@Override
	public boolean login() {
		player.getPackets().sendGameMessage(
				"This should never happen [ERR: LOGIN SFTD].");
		leave(0);
		return false;
	}

	@Override
	public boolean processMagicTeleport(WorldTile toTile) {// why does this
															// happen
		// player.getPackets().sendGameMessage("You can't do that at the moment [MAG].");
		return false;
	}

	@Override
	public boolean processItemTeleport(WorldTile toTile) {// why does this
															// happen
		// player.getPackets().sendGameMessage("You can't do that at the moment [IETM].");
		return false;
	}

	@Override
	public boolean processObjectTeleport(WorldTile toTile) {
		player.getPackets().sendGameMessage(
				"You can't do that at the moment [OBJ].");
		return false;
	}

	@Override
	public void forceClose() {
		removeController();
		player.getInterfaceManager().removeFadingInterface();
		player.reset();
	}
}
