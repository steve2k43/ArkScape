package net.nocturne;

import java.io.*;
import java.lang.reflect.Method;
import java.net.*;
import java.security.CodeSource;
import java.util.Calendar;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import com.sun.org.apache.bcel.internal.generic.NEW;
import net.nocturne.tools.CommandServer;
import net.nocturne.api.http.HTTPService;
import net.nocturne.cache.Cache;
import net.nocturne.cache.filestore.store.Index;
import net.nocturne.cache.loaders.BodyDefinitions;
import net.nocturne.cache.loaders.ItemDefinitions;
import net.nocturne.cache.loaders.NPCDefinitions;
import net.nocturne.cache.loaders.ObjectDefinitions;
import net.nocturne.executor.GameExecutorManager;
import net.nocturne.executor.LoginExecutorManager;
import net.nocturne.executor.PlayerHandlerThread;
import net.nocturne.executor.ShutDownHook;
import net.nocturne.game.World;
import net.nocturne.game.map.MapBuilder;
import net.nocturne.game.map.bossInstance.BossInstanceHandler;
import net.nocturne.game.npc.combat.CombatScriptsHandler;
import net.nocturne.game.player.Player;
import net.nocturne.game.player.actions.skills.fishing.FishingSpotsHandler;
import net.nocturne.game.player.content.FriendsChat;
import net.nocturne.game.player.content.activities.clans.ClanRank;
import net.nocturne.game.player.content.activities.clans.ClansManager;
import net.nocturne.game.player.content.activities.partyroom.PartyRoom;
import net.nocturne.game.player.content.grandExchange.GrandExchange;
import net.nocturne.game.player.controllers.ControllerHandler;
import net.nocturne.game.player.cutscenes.CutscenesHandler;
import net.nocturne.game.player.dialogues.DialogueHandler;
import net.nocturne.login.GameWorld;
import net.nocturne.login.Login;
import net.nocturne.network.GameChannelsManager;
import net.nocturne.network.LoginClientChannelManager;
import net.nocturne.network.LoginServerChannelManager;
import net.nocturne.network.encoders.LoginChannelsPacketEncoder;
import net.nocturne.tools.CommandServer;
import net.nocturne.utils.BackupManager;
import net.nocturne.utils.Censor;
import net.nocturne.utils.ItemDestroys;
import net.nocturne.utils.ItemExamines;
import net.nocturne.utils.ItemSpawns;
import net.nocturne.utils.ItemWeights;
import net.nocturne.utils.Logger;
import net.nocturne.utils.MapArchiveKeys;
import net.nocturne.utils.MapAreas;
import net.nocturne.utils.MusicEffects;
import net.nocturne.utils.MusicHints;
import net.nocturne.utils.NPCCombatDefinitionsL;
import net.nocturne.utils.NPCDrops;
import net.nocturne.utils.NPCExamines;
import net.nocturne.utils.NPCSpawns;
import net.nocturne.utils.ObjectExamines;
import net.nocturne.utils.ObjectSpawns;
import net.nocturne.utils.ScrollMessage;
import net.nocturne.utils.SerializableFilesManager;
import net.nocturne.utils.SerializationUtilities;
import net.nocturne.utils.ShopsHandler;
import net.nocturne.utils.Utils;
import net.nocturne.utils.discord.DiscordBot;
import net.nocturne.utils.huffman.Huffman;

import com.google.common.base.Stopwatch;
import net.nocturne.utils.sql.Highscores;
import net.nocturne.utils.sql.HighscoresManager;

import static net.nocturne.utils.Bugsystem.addBug;

public class Engine {

	public static volatile boolean shutdown;
	/**
	 * Time when delayed shutdown started.
	 */
	public static volatile long delayedShutdownStart;
	/**
	 * Delay in seconds when delayed shutdown will start.
	 */
	public static volatile int delayedShutdownDelay;

	private static int staffOnline;

	/**
	 * Vote connection
	 */

