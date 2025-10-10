package me.oferg.skills.Listeners;

import me.oferg.skills.Helper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class EnchantingListener implements Listener
{
    private JavaPlugin plugin;
    public EnchantingListener(JavaPlugin plugin)
    {
        this.plugin = plugin;
    }
    @EventHandler
    public void onEnchant(EnchantItemEvent event) {
        Player player = event.getEnchanter();

        String base = "players." + player.getUniqueId() + ".skills.enchanting";
        Helper.gainXp(plugin, base, player, "enchanting", "+10% more experience orbs");
    }
}
