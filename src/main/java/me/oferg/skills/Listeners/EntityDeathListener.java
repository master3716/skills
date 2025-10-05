package me.oferg.skills.Listeners;

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
            p.sendMessage(ChatColor.RED + "kill");
            String base = "players." + p.getUniqueId() + ".skills";
            String combatXp = plugin.getConfig().get(base + ".combat.xp").toString();
            int combatLevel = Integer.parseInt(plugin.getConfig().get(base + ".combat.level").toString());
            int currentXp = Integer.parseInt(combatXp.split("/")[0]);
            currentXp += (int) LevelCalculator.xpRewardForLevel(combatLevel);
            if(currentXp >= Integer.parseInt(combatXp.split("/")[1])) {
                currentXp -= LevelCalculator.getLevelThreshold(combatLevel);
                combatLevel += 1;
                LevelUpManager.levelUpMessage(p, "combat",  combatLevel);
            }

            combatXp = currentXp + "/" + LevelCalculator.getLevelThreshold(combatLevel);
            plugin.getConfig().set(base + ".combat.xp", combatXp);
            plugin.saveConfig();
        }
    }
}
