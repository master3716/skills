package me.oferg.skills;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;


public class LevelUpManager
{
    public static void levelUpMessage(Player player, String skill, int newLevel)
    {
        String message = ChatColor.GOLD + "✨ Congratulations " + ChatColor.AQUA + player.getName() + ChatColor.GOLD +
                "! ✨\n" +
                ChatColor.GREEN + "Your " + ChatColor.YELLOW + skill + ChatColor.GREEN +
                " skill has leveled up to " + ChatColor.LIGHT_PURPLE + newLevel + ChatColor.GREEN + "!\n" +
                ChatColor.BLUE + "You have earned a reward! 🎁";

        player.sendMessage(message);
    }
}
