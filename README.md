<div align="center">

# ⚡🧟 ElectricSpawners — Slimefun Legacy

**Powered mob spawners brought forward for modern Slimefun servers.**

![Slimefun Legacy](https://img.shields.io/badge/Slimefun-Legacy-6bd425?style=for-the-badge)
![Paper 26.2](https://img.shields.io/badge/Paper-26.2-blue?style=for-the-badge)
![License: GPL-3.0](https://img.shields.io/badge/License-GPL--3.0-blue?style=for-the-badge)
![Maintained for AlbionMC.com](https://img.shields.io/badge/Maintained%20for-albionmc.com-7b68ee?style=for-the-badge)

</div>

> [!IMPORTANT]
> ElectricSpawners Legacy is an **unofficial maintenance fork** developed for the Slimefun Legacy ecosystem and for use on **albionmc.com**. Preservation of the original addon and its history is the priority.

## 🧟 What does ElectricSpawners do?

ElectricSpawners adds **energy-powered monster spawners** to Slimefun. Players can build specialized machines that consume Slimefun power to spawn mobs while retaining the classic ElectricSpawners progression and behavior.

This fork aims to preserve the historical `ELECTRIC_SPAWNER_*` item IDs, recipes, research, energy behavior, spawn radius, machine ownership, and placed-block data so established worlds can move forward safely.

## ⚡ Slimefun Legacy maintenance

Current maintenance includes:

- Paper 26.2 / modern Minecraft compatibility;
- Java 25 build tooling with Java 21-compatible addon bytecode;
- direct compilation against the Slimefun Legacy API;
- preservation of the classic **240 J per mob** energy cost and **2048 J** machine buffer;
- corrected lore for the actual 2048 J capacity;
- corrected six-nearby-mob limit behavior;
- null-safe handling of missing legacy `enabled` or `owner` records;
- first-opener claiming for old unowned machines;
- optional per-spawner mob-AI control and server-wide force-disable options;
- compatibility mapping for historical entity names such as `MUSHROOM_COW`;
- removal of the obsolete in-plugin updater so it cannot overwrite the Slimefun Legacy build.

Example AI configuration:

```yaml
mob-ai:
  force-disable: false
  default-disabled: false
```

## 📦 Builds

The maintained workflow is designed to produce a directly usable JAR such as:

```text
SF_ElectricSpawners_Legacy_v1.0.0.jar
```

Install only one ElectricSpawners build at a time and back up established Slimefun data before upgrading.

## ❤️ Credits & project lineage

- **TheBusyBiscuit** — original creator of ElectricSpawners and the classic addon design.
- **Slimefun-Addon-Community/ElectricSpawners** — community upstream repository that preserved and maintained the addon.
- **Slimefun developers and addon contributors** — for the Slimefun platform and the long-running addon ecosystem.
- **wickidcow / Slimefun Legacy** — current compatibility and preservation maintenance for modern servers and albionmc.com.

This fork exists because the original developers built something worth preserving. It is not a claim of original authorship or an attempt to replace that project history.

## 📜 GNU General Public License v3.0

ElectricSpawners is distributed under the **GNU General Public License v3.0 (GPLv3)**. See `LICENSE` for the complete license text.

If you distribute the plugin or modified GPL-covered versions, comply with GPLv3, including preserving applicable notices, clearly identifying modified versions, licensing covered modified source under GPLv3, and making the required Corresponding Source available when distributing object code.

The software is provided **without warranty** to the extent stated by GPLv3.

## ⚖️ Independence & trademark notice

**NOT AN OFFICIAL MINECRAFT PRODUCT. NOT APPROVED BY OR ASSOCIATED WITH MOJANG OR MICROSOFT.**

ElectricSpawners, Slimefun Legacy, and this maintenance fork are independent community projects. They are not sponsored, endorsed, approved, or operated by Mojang Studios or Microsoft. Minecraft-related names, brands, and assets remain the property of their respective rights holders.

This fork is also not represented as an official release from TheBusyBiscuit, the Slimefun-Addon-Community, or the original Slimefun team unless explicitly stated by those parties.

---

<div align="center">

**⚡ Power in. Mobs out. Classic ElectricSpawners lives on. 🧟**

</div>
