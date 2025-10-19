package me.oferg.skills.Types;

import me.oferg.skills.Helper;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class Mission
{
    private String description;
    private String skill;
    private int reward;
    private JavaPlugin plugin;
    private ItemStack target;
    private boolean isComplete;
    private BossBarSkills bossBarSkills;
    private Map<UUID, Integer> playerProgress = new HashMap<>();

    public Mission(String description, String skill, int reward, JavaPlugin plugin, ItemStack target, boolean isComplete) {
        this.description = description;
        this.skill = skill;
        this.reward = reward;
        this.plugin = plugin;
        this.isComplete = isComplete;
        if(target == null)
            this.target = null;
        else
            this.target = new ItemStack(target.getType(), target.getAmount());
        this.bossBarSkills = new BossBarSkills();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public int getReward() {
        return reward;
    }

    public void setReward(int reward) {
        this.reward = reward;
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    public void setPlugin(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Mission mission = (Mission) o;
        return reward == mission.reward && Objects.equals(description, mission.description) && Objects.equals(skill, mission.skill);
    }

    @Override
    public int hashCode() {
        return Objects.hash(description, skill, reward);
    }

    @Override
    public String toString() {
        return "Mission{" +
                "description='" + description + '\'' +
                ", skill='" + skill + '\'' +
                ", reward=" + reward +
                ", plugin=" + plugin +
                '}';
    }

    public void createMission()
    {
        String base = "Mission.";
        String targetPath = base + "target";
        String rewardPath = base + "reward";
        String skillPath = base + "skill";
        String descriptionPath = base + "description";

        plugin.getConfig().set(targetPath, target);
        plugin.getConfig().set(skillPath, skill);
        plugin.getConfig().set(rewardPath, reward);
        plugin.getConfig().set(descriptionPath, description);
        plugin.saveConfig();

        playerProgress.clear();
        isComplete = false;

        for (Player p : Bukkit.getOnlinePlayers()) {
            String playerProgressPath = base + p.getUniqueId() + ".progress";
            int savedProgress = plugin.getConfig().getInt(playerProgressPath, 0);
            playerProgress.put(p.getUniqueId(), savedProgress);

            double progressRatio = savedProgress > 0 ? (double) savedProgress / target.getAmount() : 0;
            bossBarSkills.createBossBar(p,
                    "§b" + description + ": " + savedProgress + "/" + target.getAmount(),
                    BarColor.PURPLE,
                    BarStyle.SEGMENTED_10,
                    progressRatio
            );
        }
    }

    public void progressPlayer(Player p, ItemStack item)
    {
        if(isComplete) return;
        if(target == null) return;
        UUID playerUUID = p.getUniqueId();
        int targetAmount = target.getAmount();
        int current = playerProgress.getOrDefault(playerUUID, 0);
        if (item == null || item.getType().isAir()) {
            bossBarSkills.updateBossBar(p, "§b" + description + ": " + current + "/" + targetAmount, (double) current / targetAmount);
            return;
        }

        if (item.getType() != target.getType()) {
            bossBarSkills.updateBossBar(p, "§b" + description + ": " + current + "/" + targetAmount, (double) current / targetAmount);
            return;
        }

        int collected = item.getAmount();
        int newProgress = Math.min(targetAmount, current + collected);

        playerProgress.put(playerUUID, newProgress);

        String base = "Mission.";
        String playerProgressPath = base + playerUUID + ".progress";
        plugin.getConfig().set(playerProgressPath, newProgress);
        plugin.saveConfig();

        double progressRatio = (double) newProgress / targetAmount;
        bossBarSkills.updateBossBar(p, "§b" + description + ": " + newProgress + "/" + targetAmount, progressRatio);

        if (newProgress >= targetAmount) {
            p.sendMessage("§a✓ Mission complete!");
            this.deleteMission(p);
        }
    }

    private void deleteMission(Player winner)
    {
        String winMessage = "§6§l[MISSION] §r§e" + winner.getName() + " §6has completed the mission!";
        Bukkit.broadcastMessage(winMessage);

        String path = "players." + winner.getUniqueId() + ".skills." + skill;
        String reward = "";

        switch (skill)
        {
            case "combat":
                reward = "combat damage + 2%";
                break;
            case "foraging":
            case "mining":
            case "farming":
                reward = "chance for double drops + 5%";
                break;
            case "enchanting":
                reward = "+10% more experience orbs";
                break;
            case "alchemy":
                reward = "+3% potion duration";
                break;
            case "fishing":
                reward = "+5% rare loot chance";
                break;
        }

        Helper.gainXp(plugin, path, winner, skill, reward, this.reward);

        String base = "Mission.";
        String targetPath = base + "target";
        String rewardPath = base + "reward";
        String skillPath = base + "skill";
        String descriptionPath = base + "description";

        plugin.getConfig().set(targetPath, null);
        plugin.getConfig().set(skillPath, null);
        plugin.getConfig().set(rewardPath, null);
        plugin.getConfig().set(descriptionPath, null);

        for (Player p : Bukkit.getOnlinePlayers()) {
            bossBarSkills.removePlayer(p);
            plugin.getConfig().set(base + p.getUniqueId() + ".progress", null);
        }

        bossBarSkills.clearAllBars();

        plugin.saveConfig();

        playerProgress.clear();
        isComplete = true;
    }

    public int getProgress(Player p)
    {
        UUID playerUUID = p.getUniqueId();
        int progress = playerProgress.getOrDefault(playerUUID, 0);
        int targetAmount = target.getAmount();

        if (targetAmount <= 0) {
            return 0;
        }

        return (int) ((double) progress / targetAmount * 100);
    }

}