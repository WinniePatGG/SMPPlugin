package de.winniepat.SMPPlugin.commands;

import de.winniepat.SMPPlugin.SMPPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

public class TrashCommand implements CommandExecutor, Listener {

    private final JavaPlugin plugin;
    private final SMPPlugin smpPlugin;

    public TrashCommand(JavaPlugin plugin, SMPPlugin smpPlugin) {
        this.plugin = plugin;
        this.smpPlugin = smpPlugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage(smpPlugin.getMessage("command.error.no_player", Map.of("player", sender.getName())));
            return true;
        }

        Inventory trash = Bukkit.createInventory(p, 27, "§cTrash");
        p.openInventory(trash);
        return true;
    }

    @org.bukkit.event.EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getView().getTitle().equals("§cTrash")) {
            event.getInventory().clear();
        }
    }
}