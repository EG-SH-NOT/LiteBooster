package org.infernworld.litebooster.util;

import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GuiUtil {

    public static ItemStack loadItemFromConfig(final ConfigurationSection item) {
        val material = item.getString("material", "STONE");
        Material mat;
        try {
            mat = Material.valueOf(material);
        } catch (IllegalArgumentException e) {
            Bukkit.getLogger().info("неверный material " + material +
                    " в конфиге, камень юзается по дефаулту проверь название предмета!");
            mat = Material.STONE;
        }
        val name = ColorUtil.getColor(item.getString("name"));
        List<String> lore = item.getStringList("lore")
                .stream()
                .map(ColorUtil::getColor)
                .toList();

        ItemStack itemStack = new ItemStack(mat);
        val meta = itemStack.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(lore);

        if (item.getBoolean("hide-attribute", false)) {
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        }

        if (item.getBoolean("ench", false)) {
            meta.addEnchant(Enchantment.LUCK, 1, false);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        val action = item.getString("action");
        if (action != null) {
            meta.setLocalizedName(action);
        }

        itemStack.setItemMeta(meta);
        return itemStack;
    }
    public static ItemStack updatePlaceholders(ItemStack item, Map<String, String> placeholders) {
        if (item == null || !item.hasItemMeta()) return item;
        ItemMeta meta = item.getItemMeta();
        if (meta.hasDisplayName()) {
            String displayName = meta.getDisplayName();
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                displayName = displayName.replace(entry.getKey(), entry.getValue());
            }
            meta.setDisplayName(displayName);
        }

        if (meta.hasLore()) {
            List<String> lore = meta.getLore();
            List<String> updatedLore = new ArrayList<>();
            for (String line : lore) {
                for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                    line = line.replace(entry.getKey(), entry.getValue());
                }
                updatedLore.add(line);
            }
            meta.setLore(updatedLore);
        }
        item.setItemMeta(meta);
        return item;
    }

    public static List<Integer> slots(Object slot) {
        List<Integer> slots = new ArrayList<>();
        if (slot instanceof Integer) {
            slots.add((Integer) slot);
        } else if (slot instanceof List) {
            slots = (List<Integer>) slot;
        }
        return slots;
    }
}