package me.oferg.skills.Listeners;

import me.oferg.skills.Helper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BrewListener implements Listener {

    private final JavaPlugin plugin;
    private final Map<Location, UUID> brewingPlayers = new HashMap<>();

    public BrewListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBrewingStandUse(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;
        if (event.getClickedBlock().getType() != Material.BREWING_STAND) return;

        brewingPlayers.put(event.getClickedBlock().getLocation(), event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onBrew(BrewEvent event) {
        Location loc = event.getBlock().getLocation();
        UUID uuid = brewingPlayers.get(loc);
        if (uuid == null) return;

        Player p = Bukkit.getPlayer(uuid);
        if (p == null) return;

        String base = "players." + p.getUniqueId() + ".skills.alchemy";
        int level = plugin.getConfig().getInt(base + ".level");
        Helper.gainXp(plugin, base, p, "alchemy", "+3% potion duration");

        new BukkitRunnable() {
            @Override
            public void run() {
                BrewerInventory inv = (BrewerInventory) event.getContents();

                for (int i = 0; i < 3; i++) {
                    ItemStack item = inv.getItem(i);
                    if (item != null && item.getType() == Material.POTION) {
                        extendPotionDuration(item, level);
                    }
                }
            }
        }.runTaskLater(plugin, 1L);
    }

    private void extendPotionDuration(ItemStack potion, int level) {
        if (!(potion.getItemMeta() instanceof PotionMeta)) return;

        PotionMeta meta = (PotionMeta) potion.getItemMeta();
        PotionType potionType = meta.getBasePotionType();

        if (potionType != null && potionType != PotionType.WATER && potionType != PotionType.MUNDANE
                && potionType != PotionType.THICK && potionType != PotionType.AWKWARD) {

            String effectName = potionType.toString();
            System.out.println(effectName);
            meta.clearCustomEffects();

            for (PotionEffect effect : potionType.getPotionEffects()) {
                PotionEffect newEffect = new PotionEffect(
                        effect.getType(),
                        effect.getDuration() + (effect.getDuration() * 3 * level / 100),
                        effect.getAmplifier(),
                        effect.isAmbient(),
                        effect.hasParticles(),
                        effect.hasIcon()
                );
                meta.addCustomEffect(newEffect, true);
            }


            meta.setBasePotionType(PotionType.WATER);

            meta.setDisplayName(ChatColor.LIGHT_PURPLE + Helper.capitalizeWords(effectName.replace("_", " ").toLowerCase()));
        }

        if (meta.hasCustomEffects()) {
            for (PotionEffect effect : meta.getCustomEffects()) {
                meta.removeCustomEffect(effect.getType());
                PotionEffect newEffect = new PotionEffect(
                        effect.getType(),
                        effect.getDuration() + (effect.getDuration() * 3 * level / 100),
                        effect.getAmplifier(),
                        effect.isAmbient(),
                        effect.hasParticles(),
                        effect.hasIcon()
                );
                meta.addCustomEffect(newEffect, true);
            }
        }

        potion.setItemMeta(meta);
    }
}