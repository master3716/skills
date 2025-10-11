package me.oferg.skills.Listeners;

import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class EntityDamageListener implements Listener
{
    private final JavaPlugin plugin;

    public EntityDamageListener(JavaPlugin plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e)
    {
        Player p = null;

        if (e.getDamager() instanceof Player player)
        {
            p = player;
        }
        else if (e.getDamager() instanceof Projectile proj)
        {
            if (proj.getShooter() instanceof Player player)
                p = player;
        }

        if (p == null) return;
        if (!(e.getEntity() instanceof LivingEntity target)) return;

        String base = "players." + p.getUniqueId() + ".skills.combat";
        int combatLevel = plugin.getConfig().getInt(base + ".level");
        boolean crit = plugin.getConfig().getBoolean(base + ".benefits.crit");

        double baseDamage = e.getDamage();

        if (crit) {
            double criticalMultiplier = 1.5;
            baseDamage *= criticalMultiplier;

            target.getWorld().spawnParticle(Particle.CRIT, target.getLocation().add(0, 1, 0), 10, 0.3, 0.3, 0.3, 0.1);
            target.getWorld().playSound(target.getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 1f, 1f);
        }

        double finalDamage = baseDamage + baseDamage * combatLevel * 0.02;

        e.setDamage(finalDamage);
    }
}
