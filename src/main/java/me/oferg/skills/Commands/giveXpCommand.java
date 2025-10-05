package me.oferg.skills.Commands;

import me.oferg.skills.LevelCalculator;
import me.oferg.skills.LevelUpManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class giveXp implements CommandExecutor {
    private JavaPlugin plugin;
    public giveXp(JavaPlugin plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings)
    {
        if(commandSender instanceof Player p)
        {
            String base = "players." + p.getUniqueId() + ".skills." + strings[1];
            String xp = plugin.getConfig().get(base + ".xp").toString();
            String level = plugin.getConfig().get(base + ".level").toString();
            int currentLevel = Integer.parseInt(level);
            String[] split = xp.split("/");
            int currentXp = Integer.parseInt(split[0]) + Integer.parseInt(strings[2]);
            while(currentXp > LevelCalculator.getLevelThreshold(currentLevel))
            {
                currentXp -= LevelCalculator.getLevelThreshold(currentLevel);
                currentLevel++;
                LevelUpManager.levelUpMessage(p, strings[1], currentLevel);
            }
            xp = currentXp + "/" + LevelCalculator.getLevelThreshold(currentLevel);
            plugin.getConfig().set(base + ".xp", xp);
            plugin.getConfig().set(base + ".level", currentLevel);
            plugin.saveConfig();

        }
        return true;
    }
}
