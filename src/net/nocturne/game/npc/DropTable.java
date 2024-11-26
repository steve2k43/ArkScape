package net.nocturne.game.npc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.nocturne.cache.loaders.ItemDefinitions;
import net.nocturne.game.player.Player;
import net.nocturne.utils.Utils;
import net.nocturne.utils.sql.Database;
import net.nocturne.utils.sql.packers.npcdropsSQL;

public class DropTable {

	public static void getDropTable(Player player, NPC npc) {
		Database db = new Database("arctik.co.uk", "ArkScape",
				"ArkScape", "npcdrops");

		if (!db.init()) {
			System.err.println("[DATABASE] No connection could be made to the database.");
		}
		String list = "";
		long currentTime = Utils.currentTimeMillis();
		Drops drops = npcdropsSQL.getmydrop(npc.getId());

		if (npc == null || npc.isDead()
				|| !npc.getDefinitions().hasAttackOption() || drops == null)
			return;
		if (player.isLocked() || player == null || player.isDead())
			return;
		if (player.getRights() < 2) {
			if (player.getAttackedByDelay() + 10000 > currentTime) {
				player.getPackets().sendGameMessage(
						"You can't do that while in combat.");
				return;
			}
		}
		if (player.getInterfaceManager().containsScreenInterface()
				|| player.getInterfaceManager().containsBankInterface()) {
			player.getPackets().sendGameMessage(
					"Please finish what you're doing before opening "
							+ npc.getName() + "'s drop table.");
			return;
		}
		List<Drop> dropL = drops.getAllDrops();
		System.out.println(dropL.size());
		Boolean isitrare = drops.isitrare();
		player.getInterfaceManager().sendCentralInterface(1166);
		for (Drop drop : dropL) {
			for (int i = 0; i < 1; i++) {
				int npcid = npc.getId();
				int rarity = 0;
				double dropchance = 0;
				String dropchancestring=null;
				String query = "SELECT * FROM _"+npcid+" WHERE ID="+drop.getItemId()+" AND min="+drop.getMinAmount()+" ORDER BY rarity ASC";
				try {
					ResultSet rs = db.executeQuery(query);
					i = 0;

					while (rs.next()) {
						rarity = (rs.getInt(4));
						dropchancestring = (rs.getString(5));
						i++;
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
				System.out.println(dropchancestring);
				String[] explode = dropchancestring.split("/");
				double a = Double.parseDouble(explode[0]);
				double b = Double.parseDouble(explode[1]);
				dropchance = Utils.round(a/b, 2);
				if(player.getDifficulty() == 1)
					dropchance = Math.ceil(dropchance / 1);
				if(player.getDifficulty() == 2)
					dropchance = Math.ceil(dropchance / 1.1);
				if(player.getDifficulty() == 3)
					dropchance = Math.ceil(dropchance / 1.3);
				String color="<col=FFFFFF>";
				if(rarity==0){  color = "<col=FFFFFF>"; }
				if(rarity==1){  color = "<col=9fff33>"; }
				if(rarity==2){  color = "<col=337aff>"; }
				if(rarity==3){  color = "<col=7715ab>"; }
				if(rarity==4){  color = "<col=f38b07>"; }
				int min = Integer.parseInt(Utils.formatNumber(drop.getMinAmount()));
				int max = Integer.parseInt(Utils.formatNumber(drop.getMaxAmount()).replaceAll(",", ""));
				list += color+ ItemDefinitions.getItemDefinitions(drop.getItemId()).getName()+"</col>";

				if(max-min != 0){ list += color+" (" + Utils.formatNumber(drop.getMinAmount()) + " - " + Utils.formatNumber(drop.getMaxAmount());list += ")"; }
				if(max-min==0 && min > 1){ list += color+" (" + min;list += ")"; }
				list += " - "+dropchancestring;
				list += "<br>";
			}
		}
		player.getPackets().sendIComponentText(1166, 2,
				"Total <col=00FF00>" + dropL.size() + "</col> drops (Rare: "+isitrare+")");
		player.getPackets().sendIComponentText(1166, 23,
				npc.getName() + "'s Drop Table");
		player.getPackets().sendIComponentText(1166, 1, list);
		db.close();
	}
}