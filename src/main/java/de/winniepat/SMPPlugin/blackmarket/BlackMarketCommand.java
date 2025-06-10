package de.winniepat.SMPPlugin.blackmarket;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class BlackMarketCommand implements CommandExecutor {

    private final BlackMarketManager manager;

    public BlackMarketCommand(JavaPlugin plugin) {
        this.manager = new BlackMarketManager(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        manager.spawnMarket();
        player.sendMessage(ChatColor.DARK_PURPLE + "You have summoned the Black Market.");
        return true;
    }
}
