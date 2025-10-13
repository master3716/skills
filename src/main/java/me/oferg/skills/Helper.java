package me.oferg.skills;

import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.Bisected;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class Helper
{
    public static Map<Location, Integer> playerBlocksPlaced = new HashMap<>();
    public static Map<String, List<String>> itemCommands = new HashMap<>();
    public static Map<String, List<String>> itemPrices = new HashMap<>();
    public static String formatNumber(double num) {
        if (num >= 1_000_000_000) return String.format("%.1fB", num / 1_000_000_000.0);
        if (num >= 1_000_000)     return String.format("%.1fM", num / 1_000_000.0);
        if (num >= 1_000)         return String.format("%.1fK", num / 1_000.0);
        return String.valueOf((int) num);
    }

    public static ItemStack CraftSkillItem(JavaPlugin plugin, String path, String skillName, String description, String reward, Material item)
    {
        int skillLevel = plugin.getConfig().getInt(path + ".level");
        String skillXp = plugin.getConfig().get(path + ".xp").toString();
        String[] splitSkillXP = skillXp.split("/");
        int progressCombat = (int) ((Double.parseDouble(splitSkillXP[0]) / Double.parseDouble(splitSkillXP[1])) * 10);

        StringBuilder progressBar = new StringBuilder();
        for (int i = 0; i < progressCombat; i++) {
            progressBar.append(ChatColor.BLUE + "█");
        }
        for (int i = progressCombat; i < 10; i++) {
            progressBar.append(ChatColor.GRAY + "█");
        }
        progressBar.append(" " + ChatColor.DARK_PURPLE + Helper.formatNumber(Double.parseDouble(splitSkillXP[0])) + "/" +  Helper.formatNumber(Double.parseDouble(splitSkillXP[1])));

        ItemStack skill = new ItemStack(item);
        ItemMeta skillMeta = skill.getItemMeta();
        skillMeta.setDisplayName(ChatColor.GREEN + skillName);
        skillMeta.setLore(List.of(ChatColor.GOLD + description,
                ChatColor.AQUA + reward,
                ChatColor.LIGHT_PURPLE + "Level: " + skillLevel ,
                ChatColor.GRAY + "Progress to next level: ",
                progressBar.toString()));
        skill.setItemMeta(skillMeta);

        return skill;
    }
    public static boolean isWood(Block block) {
        if (block == null) return false;

        Material type = block.getType();
        if (Tag.LOGS.isTagged(type)) return true;
        if (Tag.PLANKS.isTagged(type)) return true;


        String name = type.name();
        return name.endsWith("_WOOD") || name.endsWith("_STEM") || name.endsWith("_HYPHAE") || name.endsWith("_LEAVES");
    }

    public static boolean isFarmingBlock(Block block) {
        if (block == null) return false;

        Material type = block.getType();

        switch (type) {
            // Farmland and soil
            case FARMLAND:
            case DIRT:
            case GRASS_BLOCK:
            case PODZOL:
            case MUDDY_MANGROVE_ROOTS:
            case ROOTED_DIRT:
            case MUD:
            case COARSE_DIRT:
            case MYCELIUM:

                // Crops
            case WHEAT:
            case CARROTS:
            case POTATOES:
            case BEETROOTS:
            case NETHER_WART:
            case COCOA:
            case SWEET_BERRY_BUSH:
            case CAVE_VINES:
            case CAVE_VINES_PLANT:
            case GLOW_BERRIES:

                // Stems
            case MELON_STEM:
            case ATTACHED_MELON_STEM:
            case PUMPKIN_STEM:
            case ATTACHED_PUMPKIN_STEM:
            case MELON:
            case PUMPKIN:
            case CARVED_PUMPKIN:

                // Plants
            case SUGAR_CANE:
            case BAMBOO:
            case BAMBOO_SAPLING:
            case CACTUS:
            case CHORUS_PLANT:
            case CHORUS_FLOWER:

                // Grass and vegetation
            case SHORT_GRASS:
            case TALL_GRASS:
            case FERN:
            case LARGE_FERN:
            case SEAGRASS:
            case TALL_SEAGRASS:
            case KELP:
            case KELP_PLANT:
            case SEA_PICKLE:

                // Mushrooms
            case BROWN_MUSHROOM:
            case RED_MUSHROOM:
            case BROWN_MUSHROOM_BLOCK:
            case RED_MUSHROOM_BLOCK:
            case MUSHROOM_STEM:

                // Flowers
            case DANDELION:
            case POPPY:
            case BLUE_ORCHID:
            case ALLIUM:
            case AZURE_BLUET:
            case RED_TULIP:
            case ORANGE_TULIP:
            case WHITE_TULIP:
            case PINK_TULIP:
            case OXEYE_DAISY:
            case CORNFLOWER:
            case LILY_OF_THE_VALLEY:
            case WITHER_ROSE:
            case SUNFLOWER:
            case LILAC:
            case ROSE_BUSH:
            case PEONY:
            case TORCHFLOWER:
            case PITCHER_PLANT:

                // Other farming-related
            case HAY_BLOCK:
            case DRIED_KELP_BLOCK:
            case MOSS_BLOCK:
            case MOSS_CARPET:
            case PITCHER_CROP:
            case TORCHFLOWER_CROP:
            case PINK_PETALS:
            case SPORE_BLOSSOM:

                // Vines
            case VINE:
            case WEEPING_VINES:
            case WEEPING_VINES_PLANT:
            case TWISTING_VINES:
            case TWISTING_VINES_PLANT:
            case GLOW_LICHEN:
            case SCULK_VEIN:
                return true;
        }

        String name = type.name();
        // Catch any crops or seeds that might have been missed
        if (name.endsWith("_CROP")) return true;
        if (name.endsWith("_SEEDS")) return true;

        return false;
    }

    public static void gainXp(JavaPlugin plugin, String path, Player p, String skillName, String reward)
    {

        String skillXp = plugin.getConfig().get(path + ".xp").toString();
        int skillLevel = Integer.parseInt(plugin.getConfig().get(path + ".level").toString());
        int currentXp = Integer.parseInt(skillXp.split("/")[0]);
        currentXp += (int) LevelCalculator.xpRewardForLevel(skillLevel);
        if(currentXp >= Integer.parseInt(skillXp.split("/")[1])) {
            currentXp -= LevelCalculator.getLevelThreshold(skillLevel);
            skillLevel += 1;
            checkExtraBonuses(plugin, p);
            LevelUpManager.levelUpMessage(p, skillName,  skillLevel, reward);
        }

        skillXp = currentXp + "/" + LevelCalculator.getLevelThreshold(skillLevel);
        plugin.getConfig().set(path + ".xp", skillXp);
        plugin.getConfig().set(path + ".level", skillLevel);
        plugin.saveConfig();
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new net.md_5.bungee.api.chat.TextComponent(ChatColor.AQUA + skillName + " +" + (int) LevelCalculator.xpRewardForLevel(skillLevel) + " " + skillXp));
        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.3f, 1.4f);

    }
    public static boolean roll(double chance) {
        if (chance <= 0) return false;
        if (chance >= 1) return true;
        return ThreadLocalRandom.current().nextDouble() < chance;
    }


    public static void markPlayerPlaced(Block block) {
        if (block == null) return;

        int age = -1;
        if (block.getBlockData() instanceof Ageable ageable) {
            age = ageable.getAge();
        }
        playerBlocksPlaced.put(block.getLocation(), age);
        if (block.getBlockData() instanceof Bisected bisected) {
            if (bisected.getHalf() == Bisected.Half.BOTTOM) {
                Block above = block.getRelative(BlockFace.UP);
                playerBlocksPlaced.put(above.getLocation(), age);
            } else {
                Block below = block.getRelative(BlockFace.DOWN);
                playerBlocksPlaced.put(below.getLocation(), age);
            }
        }
    }

    public static boolean shouldRewardXp(Block block) {
        if (block == null) return false;

        Location loc = block.getLocation();
        if (block.getBlockData() instanceof Ageable ageable) {
            int maxAge = switch (block.getType()) {
                case WHEAT, CARROTS, POTATOES, BEETROOTS -> 7;
                case NETHER_WART -> 3;
                case COCOA -> 2;
                default -> -1;
            };
            if (maxAge != -1) {
                if (ageable.getAge() != maxAge) {
                    return false;
                }
                if (playerBlocksPlaced.containsKey(loc)) {
                    int placedAge = playerBlocksPlaced.get(loc);
                    return ageable.getAge() > placedAge;
                }
                return true;
            }
        }
        return !playerBlocksPlaced.containsKey(loc);
    }



    public static void removePlayerPlaced(Block block) {
        if (block != null) playerBlocksPlaced.remove(block.getLocation());
    }


    public static ItemStack getTreasureLoot() {
        Random random = new Random();
        List<ItemStack> treasures = new ArrayList<>();
        treasures.add(new ItemStack(Material.NAME_TAG));
        treasures.add(new ItemStack(Material.SADDLE));
        treasures.add(new ItemStack(Material.NAUTILUS_SHELL));

        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
        Enchantment[] enchants = Enchantment.values();
        Enchantment randomEnchant = enchants[random.nextInt(enchants.length)];
        book.addUnsafeEnchantment(randomEnchant, 1);
        treasures.add(book);

        ItemStack bow = new ItemStack(Material.BOW);
        bow.addUnsafeEnchantment(Enchantment.UNBREAKING, random.nextInt(3) + 1);
        treasures.add(bow);

        ItemStack rod = new ItemStack(Material.FISHING_ROD);
        rod.addUnsafeEnchantment(Enchantment.LURE, random.nextInt(3) + 1);
        treasures.add(rod);

        return treasures.get(random.nextInt(treasures.size()));
    }

    public static ItemStack getJunkLoot() {
        Random random = new Random();
        List<Material> junk = Arrays.asList(
                Material.LEATHER_BOOTS,
                Material.LEATHER,
                Material.BOWL,
                Material.STRING,
                Material.BONE,
                Material.ROTTEN_FLESH
        );
        return new ItemStack(junk.get(random.nextInt(junk.size())));
    }

    public static ItemStack getFishLoot() {
        Random random = new Random();
        List<Material> fish = Arrays.asList(
                Material.COD,
                Material.SALMON,
                Material.PUFFERFISH,
                Material.TROPICAL_FISH
        );
        return new ItemStack(fish.get(random.nextInt(fish.size())));
    }
    public static String capitalizeWords(String str) {
        return Arrays.stream(str.split(" "))
                .map(word -> word.isEmpty() ? word : Character.toUpperCase(word.charAt(0)) + word.substring(1))
                .collect(Collectors.joining(" "));
    }
    public static void checkExtraBonuses(JavaPlugin plugin, Player p) {
        String base = "players." + p.getUniqueId() + ".skills.";
        for (String skill : Skills.skills) {
            int level = plugin.getConfig().getInt(base + skill + ".level");
            switch (skill) {
                case "combat" -> {
                    if(level >= 35)
                    {
                        boolean bonus = plugin.getConfig().getBoolean(base + skill + ".benefits.crit");
                        plugin.getConfig().set(base + skill + ".benefits.crit", true);
                        if(!bonus) p.sendMessage(ChatColor.GOLD + "You are now eligible for permanent crits");
                    }
                    else
                    {
                        plugin.getConfig().set(base + skill + ".benefits.crit", false);
                    }
                }
                case "foraging" -> {
                    // do something for foraging
                }
                case "mining" -> {
                    // do something for mining
                }
                case "farming" -> {
                    // do something for farming
                }
                case "fishing" -> {
                    // do something for fishing
                }
                case "alchemy" -> {
                    // do something for alchemy
                }
                case "enchanting" -> {
                    if(level >= 20)
                    {
                        boolean bonus = plugin.getConfig().getBoolean(base + skill + ".benefits.renaming");
                        plugin.getConfig().set(base + skill + ".benefits.renaming", true);
                        if(!bonus) p.sendMessage(ChatColor.GOLD + "You are now eligible for free renaming");
                    }
                    else
                        plugin.getConfig().set(base + skill + ".benefits.renaming", false);



                    if(level >= 30)
                    {
                        boolean bonus = plugin.getConfig().getBoolean(base + skill + ".benefits.customAnvil");
                        plugin.getConfig().set(base + skill + ".benefits.customAnvil", true);
                        if(!bonus) p.sendMessage(ChatColor.GOLD + "You are now eligible for no xp combination cap. Do /anvil or /av");
                    }
                    else
                        plugin.getConfig().set(base + skill + ".benefits.customAnvil", false);
                }
            }
            plugin.saveConfig();
        }
    }

    public static ItemStack mergeEnchantments(ItemStack first, ItemStack second, ItemStack result) {
        Map<Enchantment, Integer> firstEnchants = getEnchantments(first);
        Map<Enchantment, Integer> secondEnchants = getEnchantments(second);

        if (firstEnchants.isEmpty() && secondEnchants.isEmpty()) {
            return result;
        }

        firstEnchants.forEach((enchant, level) -> {
            addEnchantmentToResult(result, enchant, level);
        });

        secondEnchants.forEach((enchant, level) -> {
            Map<Enchantment, Integer> resultEnchants = getEnchantments(result);

            if (resultEnchants.containsKey(enchant)) {
                int existingLevel = resultEnchants.get(enchant);
                if (existingLevel == level) {
                    int newLevel = Math.min(level + 1, enchant.getMaxLevel());
                    addEnchantmentToResult(result, enchant, newLevel);
                } else {
                    int higherLevel = Math.max(existingLevel, level);
                    addEnchantmentToResult(result, enchant, higherLevel);
                }
            } else {
                boolean compatible = true;
                for (Enchantment existing : resultEnchants.keySet()) {
                    if (enchant.conflictsWith(existing) || existing.conflictsWith(enchant)) {
                        compatible = false;
                        break;
                    }
                }
                if (compatible) {
                    addEnchantmentToResult(result, enchant, level);
                }
            }
        });

        return result;
    }

    public static Map<Enchantment, Integer> getEnchantments(ItemStack item) {
        if (item.getType() == Material.ENCHANTED_BOOK) {
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
            return meta.getStoredEnchants();
        } else {
            return item.getEnchantments();
        }
    }

    public static void addEnchantmentToResult(ItemStack result, Enchantment enchant, int level) {
        if (result.getType() == Material.ENCHANTED_BOOK) {
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) result.getItemMeta();
            meta.addStoredEnchant(enchant, level, true);
            result.setItemMeta(meta);
        } else {
            result.addUnsafeEnchantment(enchant, level);
        }
    }

    public static ItemStack applyBookEnchantments(ItemStack item, ItemStack book, ItemStack result) {
        EnchantmentStorageMeta bookMeta = (EnchantmentStorageMeta) book.getItemMeta();
        Map<Enchantment, Integer> bookEnchants = bookMeta.getStoredEnchants();

        bookEnchants.forEach((enchant, level) -> {
            if (enchant.canEnchantItem(item) || item.getType() == Material.ENCHANTED_BOOK) {
                Map<Enchantment, Integer> resultEnchants = getEnchantments(result);

                if (resultEnchants.containsKey(enchant)) {
                    int existingLevel = resultEnchants.get(enchant);
                    if (existingLevel == level) {
                        int newLevel = Math.min(level + 1, enchant.getMaxLevel());
                        addEnchantmentToResult(result, enchant, newLevel);
                    } else {
                        int higherLevel = Math.max(existingLevel, level);
                        addEnchantmentToResult(result, enchant, higherLevel);
                    }
                } else {
                    boolean compatible = true;
                    for (Enchantment existing : resultEnchants.keySet()) {
                        if (enchant.conflictsWith(existing) || existing.conflictsWith(enchant)) {
                            compatible = false;
                            break;
                        }
                    }
                    if (compatible) {
                        addEnchantmentToResult(result, enchant, level);
                    }
                }
            }
        });

        return result;
    }

    public static void loadCommands(JavaPlugin plugin) {
        File file = new File(plugin.getDataFolder(), "shop.yml");
        System.out.println("Loading shop.yml...");
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection items = cfg.getConfigurationSection("items");
        if (items == null) return;
        System.out.println("iterating items...");
        for (String key : items.getKeys(false)) {
            var item = items.getConfigurationSection(key);
            if (item != null) {
                itemCommands.put(ChatColor.stripColor(item.getString("display_name")), item.getStringList("commands"));
                System.out.println("added item " + item.getStringList("commands"));
                List<String> lore = new ArrayList<>();
                lore.add(String.valueOf(item.get("price")));
                lore.add((item.get("skill")).toString());
                itemPrices.put(ChatColor.stripColor(item.getString("display_name")), lore);
                System.out.println("added item!!");
            }
        }
    }

    public static List<String> getShopItemCmd(String displayName)
    {
        return itemCommands.get(displayName);
    }
    public static List<String> getShopItemPrice(String displayName)
    {
        return itemPrices.get(displayName);
    }
}
