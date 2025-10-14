package me.oferg.skills.Listeners;

import me.oferg.skills.Helper;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class BlockBreakListener implements Listener {

    private final JavaPlugin plugin;

    public BlockBreakListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        Block block = e.getBlock();

        if (p.getGameMode() != GameMode.SURVIVAL)
            return;

        if (!Helper.shouldRewardXp(block)) {
            Helper.removePlayerPlaced(block);
            return;
        }

        Material brokenType = block.getType();
        var blockLoc = block.getLocation();
        List<ItemStack> drops = new ArrayList<>(block.getDrops(p.getInventory().getItemInMainHand()));

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Material currentType = blockLoc.getBlock().getType();

            if (currentType == brokenType)
                return;

            String skill;
            String base = "players." + p.getUniqueId() + ".skills.";

            if (Helper.isWood(brokenType)) {
                skill = "foraging";
            } else if (Helper.isFarmingBlock(brokenType)) {
                skill = "farming";
            } else {
                skill = "mining";
            }

            Helper.gainXp(plugin, base + skill, p, skill, "chance for double drops + 5%", brokenType);
            int skillLevel = plugin.getConfig().getInt(base + skill + ".level");

            int dropMultiplier = skillLevel * 5 / 100;
            double extraChance = (skillLevel * 5 % 100) / 100.0;

            for (ItemStack drop : drops) {
                int totalMultiplier = dropMultiplier;
                if (Helper.roll(extraChance)) {
                    totalMultiplier += 1;
                }

                if (totalMultiplier > 0) {
                    p.getWorld().dropItemNaturally(
                            blockLoc,
                            new ItemStack(drop.getType(), Math.max(1, totalMultiplier * drop.getAmount()))
                    );
                }
            }

            Helper.removePlayerPlaced(blockLoc.getBlock());
        }, 3L);
    }
}
