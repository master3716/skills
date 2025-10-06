package me.oferg.skills.Commands;

import me.oferg.skills.Helper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class SkillProgressCommand implements CommandExecutor {

    private JavaPlugin plugin;
    public SkillProgressCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender instanceof Player p)
        {
            String base = "players." + p.getUniqueId() + ".skills.";
            Inventory inv = Bukkit.createInventory(p, 54, ChatColor.BLACK + "" + ChatColor.BOLD + "Skills");
            ItemStack nothing =  new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
            ItemMeta nothingMeta = nothing.getItemMeta();
            nothingMeta.setDisplayName(" ");
            nothing.setItemMeta(nothingMeta);
            for(int i = 0; i < inv.getSize(); i++)
            {
                inv.setItem(i, nothing);
            }


            ItemStack combat = Helper.CraftSkillItem(plugin, base + "combat", "combat", "level up by killing mobs", "each level grants 2% damage increase", Material.DIAMOND_SWORD);
            inv.setItem(10, combat);

            ItemStack mining = Helper.CraftSkillItem(plugin, base + "mining", "mining", "level up by mining blocks", "each level grants 5% chance for double drops", Material.IRON_PICKAXE);
            inv.setItem(13, mining);

            ItemStack foraging = Helper.CraftSkillItem(plugin, base + "foraging", "foraging", "level up by chopping trees", "each level grants 5% chance for double drops", Material.OAK_SAPLING);
            inv.setItem(16, foraging);

            ItemStack farming = Helper.CraftSkillItem(plugin, base + "farming", "farming", "level up by harvesting crops", "each level grants 5% chance for double drops", Material.GOLDEN_HOE);
            inv.setItem(31, farming);




            p.openInventory(inv);
            plugin.getConfig().set("players." + p.getUniqueId() +".inventory", "skills");
            plugin.saveConfig();
        }


        return true;
    }
}
