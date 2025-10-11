package me.oferg.skills.Listeners;

import me.oferg.skills.Helper;
import me.oferg.skills.LevelCalculator;
import me.oferg.skills.Skills;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;

public class JoinListener implements Listener {
    private final JavaPlugin plugin;

    public JoinListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        p.sendMessage(ChatColor.GREEN + "You are online!");
        System.out.println(LevelCalculator.getLevelThreshold(1));
        Helper.checkExtraBonuses(plugin, p);
        String base = "players." + p.getUniqueId() + ".skills";

        List<String> skills = Skills.skills;
        boolean changed = false;

        for (String skill : skills) {
            String levelPath = base + "." + skill + ".level";
            String xpPath = base + "." + skill + ".xp";

            if (!plugin.getConfig().contains(levelPath)) {
                plugin.getConfig().set(levelPath, 1);
                changed = true;
            }

            if (!plugin.getConfig().contains(xpPath)) {
                plugin.getConfig().set(xpPath, "0/" + LevelCalculator.getLevelThreshold(1));
                changed = true;
            }
        }

        if (changed) {
            plugin.saveConfig();

            TextComponent text = new TextComponent(ChatColor.YELLOW + "New Skills Added Check Them Out!");
            text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "skillprogress"));
            text.setHoverEvent(new net.md_5.bungee.api.chat.HoverEvent(
                    net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT,
                    new ComponentBuilder(ChatColor.BOLD + "" + ChatColor.GOLD + "Click To See The New Skills").create()
            ));

            p.spigot().sendMessage(text);
        }
    }
}
