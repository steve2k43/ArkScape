package net.nocturne.game.player;

import net.nocturne.utils.Utils;

public class ItemCooldowns {
    private static long[] canuseagain = new long[65535];
    public static boolean onCoolDown(int id) {

        if (Utils.currentTimeMillis() > canuseagain[id])
            return false;
        return true;
    }

    public static long getRemainingTime(int id) {
        return (canuseagain[id]- Utils.currentTimeMillis())/1000;
    }
    public static void setCoolDown(long coolDownTime, int id) {
        // convert to milliseconds
        coolDownTime *= 1000;
        canuseagain[id]=Utils.currentTimeMillis()+coolDownTime;
    }
}
