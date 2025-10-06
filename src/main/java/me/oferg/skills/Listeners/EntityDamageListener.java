package me.oferg.skills.Listeners;

import me.oferg.skills.Skills;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class EntityDamageListener implements Listener
{
    private JavaPlugin plugin;
    public EntityDamageListener(JavaPlugin plugin)
    {
        this.plugin = plugin;
    }
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e)
    {
        Player p = null;
        if(e.getDamager() instanceof Player)
        {
            p = (Player)e.getDamager();
        } else if (e.getDamager() instanceof Projectile proj)
        {
            if(proj.getShooter() instanceof Player)
                p = (Player) proj.getShooter();
        }

        if(p == null)
            return;


        String base = "players." + p.getUniqueId() + ".skills.combat.level";
        int combatLevel = plugin.getConfig().getInt(base);
        p.sendMessage(ChatColor.GOLD + "" + e.getDamage() + " -> " + (int)(e.getDamage() + e.getDamage() * combatLevel * 0.02));
        e.setDamage(e.getDamage() + e.getDamage() * combatLevel * 0.02);

    }
}
