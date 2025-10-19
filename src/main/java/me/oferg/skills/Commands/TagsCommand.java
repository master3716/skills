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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.bukkit.Bukkit.getLogger;

public class TagsCommand implements CommandExecutor {
    private JavaPlugin plugin;

    public TagsCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player p) {
            Inventory inventory = Bukkit.createInventory(p, 54, ChatColor.BLACK + "Tags");
            for (int i = 0; i < inventory.getSize(); i++) {
                inventory.setItem(i, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
            }

            File file = new File(plugin.getDataFolder(), "tags.yml");
            if (!file.exists()) {
                plugin.saveResource("tags.yml", false);
            }
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
            ConfigurationSection tagsSection = cfg.getConfigurationSection("tags");

            if (tagsSection == null) {
                p.sendMessage(ChatColor.RED + "No tags found in tags.yml!");
                return true;
            }

            int slot = 0;

            for (String skill : tagsSection.getKeys(false)) {
                ConfigurationSection skillSection = tagsSection.getConfigurationSection(skill);
                if (skillSection == null) continue;

                for (String tagName : skillSection.getKeys(false)) {
                    ConfigurationSection tagSection = skillSection.getConfigurationSection(tagName);
                    if (tagSection == null) continue;

                    int requirement = tagSection.getInt("requirement", 0);
                    String materialName = tagSection.getString("material", "STONE");

                    Material material;
                    try {
                        material = Material.valueOf(materialName.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        material = Material.STONE;
                        getLogger().warning("Invalid material in tags.yml for tag " + tagName + ": " + materialName);
                    }

                    ItemStack item = new ItemStack(material);
                    ItemMeta meta = item.getItemMeta();
                    if (meta != null) {
                        meta.setDisplayName(Helper.getStyledTagName(tagName, skill));
                        meta.setLore(List.of(
                                ChatColor.GRAY + "Skill: " + ChatColor.YELLOW + skill,
                                ChatColor.GRAY + "Requirement: " + ChatColor.GREEN + requirement,
                                ChatColor.YELLOW + "Click To Apply"
                        ));
                        item.setItemMeta(meta);
                    }

                    if (slot < inventory.getSize()) {
                        int level = plugin.getConfig().getInt("players." + p.getUniqueId() + ".skills." +  skill + ".level");
                        if(level >= requirement)
                        {
                            inventory.setItem(slot, item);
                            Helper.tags.computeIfAbsent(p, k -> new HashMap<>());
                            Helper.tags.get(p).put(slot, item);
                            slot += 2;
                        }
                    }
                }
            }

            p.openInventory(inventory);
            plugin.getConfig().set("players." + p.getUniqueId() + ".inventory", "tags");
            plugin.saveConfig();
        }
        return true;
    }
}
