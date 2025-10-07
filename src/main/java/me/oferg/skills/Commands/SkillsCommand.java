package me.oferg.skills.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class SkillsCommand implements CommandExecutor {
    private JavaPlugin plugin;
    public SkillsCommand(JavaPlugin plugin)
    {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if(commandSender instanceof Player p)
        {
            String base = "players." + p.getUniqueId() + ".skills";
            String skillxp = "combat: " + plugin.getConfig().get(base + ".combat.xp") + "\n"
                    + "foraging: " + plugin.getConfig().get(base + ".foraging.xp") + "\n"
                    + "mining: " + plugin.getConfig().get(base + ".mining.xp") + "\n"
                    + "farming: " + plugin.getConfig().get(base + ".farming.xp") + "\n"
                    + "fishing: " +  plugin.getConfig().get(base + ".fishing.xp") + "\n";

            p.sendMessage(ChatColor.GREEN + skillxp);
        }


        return true;
    }
}
