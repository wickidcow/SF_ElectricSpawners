package io.github.thebusybiscuit.electricspawners;

import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.api.researches.Research;
import io.github.thebusybiscuit.slimefun4.core.attributes.EnergyNetComponent;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockPlaceHandler;
import io.github.thebusybiscuit.slimefun4.core.networks.energy.EnergyNetComponentType;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.utils.ChatUtils;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;

/**
 * The original ElectricSpawners machine, maintained for modern Paper and
 * Slimefun Legacy while preserving the historical item IDs and machine balance.
 */
@SuppressWarnings("deprecation")
public class ElectricSpawner extends SimpleSlimefunItem<BlockTicker> implements EnergyNetComponent {

    private static final int ENERGY_CONSUMPTION = 240;
    private static final int ENERGY_CAPACITY = 2048;
    private static final int MAX_NEARBY_ENTITIES = 6;
    private static final double SPAWN_RADIUS = 4.0D;
    private static final int SPAWN_EVERY_TICKER_CYCLES = 3;

    private final EntityType entity;
    private final boolean forceDisableAI;
    private final boolean defaultDisabledAI;
    private final AtomicInteger tickerCycle = new AtomicInteger();

    public ElectricSpawner(
            ItemGroup category,
            String mob,
            EntityType type,
            Research research,
            boolean forceDisableAI,
            boolean defaultDisabledAI) {
        // @formatter:off
        super(category, new SlimefunItemStack("ELECTRIC_SPAWNER_" + mob, "db6bd9727abb55d5415265789d4f2984781a343c68dcaf57f554a5e9aa1cd",
                "&ePowered Spawner &7(" + ChatUtils.humanize(mob) + ")",
                "",
                "&8\u21E8 &e\u26A1 &7Max Entity Cap: " + MAX_NEARBY_ENTITIES,
                "&8\u21E8 &e\u26A1 &7" + ENERGY_CAPACITY + " J Buffer",
                "&8\u21E8 &e\u26A1 &7" + ENERGY_CONSUMPTION + " J/Mob",
                forceDisableAI ? "&8\u21E8 &cMob AI is force-disabled" : ""
        ), RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
                null, SlimefunItems.PLUTONIUM, null,
                SlimefunItems.ELECTRIC_MOTOR, new CustomItemStack(Material.SPAWNER, "&bReinforced Spawner", "&7Type: &b" + ChatUtils.humanize(type.toString())), SlimefunItems.ELECTRIC_MOTOR,
                SlimefunItems.BLISTERING_INGOT_3, SlimefunItems.LARGE_CAPACITOR, SlimefunItems.BLISTERING_INGOT_3
        });
        // @formatter:on

        this.entity = type;
        this.forceDisableAI = forceDisableAI;
        this.defaultDisabledAI = defaultDisabledAI;

