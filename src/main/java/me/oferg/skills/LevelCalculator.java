package me.oferg.skills;

public class LevelCalculator
{
    public static int getLevelThresholdLegacy(int level)
    {
        return (int)(1000 * Math.pow(1.2, level) * Math.pow(Math.log10(Math.pow(level, 1.2)), 2)) + 10;
    }
    public static double xpRewardForLevel(int level) {

        double a = 50;  // scales overall
        double b = 0.1; // controls curve steepness
        double c = 1;   // base XP at level 1

        double reward = a * Math.log(b * level + 1) + c;
        return Math.round(reward);
    }
    public static int getLevelThreshold(int level) {
        if (level <= 0) return 0;
        if (level <= 15) {
            // Early levels
            return level * 50 + level * level * 5;
        } else if (level <= 25) {
            // Mid levels
            return 12000 + (level - 15) * 2000 + (int)Math.pow(level - 15, 2.5) * 300;
        } else if (level <= 50) {
            // High levels
            return 80000 + (int)(Math.pow(level - 25, 3) * 500);
        } else {
            // Very high levels (50+)
            return (int)(Math.pow(level - 48, 3.5) * 5000 + 2000000);
        }
    }

    public static int getTotalXPLegacy(int currentLevel) {
        if (currentLevel <= 0) return 0;

        int totalXP = 0;

        for (int level = 1; level <= currentLevel; level++) {
            totalXP += getLevelThresholdLegacy(level);
        }

        return totalXP;
    }

    public static int getLevelFromLegacy(int totalXP) {
        if (totalXP <= 0) return 0;

        int level = 0;
        int xpUsed = 0;

        while (true) {
            int xpNeeded = getLevelThreshold(level + 1);

            if (xpUsed + xpNeeded > totalXP) {
                break;
            }

            xpUsed += xpNeeded;
            level++;

            if (level > 1000) break;
        }

        return level;
    }


}
