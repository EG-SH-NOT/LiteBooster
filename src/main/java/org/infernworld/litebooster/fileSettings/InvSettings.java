package org.infernworld.litebooster.fileSettings;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.infernworld.litebooster.util.ColorUtil;

@Getter
public class InvSettings {
    private final String title;
    private final int size;

    public InvSettings(FileConfiguration inv) {
        this.size = inv.getInt("size");
        this.title = ColorUtil.getColor(inv.getString("title"));
    }
}
