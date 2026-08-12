# ElectricSpawners — Slimefun Legacy

ElectricSpawners adds powered monster spawners to Slimefun. This repository is an **unofficial maintenance fork** focused on keeping the original addon usable on modern Paper servers with **Slimefun Legacy**.

The goal is preservation first: keep the original item IDs, recipes, research, energy behavior, and existing machine data while applying compatibility, safety, and build-system updates needed by current Minecraft server software.

## Compatibility

- **Primary server target:** Paper 26.2 / Minecraft 1.21.11+
- **Build JDK:** Java 25
- **Plugin bytecode:** Java 21
- **Primary Slimefun target:** [Slimefun Legacy](https://github.com/wickidcow/Slimefun-Legacy)

Paper 26.2 requires a Java 25 server runtime. ElectricSpawners itself is compiled to Java 21 bytecode so the addon does not unnecessarily raise its own bytecode floor beyond Slimefun Legacy's compatibility target.

## Legacy maintenance changes

- Preserves the historical `ELECTRIC_SPAWNER_*` Slimefun item IDs.
- Preserves the original 240 J per mob energy cost, 2048 J buffer, spawn radius, recipes, and six-nearby-mob cap.
- Corrects the old item lore that advertised a 512 J buffer even though the machine capacity is 2048 J.
- Corrects the nearby-entity cap so a machine no longer creates a seventh matching mob when six are already nearby.
- Safely handles missing `enabled` or `owner` data from old/corrupt machine records instead of throwing null-pointer exceptions.
- Allows an old unowned machine to be claimed by its first legitimate opener.
- Adds optional per-spawner mob AI control, including a server-wide force-disable option.
- Accepts historical entity names such as `MUSHROOM_COW` and maps them to their modern Paper equivalents while retaining the original Slimefun item ID.
- Removes the obsolete in-plugin GitHub auto-updater. Releases are distributed through GitHub instead.
- Builds directly against the current Slimefun Legacy API used by this fork.

## Mob AI options

```yaml
mob-ai:
  force-disable: false
  default-disabled: false
```

`force-disable` prevents spawned mobs from using AI and locks the AI option in the spawner menu. When it is `false`, players can toggle AI per machine. `default-disabled` controls the initial state for newly placed spawners.

## Builds

GitHub Actions builds the addon against Slimefun Legacy and publishes a directly downloadable server JAR named like:

```text
SF_ElectricSpawners_Legacy_v1.0.0.jar
```

The workflow uploads the JAR as an **uncompressed/raw Actions artifact** and also attaches the same JAR directly to the GitHub release on successful `master` builds.

## Credits

ElectricSpawners was originally created by **TheBusyBiscuit** and maintained by contributors to the Slimefun addon community. This fork exists to preserve and modernize that work for Slimefun Legacy; it is not presented as a replacement for the original authors or project history.

Original community repository: [Slimefun-Addon-Community/ElectricSpawners](https://github.com/Slimefun-Addon-Community/ElectricSpawners)

## License

This project is distributed under the **GNU General Public License v3.0**, matching the `LICENSE` file included in this repository.
