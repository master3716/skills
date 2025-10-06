package me.oferg.skills;

import me.oferg.skills.Commands.SkillProgressCommand;
import me.oferg.skills.Commands.SkillsCommand;
import me.oferg.skills.Commands.giveXpCommand;
import me.oferg.skills.Listeners.*;
import org.bukkit.plugin.java.JavaPlugin;

public final class Skills extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic

        System.out.println("Plugin has been enabled! SKILLZ");
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        getServer().getPluginManager().registerEvents(new JoinListener(this), this);
        getServer().getPluginManager().registerEvents(new EntityDeathListener(this), this);
        getServer().getPluginManager().registerEvents(new EntityDamageListener(this), this);
        getServer().getPluginManager().registerEvents(new CloseMenuListener(this), this);
        getServer().getPluginManager().registerEvents(new SkillsMenuListener(this), this);
        getServer().getPluginManager().registerEvents(new BlockBreakListener(this), this);
        getServer().getPluginManager().registerEvents(new BlockPlaceListener(), this);
        getCommand("skills").setExecutor(new SkillsCommand(this));
        getCommand("giveXp").setExecutor(new giveXpCommand(this));
        getCommand("skillProgress").setExecutor(new SkillProgressCommand(this));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
