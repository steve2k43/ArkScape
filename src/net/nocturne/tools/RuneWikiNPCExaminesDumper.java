package net.nocturne.tools;

import net.nocturne.cache.Cache;
import net.nocturne.cache.loaders.ItemDefinitions;
import net.nocturne.cache.loaders.NPCDefinitions;
import net.nocturne.tools.ItemDropsPacker1.NPCDrop1;
import net.nocturne.utils.Utils;
import org.apache.commons.lang3.StringUtils;

import javax.net.ssl.HttpsURLConnection;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.xml.ws.http.HTTPException;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.*;

public class RuneWikiNPCExaminesDumper {
	private static boolean DEBUG = true;
	static List<String> list = new ArrayList<>();
	static File file = new File("data/npcs/examines.txt");
	static BufferedWriter writer;

    static {
        try {
            writer = new BufferedWriter(new FileWriter(file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static final void main(String[] args) throws IOException, HTTPException, InterruptedException, ScriptException {
		Cache.init();
		int specific = 0;
		int startnpc = 0;
		if(specific != 0){
			dumpNPC(specific);

			System.out.println("Dumped NPCid: "+specific+" - "+NPCDefinitions.getNPCDefinitions(specific).getName());
		}else {
			for (int i = startnpc; i < Utils.getNPCDefinitionsSize(); i++) {
				NPCDefinitions defs = NPCDefinitions.getNPCDefinitions(i);
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
		;
		if(DEBUG) {
			System.out.println(("DEBUG TRUE"));
			System.out.println(("NPC NAME: "+NPCDefinitions.getNPCDefinitions(npcId).getName()));
		}
		List<NPCDrop1> drops = new ArrayList<NPCDrop1>();
		String npcname = NPCDefinitions.getNPCDefinitions(npcId).name;
		String examine = "";
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
			String[] Examinelist = new String[0];

			inputLine=inputLine.replace(";",",");
			//System.out.println(inputLine);
			if(inputLine.startsWith("|examine1 =")){
				skipfile=false;
				String[] examineline = inputLine.split(("="));
				if(examineline.length > 1) {
					examine = trim(examineline[1]);
				} else {
					skipfile=true;
				}
				if(examine.startsWith("{{*}}")) {
					String examineline2 = StringUtils.substringBetween(examine, "{{*}}", "&");
					examineline2 = trim(examineline2);
					examineline2 = strip(examineline2);
					examine = examineline2;
				}
			} else if(inputLine.startsWith("|examine =")){
				skipfile=false;
				String[] examineline = inputLine.split(("="));
				if(examineline.length > 1) {
					examine = trim(examineline[1]);
				}else{
					skipfile=true;
				}
				if(examine.startsWith("{{*}}")) {
					String examineline2 = StringUtils.substringBetween(examine, "{{*}}", "&");
					examineline2 = trim(examineline2);
					examineline2 = strip(examineline2);
					examine = examineline2;
				}
			}


		}

		if(DEBUG) {

		}
		if(!skipfile && examine != "") {



		}else{
			writer.write("// "+npcId+" - "+npcname);
			writer.newLine();
			writer.flush();
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
