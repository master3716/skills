package me.oferg.skills.Listeners;

import me.oferg.skills.Helper;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class BlockBreakListener implements Listener
{

    private final JavaPlugin plugin;
    public BlockBreakListener(JavaPlugin plugin)
    {
        this.plugin = plugin;
    }
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e)
    {
        Player p = e.getPlayer();
        Block block = e.getBlock();
        if(p.getGameMode() != GameMode.SURVIVAL)
            return;
        if(!Helper.shouldRewardXp(block))
        {
            Helper.removePlayerPlaced(block);
            return;
        }
        String skill = "";
        String base = "players." + p.getUniqueId() + ".skills.";
        List<ItemStack> drops = new ArrayList<>(block.getDrops());

        if(Helper.isWood(block)) {
            skill = "foraging";
        } else if(Helper.isFarmingBlock(block)) {
            skill = "farming";
        } else {
            skill = "mining";
        }


        Helper.gainXp(plugin, base + skill, p, skill, "chance for double drops + 5%");
        int skillLevel = plugin.getConfig().getInt(base + skill + ".level");

        int dropMultiplier = skillLevel * 5 / 100;
        double extraChance = (skillLevel * 5 % 100) / 100.0;

        for (int i = 0; i < drops.size(); i++) {
            int totalMultiplier = dropMultiplier;
            if (Helper.roll(extraChance)) {
                totalMultiplier += 1;
            }
            p.getWorld().dropItemNaturally(
                    block.getLocation(),
                    new ItemStack(drops.get(i).getType(), totalMultiplier * drops.get(i).getAmount())
            );
        }
        Helper.removePlayerPlaced(block);

    }
}
