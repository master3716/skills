package me.oferg.skills.Listeners;

import me.oferg.skills.Helper;
import me.oferg.skills.LevelCalculator;
import me.oferg.skills.Skills;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
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
        boolean legacy = false;
        int level = 0;
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
            else
            {
                String xp = plugin.getConfig().get(xpPath).toString();
                level = plugin.getConfig().getInt(levelPath);
                if(Integer.parseInt(xp.split("/")[1]) == LevelCalculator.getLevelThresholdLegacy(level))
                    legacy = true;

            }

            if(legacy)
            {
                p.sendMessage(ChatColor.LIGHT_PURPLE + "xp system updated");
                int totalXp = LevelCalculator.getTotalXPLegacy(level);
                int newLevel = LevelCalculator.getLevelFromLegacy(totalXp);
                plugin.getConfig().set(xpPath, "0/" + LevelCalculator.getLevelThreshold(newLevel));
                plugin.getConfig().set(levelPath, newLevel);
                plugin.saveConfig();
            }
        }

        if (changed) {
            plugin.saveConfig();

            TextComponent text = new TextComponent(ChatColor.YELLOW + "New Skills Added Check Them Out!");
            text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "skillprogress"));
            text.setHoverEvent(new HoverEvent(
                    HoverEvent.Action.SHOW_TEXT,
                    new ComponentBuilder(ChatColor.BOLD + "" + ChatColor.GOLD + "Click To See The New Skills").create()
            ));

            p.spigot().sendMessage(text);
        }

    }
}
