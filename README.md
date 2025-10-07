# SKILLS — Minecraft Skill Leveling Plugin

<img width="1024" height="1024" alt="icon" src="https://github.com/user-attachments/assets/a87b925e-d1cb-4457-9726-9e524412299a" />

# 🧠 SKILLS — Minecraft Skill Leveling Plugin

**SKILLS** is a lightweight, customizable Spigot plugin that adds a full RPG-style **skill leveling system** to Minecraft.  
Players can level up skills like **Mining**, **Foraging**, **Farming**, and **Fishing**, gaining rewards and bonuses as they progress.

---

## ✨ Features

- 🪓 **Level up through actions** — gain XP by breaking blocks, farming crops, chopping trees, and catching fish.
- 📈 **Dynamic XP system** — XP required per level scales automatically.
- 💎 **Skill rewards** — earn gameplay bonuses (like double drops) as you level up.
- 🧱 **Anti-exploit protection** — blocks placed by players don't grant XP.
- 🌾 **Crop growth detection** — XP is only rewarded for fully-grown crops.
- 🐟 **Fishing system** — gain XP from fishing, with rare treasure items becoming more likely at higher levels.
- 💬 **Action Bar Messages** — players see a floating message above the XP bar when gaining skill XP.
- 🔔 **Reward Sound Effects** — a short note block sound plays whenever players earn skill XP.
- 🧰 **Configurable system** — all skill data is saved in the plugin config file.

---

## 🛠️ Installation

1. Download the latest release of **SKILLS.jar** from the [Releases](../../releases) page.  
2. Drop the file into your server’s `plugins/` folder.  
3. Restart or reload your server.  
4. A new `config.yml` will be generated automatically.

---

## ⚙️ Configuration

All player skill data is stored in the plugin config.  
Example:

```yaml
players:
  123e4567-e89b-12d3-a456-426614174000:
    skills:
      mining:
        level: 3
        xp: 250/500
      foraging:
        level: 2
        xp: 120/300
      farming:
        level: 5
        xp: 470/1000
      fishing:
        level: 4
        xp: 350/800
```

---

## 🧾 Skills Implemented

| Skill     | How to Level Up                       | Bonus per Level                 |
|------------|---------------------------------------|---------------------------------|
| Mining     | Break natural ores and stone blocks   | +5% chance for double drops     |
| Foraging   | Chop down trees or wooden blocks      | +5% chance for double drops     |
| Farming    | Harvest fully-grown crops and other farming blocks | +5% chance for double drops     |
| Combat     | Kill mobs                            | +2% extra damage per level      |
| Fishing    | Catch fish                            | +5% chance for treasure items |

---

## 💬 Commands

| Command | Description | Permission |
|----------|--------------|-------------|
| `/skills` | Shows your skill progress in chat | All players |
| `/skillprogress` | Opens the skill progress menu | All players |
| `/givexp <player> <skill> <amount>` | Gives XP to a player for a skill | OP only |

---

## 🚀 Future Plans
- 🪄 **Alchemy  

---

## 👩‍💻 Developer Info

**Author:** [Ofer Goldberg](https://github.com/master3716)  
**Minecraft Version:** 1.21+  
**API:** Spigot / Paper  
**Language:** Java  

Contributions, bug reports, and feature requests are always welcome!  
Just open an [issue](../../issues) or a [pull request](../../pulls).

---

## 🧡 License

This project is licensed under the **MIT License** — see the [LICENSE](LICENSE) file for details.
