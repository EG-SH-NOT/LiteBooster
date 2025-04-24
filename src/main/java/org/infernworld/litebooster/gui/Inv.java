package org.infernworld.litebooster.gui;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.infernworld.litebooster.LiteBooster;
import org.infernworld.litebooster.fileSettings.InvSettings;
import org.infernworld.litebooster.manager.BoosterType;
import org.infernworld.litebooster.util.GuiUtil;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Inv implements InventoryHolder {
    private final Inventory inv;
    private final LiteBooster plugin;
    private final InvSettings invCfg;
    private final Player player;

    public Inv(LiteBooster plugin, InvSettings invCfg, Player player ) {
        this.plugin = plugin;
        this.invCfg = invCfg;
        this.player = player;
        this.inv = Bukkit.createInventory(this, invCfg.getSize(), invCfg.getTitle());
        loadItems();
    }

    private void loadItems() {
        ConfigurationSection sel = plugin.getInvs().getConfigurationSection("items");
        for (String key : sel.getKeys(false)) {
            ConfigurationSection items = sel.getConfigurationSection(key);
            ItemStack upItem = updateItem(items, player);
            setSlot(upItem, items);
        }
    }

    private void setSlot(ItemStack item, ConfigurationSection items) {
        List<Integer> slots = GuiUtil.slots(items.get("slot"));
        for (Integer slot : slots) {
            this.inv.setItem(slot, item);
        }
    }

    public void updatePlayers(Player player) {
        ConfigurationSection itemsSection = plugin.getInvs().getConfigurationSection("items");
        for (String key : itemsSection.getKeys(false)) {
            ConfigurationSection items = itemsSection.getConfigurationSection(key);
            ItemStack upItem = updateItem(items, player);
            List<Integer> slots = GuiUtil.slots(items.get("slot"));
            for (Integer slot : slots) {
                inv.setItem(slot, upItem);
            }
        }
        player.updateInventory();
    }

    private ItemStack updateItem(ConfigurationSection items, Player player) {
        Map<String, String> placeholders = new HashMap<>();
        String action = items.getString("action", "");
        if (action.equals("exp")) {
            placeholders.put("%litebooster_exp%",
                    plugin.getBoosterManager().getBoosterTime(player.getUniqueId(), BoosterType.EXP));
        } else if (action.equals("cult")) {
            placeholders.put("%litebooster_cult%",
                    plugin.getBoosterManager().getBoosterTime(player.getUniqueId(), BoosterType.CULT));
        }
        ItemStack item = GuiUtil.loadItemFromConfig(items);
        return GuiUtil.updatePlaceholders(item, placeholders);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return this.inv;
    }
}
