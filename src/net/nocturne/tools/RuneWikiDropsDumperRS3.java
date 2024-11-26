package net.nocturne.tools;

import net.nocturne.cache.Cache;
import net.nocturne.cache.loaders.ItemDefinitions;
import net.nocturne.cache.loaders.NPCDefinitions;
import net.nocturne.tools.ItemDropsPacker1.NPCDrop1;
import net.nocturne.utils.Utils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.Fraction;

import javax.net.ssl.HttpsURLConnection;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.xml.ws.http.HTTPException;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RuneWikiDropsDumperRS3 {
	private static double GUAM = 25;
	private static double TARROMIN = 14.1;
	private static double MARRENTILL = 18.8;
	private static double HARRALANDER = 10.9;
	private static double RANARR = 8.59;
	private static double IRIT = 6.25;
	private static double AVANTOE = 6.29;
	private static double KWUARM = 3.91;
	private static double CADANTINE = 3.12;
	private static double LANTADYME = 2.34;
	private static double DWARFWEED = 2.34;
    private static boolean DEBUG = true;




	public static final void main(String[] args) throws IOException, HTTPException, InterruptedException, ScriptException {
		Cache.init();
		int specific = 0;
		int startnpc = 16700;
		if(specific != 0){
			dumpNPC(specific);

				System.out.println("Dumped NPCid: "+specific+" - "+NPCDefinitions.getNPCDefinitions(specific).getName());
		}else {
			for (int i = startnpc; i < Utils.getNPCDefinitionsSize(); i++) {
				NPCDefinitions defs = NPCDefinitions.getNPCDefinitions(i);
				if (!defs.hasAttackOption())
					continue;
				try {
					if (!dumpNPC(i))

						System.out.println("Failed dumping npc: " + i + ", "
								+ NPCDefinitions.getNPCDefinitions(i).name);
					else
						System.out.println("Dumped npc: " + i + ", "
								+ NPCDefinitions.getNPCDefinitions(i).name + " / " + Utils.getNPCDefinitionsSize());
				} catch (java.lang.StringIndexOutOfBoundsException e) {
					System.out.println("Failed on NPC:" +i);
					e.printStackTrace();

				}
				catch (ScriptException e) {
					System.out.println("Failed on NPC:" +i);
					e.printStackTrace();

				}
				catch (IOException f) {
					System.out.println("Failed on NPC:" +i);
					System.err.println(f);
					System.err.println(f.getMessage());
					i--;
					if(f.getMessage().contains("Server returned HTTP response code: 429")){
						System.out.println("Sleeping for 10 seconds before retrying NPC: "+(i+1));
						TimeUnit.SECONDS.sleep(10);
						System.out.println("Retrying id: "+(i+1));
					} else {
						break;
					}
				}
				catch (HTTPException e){
					System.out.println("Failed on NPC:" +i);
					e.printStackTrace();
					i--;
					System.out.println("Sleeping for 10 seconds before retrying NPC: "+i);
					TimeUnit.SECONDS.sleep(10);
					System.out.println("Retrying id: "+i);
					//break;
				}
			}
		}
	}

	public static boolean dumpNPC(int npcId) throws IOException, ScriptException {
		if(DEBUG) {
			System.out.println(("DEBUG TRUE"));
			System.out.println(("NPC NAME: "+NPCDefinitions.getNPCDefinitions(npcId).getName()));
		}
		List<NPCDrop1> drops = new ArrayList<NPCDrop1>();
		String npcname = NPCDefinitions.getNPCDefinitions(npcId).name;
		npcname = npcname.replaceAll(" ", "_");
		String httpsURL = "https://runescape.wiki/w/"+npcname+"?action=edit";
		URL myurl = new URL(httpsURL);
		HttpsURLConnection con = (HttpsURLConnection) myurl.openConnection();
		con.setRequestProperty ( "User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:63.0) Gecko/20100101 Firefox/63.0" );
		InputStream ins = con.getInputStream();
		InputStreamReader isr = new InputStreamReader(ins);
		BufferedReader in = new BufferedReader(isr);
		String inputLine;
		boolean raredroptable=false;
		boolean skipfile=true;
		boolean herbs=false;
		boolean seeds=false;
		String herbchance=null;
		int dropcount=0;
		//System.out.println(NPCDefinitions.getNPCDefinitions(npcId).name);
		while ((inputLine = in.readLine()) != null) {
			int id = -1;
			boolean noted = false;
			String[] Droplist = new String[0];
			inputLine=inputLine.replace(";",",");

			if(inputLine.startsWith("{{Rare drop table}}")){
				skipfile=false;
				raredroptable=true;
			}
			if(inputLine.startsWith("{{DropsLine|")){
				//inputLine="{{DropsLine|name=Large plated rune salvage|quantity=  15 (noted)|rarity=Common}}";
				//System.out.println("BEFORE: "+ inputLine);
				if(DEBUG)
					System.out.println("Entered Dropsline Area");
				inputLine = inputLine.replaceAll("}", "|");
				//System.out.println("BEFORE: "+ inputLine);
				//name
				String nameline[] = StringUtils.substringsBetween(inputLine, "name", "|");
				nameline[0]= StringUtils.replace(nameline[0], "=","");
				nameline[0]=nameline[0].trim();
				if(nameline[0] != null) {
					if (nameline[0].contains("Sealed clue scroll (easy)"))
						nameline[0] = "Clue scroll (easy)";
					if (nameline[0].contains("Sealed clue scroll (medium)"))
						nameline[0] = "Clue scroll (medium)";
					if (nameline[0].contains("Sealed clue scroll (hard)"))
						nameline[0] = "Clue scroll (hard)";
				}
				
				//ID
				id = getItemId(nameline[0]);
				//quantity
				int qmin=0;
				int qmax=0;
				String qline[];
				if(inputLine.contains("quantity")) {
					qline = StringUtils.substringsBetween(inputLine, "quantity", "|");
					qline[0] = StringUtils.replace(qline[0], "=", "");
					//System.out.println("QLINE: "+qline[0]);
					qline[0] = qline[0].trim();
				} else {
					qline = new String[]{"", ""};
				}
				//System.out.println("QLINE: "+qline);
				//noted
				if (qline[0].contains(" (noted)")) {
					noted = true;
					qline[0] = qline[0].replace((" (noted)"), "");
				}
				if (qline[0].contains(" noted)")) {
					noted = true;
					qline[0] = qline[0].replace((" noted)"), "");
				}
				if (qline[0].contains("(noted)")) {
					noted = true;
					qline[0] = qline[0].replace(("(noted)"), "");
				}
				if (qline[0].contains("(Noted)")) {
					noted = true;
					qline[0] = qline[0].replace(("(Noted)"), "");
				}
				if (qline[0].contains("noted")) {
					noted = true;
					qline[0] = qline[0].replace(("noted"), "");
				}
				//qline[0]="10-13, 10, 2, 7 , 9 - 53,21";
				//System.out.println("qline0 = "+qline[0]);
				String quantitystring[] = qline[0].split(",");
				int quantitylength = quantitystring.length;
				//System.out.println("Q length = "+quantitylength);
				List<Integer> quantlist = new ArrayList<>();
				if(quantitylength > 1){
					for(int ii=0; ii<quantitylength; ii++){
						//System.out.println(quantitystring[ii]);
						if(quantitystring[ii].contains("-")){
							String qq[] =quantitystring[ii].split("-");
							quantlist.add(Integer.valueOf(StringUtils.deleteWhitespace(qq[0])));
							quantlist.add(Integer.valueOf(StringUtils.deleteWhitespace(qq[1])));
						}else{
							quantlist.add(Integer.valueOf(StringUtils.deleteWhitespace(quantitystring[ii])));
						}
					}
					Collections.sort(quantlist);
					qline[0]=""+quantlist.get(0)+"-"+quantlist.get(quantlist.size()-1);
				}
				//System.out.println("qline0 = "+qline[0]);
				//System.out.print(quantlist);
				qline[0]=StringUtils.deleteWhitespace(qline[0]);
				if(qline[0].contains("-")||qline[0].contains(",")){
					if(qline[0].contains("-")){
						String quantity[] = qline[0].split("-");
						qmin = Integer.parseInt(StringUtils.deleteWhitespace(quantity[0]));
						qmax = Integer.parseInt(StringUtils.deleteWhitespace(quantity[1]));
					}
					if(qline[0].contains(",")){
						String quantity[] = qline[0].split(",");
						qmin = Integer.parseInt(StringUtils.deleteWhitespace(quantity[0]));
						qmax = Integer.parseInt(StringUtils.deleteWhitespace(quantity[1]));
					}
				}else if(!qline[0].isEmpty()){
					//System.out.println(Integer.parseInt(qline[0]));
					qmin= Integer.parseInt(qline[0]);
					qmax=qmin;
				} else{
					if(DEBUG)
					System.out.println("ERROR - SKIPPING");
				}
				if(qmin==0 || qmax==0){
					if(DEBUG)
						System.out.println("THERE WAS AN ERROR IN QUANTITY. SETTING TO 1");
					qmin=1;qmax=1;
				}
				//rarity
				String rarityline[]=null;
				if(inputLine.contains("rarity=")) {
					rarityline = StringUtils.substringsBetween(inputLine, "rarity=", "|");
				} else if(inputLine.contains("rarity =")) {

						String rarityline1[] = StringUtils.substringsBetween(inputLine, "rarity =", "|");
						rarityline=rarityline1;

				}

				System.out.println(rarityline[0]);
				rarityline[0]= StringUtils.replace(rarityline[0], "=","");
				rarityline[0]=rarityline[0].trim();
				if(rarityline[0].contains("#expr")){
					System.out.println("FOUND EXPRESSION ERROR");
					String expr[]=StringUtils.substringsBetween(rarityline[0], "#expr:", ")");
					expr[0]=expr[0]+")";
					System.out.println("expr[0]: "+expr[0]);
					String expr1[] = expr[0].split("^([^/]+)/");
					String expr2[] = expr[0].split("/");

					ScriptEngineManager manager = new ScriptEngineManager();
					ScriptEngine engine
							= manager.getEngineByName("JavaScript");   // Retrieve a JavaScript engine from the manager
					String str = "10-4*5";    // Define a mathematical expression to evaluate
					System.out.println(engine.eval(str));
					int exprnum = Integer.parseInt(expr2[0]);
					double exprden = (double) engine.eval(expr1[1]);
					exprden = Utils.round(1/exprden, 1);
					System.out.println("RESOLVED: "+exprnum+"/"+exprden);
					rarityline[0]=exprnum+"/"+exprden;
				}
				if(rarityline[0].contains("Always")|| rarityline[0].contains("always"))
					rarityline[0] = "1/1";
				if(rarityline[0].equals("Common") || rarityline[0].equals("common"))
					rarityline[0] = "1/10";
				if(rarityline[0].equals("Uncommon") || rarityline[0].equals("uncommon"))
					rarityline[0] = "1/30";
				if(rarityline[0].equals("Rare") || rarityline[0].equals("rare"))
					rarityline[0] = "1/100";
				if(rarityline[0].equals("very rare") || rarityline[0].equals("Very rare") || rarityline[0].equals("very Rare") || rarityline[0].equals("Very Rare"))
					rarityline[0] = "1/500";
				if(rarityline[0].contains("Random")|| rarityline[0].contains("random"))
					rarityline[0] = "1/100";
				if(rarityline[0].toLowerCase().contains("unknown"))
					rarityline[0] = "1/100";
				if(rarityline[0].toLowerCase().contains("varies"))
					rarityline[0] = "1/100";
				if(rarityline[0].isEmpty() || rarityline[0].contains("notfound")){
					rarityline[0] = "1/10";
				}
				String name = nameline[0];
				String rarity = rarityline[0];
				rarity = rarity.replaceAll(",", "");
				if(DEBUG) {
					System.out.println("ID === " + id);
					System.out.println("NAME === " + nameline[0]);
					System.out.println("quantity === " + qline[0]);
					System.out.println("rarity === " + rarityline[0]);
					System.out.println("qmin === " + qmin);
					System.out.println("qmax === " + qmax);
					System.out.println("noted === " + noted);
				}

				skipfile=false;
				dropcount++;




				if(noted) {
					//System.out.println("OLD ID: "+id);
					id = ItemDefinitions.getItemDefinitions(id).certId;
					//System.out.println("NEW ID: "+id);
				}

				if(id!=-1 && name!= null) {


					if(DEBUG) {
						System.out.println("Item name: " + name + " ID: " + id + " Qm: " + qmin + " Qmm: " + qmax + " Rarity: " + rarity + " Noted: "+noted);
					}
					 if(isParsable(rarity)){
						 drops.add(new NPCDrop1(id, qmin, qmax,
								 rarity));
					}else{
						 drops.add(new NPCDrop1(id, qmin, qmax,
								rarity));
					 }


				}

			}
			if (inputLine.startsWith("{{HerbDropTableIntro|")) {
				if(inputLine.contains("override")){
					//if(DEBUG)
					System.out.println("STRING"+ inputLine);
					String splitted[] = inputLine.split("/");
					String str1 = splitted[0].substring(splitted[0].lastIndexOf(" ")+1);


					String str2 = splitted[1].substring(0, splitted[1].indexOf(" "));

					if(str1.contains("|")){
						str1 = str1.substring(str1.lastIndexOf("|") + 1);
					}
					if(str2.contains("|")){
						str2 = str2.substring(0, str2.indexOf("|"));
					}
					System.out.println("0 = "+str1);
					System.out.println("1 = "+str2);
					//String str1 = splitted[0].replaceAll("[^0-9. ]", "");
					//String str2 = splitted[1].replaceAll("[^0-9. ]", "");
					str1 = str1.replaceAll("\\s.*", "");
					str2 = str2.replaceAll("\\s.*", "");
					herbs = true;
					herbchance=str1+"/"+str2;
					System.out.println("Herb Chance: " + herbchance);
				} else {
					herbs = true;
					herbchance = StringUtils.substringBetween(inputLine, "|", "}");
					//System.out.println("Herb Chance: " + herbchance);
				}
			}
		}
		if(herbs){
			if(DEBUG) {
				System.out.println("HERBS DETECTED");
				System.out.println(herbchance);
			}
			String[] herbchancesplit = herbchance.split("/");
			int a = Integer.valueOf(herbchancesplit[0]);
			double b = Double.valueOf(herbchancesplit[1]);
			double c = Utils.round(a/b*100, 2);

			if(DEBUG) {
				System.out.println("A: "+a);
				System.out.println("B: "+b);
				System.out.println("C: "+c);
				System.out.println("Herb Chance: " + c);
			}
			double guamchance = Utils.round(1/(c/100*GUAM)*100, 2);
			double marrentilchance = Utils.round(1/(c/100*MARRENTILL)*100, 2);
			double tarrochance = Utils.round(1/(c/100*TARROMIN)*100, 2);
			double harrachance = Utils.round(1/(c/100*HARRALANDER)*100, 2);
			double ranarrchance = Utils.round(1/(c/100*RANARR)*100, 2);
			double iritchance = Utils.round(1/(c/100*IRIT)*100, 2);
			double avantoechance = Utils.round(1/(c/100*AVANTOE)*100, 2);
			double kwuarmchance = Utils.round(1/(c/100*KWUARM)*100, 2);
			double cadchance = Utils.round(1/(c/100*CADANTINE)*100, 2);
			double lantchance = Utils.round(1/(c/100*LANTADYME)*100, 2);
			double dwarfchance = Utils.round(1/(c/100*DWARFWEED)*100, 2);
			//System.out.println("GUAM Chance: 1/"+guamchance);
			drops.add(new NPCDrop1(199, 1, 1, "1/"+String.valueOf(guamchance)));
			drops.add(new NPCDrop1(201, 1, 1, "1/"+String.valueOf(marrentilchance)));
			drops.add(new NPCDrop1(203, 1, 1, "1/"+String.valueOf(tarrochance)));
			drops.add(new NPCDrop1(205, 1, 1, "1/"+String.valueOf(harrachance)));
			drops.add(new NPCDrop1(207, 1, 1, "1/"+String.valueOf(ranarrchance)));
			drops.add(new NPCDrop1(209, 1, 1, "1/"+String.valueOf(iritchance)));
			drops.add(new NPCDrop1(211, 1, 1, "1/"+String.valueOf(avantoechance)));
			drops.add(new NPCDrop1(213, 1, 1, "1/"+String.valueOf(kwuarmchance)));
			drops.add(new NPCDrop1(215, 1, 1, "1/"+String.valueOf(cadchance)));
			drops.add(new NPCDrop1(2485, 1, 1, "1/"+String.valueOf(lantchance)));
			drops.add(new NPCDrop1(217, 1, 1, "1/"+String.valueOf(dwarfchance)));

		}
		if(DEBUG) {
			System.out.println("Drop Count: " + dropcount);
			System.out.println("Skip file = " + skipfile);
			System.out.println("DROP SIZE = " + drops.size());
		}
			if(!skipfile && drops.size() != 0) {

				File file = new File("data/npcs/drops/" + npcId + ".txt");
				BufferedWriter writer = new BufferedWriter(new FileWriter(file));

				writer.write("RareDropTable=" + raredroptable);
				writer.newLine();
				writer.flush();
				for (NPCDrop1 drop : drops) {
					writer.write(drop.getItemId()+", "+drop.getMinAmount()+", "+drop.getMaxAmount()+", "+drop.getRarity());
					writer.newLine();
					writer.flush();
				}

				writer.close();
			}else{
				if(DEBUG)
				System.out.println("EMPTY");
			}


		//System.out.println(dropcount);
		//System.out.println("Raredroptable: "+raredroptable);

		in.close();
		return true;
	}
	public static boolean isParsable(String input) {
		try {
			Integer.parseInt(input);
			return true;
		} catch (final NumberFormatException e) {
			return false;
		}
	}

	public static int getItemId(String name) {
		name = name.replace(" (General Graardor)", "");
		name = name.replace(" (blue)", "");
		name = name.replace(" (black)", "");
		name = name.replace("Chefs hat", "Chef's hat");
		name = name.replace("(top)", "top");
		name = name.replace("(bottom)", "bottom");
		name = name.replace("Clue scroll{{!", "");
		name = name.replace(" (light)", "");
		name = name.replace(" (dark)", "");
		for (int itemId = 0; itemId < Utils.getItemDefinitionsSize(); itemId++) {
			ItemDefinitions item = ItemDefinitions.getItemDefinitions(itemId);
			if (item.isNoted() || item.isLended())
				continue;
			if (item.getName().equalsIgnoreCase(name))
				return itemId;
		}
		return -1;
	}
	static String getToken(String s) {
		Matcher matcher = Pattern.compile("[\\[{]*([^\\]\\[{}]+)[|}]*").matcher(s);
		String token="";
		if(matcher.find()) {
			token = matcher.group(1);
		}
		return token;
	}


}
