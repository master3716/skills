package me.oferg.skills.Listeners;

import me.oferg.skills.Helper;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantInventory;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class VillagerTradeListener implements Listener {
    private JavaPlugin plugin;
    private File recipesFile;
    private FileConfiguration recipesConfig;
    private Set<UUID> savedVillagers = new HashSet<>();

    public VillagerTradeListener(JavaPlugin plugin) {
        this.plugin = plugin;
        initRecipesFile();
        loadSavedVillagers();
    }

    private void initRecipesFile() {
        recipesFile = new File(plugin.getDataFolder(), "villager-recipes.yml");
        if (!recipesFile.exists()) {
            try {
                plugin.getDataFolder().mkdirs();
                recipesFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        recipesConfig = YamlConfiguration.loadConfiguration(recipesFile);
    }

    private void loadSavedVillagers() {
        if (recipesConfig.contains("saved-villagers")) {
            savedVillagers.addAll(recipesConfig.getStringList("saved-villagers").stream()
                    .map(UUID::fromString)
                    .toList());
        }
    }

    @EventHandler
    public void onVillagerInteract(PlayerInteractAtEntityEvent event) {
        if (!(event.getRightClicked() instanceof Villager villager)) return;

        UUID villagerId = villager.getUniqueId();

        if (!savedVillagers.contains(villagerId)) {
            String villagerKey = "villagers." + villagerId;
            List<String> serialized = new ArrayList<>();

            for (MerchantRecipe recipe : villager.getRecipes()) {
                serialized.add(serializeRecipe(recipe));
            }

            recipesConfig.set(villagerKey, serialized);
            savedVillagers.add(villagerId);

            List<String> allSaved = new ArrayList<>(savedVillagers.stream().map(UUID::toString).toList());
            recipesConfig.set("saved-villagers", allSaved);

            try {
                recipesConfig.save(recipesFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @EventHandler
    public void onMerchantOpen(InventoryOpenEvent event) {
        if (!(event.getInventory() instanceof MerchantInventory inv)) return;
        if (!(event.getPlayer() instanceof Player p)) return;
        if (!(inv.getHolder() instanceof Villager villager)) return;

        UUID villagerId = villager.getUniqueId();
        UUID playerId = p.getUniqueId();

        String base = "players." + playerId + ".skills.";
        int level = plugin.getConfig().getInt(base + "trading.level");

        if (level > 0) {
            List<MerchantRecipe> originals = getOriginalRecipes(villagerId);
            if (originals.isEmpty()) return;

            float priceMultiplier = (float) (1 - Math.min(level * 0.01, 0.99));
            List<MerchantRecipe> discountedRecipes = new ArrayList<>();

            for (MerchantRecipe original : originals) {
                MerchantRecipe discounted = cloneRecipe(original);
                discounted.setIngredients(lowerIngredients(discounted.getIngredients(), priceMultiplier));
                discountedRecipes.add(discounted);
            }

            villager.setRecipes(discountedRecipes);
        }
    }

    private List<MerchantRecipe> getOriginalRecipes(UUID villagerId) {
        String villagerKey = "villagers." + villagerId;
        List<MerchantRecipe> recipes = new ArrayList<>();

        if (recipesConfig.contains(villagerKey)) {
            List<?> serialized = recipesConfig.getList(villagerKey);
            if (serialized != null) {
                for (Object obj : serialized) {
                    if (obj instanceof String str) {
                        MerchantRecipe recipe = deserializeRecipe(str);
                        if (recipe != null) {
                            recipes.add(recipe);
                        }
                    }
                }
            }
        }

        return recipes;
    }

    private String serializeRecipe(MerchantRecipe recipe) {
        ItemStack result = recipe.getResult();
        StringBuilder sb = new StringBuilder();

        FileConfiguration temp = new YamlConfiguration();
        temp.set("result", result);
        sb.append(temp.saveToString()).append("|");

        for (int i = 0; i < recipe.getIngredients().size(); i++) {
            ItemStack ing = recipe.getIngredients().get(i);
            if (i > 0) sb.append(";;;");

            FileConfiguration tempIng = new YamlConfiguration();
            tempIng.set("ingredient", ing);
            sb.append(tempIng.saveToString());
        }

        return sb.toString();
    }

    private MerchantRecipe deserializeRecipe(String str) {
        try {
            String[] parts = str.split("\\|", 2);
            if (parts.length < 2) return null;

            YamlConfiguration resultConfig = new YamlConfiguration();
            resultConfig.loadFromString(parts[0]);
            ItemStack result = resultConfig.getItemStack("result");

            if (result == null) return null;

            MerchantRecipe recipe = new MerchantRecipe(result, 0, 999999, false);

            String[] ingredients = parts[1].split(";;;");
            List<ItemStack> ingList = new ArrayList<>();
            for (String ing : ingredients) {
                if (ing.trim().isEmpty()) continue;
                YamlConfiguration ingConfig = new YamlConfiguration();
                ingConfig.loadFromString(ing);
                ItemStack item = ingConfig.getItemStack("ingredient");
                if (item != null) {
                    ingList.add(item);
                }
            }
            recipe.setIngredients(ingList);

            return recipe;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<ItemStack> lowerIngredients(List<ItemStack> ingredients, float priceMultiplier) {
        List<ItemStack> newPrices = new ArrayList<>();
        for (ItemStack item : ingredients) {
            if (item == null) continue;

            ItemStack discountedItem = item.clone();
            int newAmount = Math.max(1, (int) (discountedItem.getAmount() * priceMultiplier));
            discountedItem.setAmount(newAmount);
            newPrices.add(discountedItem);
        }
        return newPrices;
    }

    private MerchantRecipe cloneRecipe(MerchantRecipe recipe) {
        MerchantRecipe cloned = new MerchantRecipe(
                recipe.getResult().clone(),
                recipe.getUses(),
                recipe.getMaxUses(),
                recipe.hasExperienceReward()
        );

        List<ItemStack> ingredients = new ArrayList<>();
        for (ItemStack item : recipe.getIngredients()) {
            ingredients.add(item.clone());
        }
        cloned.setIngredients(ingredients);
        return cloned;
    }

    @EventHandler
    public void onTrade(InventoryClickEvent event) {
        if (!(event.getInventory() instanceof MerchantInventory inv)) return;
        if (event.getSlotType() != SlotType.RESULT) return;
        if (!(event.getWhoClicked() instanceof Player p)) return;

        MerchantRecipe recipe = inv.getSelectedRecipe();
        if (recipe == null) return;

        handleTrade(p, recipe);
    }

    private void handleTrade(Player p, MerchantRecipe recipe) {
        String base = "players." + p.getUniqueId() + ".skills.";
        Helper.gainXp(plugin, base + "trading", p, "trading", "+5% discount for trading", null);
    }
}