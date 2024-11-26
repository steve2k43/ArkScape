package net.nocturne.utils.sql.packers;

import net.nocturne.game.item.Item;
import net.nocturne.game.player.content.Shop;
import net.nocturne.utils.Logger;
import net.nocturne.utils.sql.Database;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.Statement;

import static net.nocturne.utils.ShopsHandler.addShop;

public class shops2sql {
    private static final String UNPACKED_PATH = "data/items/unpackedShops.txt";
    private static final int SHOP_QUANTITY_RATE = 10;
    public static void main(String[] args) {
        try {
            Database db = new Database("arctik.co.uk", "ArkScape",
                    "ArkScape", "arkscape");

            if (!db.init()) {
                System.err.println("[DATABASE] No connection could be made to the database.");
            }
            Connection conn = db.connect();
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("DROP TABLE IF EXISTS shops");
            String query = "CREATE TABLE shops (ID INTEGER, Money_ID INTEGER, General_Store INTEGER, Iron_Man INTEGER," +
                    "Store_Name Varchar(50)";
            for (int i = 1; i <= 50; i++) {
                query += ",id"+i;
                query += " INTEGER,";
                query += "q"+i;
                query += " INTEGER";
            }
            query += ")";
            stmt.executeUpdate(query);
            @SuppressWarnings("resource")
            BufferedReader in = new BufferedReader(
                    new FileReader(UNPACKED_PATH));
            /*
             * BufferedWriter writer = new BufferedWriter(new FileWriter(PATH,
             * true));
             */
            while (true) {
                String line = in.readLine();
                if (line == null)
                    break;
                if (line.startsWith("//"))
                    continue;
                String[] splitedLine = line.split(" - ", 3);
                if (splitedLine.length != 3)
                    throw new RuntimeException("Invalid list for shop line: "
                            + line);
                String[] splitedInform = splitedLine[0].split(" ", 4);
                if (splitedInform.length != 4)
                    throw new RuntimeException("Invalid list for shop line: "
                            + line);
                String[] splitedItems = splitedLine[2].split(" ");
                //System.out.println(splitedItems.length/2);
                int key = Integer.valueOf(splitedInform[0]);
                int money = Integer.valueOf(splitedInform[1]);
                boolean generalStore = Boolean.valueOf(splitedInform[2]);
                boolean ironMan = Boolean.valueOf(splitedInform[3]);
                int generalStore1 = Boolean.valueOf(splitedInform[2]) ? 1 : 0;
                int ironMan1 = Boolean.valueOf(splitedInform[3]) ? 1 : 0;
                String shopname = splitedLine[1];
                if(shopname.contains("'")){
                    shopname = shopname.replace("'","''");
                }
                String uquery = "INSERT INTO shops (ID, Money_ID, General_Store, Iron_Man, Store_Name) VALUES" +
                        " ("+key+","+money+","+generalStore1+","+ironMan1+",'"+shopname+"')";
                stmt.executeUpdate(uquery);
                Item[] items = new Item[splitedItems.length / 2];
                String[] itemss = new String[splitedItems.length / 2];
                int count = 0;
                for (int i = 0; i < items.length; i++) {
                    items[i] = new Item(Integer.valueOf(splitedItems[count++]),
                            Integer.valueOf(splitedItems[count++]), 0, true);

                }
                count = 0;
                System.out.println("Shop: "+key);
                for (int i = 0; i < itemss.length; i++) {
                    String u = "UPDATE shops SET id"+(i+1)+"="+splitedItems[count++]+" WHERE ID="+key;
                    String u1 = "UPDATE shops SET q"+(i+1)+"="+splitedItems[count++]+" WHERE ID="+key;
                    System.out.println(u);
                    System.out.println(u1);
                    stmt.executeUpdate(u);
                    stmt.executeUpdate(u1);
                }
                if (key < 1200) {
                    for (int i = 0; i < items.length; i++) {
                        // handleGeStuff(items[i].getId(), writer);
                        int value = items[i].getAmount();
                        if (value < Short.MAX_VALUE) // 32k
                            items[i].setAmount(value * SHOP_QUANTITY_RATE);
                    }
                } else {
                    for (int i = 0; i < items.length; i++) {
                        if (items[i].getDefinitions().isStackable()
                                || items[i].getDefinitions().isNoted())
                            items[i].setAmount(items[i].getId() == 995 ? 2000000000
                                    : 1000000);
                        else
                            items[i].setAmount(5000);
                    }
                }

                // out.writeInt(key);
                //writeAlexString(out, splitedLine[1]);
                //out.writeShort(money);
                //out.writeBoolean(generalStore);
                //out.writeBoolean(ironMan);
                //out.writeByte(items.length);
                for (Item item : items) {
                    //out.writeShort(item.getId());
                    //out.writeInt(item.getAmount());
                    int a=1;
                }
                addShop(key, new Shop(key, splitedLine[1], money, items,
                        generalStore, ironMan));
            }
            in.close();
            //out.close();
            // writer.close();
        } catch (Throwable e) {
            Logger.handle(e);
        }
    }
}
