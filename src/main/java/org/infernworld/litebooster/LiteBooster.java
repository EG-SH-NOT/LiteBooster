package org.infernworld.litebooster;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.infernworld.litebooster.command.BoosterCommand;
import org.infernworld.litebooster.command.Command;
import org.infernworld.litebooster.command.TabCompleter;
import org.infernworld.litebooster.fileSettings.Config;
import org.infernworld.litebooster.fileSettings.InvSettings;
import org.infernworld.litebooster.gui.Inv;
import org.infernworld.litebooster.listener.Events;
import org.infernworld.litebooster.listener.invEvents.InvEvents;
import org.infernworld.litebooster.manager.BoosterManager;

import java.io.File;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public final class LiteBooster extends JavaPlugin {
    private FileConfiguration config;
    private FileConfiguration invs;

    private BoosterManager boosterManager;
    private InvSettings invSettings;

    private Config cfg;
    private BukkitTask task;
    private final Map<UUID, Inv> guiKesh = new ConcurrentHashMap<>();

    @Override
    public void onEnable() {
        loadFiles();

        cfg = new Config(getConfig());
        boosterManager = new BoosterManager(this, cfg);
        invSettings = new InvSettings(invs);

        this.registerCmd();
        boosterManager.loadBoosters();
        registerEvents();

        task = Bukkit.getScheduler().runTaskTimer(this, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getOpenInventory().getTopInventory().getHolder() instanceof Inv) {
                    ((Inv) player.getOpenInventory().getTopInventory().getHolder()).updatePlayers(player);
                }
            }
        }, 0L, 20L);
    }

    private void registerEvents() {
        PluginManager pluginManager = getServer().getPluginManager();
        Listener[] listeners = {
                new InvEvents(invSettings,boosterManager,this, cfg),
                new Events(boosterManager, cfg,this)
        };
        for (Listener listener : listeners) {
            pluginManager.registerEvents(listener, this);
        }
    }

    private void registerCmd() {
        PluginCommand cmd = getCommand("litebooster");
        if (cmd == null) return;

        cmd.setTabCompleter(new TabCompleter());
        cmd.setExecutor(new Command(boosterManager));

        PluginCommand bcmd = getCommand("booster");
        if (bcmd == null) return;
        bcmd.setExecutor(new BoosterCommand(this));
    }

    private FileConfiguration addonCfgFile(String path, boolean saveDefault) {
        final File file = new File(getDataFolder(), path);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            if (saveDefault) {
                saveResource(path, true);
            }
        }
        return YamlConfiguration.loadConfiguration(file);
    }

    private void loadFiles() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        this.invs = addonCfgFile("inv.yml", true);
        this.config = addonCfgFile("config.yml", true);
    }

    public Inv getCreateInv(Player player) {
        return guiKesh.computeIfAbsent(player.getUniqueId(), uuid -> new Inv(this, invSettings, player));
    }

    @Override
    public void onDisable() {
        boosterManager.saveBoosters();
        this.task.cancel();
    }
}
