package me.oferg.skills;

import me.oferg.skills.Commands.SkillsCommand;
import me.oferg.skills.Commands.giveXpCommand;
import me.oferg.skills.Listeners.EntityDamageListener;
import me.oferg.skills.Listeners.EntityDeathListener;
import me.oferg.skills.Listeners.JoinListener;
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
        getCommand("skills").setExecutor(new SkillsCommand(this));
        getCommand("giveXp").setExecutor(new giveXpCommand(this));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
