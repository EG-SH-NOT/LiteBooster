package org.infernworld.litebooster.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TabCompleter implements org.bukkit.command.TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return List.of("give");
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
            return List.of("exp");
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
            List<String> players = new ArrayList<>();
            String prefix = args[2].toLowerCase();
            for (Player player : Bukkit.getOnlinePlayers()) {
                String name = player.getName();
                if (name.toLowerCase().startsWith(prefix)) {
                    players.add(name);
                }
            }
            return players;
        }

        return List.of();
    }
}
