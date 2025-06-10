package de.winniepat.SMPPlugin.waypoints;

import de.winniepat.SMPPlugin.SMPPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Map;

public class WaypointCommands implements CommandExecutor {
    private final WaypointsDatabase sqliteManager;
    private final SMPPlugin smpPlugin;

    public WaypointCommands(WaypointsDatabase sqliteManager, SMPPlugin smpPlugin) {
        this.sqliteManager = sqliteManager;
        this.smpPlugin = smpPlugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) return false;

        if (label.equalsIgnoreCase("getwaypoint")) {
            player.getInventory().addItem(createWaypointStone());
            player.sendMessage(smpPlugin.getMessage("getwaypoint.success", Map.of("player", player.getName())));
            return true;
        }

        if (label.equalsIgnoreCase("addwaypoint") && args.length >= 1) {
            String name = String.join(" ", args);
            boolean success = sqliteManager.saveWaypoint(player.getUniqueId(), new Waypoint(name, player.getLocation()));
            if (success) {
                player.sendMessage(ChatColor.GREEN + "Waypoint '" + name + "' saved.");
            } else {
                player.sendMessage(smpPlugin.getMessage("addwaypoint.already_exists", Map.of("player", player.getName())));
            }
            return true;
        }

        return false;
    }

    private ItemStack createWaypointStone() {
        ItemStack item = new ItemStack(Material.LODESTONE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + "Your Waypoints");
        meta.getPersistentDataContainer().set(new NamespacedKey(SMPPlugin.getInstance(), "waypoint"), PersistentDataType.STRING, "true");
        item.setItemMeta(meta);
        return item;
    }
}