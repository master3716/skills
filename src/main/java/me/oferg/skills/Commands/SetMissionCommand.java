package me.oferg.skills.Commands;

import me.oferg.skills.Skills;
import me.oferg.skills.Types.Mission;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;

import static me.oferg.skills.Skills.mission;

public class SetMissionCommand implements CommandExecutor
{
    private JavaPlugin plugin;
    private static final List<String> VALID_SKILLS = Arrays.asList(
            "combat", "foraging", "mining", "farming", "enchanting", "alchemy", "fishing"
    );

    public SetMissionCommand(JavaPlugin plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if(commandSender instanceof Player p)
        {
            if (strings.length < 5) {
                p.sendMessage(ChatColor.RED + "Usage: /mission <description> <skill> <reward> <item> <amount>");
                return true;
            }

            int skillIndex = -1;
            for (int i = 1; i < strings.length - 3; i++) {
                if (VALID_SKILLS.contains(strings[i].toLowerCase())) {
                    skillIndex = i;
                    break;
                }
            }

            if (skillIndex == -1) {
                p.sendMessage(ChatColor.RED + "Invalid skill! Valid skills: " + String.join(", ", VALID_SKILLS));
                return true;
            }

            String description = String.join(" ", Arrays.copyOfRange(strings, 0, skillIndex));
            String skill = strings[skillIndex].toLowerCase();

            Material material;
            int reward;
            int amount;

            try {
                reward = Integer.parseInt(strings[skillIndex + 1]);
                material = Material.matchMaterial(strings[skillIndex + 2].toUpperCase());

                if (material == null) {
                    p.sendMessage(ChatColor.RED + "Invalid material: " + strings[skillIndex + 2]);
                    return true;
                }

                amount = Integer.parseInt(strings[skillIndex + 3]);
            } catch (NumberFormatException e) {
                p.sendMessage(ChatColor.RED + "Reward and amount must be numbers!");
                return true;
            } catch (ArrayIndexOutOfBoundsException e) {
                p.sendMessage(ChatColor.RED + "Not enough arguments!");
                return true;
            }

            ItemStack target = new ItemStack(material, amount);

            Skills.mission = new Mission(description, skill, reward, plugin, target, false);
            Skills.mission.createMission();
            p.sendMessage(ChatColor.GREEN + "Mission has been created!");
        }
        return true;
    }
}