package me.oferg.skills.Commands;

import me.oferg.skills.Menus.CustomAnvil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class AnvilCommand implements CommandExecutor {
    private JavaPlugin plugin;
    public AnvilCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings)
    {
        if(commandSender instanceof Player p)
        {
            CustomAnvil customAnvil = new CustomAnvil(plugin);
            customAnvil.open(p);
        }
        return true;
    }
}
