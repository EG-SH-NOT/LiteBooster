package org.infernworld.litebooster.manager;

import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.infernworld.litebooster.LiteBooster;
import org.infernworld.litebooster.fileSettings.Config;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BoosterManager {
    private final LiteBooster plugin;
    private final Config config;
    private final Map<UUID, Map<String, Long>> activeBoosters = new ConcurrentHashMap<>();

    public BoosterManager(LiteBooster plugin, Config config) {
        this.plugin = plugin;
        this.config = config;
    }

    public void giveBooster(Player player, String boosterType, long durationSeconds) {
        UUID uuid = player.getUniqueId();
        long expireTime = System.currentTimeMillis() + (durationSeconds * 1000);

        activeBoosters.computeIfAbsent(uuid, k -> new HashMap<>())
                .put(boosterType, expireTime);

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            saveBoosters();
        });
    }

    public boolean isBoosterActive(UUID uuid, String boosterType) {
        if (!activeBoosters.containsKey(uuid)) return false;

        Long expireTime = activeBoosters.get(uuid).get(boosterType);
        if (expireTime == null) return false;

        if (expireTime <= System.currentTimeMillis()) {
            removeExpiredBooster(uuid, boosterType);
            return false;
        }

        return true;
    }

    private void removeExpiredBooster(UUID uuid, String boosterType) {
        Map<String, Long> userBoosters = activeBoosters.get(uuid);
        if (userBoosters != null) {
            userBoosters.remove(boosterType);
            if (userBoosters.isEmpty()) {
                activeBoosters.remove(uuid);
            }
        }
    }

    public String getBoosterTime(UUID uuid, BoosterType boosterType) {
        return isBoosterActive(uuid, boosterType.getId())
                ? getTime(uuid, boosterType.getId())
                : "- - -";
    }

    public void loadBoosters() {
        File file = new File(plugin.getDataFolder(), "data.yml");
        if (!file.exists()) return;
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        for (String uuidStr : yaml.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(uuidStr);
                Map<String, Long> boosters = new HashMap<>();
                ConfigurationSection boosterSec = yaml.getConfigurationSection(uuidStr);
                if (boosterSec != null) {
                    for (String booster : boosterSec.getKeys(false)) {
                        long time = boosterSec.getLong(booster);
                        if (time > System.currentTimeMillis()) {
                            boosters.put(booster, time);
                        }
                    }
                    if (!boosters.isEmpty()) {
                        activeBoosters.put(uuid, boosters);
                    }
                }

            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Неверный UUID в файле данных " + uuidStr);
            }
        }
    }

    public void saveBoosters() {
        File file = new File(plugin.getDataFolder(), "data.yml");
        YamlConfiguration yaml = new YamlConfiguration();
        activeBoosters.forEach((uuid, boosters) -> {
            String uuidStr = uuid.toString();
            boosters.forEach((booster, expireTime) -> {
                yaml.set(uuidStr + "." + booster, expireTime);
            });
        });
        try {
            yaml.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Не удалось сохранить данные бустеров");
        }
    }

    public String getTime(UUID uuid, String boosterType) {
        long left = (activeBoosters.get(uuid).get(boosterType) - System.currentTimeMillis()) / 1000;
        return formatTime(left);
    }

    private String formatTime(long seconds) {
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, secs);
    }
}

