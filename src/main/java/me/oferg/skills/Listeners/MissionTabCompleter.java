package me.oferg.skills.Listeners;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MissionTabCompleter implements TabCompleter {

    private static final List<String> VALID_SKILLS = Arrays.asList(
            "mining", "farming", "combat", "fishing", "enchanting", "alchemy", "foraging"
    );

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        // /mission <description> <skill> <reward> <item> <amount>

        if (args.length < 2) {
            return Collections.emptyList();
        }

        // Find the skill index
        int skillIndex = -1;
        for (int i = 1; i < args.length; i++) {
            if (VALID_SKILLS.contains(args[i].toLowerCase())) {
                skillIndex = i;
                break;
            }
        }

        // If we haven't found a skill yet, suggest skill completions
        if (skillIndex == -1) {
            String input = args[args.length - 1].toLowerCase();
            return VALID_SKILLS.stream()
                    .filter(skill -> skill.startsWith(input))
                    .collect(Collectors.toList());
        }

        // After skill is found, handle reward, item, and amount arguments
        int argIndexAfterSkill = args.length - skillIndex - 1;

        // argIndexAfterSkill = 1 -> reward argument
        // argIndexAfterSkill = 2 -> item argument
        // argIndexAfterSkill = 3 -> amount argument

        if (argIndexAfterSkill == 2) { // The item argument
            String input = args[args.length - 1].toUpperCase();

            // Suggest matching materials (limit to avoid lag)
            return Arrays.stream(Material.values())
                    .map(Material::name)
                    .filter(name -> name.startsWith(input))
                    .limit(25)
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }
}