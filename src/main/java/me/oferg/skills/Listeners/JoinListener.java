package me.oferg.skills.Listeners;

import me.oferg.skills.LevelCalculator;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class JoinListener implements Listener {
    private JavaPlugin plugin;
    public JoinListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e)
    {
        Player p = e.getPlayer();
        p.sendMessage(ChatColor.GREEN + "You are online!");
        System.out.println(LevelCalculator.getLevelThreshold(1));
        if (!p.hasPlayedBefore()) {
            String base = "players." + p.getUniqueId() + ".skills";

            plugin.getConfig().set(base + ".combat.level", 1);
            plugin.getConfig().set(base + ".combat.xp", "0/" + LevelCalculator.getLevelThreshold(1));

            plugin.getConfig().set(base + ".foraging.level", 1);
            plugin.getConfig().set(base + ".foraging.xp", "0/" + LevelCalculator.getLevelThreshold(1));

            plugin.getConfig().set(base + ".mining.level", 1);
            plugin.getConfig().set(base + ".mining.xp", "0/" + LevelCalculator.getLevelThreshold(1));

            plugin.getConfig().set(base + ".farming.level", 1);
            plugin.getConfig().set(base + ".farming.xp", "0/" + LevelCalculator.getLevelThreshold(1));

            plugin.saveConfig();

        }


    }
}
