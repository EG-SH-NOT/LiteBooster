package org.infernworld.litebooster.listener.invEvents;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.infernworld.litebooster.LiteBooster;
import org.infernworld.litebooster.fileSettings.Config;
import org.infernworld.litebooster.fileSettings.InvSettings;
import org.infernworld.litebooster.manager.BoosterManager;
import org.infernworld.litebooster.manager.BoosterType;

import java.util.UUID;

public class InvEvents implements Listener {
    private final InvSettings invSettings;
    private final BoosterManager bm;
    private final LiteBooster plugin;
    private final Config cfg;

    public InvEvents(InvSettings invSettings, BoosterManager bm, LiteBooster plugin, Config cfg) {
        this.invSettings = invSettings;
        this.bm = bm;
        this.plugin = plugin;
        this.cfg = cfg;
    }

    @EventHandler
    public void onInvClick(InventoryClickEvent e) {
        if (!e.getView().getTitle().equalsIgnoreCase(invSettings.getTitle())) return;

        invClickEvent(e);
    }

    private void invClickEvent(InventoryClickEvent e) {
        e.setCancelled(true);
        Player player = (Player) e.getWhoClicked();
        ItemStack item = e.getCurrentItem();
        if (item == null) return;

        String action = item.getItemMeta().getLocalizedName();

        if (action.startsWith("exp")) {
            boosterBuy(player,"exp", BoosterType.EXP.getDisplayName());
        } else if (action.startsWith("cult")) {
            boosterBuy(player,"cult", BoosterType.CULT.getDisplayName());
        } else if (action.startsWith("all")) {
            boosterBuy(player,"all",BoosterType.ALL.getDisplayName());
        }
    }

    private void boosterBuy(Player player, String boosterType, String name) {
        PlayerPointsAPI ppApi = PlayerPoints.getInstance().getAPI();
        UUID playerId = player.getUniqueId();
        if (bm.isBoosterActive(playerId, boosterType)) {
            player.sendMessage(cfg.getActiveBooster());
            return;
        }

        ConfigurationSection items = plugin.getInvs().getConfigurationSection("items");
        if (items == null) return;

        ConfigurationSection booster = items.getConfigurationSection(boosterType);
        if (booster == null) return;
        int price = booster.getInt("price");
        if (ppApi.look(playerId) >= price) {
            bm.giveBooster(player, boosterType, 86400);
            player.sendMessage(cfg.getGiveBooster()
                    .replace("%player%", player.getName())
                    .replace("%booster%", name));
            ppApi.take(playerId, price);
        } else {
            player.sendMessage(cfg.getNoPoint());
        }
    }
}
