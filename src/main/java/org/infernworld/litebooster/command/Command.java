package org.infernworld.litebooster.command;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.infernworld.litebooster.manager.BoosterManager;
import org.jetbrains.annotations.NotNull;

public class Command implements CommandExecutor {
    private final BoosterManager boosterManager;

    public Command(BoosterManager boosterManager) {
        this.boosterManager = boosterManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("litebooster.admin")) return true;

        if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
            switch (args[1]) {
                case "exp":
                    Player player = Bukkit.getPlayerExact(args[2]);
                    if (player == null ) {
                        sender.sendMessage("нет игрока");
                        return true;
                    }
                    boosterManager.giveBooster(player, "exp", 86400);
                    break;
            }
        }
        return false;
    }
}
