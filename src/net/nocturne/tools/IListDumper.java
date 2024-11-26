package net.nocturne.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import net.nocturne.Settings;
import net.nocturne.cache.Cache;
import net.nocturne.cache.loaders.ItemDefinitions;
import net.nocturne.utils.Utils;

public class IListDumper {

	public static void main(String[] args) {
		try {
			new IListDumper();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public IListDumper() throws IOException {
		Cache.init();
		File file = new File("itemList.txt"); // = new
		// File("information/itemlist.txt");
		if (file.exists())
			file.delete();
		else
			file.createNewFile();
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		// writer.append("//Version = 709\n");
		writer.append("//Version = " + Settings.MAJOR_VERSION);
		writer.newLine();
		writer.flush();
		for (int id = 0; id < Utils.getItemDefinitionsSize(); id++) {
			ItemDefinitions def = ItemDefinitions.getItemDefinitions(id);
			/*
			 * if (def.getName().equals("null")) continue;
			 */
			writer.append(id + " - " + def.getName());
			writer.newLine();
			writer.flush();
		}
		writer.close();
	}

	public static int convertInt(String str) {
		try {
			int i = Integer.parseInt(str);
			return i;
		} catch (NumberFormatException e) {
		}
		return 0;
	}
}