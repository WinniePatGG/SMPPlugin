package de.winniepat.SMPPlugin.bloodmoon;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class BloodmoonCommand implements CommandExecutor {

    private final BloodmoonManager manager;

    public BloodmoonCommand(BloodmoonManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("bloodmoon.toggle")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to do that.");
            return true;
        }

        if (manager.isActive()) {
            manager.endBloodmoon();
            sender.sendMessage(ChatColor.GRAY + "☀ Bloodmoon ended.");
        } else {
            manager.startBloodmoon();
            sender.sendMessage(ChatColor.RED + "☠ Bloodmoon started.");

        }

        return true;
    }
}
