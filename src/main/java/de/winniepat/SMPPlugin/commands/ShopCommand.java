package de.winniepat.SMPPlugin.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ShopCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),"minecraft:tp " + sender.getName() + " -696 64 -898 0.0 -7.0");
        return false;
    }
}
