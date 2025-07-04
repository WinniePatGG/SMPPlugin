package de.winniepat.SMPPlugin.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class SpawnCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),"minecraft:tp " + sender.getName() + " -635.5 68 -837.5 -90 0");
        return false;
    }
}
