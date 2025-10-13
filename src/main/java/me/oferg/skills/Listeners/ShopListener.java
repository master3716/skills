package me.oferg.skills.Listeners;

import me.oferg.skills.Helper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class ShopListener implements Listener
{
    private final JavaPlugin plugin;
    public ShopListener(JavaPlugin plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler
    public  void onInventoryClick(InventoryClickEvent e)
    {
        if(!(e.getWhoClicked() instanceof Player p)) return;
        String base = "players." + p.getUniqueId() +".inventory";
        if(!plugin.getConfig().get(base).equals("shop")) return;
        Inventory inv = e.getClickedInventory();
        if(inv == null) return;
        for (int i = 0; i < inv.getSize(); i++)
        {
            if(inv.getItem(i).getType().equals(Material.BLACK_STAINED_GLASS_PANE) || i != e.getSlot()) continue;


            String itemName = inv.getItem(i).getItemMeta().getDisplayName();
            List<String> commands = Helper.getShopItemCmd(ChatColor.stripColor(itemName));
            System.out.println(itemName + " " + commands);
            if (commands == null) {
                p.sendMessage(ChatColor.RED + "Not able to buy. contact admin for support");
                return;
            }
            String baseTag = "players." + p.getUniqueId() + ".skills.";
            int price = Integer.parseInt(Helper.getShopItemPrice(ChatColor.stripColor(itemName)).get(0));
            String skill = Helper.getShopItemPrice(ChatColor.stripColor(itemName)).get(1);

            String skillXp = plugin.getConfig().get(baseTag + skill + ".xp").toString();
            int currentXp = Integer.parseInt(skillXp.split("/")[0]);
            if(currentXp < price){
                p.sendMessage(ChatColor.RED + "Not Enough XP");
                return;
            }
            for (String cmd : commands) {
                Bukkit.getLogger().info("Before: " + cmd);
                String parsed = cmd.replace("%player%", p.getName());
                Bukkit.getLogger().info("After: " + parsed);
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), parsed);
            }
            currentXp -= price;
            p.sendMessage(ChatColor.GREEN + "Purchased " + itemName + "!");

            plugin.getConfig().set(baseTag + skill + ".xp",  currentXp + "/" + skillXp.split("/")[1]);
            plugin.saveConfig();
        }
    }
}
