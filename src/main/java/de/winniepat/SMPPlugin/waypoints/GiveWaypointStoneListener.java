package de.winniepat.SMPPlugin.waypoints;

import de.winniepat.SMPPlugin.SMPPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Map;

public class GiveWaypointStoneListener implements Listener {

    private final SMPPlugin smpPlugin;

    public GiveWaypointStoneListener(SMPPlugin smpPlugin) {
        this.smpPlugin = smpPlugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (!player.hasPlayedBefore()) {
            ItemStack item = createWaypointStone();
            player.getInventory().addItem(item);
            player.sendMessage(smpPlugin.getMessage("waypoint.first_join", Map.of("player", player.getName())));
        }
    }

    private ItemStack createWaypointStone() {
        ItemStack item = new ItemStack(Material.LODESTONE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + "Waypoint Stone");
        meta.getPersistentDataContainer().set(new NamespacedKey(SMPPlugin.getInstance(), "waypoint"), PersistentDataType.STRING, "true");
        item.setItemMeta(meta);
        return item;
    }
}
