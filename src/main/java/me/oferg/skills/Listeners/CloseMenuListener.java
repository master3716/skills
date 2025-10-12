package me.oferg.skills.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class CloseMenuListener implements Listener
{
    private JavaPlugin plugin;
    public CloseMenuListener(JavaPlugin plugin)
    {
        this.plugin = plugin;
    }
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e)
    {
        Player p = (Player)e.getPlayer();
        String base = "players." + p.getUniqueId();
        if(!plugin.getConfig().get(base + ".inventory").equals("anvil")) {
            plugin.getConfig().set(base + ".inventory", "");
            plugin.saveConfig();
        }
    }
}
