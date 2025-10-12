package me.oferg.skills;

import me.oferg.skills.Commands.AnvilCommand;
import me.oferg.skills.Commands.SkillProgressCommand;
import me.oferg.skills.Commands.SkillsCommand;
import me.oferg.skills.Commands.GiveXpCommand;
import me.oferg.skills.Listeners.*;
import me.oferg.skills.Menus.CustomAnvil;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;

public final class Skills extends JavaPlugin {
    public static List<String> skills;
    @Override
    public void onEnable() {
        // Plugin startup logic

        skills = Arrays.asList(
                "combat",
                "foraging",
                "mining",
                "farming",
                "fishing",
                "alchemy",
                "enchanting"
        );
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
        getServer().getPluginManager().registerEvents(new FishListener(this), this);
        getServer().getPluginManager().registerEvents(new BrewListener(this), this);
        getServer().getPluginManager().registerEvents(new EnchantingListener(this), this);
        getServer().getPluginManager().registerEvents(new RenameListener(this), this);
        getServer().getPluginManager().registerEvents(new CustomAnvil(this), this);
        getCommand("skills").setExecutor(new SkillsCommand(this));
        getCommand("giveXp").setExecutor(new GiveXpCommand(this));
        getCommand("skillProgress").setExecutor(new SkillProgressCommand(this));
        getCommand("anvil").setExecutor(new AnvilCommand(this));

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
