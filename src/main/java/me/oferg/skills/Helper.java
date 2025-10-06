package me.oferg.skills;

import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Helper
{
    public static Map<Location, Integer> playerBlocksPlaced = new HashMap<>();
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
}
