package me.oferg.skills.Listeners;

import me.oferg.skills.Helper;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.view.AnvilView;
import org.bukkit.plugin.java.JavaPlugin;

public class RenameListener implements Listener
{
    private JavaPlugin plugin;
    public RenameListener(JavaPlugin plugin)
    {
        this.plugin = plugin;
    }
    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        AnvilView view = event.getView();
        AnvilInventory inv = event.getInventory();
        ItemStack first = inv.getItem(0);
        ItemStack second = inv.getItem(1);
        ItemStack result = event.getResult();

        Player p = (Player) view.getPlayer();
        String base = "players." + p.getUniqueId() + ".skills.enchanting";
        if (first == null || result == null) return;

        String renameText = view.getRenameText(); // text typed in the anvil
        String originalName = (first.hasItemMeta() && first.getItemMeta().hasDisplayName())
                ? first.getItemMeta().getDisplayName()
                : null;

        boolean isRenaming;
        if (renameText == null || renameText.isBlank()) {
            isRenaming = false;
        } else {
            isRenaming = !renameText.equals(originalName);
        }
        if (!isRenaming) return;

        int totalCost = view.getRepairCost();
        if(plugin.getConfig().getBoolean(base + ".benefits.renaming")) {
            if (second == null || second.getType() == Material.AIR) {
                view.setRepairCost(0);
            } else {
                int adjusted = Math.max(0, totalCost - 1);
                view.setRepairCost(adjusted);
            }
        }
    }

}
