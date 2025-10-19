package me.oferg.skills.Listeners;

import me.oferg.skills.Helper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class TagsListener implements Listener
{
    private JavaPlugin plugin;

    public TagsListener(JavaPlugin plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e)
    {
        if (!(e.getWhoClicked() instanceof Player p)) return;

        String base = "players." + p.getUniqueId();
        String inv = plugin.getConfig().getString(base + ".inventory");

        if (inv != null && inv.equals("tags"))
        {
            e.setCancelled(true);
            ItemStack item = Helper.tags.get(p).get(e.getSlot());
            if (item == null || !item.hasItemMeta()) return;

            String tagName = item.getItemMeta().getDisplayName();

            String lastColors = ChatColor.getLastColors(tagName);
            ChatColor color = lastColors.isEmpty() ? ChatColor.WHITE : ChatColor.getByChar(lastColors.charAt(lastColors.length() - 1));

            p.setDisplayName(color + "[" + tagName + "]" + ChatColor.RESET + " " + p.getName());
        }
    }


}
