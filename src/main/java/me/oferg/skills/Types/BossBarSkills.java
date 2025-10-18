package me.oferg.skills.Types;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BossBarSkills {

    private final Map<UUID, BossBar> bars = new HashMap<>();

    // Create a boss bar for a player
    public void createBossBar(Player player, String title, BarColor color, BarStyle style, double progress) {
        removePlayer(player); // Remove old bar if exists

        BossBar bar = Bukkit.createBossBar(title, color, style);
        bar.addPlayer(player);
        bar.setProgress(clamp(progress));
        bar.setVisible(true);

        bars.put(player.getUniqueId(), bar);
    }

    // Update the boss bar for a player
    public void updateBossBar(Player player, String title, double progress) {
        BossBar bar = bars.get(player.getUniqueId());
        if (bar != null) {
            bar.setTitle(title);
            bar.setProgress(clamp(progress));
        } else {
            // If bar doesn't exist yet, create it
            createBossBar(player, title, BarColor.PURPLE, BarStyle.SEGMENTED_10, progress);
        }
    }

    // Remove the boss bar for a player
    public void removePlayer(Player player) {
        BossBar bar = bars.remove(player.getUniqueId());
        if (bar != null) {
            bar.removeAll();
        }
    }

    private double clamp(double progress) {
        return Math.max(0, Math.min(1, progress));
    }
    public void clearAllBars() {
        for (BossBar bar : bars.values()) {
            bar.removeAll();
        }
        bars.clear();
    }
}
