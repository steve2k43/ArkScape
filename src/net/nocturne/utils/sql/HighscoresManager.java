package net.nocturne.utils.sql;

import net.nocturne.Settings;

import java.util.LinkedList;
import java.util.Queue;

public class HighscoresManager {

	private static Queue<String> queue = new LinkedList<String>();
	@SuppressWarnings("unused")
	private static int QUERY_LIMIT = 50;
	private static boolean isBusy;

	public static Queue<String> getQueue() {
		return queue;
	}

	public static void addToQueue(String query) {
		if (query.toLowerCase().startsWith("select")) {
			System.err
					.println("DbManager is only for UPDATE, DELETE, and Insert Statements.");
			return;
		}
		queue.add(query);
	}

	public static void process() {
		new Thread() {
			public void run() {
				if (isBusy || queue.isEmpty())
					return;

				System.err.println("[HiScores] Beginning to process "
						+ queue.size() + " queries.");
				Database db = new Database(Settings.MYSQL_HOST, Settings.MYSQL_USER,
						Settings.MYSQL_PASS, "arkscape");

				if (!db.init()) {
					System.err
							.println("[DATABASE] No connection could be made to the database.");
					return;
				}

				int processed = 0;

				isBusy = true;

				while (!getQueue().isEmpty()) {
					try {
						db.executeUpdate(getQueue().poll());
						processed++;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				isBusy = false;

				System.err.println("[HiScores] Processed " + processed
						+ " queries with " + queue.size() + " remaining.");
				db.destroyAll();
			}
		}.start();
	}
}
