package me.oferg.skills.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class SkillsMenuListener implements Listener
{
    private JavaPlugin plugin;

    public SkillsMenuListener(JavaPlugin plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e)
    {
        if(e.getWhoClicked() instanceof Player p)
        {
            String base = "players." + p.getUniqueId();
            String inv = plugin.getConfig().getString(base + ".inventory");
            if(!inv.equals(""))
            {
                e.setCancelled(true);
            }
        }
    }
}
