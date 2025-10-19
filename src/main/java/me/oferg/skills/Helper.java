package me.oferg.skills;

import me.oferg.skills.Types.Mission;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockType;
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
    public static Map<Player, Map<Integer, ItemStack>> tags = new HashMap<>();
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
    public static boolean isWood(Material type) {
        if (Tag.LOGS.isTagged(type)) return true;
        if (Tag.PLANKS.isTagged(type)) return true;


        String name = type.name();
        return name.endsWith("_WOOD") || name.endsWith("_STEM") || name.endsWith("_HYPHAE") || name.endsWith("_LEAVES");
    }

    public static boolean isFarmingBlock(Material type) {
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

    public static void gainXp(JavaPlugin plugin, String path, Player p, String skillName, String reward, Material b)
    {
        double mult;
        if(b == null)
            mult = 1;
        else
            mult = Helper.getBlockRarityMultiplier(b);

        String skillXp = plugin.getConfig().get(path + ".xp").toString();
        int skillLevel = Integer.parseInt(plugin.getConfig().get(path + ".level").toString());
        int currentXp = Integer.parseInt(skillXp.split("/")[0]);
        currentXp += (int) ((int) LevelCalculator.xpRewardForLevel(skillLevel) * mult);
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
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new net.md_5.bungee.api.chat.TextComponent(ChatColor.AQUA + skillName + " +" + (int) (LevelCalculator.xpRewardForLevel(skillLevel) * mult) + " " + skillXp));
        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.3f, 1.4f);

    }
    public static void gainXp(JavaPlugin plugin, String path, Player p, String skillName, String reward, int add)
    {


        String skillXp = plugin.getConfig().get(path + ".xp").toString();
        int skillLevel = Integer.parseInt(plugin.getConfig().get(path + ".level").toString());
        int currentXp = Integer.parseInt(skillXp.split("/")[0]);
        currentXp += add;
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
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new net.md_5.bungee.api.chat.TextComponent(ChatColor.AQUA + skillName + " +" + add + " " + skillXp));
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


    public static double getBlockRarityMultiplier(Material type) {
        switch (type) {
            // Extremely Common (0.34) - Basic building blocks and common resources
            case STONE:
            case DIRT:
            case GRASS_BLOCK:
            case COARSE_DIRT:
            case PODZOL:
            case MYCELIUM:
            case SAND:
            case RED_SAND:
            case GRAVEL:
            case COBBLESTONE:
            case MOSSY_COBBLESTONE:
            case DEEPSLATE:
            case COBBLED_DEEPSLATE:
            case SANDSTONE:
            case RED_SANDSTONE:
            case SMOOTH_SANDSTONE:
            case CUT_SANDSTONE:
            case CHISELED_SANDSTONE:
            case NETHERRACK:
            case OAK_LOG:
            case OAK_WOOD:
            case STRIPPED_OAK_LOG:
            case STRIPPED_OAK_WOOD:
            case OAK_PLANKS:
            case SPRUCE_LOG:
            case SPRUCE_WOOD:
            case STRIPPED_SPRUCE_LOG:
            case STRIPPED_SPRUCE_WOOD:
            case SPRUCE_PLANKS:
            case BIRCH_LOG:
            case BIRCH_WOOD:
            case STRIPPED_BIRCH_LOG:
            case STRIPPED_BIRCH_WOOD:
            case BIRCH_PLANKS:
            case JUNGLE_LOG:
            case JUNGLE_WOOD:
            case STRIPPED_JUNGLE_LOG:
            case STRIPPED_JUNGLE_WOOD:
            case JUNGLE_PLANKS:
            case ACACIA_LOG:
            case ACACIA_WOOD:
            case STRIPPED_ACACIA_LOG:
            case STRIPPED_ACACIA_WOOD:
            case ACACIA_PLANKS:
            case DARK_OAK_LOG:
            case DARK_OAK_WOOD:
            case STRIPPED_DARK_OAK_LOG:
            case STRIPPED_DARK_OAK_WOOD:
            case DARK_OAK_PLANKS:
            case MANGROVE_LOG:
            case MANGROVE_WOOD:
            case STRIPPED_MANGROVE_LOG:
            case STRIPPED_MANGROVE_WOOD:
            case MANGROVE_PLANKS:
            case MANGROVE_ROOTS:
            case MUDDY_MANGROVE_ROOTS:
            case CHERRY_LOG:
            case CHERRY_WOOD:
            case STRIPPED_CHERRY_LOG:
            case STRIPPED_CHERRY_WOOD:
            case CHERRY_PLANKS:
            case BAMBOO_BLOCK:
            case STRIPPED_BAMBOO_BLOCK:
            case BAMBOO_PLANKS:
            case BAMBOO_MOSAIC:
            case WHEAT:
            case CARROTS:
            case POTATOES:
            case BEETROOTS:
            case CLAY:
            case TERRACOTTA:
            case WHITE_TERRACOTTA:
            case ORANGE_TERRACOTTA:
            case LIGHT_GRAY_TERRACOTTA:
            case YELLOW_TERRACOTTA:
            case LIME_TERRACOTTA:
            case PINK_TERRACOTTA:
            case GRAY_TERRACOTTA:
            case LIGHT_BLUE_TERRACOTTA:
            case MAGENTA_TERRACOTTA:
            case CYAN_TERRACOTTA:
            case PURPLE_TERRACOTTA:
            case BLUE_TERRACOTTA:
            case BROWN_TERRACOTTA:
            case GREEN_TERRACOTTA:
            case RED_TERRACOTTA:
            case BLACK_TERRACOTTA:
            case MOSS_BLOCK:
            case MOSS_CARPET:
            case TALL_GRASS:
            case SHORT_GRASS:
            case FERN:
            case LARGE_FERN:
            case DEAD_BUSH:
            case SEAGRASS:
            case TALL_SEAGRASS:
            case VINE:
            case LILY_PAD:
            case WATER:
            case LAVA:
            case ANDESITE:
            case POLISHED_ANDESITE:
            case DIORITE:
            case POLISHED_DIORITE:
            case GRANITE:
            case POLISHED_GRANITE:
            case CALCITE:
            case TUFF:
            case DRIPSTONE_BLOCK:
            case POINTED_DRIPSTONE:
            case MUD:
            case PACKED_MUD:
            case MUD_BRICKS:
            case OAK_LEAVES:
            case SPRUCE_LEAVES:
            case BIRCH_LEAVES:
            case JUNGLE_LEAVES:
            case ACACIA_LEAVES:
            case DARK_OAK_LEAVES:
            case MANGROVE_LEAVES:
            case CHERRY_LEAVES:
            case AZALEA_LEAVES:
            case FLOWERING_AZALEA_LEAVES:
            case GLASS:
            case WHITE_STAINED_GLASS:
            case ORANGE_STAINED_GLASS:
            case MAGENTA_STAINED_GLASS:
            case LIGHT_BLUE_STAINED_GLASS:
            case YELLOW_STAINED_GLASS:
            case LIME_STAINED_GLASS:
            case PINK_STAINED_GLASS:
            case GRAY_STAINED_GLASS:
            case LIGHT_GRAY_STAINED_GLASS:
            case CYAN_STAINED_GLASS:
            case PURPLE_STAINED_GLASS:
            case BLUE_STAINED_GLASS:
            case BROWN_STAINED_GLASS:
            case GREEN_STAINED_GLASS:
            case RED_STAINED_GLASS:
            case BLACK_STAINED_GLASS:
            case WHITE_WOOL:
            case ORANGE_WOOL:
            case MAGENTA_WOOL:
            case LIGHT_BLUE_WOOL:
            case YELLOW_WOOL:
            case LIME_WOOL:
            case PINK_WOOL:
            case GRAY_WOOL:
            case LIGHT_GRAY_WOOL:
            case CYAN_WOOL:
            case PURPLE_WOOL:
            case BLUE_WOOL:
            case BROWN_WOOL:
            case GREEN_WOOL:
            case RED_WOOL:
            case BLACK_WOOL:
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
            case SUNFLOWER:
            case LILAC:
            case ROSE_BUSH:
            case PEONY:
            case PINK_PETALS:
            case TORCH:
            case REDSTONE_TORCH:
            case CRAFTING_TABLE:
            case FURNACE:
            case CHEST:
            case LADDER:
            case BOOKSHELF:
            case COBWEB:
                return 0.34;

            // Common (0.67) - Ores, ocean blocks, nether materials
            case COAL_ORE:
            case DEEPSLATE_COAL_ORE:
            case IRON_ORE:
            case DEEPSLATE_IRON_ORE:
            case COPPER_ORE:
            case DEEPSLATE_COPPER_ORE:
            case GOLD_ORE:
            case DEEPSLATE_GOLD_ORE:
            case NETHER_GOLD_ORE:
            case REDSTONE_ORE:
            case DEEPSLATE_REDSTONE_ORE:
            case LAPIS_ORE:
            case DEEPSLATE_LAPIS_ORE:
            case DIAMOND_ORE:
            case DEEPSLATE_DIAMOND_ORE:
            case EMERALD_ORE:
            case NETHER_QUARTZ_ORE:
            case OBSIDIAN:
            case CRYING_OBSIDIAN:
            case ANCIENT_DEBRIS:
            case ICE:
            case PACKED_ICE:
            case BLUE_ICE:
            case SNOW:
            case SNOW_BLOCK:
            case POWDER_SNOW:
            case CACTUS:
            case SUGAR_CANE:
            case BAMBOO:
            case KELP:
            case KELP_PLANT:
            case DRIED_KELP_BLOCK:
            case SEA_PICKLE:
            case SPONGE:
            case WET_SPONGE:
            case PRISMARINE:
            case PRISMARINE_BRICKS:
            case DARK_PRISMARINE:
            case SEA_LANTERN:
            case TUBE_CORAL_BLOCK:
            case BRAIN_CORAL_BLOCK:
            case BUBBLE_CORAL_BLOCK:
            case FIRE_CORAL_BLOCK:
            case HORN_CORAL_BLOCK:
            case DEAD_TUBE_CORAL_BLOCK:
            case DEAD_BRAIN_CORAL_BLOCK:
            case DEAD_BUBBLE_CORAL_BLOCK:
            case DEAD_FIRE_CORAL_BLOCK:
            case DEAD_HORN_CORAL_BLOCK:
            case TUBE_CORAL:
            case BRAIN_CORAL:
            case BUBBLE_CORAL:
            case FIRE_CORAL:
            case HORN_CORAL:
            case TUBE_CORAL_FAN:
            case BRAIN_CORAL_FAN:
            case BUBBLE_CORAL_FAN:
            case FIRE_CORAL_FAN:
            case HORN_CORAL_FAN:
            case MAGMA_BLOCK:
            case SOUL_SAND:
            case SOUL_SOIL:
            case BASALT:
            case SMOOTH_BASALT:
            case POLISHED_BASALT:
            case BLACKSTONE:
            case GILDED_BLACKSTONE:
            case POLISHED_BLACKSTONE:
            case POLISHED_BLACKSTONE_BRICKS:
            case CRACKED_POLISHED_BLACKSTONE_BRICKS:
            case CHISELED_POLISHED_BLACKSTONE:
            case NETHER_BRICKS:
            case RED_NETHER_BRICKS:
            case CRACKED_NETHER_BRICKS:
            case CHISELED_NETHER_BRICKS:
            case NETHER_WART_BLOCK:
            case WARPED_WART_BLOCK:
            case CRIMSON_STEM:
            case WARPED_STEM:
            case STRIPPED_CRIMSON_STEM:
            case STRIPPED_WARPED_STEM:
            case CRIMSON_HYPHAE:
            case WARPED_HYPHAE:
            case STRIPPED_CRIMSON_HYPHAE:
            case STRIPPED_WARPED_HYPHAE:
            case CRIMSON_PLANKS:
            case WARPED_PLANKS:
            case CRIMSON_NYLIUM:
            case WARPED_NYLIUM:
            case CRIMSON_FUNGUS:
            case WARPED_FUNGUS:
            case CRIMSON_ROOTS:
            case WARPED_ROOTS:
            case NETHER_SPROUTS:
            case WEEPING_VINES:
            case TWISTING_VINES:
            case SHROOMLIGHT:
            case GLOWSTONE:
            case END_STONE:
            case END_STONE_BRICKS:
            case PURPUR_BLOCK:
            case PURPUR_PILLAR:
            case PURPUR_STAIRS:
            case PURPUR_SLAB:
            case CHORUS_PLANT:
            case CHORUS_FLOWER:
            case SHULKER_BOX:
            case WHITE_SHULKER_BOX:
            case ORANGE_SHULKER_BOX:
            case MAGENTA_SHULKER_BOX:
            case LIGHT_BLUE_SHULKER_BOX:
            case YELLOW_SHULKER_BOX:
            case LIME_SHULKER_BOX:
            case PINK_SHULKER_BOX:
            case GRAY_SHULKER_BOX:
            case LIGHT_GRAY_SHULKER_BOX:
            case CYAN_SHULKER_BOX:
            case PURPLE_SHULKER_BOX:
            case BLUE_SHULKER_BOX:
            case BROWN_SHULKER_BOX:
            case GREEN_SHULKER_BOX:
            case RED_SHULKER_BOX:
            case BLACK_SHULKER_BOX:
            case COAL_BLOCK:
            case IRON_BLOCK:
            case GOLD_BLOCK:
            case COPPER_BLOCK:
            case EXPOSED_COPPER:
            case WEATHERED_COPPER:
            case OXIDIZED_COPPER:
            case CUT_COPPER:
            case EXPOSED_CUT_COPPER:
            case WEATHERED_CUT_COPPER:
            case OXIDIZED_CUT_COPPER:
            case WAXED_COPPER_BLOCK:
            case WAXED_EXPOSED_COPPER:
            case WAXED_WEATHERED_COPPER:
            case WAXED_OXIDIZED_COPPER:
            case REDSTONE_BLOCK:
            case LAPIS_BLOCK:
            case QUARTZ_BLOCK:
            case SMOOTH_QUARTZ:
            case QUARTZ_PILLAR:
            case QUARTZ_BRICKS:
            case CHISELED_QUARTZ_BLOCK:
            case AMETHYST_BLOCK:
            case BUDDING_AMETHYST:
            case SMALL_AMETHYST_BUD:
            case MEDIUM_AMETHYST_BUD:
            case LARGE_AMETHYST_BUD:
            case AMETHYST_CLUSTER:
            case BONE_BLOCK:
            case HAY_BLOCK:
            case HONEYCOMB_BLOCK:
            case HONEY_BLOCK:
            case SLIME_BLOCK:
            case MELON:
            case PUMPKIN:
            case JACK_O_LANTERN:
            case CARVED_PUMPKIN:
            case COMPOSTER:
            case BARREL:
            case SMOKER:
            case BLAST_FURNACE:
            case GRINDSTONE:
            case STONECUTTER:
            case CARTOGRAPHY_TABLE:
            case FLETCHING_TABLE:
            case SMITHING_TABLE:
            case LOOM:
            case CAULDRON:
            case BREWING_STAND:
            case ENDER_CHEST:
            case TRAPPED_CHEST:
            case HOPPER:
            case DROPPER:
            case DISPENSER:
            case OBSERVER:
            case PISTON:
            case STICKY_PISTON:
            case REDSTONE_LAMP:
            case NOTE_BLOCK:
            case JUKEBOX:
            case TNT:
            case LECTERN:
            case BELL:
            case LANTERN:
            case SOUL_LANTERN:
            case RESPAWN_ANCHOR:
            case LODESTONE:
            case TARGET:
            case LIGHTNING_ROD:
            case SCULK:
            case SCULK_VEIN:
            case SCULK_CATALYST:
            case REINFORCED_DEEPSLATE:
                return 0.67;

            // Rare (1.0) - Valuable blocks and special items
            case NETHERITE_BLOCK:
            case DIAMOND_BLOCK:
            case EMERALD_BLOCK:
            case BEACON:
            case ENCHANTING_TABLE:
            case ANVIL:
            case CHIPPED_ANVIL:
            case DAMAGED_ANVIL:
            case END_PORTAL_FRAME:
            case SPAWNER:
            case CONDUIT:
            case END_GATEWAY:
            case END_ROD:
            case DRAGON_HEAD:
            case DRAGON_WALL_HEAD:
            case WITHER_SKELETON_SKULL:
            case WITHER_SKELETON_WALL_SKULL:
            case PLAYER_HEAD:
            case PLAYER_WALL_HEAD:
            case CREEPER_HEAD:
            case CREEPER_WALL_HEAD:
            case ZOMBIE_HEAD:
            case ZOMBIE_WALL_HEAD:
            case SKELETON_SKULL:
            case SKELETON_WALL_SKULL:
            case PIGLIN_HEAD:
            case PIGLIN_WALL_HEAD:
            case SHULKER_SHELL:
            case ELYTRA:
            case TOTEM_OF_UNDYING:
            case NETHER_STAR:
            case HEART_OF_THE_SEA:
            case TRIDENT:
            case MUSIC_DISC_13:
            case MUSIC_DISC_CAT:
            case MUSIC_DISC_BLOCKS:
            case MUSIC_DISC_CHIRP:
            case MUSIC_DISC_FAR:
            case MUSIC_DISC_MALL:
            case MUSIC_DISC_MELLOHI:
            case MUSIC_DISC_STAL:
            case MUSIC_DISC_STRAD:
            case MUSIC_DISC_WARD:
            case MUSIC_DISC_11:
            case MUSIC_DISC_WAIT:
            case MUSIC_DISC_PIGSTEP:
            case MUSIC_DISC_OTHERSIDE:
            case MUSIC_DISC_5:
            case MUSIC_DISC_RELIC:
            case ENCHANTED_GOLDEN_APPLE:
            case SCULK_SENSOR:
            case CALIBRATED_SCULK_SENSOR:
            case SCULK_SHRIEKER:
                return 1.0;

            // Extremely Rare (1.34) - Rarest items in the game
            case DEEPSLATE_EMERALD_ORE:
            case DRAGON_EGG:
            case COMMAND_BLOCK:
            case CHAIN_COMMAND_BLOCK:
            case REPEATING_COMMAND_BLOCK:
            case STRUCTURE_BLOCK:
            case STRUCTURE_VOID:
            case JIGSAW:
            case BARRIER:
            case LIGHT:
            case DEBUG_STICK:
            case KNOWLEDGE_BOOK:
                return 1.34;

            default:
                return 0.67; // Default to common rarity
        }
    }

    public static void loadPlayerPlacedBlocks(JavaPlugin plugin) {
        String base = "PlayerPlacedBlocks";

        if (!plugin.getConfig().contains(base)) {
            return;
        }

        ConfigurationSection section = plugin.getConfig().getConfigurationSection(base);
        if (section == null) {
            return;
        }

        playerBlocksPlaced.clear();

        for (String worldName : section.getKeys(false)) {
            World world = plugin.getServer().getWorld(worldName);
            if (world == null) continue;

            ConfigurationSection worldSection = section.getConfigurationSection(worldName);
            if (worldSection == null) continue;

            for (String xStr : worldSection.getKeys(false)) {
                ConfigurationSection xSection = worldSection.getConfigurationSection(xStr);
                if (xSection == null) continue;

                for (String yStr : xSection.getKeys(false)) {
                    ConfigurationSection ySection = xSection.getConfigurationSection(yStr);
                    if (ySection == null) continue;

                    for (String zStr : ySection.getKeys(false)) {
                        int x = Integer.parseInt(xStr);
                        int y = Integer.parseInt(yStr);
                        int z = Integer.parseInt(zStr);
                        int count = ySection.getInt(zStr);

                        Location loc = new Location(world, x, y, z);
                        playerBlocksPlaced.put(loc, count);
                    }
                }
            }
        }
    }
    public static void savePlayerPlacedBlocks(JavaPlugin plugin) {
        String base = "PlayerPlacedBlocks";

        plugin.getConfig().set(base, null);

        for (Map.Entry<Location, Integer> entry : playerBlocksPlaced.entrySet()) {
            Location loc = entry.getKey();
            Integer count = entry.getValue();

            String locPath = base + "." +
                    loc.getWorld().getName() + "." +
                    loc.getBlockX() + "." +
                    loc.getBlockY() + "." +
                    loc.getBlockZ();

            plugin.getConfig().set(locPath, count);
        }

        plugin.saveConfig();
    }

    public static void loadMission(JavaPlugin plugin)
    {
        String base = "Mission.";
        String targetPath = base + "target";
        String rewardPath = base + "reward";
        String skillPath = base + "skill";
        String descriptionPath = base + "description";

        ItemStack target = plugin.getConfig().getItemStack(targetPath);
        String skill = plugin.getConfig().getString(skillPath);
        int reward = plugin.getConfig().getInt(rewardPath);
        String description = plugin.getConfig().getString(descriptionPath);

        Skills.mission = new Mission(description, skill, reward, plugin, target, false);
    }

    public static String getStyledTagName(String name, String skill) {
        Map<String, String> skillEmojis = Map.of(
                "mining", ChatColor.GRAY + "⛏",
                "farming", ChatColor.GREEN + "♣",
                "foraging", ChatColor.DARK_GREEN + "♠",
                "fishing", ChatColor.AQUA + "✿",
                "combat", ChatColor.RED + "✦",
                "alchemy", ChatColor.LIGHT_PURPLE + "✹",
                "enchanting", ChatColor.BLUE + "➤"
        );

        String emoji = skillEmojis.getOrDefault(skill.toLowerCase(), "⭐");

        List<ChatColor> colors = List.of(
                ChatColor.AQUA, ChatColor.BLUE, ChatColor.GREEN, ChatColor.GOLD,
                ChatColor.LIGHT_PURPLE, ChatColor.RED, ChatColor.YELLOW
        );
        ChatColor color = colors.get(new Random().nextInt(colors.size()));

        return emoji + " " + color + name;
    }
}
