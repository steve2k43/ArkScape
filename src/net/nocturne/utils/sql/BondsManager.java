package net.nocturne.utils.sql;

import net.nocturne.game.EntityList;
import net.nocturne.game.World;
import net.nocturne.game.player.Player;
import net.nocturne.game.item.ItemIdentifiers;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BondsManager implements Runnable {

	public static void main(String[] args) throws IOException {
		new Thread(new BondsManager()).start();
	}

	@Override
	public void run() {
		try {
			Database db = new Database("arctik.co.uk", "ArkScape",
					"ArkScape", "arkscape");

			if (!db.initBatch()) {
				System.err.println("[DATABASE] No connection could be made to the database.");
				return;
			}

			String query = "SELECT * FROM bonds";
			try {
				ResultSet rs = db.executeQuery(query);
				while (rs.next()) {
					String username = rs.getString("username");
					Boolean isonline = false;
					isonline = isonline(username);
					if (isonline) {
						process(username);
					}

				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
db.close();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	private static Boolean isonline(String username){
		try{
			if(World.getPlayer(username).isOnline()) {
				return true;
			}
		} catch(NullPointerException e){
				return false;
			}
		return false;
	}

	public static void process(String username) {
		try {
			Database db = new Database("arctik.co.uk", "ArkScape",
					"ArkScape", "arkscape");

			if (!db.initBatch()) {
				System.err.println("[DATABASE] No connection could be made to the database.");
				return;
			}
			int count=0;
			String query = "SELECT COUNT(*) AS total FROM bonds WHERE username='"+username+"'";
			try {
				ResultSet rs = db.executeQuery(query);
				while(rs.next())
					count=rs.getInt(1);
			}catch (SQLException e){
				System.out.println(e);
			}
			int bondcount = 0;
			if(count>0){
				query="SELECT * FROM bonds WHERE username='"+username+"'";
				ResultSet rs=db.executeQuery(query);

				while (rs.next()){
					int ID = rs.getInt("ID");
					bondcount+=rs.getInt("bondcount");
					db.executeUpdate("DELETE FROM bonds WHERE username='"+username+"'");
				}
				World.getPlayer(username).getBank().addItem(ItemIdentifiers.BOND_UNTRADEABLE, bondcount, false);
				World.getPlayer(username).getPackets().sendGameMessage("Thank you for donating! "+bondcount+" Bonds have been added" +
						" to your bank account!");
			}
			db.close();
		}catch (Throwable e){

		}
	}
}