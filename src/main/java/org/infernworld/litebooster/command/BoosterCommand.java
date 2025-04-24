package org.infernworld.litebooster.command;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.infernworld.litebooster.LiteBooster;
import org.infernworld.litebooster.gui.Inv;
import org.jetbrains.annotations.NotNull;

public class BoosterCommand implements CommandExecutor {
    private final LiteBooster plugin;

    public BoosterCommand(LiteBooster plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player p = (Player) sender;
        Inv playerInv = plugin.getCreateInv(p);
        p.openInventory(playerInv.getInventory());
        return true;
    }
}
