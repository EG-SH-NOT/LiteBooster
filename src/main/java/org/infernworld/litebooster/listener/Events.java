package org.infernworld.litebooster.listener;

import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.infernworld.litebooster.LiteBooster;
import org.infernworld.litebooster.fileSettings.Config;
import org.infernworld.litebooster.gui.Inv;
import org.infernworld.litebooster.manager.BoosterManager;

import java.util.Collection;
import java.util.UUID;

public class Events implements Listener {
    private final BoosterManager boosterManager;
    private final Config config;
    private final LiteBooster plugin;

    public Events(BoosterManager boosterManager, Config config, LiteBooster plugin) {
        this.boosterManager = boosterManager;
        this.config = config;
        this.plugin = plugin;
    }

    @EventHandler
    public void onExpPickup(PlayerPickupExperienceEvent e) {
        Player player = e.getPlayer();
        UUID playerId = player.getUniqueId();
        if (boosterManager.isBoosterActive(playerId, "exp") ||
                boosterManager.isBoosterActive(playerId, "all")) {
            double multiplier = config.getMultiply();
            int exp = e.getExperienceOrb().getExperience();
            int booster = (int) Math.round(exp * (1 + multiplier));
            e.getExperienceOrb().setExperience(booster);
            player.sendMessage("робит");
        }
    }

    @EventHandler
    public void onCultBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();
        UUID playerId = player.getUniqueId();
        if (boosterManager.isBoosterActive(playerId, "cult") ||
                boosterManager.isBoosterActive(playerId, "all")) {
            if (config.getCultList().contains(e.getBlock().getType().name())) {
                if (Math.random() < config.getCultMult()) {
                    Collection<ItemStack> drops = e.getBlock().getDrops();
                    for (ItemStack drop : drops) {
                        player.getWorld().dropItemNaturally(e.getBlock().getLocation(), drop.clone());
                    }
                }
                player.sendMessage("робит");
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if (e.getInventory().getHolder() instanceof Inv) {
            Player player = (Player) e.getPlayer();
            UUID playerId = player.getUniqueId();
            plugin.getGuiKesh().remove(playerId);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player player = (Player) e.getPlayer();
        UUID playerId = player.getUniqueId();
        plugin.getGuiKesh().remove(playerId);
    }
}