        addItemHandler(onBlockPlace());
        registerMenu();
        research.addItems(this);
    }

    private void registerMenu() {
        new BlockMenuPreset(getId(), "&cPowered Spawner") {

            @Override
            public void init() {
                for (int i = 0; i < 9; i++) {
                    if (i != 4 && i != 7) {
                        addItem(
                                i,
                                new CustomItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE, " "),
                                (p, slot, item, action) -> false);
                    }
                }
            }

            @Override
            public void newInstance(BlockMenu menu, Block b) {
                renderPowerToggle(menu, b);
                renderAiToggle(menu, b);
            }

            @Override
            public boolean canOpen(Block b, Player p) {
                if (p.hasPermission("slimefun.cargo.bypass")) {
                    return true;
                }

                String owner = BlockStorage.getLocationInfo(b.getLocation(), "owner");
                if (owner == null || owner.isBlank()) {
                    // Recover old/unowned machine data by assigning the first legitimate opener.
                    BlockStorage.addBlockInfo(b, "owner", p.getUniqueId().toString());
                    return true;
                }

                return owner.equals(p.getUniqueId().toString());
            }

            @Override
            public int[] getSlotsAccessedByItemTransport(ItemTransportFlow flow) {
                return new int[0];
            }
        };
    }

    private void renderPowerToggle(BlockMenu menu, Block b) {
        boolean enabled = readBoolean(b, "enabled", false);
        Material material = enabled ? Material.REDSTONE : Material.GUNPOWDER;
        String state = enabled ? "&2\u2714" : "&4\u2718";
        String action = enabled ? "disable" : "enable";

        menu.replaceExistingItem(
                4,
                new CustomItemStack(
                        material,
                        "&7Enabled: " + state,
                        "",
                        "&e> Click to " + action + " this Machine"));
        menu.addMenuClickHandler(4, (p, slot, item, clickAction) -> {
            BlockStorage.addBlockInfo(b, "enabled", Boolean.toString(!enabled));
            renderPowerToggle(menu, b);
            return false;
        });
    }

    private void renderAiToggle(BlockMenu menu, Block b) {
        boolean disabled = forceDisableAI || readBoolean(b, "disable_ai", defaultDisabledAI);

        if (forceDisableAI) {
            menu.replaceExistingItem(
                    7,
                    new CustomItemStack(
                            Material.BARRIER,
                            "&7Mob AI: &4Disabled",
                            "",
                            "&cDisabled by server configuration"));
            menu.addMenuClickHandler(7, (p, slot, item, action) -> false);
            return;
        }

        menu.replaceExistingItem(
                7,
                new CustomItemStack(
                        disabled ? Material.ZOMBIE_HEAD : Material.PLAYER_HEAD,
                        "&7Mob AI: " + (disabled ? "&4Disabled" : "&2Enabled"),
                        "",
                        "&e> Click to toggle Mob AI"));
        menu.addMenuClickHandler(7, (p, slot, item, action) -> {
            BlockStorage.addBlockInfo(b, "disable_ai", Boolean.toString(!disabled));
            renderAiToggle(menu, b);
            return false;
        });
    }

    private BlockPlaceHandler onBlockPlace() {
        return new BlockPlaceHandler(false) {
            @Override
            public void onPlayerPlace(BlockPlaceEvent event) {
                Block block = event.getBlock();
                Player player = event.getPlayer();

                BlockStorage.addBlockInfo(block, "enabled", "false");
                BlockStorage.addBlockInfo(block, "owner", player.getUniqueId().toString());
                BlockStorage.addBlockInfo(
                        block, "disable_ai", Boolean.toString(forceDisableAI || defaultDisabledAI));
            }
        };
    }

    private boolean readBoolean(Block block, String key, boolean fallback) {
        String value = BlockStorage.getLocationInfo(block.getLocation(), key);
        return value == null ? fallback : Boolean.parseBoolean(value);
    }

    public int getEnergyConsumption() {
        return ENERGY_CONSUMPTION;
    }

    protected void tick(Block block) {
        if (tickerCycle.get() != 0) {
            return;
        }

        // Missing data from very old/corrupt machines is treated as disabled rather than throwing.
        if (!readBoolean(block, "enabled", false)) {
            return;
        }

        if (getCharge(block.getLocation()) < getEnergyConsumption()) {
            return;
        }

        int count = 0;
        for (Entity nearby : block.getWorld()
                .getNearbyEntities(block.getLocation(), SPAWN_RADIUS, SPAWN_RADIUS, SPAWN_RADIUS)) {
            if (nearby.getType() == entity && ++count >= MAX_NEARBY_ENTITIES) {
                return;
            }
        }

        Location spawnLocation = new Location(
                block.getWorld(), block.getX() + 0.5D, block.getY() + 1.5D, block.getZ() + 0.5D);
        Entity spawned = block.getWorld().spawnEntity(spawnLocation, entity);

        if (spawned instanceof LivingEntity living
                && (forceDisableAI || readBoolean(block, "disable_ai", defaultDisabledAI))) {
            living.setAI(false);
        }

        // Only consume power after the entity was spawned successfully.
        removeCharge(block.getLocation(), getEnergyConsumption());
    }

    @Override
    public BlockTicker getItemHandler() {
        return new BlockTicker() {
            @Override
            public void tick(Block b, SlimefunItem sf, Config data) {
                ElectricSpawner.this.tick(b);
            }

            @Override
            public void uniqueTick() {
                tickerCycle.updateAndGet(cycle -> (cycle + 1) % SPAWN_EVERY_TICKER_CYCLES);
            }

            @Override
            public boolean isSynchronized() {
                return true;
            }
        };
    }

    @Override
    public int getCapacity() {
        return ENERGY_CAPACITY;
    }

    @Override
    public EnergyNetComponentType getEnergyComponentType() {
        return EnergyNetComponentType.CONSUMER;
    }
}
