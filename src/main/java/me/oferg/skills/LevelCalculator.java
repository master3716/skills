package me.oferg.skills;

public class LevelCalculator
{
    public static int getLevelThreshold(int level)
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


}