	public static long currentTime;
	private static long ticks = Engine.currentTime - Utils.currentTimeMillis();
	static int seconds = Math.abs((int) (ticks / 1000) % 60);
	static int minutes = Math.abs((int) ((ticks / (1000 * 60)) % 60));
	static int hours = Math.abs((int) ((ticks / (1000 * 60 * 60)) % 24));
	static int days = Math.abs((int) ((ticks / (1000 * 60 * 60 * 60)) % 24));
	private static DiscordBot discord;
	public static String ip = "";

	public static DiscordBot getDiscordBot() {
		return discord;
	}

	public static void main(String[] args) throws Exception {
		if (args.length > 0) {
			System.out.println("The command line"
					+ " arguments are:");

			// iterating the args array and printing
			// the command line arguments
			for (String val : args)
				System.out.println(val); ip=args[0];
		}
		else
			System.out.println("No command line "
					+ "arguments found.");
		System.out.println("Dropbox Location = "+ Settings.getDropboxLocation());
		Stopwatch stopwatch = Stopwatch.createStarted();
		Settings.init(ip);
		long currentTime = Utils.currentTimeMillis();
		Logger.log("LoginEngine", "Starting login core...");
		Login.init();
		Logger.log("LoginEngine", "Starting executors...");
		LoginExecutorManager.init();
		Logger.log("LoginEngine", "Initiating Login Server Channel Manager...");
		try {
			LoginServerChannelManager.init();
		} catch (Throwable e) {
			Logger.handle(e);
			Logger.log("LoginEngine",
					"Failed Initiating Login Server Channel Manager. Shutting down...");
			System.exit(1);
			return;
		}
		Logger.log("LoginEngine",
				"Login server took "
						+ (Utils.currentTimeMillis() - currentTime)
						+ " milli seconds to launch.");

		GameWorld world = Login.getWorld(2);
		LoginServerChannelManager.sendReliablePacket(
				world,
				LoginChannelsPacketEncoder.encodeConsoleMessage(
						"Login Engine connected.").getBuffer());
		Logger.log("Engine", "Initiating Settings...");
		Settings.init(ip);
		Logger.log("Engine", "Initiating Cache...");
		Cache.init();
		if (Settings.HOSTED) {
		//	Logger.log("Engine", "Initiating Discord Bot...");
		//	discord = new DiscordBot();
		//	TimeUnit.SECONDS.sleep(1);
		}
		Logger.log("Engine", "Initiating Command Server...");
		//CommandServer.init();
		Logger.log("Engine", "Initiating Shops...");
		ShopsHandler.init(); //sql
		Huffman.init();
		BodyDefinitions.init();
		//BackupManager.init();
		Logger.log("Engine", "Initiating Data Files...");
		Censor.init();
		ClanRank.init();
		Logger.log("Engine", "Initiating Maps...");
		MapArchiveKeys.init();
		MapAreas.init();
		Logger.log("Engine", "Initiating Objects...");
		ObjectSpawns.init();
		ObjectExamines.init();
		Logger.log("Engine", "Initiating NPCs...");
		NPCCombatDefinitionsL.init();
		// TimeUnit.SECONDS.sleep(30);
		NPCDrops.init(); //sql
		NPCExamines.init(); //sql
		Logger.log("Engine", "Initiating Items...");
		ItemExamines.init(); //sql
		ItemWeights.init(); //sql
		ItemDestroys.init(); //sql
		ItemSpawns.init(); //sql
		Logger.log("Engine", "Initiating Music Hints...");
		MusicHints.init();
		Logger.log("Engine", "Initiating Music Effects...");
		MusicEffects.init();
		Logger.log("Engine", "Initiating Shops...");
		ShopsHandler.init(); //sql
		Logger.log("Engine", "Initiating Scroll Messages...");
		ScrollMessage.load();
		Logger.log("Engine", "Initiating Grand Exchange...");
		GrandExchange.init();
		Logger.log("Engine", "Initiating Controllers...");
		ControllerHandler.init();
		Logger.log("Engine", "Initiating Boss Instances...");
		BossInstanceHandler.init();
		Logger.log("Engine", "Initiating Fishing Spots...");
		FishingSpotsHandler.init();
		Logger.log("Engine", "Initiating NPC Combat Scripts...");
		CombatScriptsHandler.init();
		Logger.log("Engine", "Initiating Dialogues...");
		DialogueHandler.init();
		Logger.log("Engine", "Initiating Cutscenes...");
		CutscenesHandler.init();
		Logger.log("Engine", "Initiating Friend Chats...");
		FriendsChat.init();
		Logger.log("Engine", "Initiating Clans Manager...");
		ClansManager.init();
		Logger.log("Engine", "Initiating Executor Manager...");
		GameExecutorManager.init();
		Logger.log("Engine", "Initiating World...");
		World.init();
		Logger.log("Engine", "Initiating Region Builder...");
		MapBuilder.init();
		Logger.log("Engine", "Loading the Party Room...");
		PartyRoom.init();
		Logger.log("Engine", "Loading NPC Spawns...");
		NPCSpawns.init();
		Logger.log("Engine", "Initiating HTTP Executor...");
		HTTPService.create();
		Logger.log("Engine", "Initiating Game Channels Manager...");
		ShutDownHook.get().join();
		// Runtime.getRuntime().addShutdownHook(ShutDownHook.get());
		try {
			GameChannelsManager.init();
		} catch (Throwable e) {
			Logger.handle(e);
			Logger.log("Engine",
					"Failed Initiating Game Channels Manager. Shutting down...");
			System.exit(1);
			return;
		}
		Logger.log("Engine", "Initiating Login Client Channel Manager...");
		try {
			LoginClientChannelManager.init();
		} catch (Throwable e) {
			Logger.handle(e);
			Logger.log("Engine",
					"Failed Initiating Login Client Manager. Shutting down...");
			System.exit(1);
			return;
		}
		stopwatch.stop();
		if (Settings.HOSTED) {
			System.err.println(Settings.SERVER_NAME + ":hosted (host: "
					+ docoumentHoster() + ") world " + Settings.WORLD_ID
					+ " - took " + stopwatch.elapsed(TimeUnit.MILLISECONDS)
					+ " milli seconds to launch.");
			// if (Settings.HOSTED) -- Removed due to it causing a console error
			// on boot
			// discord.getChannel().sendMessage("Nocturne RS3 (World " +
			// Settings.WORLD_ID + ") is now online!");
			// } else
			// System.err.println(
			// Settings.SERVER_NAME + ":localhost (host: " + docoumentHoster() +
			// ") world " + Settings.WORLD_ID
			// + " - took " + stopwatch.elapsed(TimeUnit.MILLISECONDS) +
			// " milli seconds to launch.");
			addAutoSavingTask();
			addCleanMemoryTask();
			addRecalculatePricesTask();
			Runtime.getRuntime().addShutdownHook(new Thread() {
				@Override
				public void run() {
					savePlayers();
				}
			});
			Thread console = new Thread("console thread") {
				@Override
				public void run() {
					Scanner scanner = new Scanner(System.in);
					while (!shutdown) {
						try {
							String cmd = scanner.nextLine();
							if (cmd.startsWith("kick ")) {
								String[] spl = cmd.substring(5).split(
										"\\s\\|\\=\\|\\s");
								Player player = World
										.getPlayerByDisplayNameAll(spl[0]);
								if (player != null) {
									player.disconnect(true, false);
									System.err.println("Kicked: "
											+ player.getDisplayName());
								} else {
									System.err.println("Player is offline");
								}
							} else if (cmd.equals("players")
									|| cmd.equals("ppl")) {
								System.err.println("There are "
										+ World.getPlayers().size()
										+ " players online");
							} else if (cmd.equals("staff")) {
								for (Player staff : World.getPlayers()) {
									if (staff.getRights() == 0)
										continue;
									staffOnline += 1;
								}
								System.err.println("There are " + staffOnline
										+ " staff online");
								staffOnline = 0;
							} else if (cmd.equals("uptime")) {
								long ticks = currentTime
										- Utils.currentTimeMillis();
								int seconds = Math
										.abs((int) (ticks / 1000) % 60);
								int minutes = Math
										.abs((int) ((ticks / (1000 * 60)) % 60));
								int hours = Math
										.abs((int) ((ticks / (1000 * 60 * 60)) % 24));
								System.err.println("Uptime: "
										+ hours
										+ (hours != 1 ? " hours" : "hour")
										+ ", "
										+ minutes
										+ (minutes != 1 ? " minutes"
												: " minute")
										+ " and "
										+ seconds
										+ (seconds != 1 ? " seconds."
												: " second."));
							} else if (cmd.startsWith("/"))
								World.sendEngineMessage(cmd.substring(1));
							else if (cmd.startsWith("shutdown "))
								shutdown(Integer.parseInt(cmd.substring(9)),
										true, true);
							else
								Logger.log("Console", "The command '" + cmd
										+ "' does not exist.");
						} catch (Throwable t) {
							Logger.handle(t);
						}
					}
					scanner.close();
				}
			};
			Thread commandconsole = new Thread("command console thread") {
				public void run() {
				CommandServer.init();
				}
			};
			console.setDaemon(true);
			console.start();
			commandconsole.start();
			start("net.nocturne.tools.CommandServer2");
			while (!shutdown) {
				try {
					Thread.sleep(1000);
				} catch (Throwable t) {

				}
			}
			processShutdown(true);
		}
	}

