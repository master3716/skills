package me.oferg.skills;

import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Note;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import java.awt.*;


public class LevelUpManager
{
    public static void levelUpMessage(Player player, String skill, int newLevel, String reward)
    {
        String message = ChatColor.GOLD + "✨ Congratulations " + ChatColor.AQUA + player.getName() + ChatColor.GOLD +
                "! ✨\n" +
                ChatColor.GREEN + "Your " + ChatColor.YELLOW + skill + ChatColor.GREEN +
                " skill has leveled up to " + ChatColor.LIGHT_PURPLE + newLevel + ChatColor.GREEN + "!\n" +
                ChatColor.BLUE + reward + " 🎁";

        TextComponent text = new TextComponent(message);
        text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "skillprogress"));
        text.setHoverEvent(new net.md_5.bungee.api.chat.HoverEvent(
                net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder(ChatColor.BOLD + "" + ChatColor.GOLD + "Click To See Skill Progress").create()
        ));

        player.spigot().sendMessage(text);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
    }
}
