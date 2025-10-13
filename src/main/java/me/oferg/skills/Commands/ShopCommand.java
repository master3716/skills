package me.oferg.skills.Commands;

import me.oferg.skills.Helper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;

import static org.bukkit.Bukkit.getLogger;

public class ShopCommand implements CommandExecutor {
    private JavaPlugin plugin;

    public ShopCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player p) {
            Inventory inventory = Bukkit.createInventory(p, 27, ChatColor.BLACK + "Shop");
            for (int i = 0; i < 27; i++) {
                inventory.setItem(i, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
            }

            File file = new File(plugin.getDataFolder(), "shop.yml");
            if (!file.exists()) {
                plugin.saveResource("shop.yml", false);
            }
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
            ConfigurationSection section = cfg.getConfigurationSection("items");
            int currentAddition = 1;
            if (section != null) {
                for (String key : section.getKeys(false)) {
                    ConfigurationSection itemSection = section.getConfigurationSection(key);

                    if (itemSection == null) continue;

                    String displayName = itemSection.getString("display_name");
                    String material = itemSection.getString("material");
                    String description = itemSection.getString("description");
                    int price = itemSection.getInt("price");
                    int amount = itemSection.getInt("amount");
                    String skill = itemSection.getString("skill");
                    List<String> commands = itemSection.getStringList("commands");
                    try {
                        ItemStack item = new ItemStack(Material.getMaterial(material));
                        ItemMeta meta = item.getItemMeta();
                        meta.setDisplayName(ChatColor.GOLD + displayName);
                        meta.setLore(List.of(ChatColor.BLUE  + description, price + " " + ChatColor.AQUA + skill + " xp"));
                        item.setItemMeta(meta);
                        inventory.setItem(currentAddition, item);
                        currentAddition += 2;
                    }
                    catch (Exception e) {
                        p.sendMessage(ChatColor.RED + "something went wrong contact admin");
                    }



                    getLogger().info("Loaded item: " + key);
                    getLogger().info("  Name: " + displayName);
                    getLogger().info("  Material: " + material);
                    getLogger().info("  Price: " + price);
                    getLogger().info("  Commands: " + commands);
                }
            }
            p.openInventory(inventory);
            plugin.getConfig().set("players." + p.getUniqueId() +".inventory", "shop");
            plugin.saveConfig();
            Helper.loadCommands(plugin);

        }
        return true;
    }
}