	private static InetAddress docoumentHoster() {
		try {
			return InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static boolean shutdown(int delay, boolean force, boolean reboot) {
		if (!force) {
			if (shutdown || delayedShutdownStart != 0)
				return false;
		}
		delayedShutdownStart = Utils.currentTimeMillis();
		delayedShutdownDelay = delay;
		for (Player player : World.getPlayers()) {
			if (player == null || !player.hasStarted() || player.hasFinished())
				continue;
			player.getPackets().sendSystemUpdate(delay, false);
		}
		for (Player player : World.getLobbyPlayers()) {
			if (player == null || !player.hasStarted() || player.hasFinished())
				continue;
			player.getPackets().sendSystemUpdate(delay, false);
		}
		Logger.log("Engine", delay + " seconds remaining.");
		// Using Java 8 Lambda
		GameExecutorManager.slowExecutor.schedule(() -> initShutdown(reboot),
				delay, TimeUnit.SECONDS);
		return true;
	}

	public static boolean initShutdown(boolean reboot) {
		if (shutdown)
			return false;
		shutdown = true;
		processShutdown(reboot);
		return true;
	}

	private static void processShutdown(boolean reboot) {
		HighscoresManager.process();
		Logger.log("Engine", "Shutdown process started...");
		Logger.log("Engine", "Shutting down game network channels...");
		GameChannelsManager.shutdown();
		for (int cycle = 0;; cycle++) {
			for (Player p : World.getPlayers()) {
				Logger.log("Engine", "Force-logging out: "
						+ (World.getPlayers().size() + World.getLobbyPlayers()
								.size()) + " -> cycle: " + cycle + ".");
				try {
					if (p == null || !p.hasStarted() || p.hasFinished())
						continue;
					byte[] data = SerializationUtilities.tryStoreObject(p);
					if (data == null || data.length <= 0)
						continue;
					PlayerHandlerThread.addSave(p.getUsername(), data);
				} catch (Exception e) {
					Logger.log("Engine", "An error has occured: " + e);
				}
			}
			if (World.getPlayers().size() == 0
					&& World.getLobbyPlayers().size() == 0)
				break;
			for (Player player : World.getPlayers())
				player.disconnect(true, false);

			for (Player player : World.getLobbyPlayers())
				player.disconnect(true, false);

			Logger.log("Engine", "Saving data -> cycle: " + cycle + ".");
			GrandExchange.save();
			GrandExchange.savePrices();
			try {
				HighscoresManager.process();
				Thread.sleep(2000);
			} catch (Throwable t) {
				t.printStackTrace();
			}


			try {
				Thread.sleep(2000);
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}

		Logger.log("Engine", "Completed, ready for reboot");
		restart();
	}

	private static void restart() {
		try {
			Runtime.getRuntime().exec(
					"java -Dfile.encoding=UTF-8 -Xmx8G -classpath bin;library/* net.nocturne.Engine arctik.co.uk");
			System.exit(0);

		} catch (final Throwable e) {
			Logger.handle(e);
		}
	}

	private static void addCleanMemoryTask() {
		GameExecutorManager.slowExecutor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				try {
					cleanMemory(Runtime.getRuntime().freeMemory() < Settings.MIN_FREE_MEM_ALLOWED);
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, 0, 10, TimeUnit.MINUTES);
	}

	private static void addAutoSavingTask() {
		GameExecutorManager.slowExecutor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				try {
					savePlayers();
					ClanRank.save();
				} catch (Throwable e) {
					Logger.handle(e);
				}

			}
		}, 0, 1, TimeUnit.MINUTES);
	}

