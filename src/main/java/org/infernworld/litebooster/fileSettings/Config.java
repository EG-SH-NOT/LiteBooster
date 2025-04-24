package org.infernworld.litebooster.fileSettings;

import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.infernworld.litebooster.util.ColorUtil;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Config {
    private double multiply, cultMult;
    private int time, cultTime;
    private String giveBooster, noPoint, activeBooster;
    private List<String> cultList = new ArrayList<>();

    public Config(FileConfiguration cfg) {
        ConfigurationSection msg= cfg.getConfigurationSection("message");
        if (msg== null ) return;
        ConfigurationSection sel = cfg.getConfigurationSection("list");
        if (sel == null ) return;
        ConfigurationSection exp = sel.getConfigurationSection("exp");
        if (exp == null) return;
        ConfigurationSection cult = sel.getConfigurationSection("cult");

        this.giveBooster = ColorUtil.getColor(msg.getString("sucess-give"));
        this.noPoint = ColorUtil.getColor(msg.getString("no-point"));
        this.time = exp.getInt("boost-time");
        this.multiply = exp.getDouble("multiply", 0.0);
        this.cultTime = cult.getInt("boost-time");
        this.cultMult = cult.getDouble("multiply", 0.0);
        this.cultList = cult.getStringList("inclusions");
        this.activeBooster = ColorUtil.getColor(msg.getString("active-booster"));
    }
}
