package me.oferg.skills.Listeners;

import me.oferg.skills.Helper;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

public class FishListener implements Listener
{
    private JavaPlugin plugin;
    public FishListener(JavaPlugin plugin)
    {
        this.plugin = plugin;
    }
    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        Player player = event.getPlayer();
        String base = "players." + player.getUniqueId() + ".skills.fishing";

        if(event.getState() == PlayerFishEvent.State.CAUGHT_FISH && event.getCaught() instanceof Item item)
        {
            Helper.gainXp(plugin, base, player, "fishing", "+5% rare loot chance");
            Random random = new Random();
            int level = plugin.getConfig().getInt(base + ".level");
            double treasureWeight = Math.min(100, 5 * level);
            double remaining = 100 - treasureWeight;
            double junkWeight = (1.0 / 9.5) * remaining;
            double fishWeight = (8.5 / 9.5) * remaining;
            double total = treasureWeight + junkWeight + fishWeight;
            double roll = random.nextDouble() * total;
            if (roll < treasureWeight) {
                item.setItemStack(Helper.getTreasureLoot());
                event.getPlayer().sendMessage("✨ You caught a rare treasure!");
            } else if (roll < treasureWeight + junkWeight) {
                item.setItemStack(Helper.getJunkLoot());
                event.getPlayer().sendMessage("🗑️ You fished up some junk...");
            } else {
                item.setItemStack(Helper.getFishLoot());
                event.getPlayer().sendMessage("🎣 You caught a fish!");
            }
        }

    }

}

