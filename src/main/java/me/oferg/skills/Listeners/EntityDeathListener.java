package me.oferg.skills.Listeners;

import me.oferg.skills.Helper;
import me.oferg.skills.LevelCalculator;
import me.oferg.skills.LevelUpManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class EntityDeathListener implements Listener
{
    private JavaPlugin plugin;
    public EntityDeathListener(JavaPlugin plugin){
        this.plugin = plugin;
    }
    @EventHandler
    public void onEntityDeath(EntityDeathEvent e)
    {
        if(e.getEntity().getKiller() != null && e.getEntity().getKiller() instanceof Player p)
        {

            String base = "players." + p.getUniqueId() + ".skills";
            Helper.gainXp(plugin, base + ".combat", p, "combat","combat damage + 2%", null);

            int enchantingLevel = plugin.getConfig().getInt(base + ".enchanting.level");
            int baseExp = e.getDroppedExp();
            double multiplier = 1.0 + (enchantingLevel * 0.10);
            int newExp = (int) Math.round(baseExp * multiplier);
            e.setDroppedExp(newExp);
        }
    }
}