	private static void addRecalculatePricesTask() {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_MONTH, 1);
		c.set(Calendar.HOUR, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		int minutes = (int) ((c.getTimeInMillis() - Utils.currentTimeMillis()) / 1000 / 60);
		int halfDay = 12 * 60;
		if (minutes > halfDay)
			minutes -= halfDay;
		GameExecutorManager.slowExecutor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				try {
					GrandExchange.recalcPrices();
				} catch (Throwable e) {
					Logger.handle(e);
				}

			}
		}, minutes, halfDay, TimeUnit.MINUTES);
	}

	private static void savePlayers() {
		for (Player player : World.getPlayers()) {
			try {
				if (player == null || !player.hasStarted()
						|| player.hasFinished())
					continue;
				byte[] data = SerializationUtilities.tryStoreObject(player);
				if (data == null || data.length <= 0)
					continue;
				if (player.getClanManager() != null
						&& player.getClanManager().getClan() != null)
					SerializableFilesManager.saveClan(player.getClanManager()
							.getClan());
				PlayerHandlerThread.addSave(player.getUsername(), data);
			} catch (Exception e) {
				Logger.logErr("Engine", "An error has occured: " + e);
			}
		}
	}

	private static void cleanMemory(boolean force) throws IOException {
		if (force) {
			ItemDefinitions.clearItemsDefinitions();
			NPCDefinitions.clearNPCDefinitions();
			ObjectDefinitions.clearObjectDefinitions();
		}
		for (Index index : Cache.STORE.getIndexes())
			if (index != null) {
				index.resetCachedFiles();
				index.getMainFile().resetCachedArchives();
			}
		GameExecutorManager.fastExecutor.purge();
		System.gc();
	}
	private static class CustomClassLoader extends URLClassLoader {
		public CustomClassLoader() {
			super(new URL[0]);
		}

		protected java.lang.Class<?> findClass(String name)
				throws ClassNotFoundException {
			try{
				String c = name.replace('.', File.separatorChar) +".class";
				URL u = ClassLoader.getSystemResource(c);
				String classPath = ((String) u.getFile()).substring(1);
				File f = new File(classPath);

				FileInputStream fis = new FileInputStream(f);
				DataInputStream dis = new DataInputStream(fis);

				byte buff[] = new byte[(int) f.length()];
				dis.readFully(buff);
				dis.close();

				return defineClass(name, buff, 0, buff.length, (CodeSource) null);

			} catch(Exception e){
				throw new ClassNotFoundException(e.getMessage(), e);
			}
		}
	}
	private static void start(final String classToStart, final String... args) {

		// start a new thread
		new Thread(new Runnable() {
			public void run() {
				try {
					// create the custom class loader
					ClassLoader cl = new CustomClassLoader();

					// load the class
					Class<?> clazz = cl.loadClass(classToStart);

					// get the main method
					Method main = clazz.getMethod("main", args.getClass());

					// and invoke it
					main.invoke(null, (Object) args);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
}
