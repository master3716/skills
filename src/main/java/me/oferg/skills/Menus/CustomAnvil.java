package me.oferg.skills.Menus;

import me.oferg.skills.Helper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class CustomAnvil implements Listener {

    private final JavaPlugin plugin;
    private final Set<UUID> processingPlayers = new HashSet<>();
    private final ItemStack available;
    private final ItemStack unavailable;

    public CustomAnvil(JavaPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);

        available = new ItemStack(Material.ANVIL);
        ItemMeta meta = available.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "Combine Items");
        available.setItemMeta(meta);

        unavailable = new ItemStack(Material.BARRIER);
        meta = unavailable.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "Combine Not Available");
        meta.setLore(Arrays.asList(
                ChatColor.GRAY + "Place two valid items",
                ChatColor.GRAY + "to enable combining"
        ));
        unavailable.setItemMeta(meta);
    }

    public void open(Player p) {
        String base = "players." + p.getUniqueId() + ".skills.enchanting";
        FileConfiguration cfg = plugin.getConfig();

        if (!cfg.getBoolean(base + ".benefits.customAnvil")) {
            p.sendMessage(ChatColor.RED + "You don't have access to the custom anvil");
            return;
        }

        Inventory anvil = Bukkit.createInventory(null, 45, ChatColor.BLACK + "Anvil");

        ItemStack pane = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta paneMeta = pane.getItemMeta();
        paneMeta.setDisplayName(" ");
        pane.setItemMeta(paneMeta);

        for (int i = 0; i < 45; i++) {
            anvil.setItem(i, pane);
        }

        anvil.setItem(20, null); // first input
        anvil.setItem(24, null); // second input
        anvil.setItem(13, null); // result preview

        updateCombineButton(anvil, p);

        p.openInventory(anvil);

        plugin.getConfig().set("players." + p.getUniqueId() + ".inventory", "anvil");
        plugin.saveConfig();
    }

    private void updateCombineButton(Inventory inv, Player p) {
        ItemStack first = inv.getItem(20);
        ItemStack second = inv.getItem(24);

        FileConfiguration cfg = plugin.getConfig();
        String base = "players." + p.getUniqueId() + ".skills.enchanting";
        int cost = calculateCost(first, second);

        ItemStack button;

        if (first != null && first.getType() != Material.AIR &&
                second != null && second.getType() != Material.AIR) {
            button = available.clone();
            ItemMeta meta = button.getItemMeta();
            meta.setLore(Arrays.asList(
                    ChatColor.GRAY + "Cost: " + ChatColor.GOLD + cost + " Levels",
                    "",
                    p.getLevel() >= cost
                            ? ChatColor.YELLOW + "Click to combine!"
                            : ChatColor.RED + "Not enough levels!"
            ));
            button.setItemMeta(meta);
        } else {
            button = unavailable.clone();
        }

        inv.setItem(40, button);
    }
    public static int calculateCost(ItemStack first, ItemStack second) {
        if (first == null || first.getType().isAir() || second == null || second.getType().isAir()) {
            return 0;
        }

        int totalCost = 0;


        int firstRepairCost = getRepairCost(first);
        int secondRepairCost = getRepairCost(second);

        totalCost += firstRepairCost + secondRepairCost;


        for (Map.Entry<Enchantment, Integer> sacrificeEnchant : second.getEnchantments().entrySet()) {
            Enchantment ench = sacrificeEnchant.getKey();
            int sacLevel = sacrificeEnchant.getValue();

            int firstLevel = first.getEnchantments().getOrDefault(ench, 0);

            int finalLevel;
            if (firstLevel == sacLevel) {
                finalLevel = firstLevel + 1;
            } else if (firstLevel > sacLevel) {
                finalLevel = firstLevel;
            } else {
                finalLevel = sacLevel;
            }

            int costForEnchant = getEnchantmentBaseCost(ench, sacLevel);

            if (firstLevel == 0) {
                totalCost += costForEnchant;
            } else if (finalLevel > firstLevel) {

                totalCost += costForEnchant;
            } else {
                totalCost += getEnchantmentBaseCost(ench, sacLevel) / 2;
            }
        }
        totalCost += 2;

        return Math.max(1,Math.min(totalCost, 50));
    }


    private static int getRepairCost(ItemStack item) {
        if (item.getItemMeta() instanceof Repairable) {
            return ((Repairable) item.getItemMeta()).getRepairCost();
        }
        return 0;
    }


    private static int getEnchantmentBaseCost(Enchantment ench, int level) {
        int weight;

        switch (ench.getName().toUpperCase()) {
            case "PROTECTION":
            case "FIRE_PROTECTION":
            case "BLAST_PROTECTION":
            case "PROJECTILE_PROTECTION":
            case "RESPIRATION":
            case "AQUA_AFFINITY":
            case "THORNS":
            case "DEPTH_STRIDER":
            case "FEATHER_FALLING":
            case "SHARPNESS":
            case "SMITE":
            case "BANE_OF_ARTHROPODS":
            case "KNOCKBACK":
            case "FIRE_ASPECT":
            case "EFFICIENCY":
            case "LURE":
            case "PIERCING":
            case "QUICK_CHARGE":
            case "RIPTIDE":
            case "MULTISHOT":
                weight = 1; // Common/Uncommon
                break;
            case "UNBREAKING":
            case "POWER":
            case "PUNCH":
            case "FLAME":
            case "INFINITY":
            case "MENDING":
            case "SWEEPING_EDGE":
            case "LOOTING":
            case "FORTUNE":
            case "LUCK_OF_THE_SEA":
                weight = 2; // Rare/High cost
                break;
            case "SILK_TOUCH":
            case "CURSE_OF_VANISHING":
            case "CURSE_OF_BINDING":
            case "FROST_WALKER":
            case "CHANNELING":
            case "IMPALING":
            case "LOYALTY":
                weight = 4; // Very Rare/Treasure
                break;
            default:
                weight = 1; // Default
        }

        // Base Cost Formula: (Level Cost Multiplier * Enchantment Level)
        return weight * level;
    }



    private ItemStack calculateResult(ItemStack first, ItemStack second) {
        if (first == null || second == null) return null;

        ItemStack result = first.clone();

        if (first.getType() == Material.ENCHANTED_BOOK && second.getType() == Material.ENCHANTED_BOOK) {
            result = Helper.mergeEnchantments(first, second, result);
        } else if (second.getType() == Material.ENCHANTED_BOOK) {
            result = Helper.applyBookEnchantments(first, second, result);
        } else if (first.getType() == second.getType() && first.getType().getMaxDurability() > 0) {
            short firstDurability = first.getDurability();
            short secondDurability = second.getDurability();
            short maxDurability = first.getType().getMaxDurability();

            int firstRemaining = maxDurability - firstDurability;
            int secondRemaining = maxDurability - secondDurability;
            int totalRemaining = firstRemaining + secondRemaining + (maxDurability * 12 / 100);

            short newDurability = (short) Math.max(0, maxDurability - Math.min(totalRemaining, maxDurability));
            result.setDurability(newDurability);

            result = Helper.mergeEnchantments(first, second, result);
        }

        // Increment the anvil use count
        if (result.hasItemMeta() && result.getItemMeta() instanceof Repairable repairable) {
            repairable.setRepairCost(repairable.getRepairCost() + 1);
            result.setItemMeta((ItemMeta) repairable);
        }

        return result;
    }


    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player p)) return;
        if (!plugin.getConfig().getString("players." + p.getUniqueId() + ".inventory", "").equals("anvil")) return;

        Inventory inv = e.getInventory();
        Inventory clickedInv = e.getClickedInventory();
        int slot = e.getRawSlot();

        if (slot >= 45 && e.isShiftClick()) {
            e.setCancelled(true);
            ItemStack clicked = e.getCurrentItem();
            if (clicked == null || clicked.getType() == Material.AIR) return;

            if (inv.getItem(20) == null || inv.getItem(20).getType() == Material.AIR) {
                inv.setItem(20, clicked.clone());
                clickedInv.setItem(e.getSlot(), null);
            } else if (inv.getItem(24) == null || inv.getItem(24).getType() == Material.AIR) {
                inv.setItem(24, clicked.clone());
                clickedInv.setItem(e.getSlot(), null);
            }
            Bukkit.getScheduler().runTask(plugin, () -> {
                ItemStack first = inv.getItem(20);
                ItemStack second = inv.getItem(24);
                ItemStack result = calculateResult(first, second);
                inv.setItem(13, result);
                updateCombineButton(inv, p);
            });
            return;
        }

        if (slot == 20 || slot == 24) {
            Bukkit.getScheduler().runTask(plugin, () -> {
                ItemStack first = inv.getItem(20);
                ItemStack second = inv.getItem(24);
                ItemStack result = calculateResult(first, second);
                inv.setItem(13, result);
                updateCombineButton(inv, p);
            });
            e.setCancelled(false);
            return;
        }

        if (slot == 40) {
            e.setCancelled(true);
            if (e.getClick() != org.bukkit.event.inventory.ClickType.LEFT) return;
            if (processingPlayers.contains(p.getUniqueId())) return;

            ItemStack first = inv.getItem(20);
            ItemStack second = inv.getItem(24);
            ItemStack result = calculateResult(first, second);

            if (result == null) return;

            processingPlayers.add(p.getUniqueId());

            FileConfiguration cfg = plugin.getConfig();
            String base = "players." + p.getUniqueId() + ".skills.enchanting";
            int cost = calculateCost(first, second);

            if (p.getLevel() < cost) {
                p.sendMessage(ChatColor.RED + "You need " + cost + " levels!");
                processingPlayers.remove(p.getUniqueId());
                return;
            }

            p.setLevel(p.getLevel() - cost);
            for (ItemStack leftover : p.getInventory().addItem(result.clone()).values()) {
                p.getWorld().dropItemNaturally(p.getLocation(), leftover);
            }

            inv.setItem(20, null);
            inv.setItem(24, null);
            inv.setItem(13, null);
            updateCombineButton(inv, p);

            p.sendMessage(ChatColor.GREEN + "Item successfully combined!");
            p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_USE, 0.3f, 1.4f);
            Bukkit.getScheduler().runTaskLater(plugin, () -> processingPlayers.remove(p.getUniqueId()), 10L);
            return;
        }

        e.setCancelled(true);
    }


    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();

        if (!plugin.getConfig().getString("players." + p.getUniqueId() + ".inventory", "").equals("anvil"))
            return;

        Inventory inv = e.getInventory();
        for (int slot : new int[]{20, 24}) {
            ItemStack item = inv.getItem(slot);
            if (item != null && item.getType() != Material.AIR && item.getType() != Material.GRAY_STAINED_GLASS_PANE) {
                for (ItemStack leftover : p.getInventory().addItem(item).values()) {
                    p.getWorld().dropItemNaturally(p.getLocation(), leftover);
                }
            }
        }

        plugin.getConfig().set("players." + p.getUniqueId() + ".inventory", null);
        plugin.saveConfig();

        processingPlayers.remove(p.getUniqueId());
    }
}
