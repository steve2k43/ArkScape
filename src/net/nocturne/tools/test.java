package net.nocturne.tools;// Java code for serialization and deserialization
// of a Java object


import minifs.MiniFS;
import net.nocturne.cache.Cache;
import net.nocturne.game.World;
import net.nocturne.game.WorldTile;
import net.nocturne.game.item.Item;
import net.nocturne.game.item.ItemIdentifiers;
import net.nocturne.game.npc.Drop;
import net.nocturne.game.npc.Drops;
import net.nocturne.game.player.DonationManager;
import net.nocturne.game.player.Player;
import net.nocturne.login.GameWorld;
import net.nocturne.login.account.Account;
import net.nocturne.utils.*;
import net.nocturne.utils.sql.Database;

import java.awt.event.KeyListener;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import net.nocturne.utils.sql.packers.npcdropsSQL;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import java.awt.event.KeyEvent;
import java.util.List;

import net.nocturne.utils.ObjectSpawns;
import org.apache.commons.lang3.StringUtils;

public class test {
    private static final int[][] TOOLBELT_ITEMS = new int[][] {
            { 946 }, // knife 1
            { 1735 }, // shears 2
            { 1595 }, // amulet mould 3
            { 1755 }, // chisel 4
            { 1599 }, // holy mould 5
            { 1597 }, // necklake mould 6
            { 1733 }, // needle 7
            { 1592 }, // ring mold 8
            { 5523 }, // tiara mold 9
            { 13431 }, // crayfish cage 10
            { 307 }, // fishing road 11
            { 309 }, // fly fishing road 12
            { 311 }, // harpoon 13
            { 301 }, // lobster pot 14
            { 303 }, // small fishing net 15
            { 1265 }, // bronze pickaxe 16
            { 2347 },// hammer 16 bronze hatchet
            { 1351 }, // hatchet 18 chompy
            { 590 }, // tinderbox 19
            { -1 }, //20
            { 1267, 1269, 1273, 1271, 1275, 15259, 32646 }, // Pickaxes
            { 1349, 1353, 1357, 1355, 1359, 6739, 32645 }, //Hatchets
            { 8794 }, //saw
            { 4 }, //24
            { 9434 }, { 11065 }, { 1785 }, { 2976 }, { 1594 }, { 5343 },
            { 5325 }, { 5341 }, { 5329 }, { 233 }, { 952 }, { 305 }, { 975 },
            { 11323 }, { 2575 }, { 2576 }, { 13153 }, { 10150 }, {19675}, {100000} }; //43
    private static final int[][] DUNG_TOOLBELT_ITEMS = new int[][] {
            { 16295, 16297, 16299, 16301, 16303, 16305, 16307, 16309, 16311,
                    16313, 16315 },
            { 16361, 16363, 16365, 16367, 16369, 16371, 16373, 16375, 16377,
                    16379, 16381 }, { 17883 }, { 17678 }, { 17794 }, { 17754 },
            { 17446 }, { 17444 } };

    private static final int[] VAR_IDS = new int[] { 1102, 1103 };
    private static final int[] DUNG_VAR_IDS = new int[] { 1107 };
    private static int[][] items;
    private transient Player player;
    private static transient boolean dungeonnering;

    public static void main(String[] args) throws IOException {
        items = new int[][] { new int[TOOLBELT_ITEMS.length],
                new int[DUNG_TOOLBELT_ITEMS.length] };

        int id = 1597;
        Item item = new Item(id);
        int[] slot = getItemSlot(id);
        System.out.println("Slot 0 = "+slot[0]);
        System.out.println("Slot 1 = "+slot[1]);
        System.out.println("getItems()[slot0] = "+getItems()[slot[0]]);
        System.out.println(getIncremment(slot[0]));
        getItems()[slot[0]] = slot[1] + 1;
        System.out.println("Slot [1]+1 = "+slot[1]+1);


        refreshConfigs();
    }
    private static int[] getItemSlot(int id) {
        for (int i = 0; i < getToolbeltItems().length; i++)
            for (int i2 = 0; i2 < getToolbeltItems()[i].length; i2++)
                if (getToolbeltItems()[i][i2] == id)
                    return new int[] { i, i2 };
        return null;
    }
    public static int[][] getToolbeltItems() {
        return dungeonnering ? DUNG_TOOLBELT_ITEMS : TOOLBELT_ITEMS;
    }
    public static int[] getItems() {
        return items[dungeonnering ? 1 : 0];
    }

    private static void refreshConfigs() {

        int[] varValues = new int[getVars().length];
        System.out.println("Vars.length= " +getVars().length);
        System.out.println(("varValues = "+ Arrays.toString(varValues)));
        System.out.println("getItems length: "+getItems().length);
        int indexIncremment = 0;
        for (int i = 0; i < getItems().length; i++) {
            System.out.println("Int # "+i);
            System.out.println(getItems()[i]);
            if (getItems()[i] != 0) {
                int index = getVarIndex(indexIncremment);
                System.out.println("getvarindex = "+getVarIndex(indexIncremment));
                System.out.println("Index = "+index);
                varValues[index] |= getItems()[i] << (indexIncremment - (index * 28));
                System.out.println("Var Values = "+varValues[index]);
                System.out.println("getItems[i] = "+getItems()[i]);
                System.out.println("index inc - index * 28 = "+(indexIncremment - (index * 28)));

            }
            indexIncremment += getIncremment(i);
        }
        for (int i = 0; i < getVars().length; i++) {
            System.out.println("PLAYER WRITE:");
            System.out.println("Get vars = " + getVars()[i]);
            System.out.println(("Var Values = "+varValues[i]));
            //player.getVarsManager().sendVar(getVars()[i], varValues[i]);
        }
    }

    public static int[] getVars() {
        return dungeonnering ? DUNG_VAR_IDS : VAR_IDS;
    }
    private static int getVarIndex(int i) {
        return i / 28;
    }
    public static int getIncremment(int slot) {
        if (!dungeonnering)
            return slot == 20 || slot == 21 ? 4 : 1; //axes or pickaaxes
        return slot == 0 ? 5 : slot == 1 ? 4 : 1;
    }

    }




