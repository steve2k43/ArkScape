package net.nocturne.tools;

import net.nocturne.cache.Cache;
import net.nocturne.cache.loaders.NPCDefinitions;
import net.nocturne.utils.Utils;
import net.nocturne.utils.sql.Database;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ItemDropsPacker1 {

	static class NPCDrop1 {

		private int itemId, minAmount, maxAmount;
		private String rarity;

		NPCDrop1(int itemId, int minAmount, int maxAmount, String rarity) {
			if (itemId == 617)
				itemId = 995;
			if (itemId == 2513)
				itemId = 3140;
			this.itemId = itemId;
			this.minAmount = minAmount;
			this.maxAmount = maxAmount;
			this.rarity = rarity;
		}

		int getMinAmount() {
			return minAmount;
		}

		int getMaxAmount() {
			return maxAmount;
		}

		public int getItemId() {
			return itemId;
		}

		public String getRarity() {
			return rarity;
		}

	}
}