package de.winniepat.SMPPlugin.waypoints;

import de.winniepat.SMPPlugin.SMPPlugin;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class WaypointListener implements Listener {
    private final WaypointsDatabase sqliteManager;
    private final Map<UUID, Location> pendingWaypoints = new HashMap<>();
    private final Map<UUID, Location> pendingLocations = new HashMap<>();
    private final SMPPlugin smpPlugin;

    public WaypointListener(WaypointsDatabase sqliteManager, SMPPlugin smpPlugin) {
        this.sqliteManager = sqliteManager;
        this.smpPlugin = smpPlugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        if (e.getItem() == null || !e.getItem().hasItemMeta()) return;

        ItemMeta meta = e.getItem().getItemMeta();
        if (!"true".equals(meta.getPersistentDataContainer()
                .get(new NamespacedKey(SMPPlugin.getInstance(), "waypoint"), PersistentDataType.STRING))) return;

        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            e.setCancelled(true);
            Block block = e.getClickedBlock();
            if (block == null) return;

            Location loc = block.getLocation().add(0.5, 1, 0.5);
            pendingWaypoints.put(player.getUniqueId(), loc);
            pendingLocations.put(player.getUniqueId(), player.getLocation().clone());
            player.sendMessage(ChatColor.YELLOW + "Please enter a name for your waypoint in chat. Don't move!");
        } else if (e.getAction() == Action.RIGHT_CLICK_AIR) {
            e.setCancelled(true);
            openWaypointMenu(player);
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        if (pendingWaypoints.containsKey(player.getUniqueId())) {
            e.setCancelled(true);
            String name = e.getMessage().trim();
            Location loc = pendingWaypoints.remove(player.getUniqueId());
            pendingLocations.remove(player.getUniqueId());

            Bukkit.getScheduler().runTask(SMPPlugin.getInstance(), () -> {
                boolean success = sqliteManager.saveWaypoint(player.getUniqueId(), new Waypoint(name, loc));
                if (success) {
                    player.sendMessage(ChatColor.GREEN + "Waypoint '" + name + "' set!");
                    player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1f, 1f);
                } else {
                    player.sendMessage(smpPlugin.getMessage("waypointlistener.already_exists", Map.of("player", player.getName())));
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 1f);
                }
            });
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if (pendingLocations.containsKey(player.getUniqueId())) {
            Location from = pendingLocations.get(player.getUniqueId());
            if (e.getTo() != null && e.getTo().distance(from) > 0.1) {
                pendingWaypoints.remove(player.getUniqueId());
                pendingLocations.remove(player.getUniqueId());
                player.sendMessage(smpPlugin.getMessage("waypointlistener.movement", Map.of("player", player.getName())));
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 0.5f);
            }
        }
    }

    public void openWaypointMenu(Player player) {
        List<Waypoint> waypoints = sqliteManager.getWaypoints(player.getUniqueId());
        Inventory gui = Bukkit.createInventory(null, 9 * 3, "Your Waypoints");
        for (Waypoint wp : waypoints) {
            ItemStack item = new ItemStack(Material.LODESTONE);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.YELLOW + wp.getName());
            item.setItemMeta(meta);
            gui.addItem(item);
        }
        player.openInventory(gui);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getView().getTitle().equals("Your Waypoints")) {
            e.setCancelled(true);
            ItemStack clicked = e.getCurrentItem();
            if (clicked != null && clicked.hasItemMeta()) {
                String name = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());
                Player player = (Player) e.getWhoClicked();
                List<Waypoint> waypoints = sqliteManager.getWaypoints(player.getUniqueId());
                for (Waypoint wp : waypoints) {
                    if (wp.getName().equals(name)) {
                        player.closeInventory();
                        startTeleportCountdown(player, wp);
                        break;
                    }
                }
            }
        }
    }

    private void startTeleportCountdown(Player player, Waypoint waypoint) {
        Location initialLoc = player.getLocation().clone();
        player.sendMessage(ChatColor.YELLOW + "Teleporting to " + waypoint.getName() + " in 5 seconds. Don't move!");

        new BukkitRunnable() {
            int countdown = 5;

            @Override
            public void run() {
                if (!player.isOnline() || player.getLocation().distance(initialLoc) > 0.1) {
                    player.sendMessage(ChatColor.RED + "Teleport cancelled due to movement.");
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 0.5f);
                    cancel();
                    return;
                }

                if (countdown > 0) {
                    player.sendActionBar(ChatColor.YELLOW + "Teleporting in " + countdown + "...");
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
                    countdown--;
                } else {
                    player.teleport(waypoint.getLocation());
                    player.sendMessage(ChatColor.GREEN + "Teleported to " + waypoint.getName() + "!");
                    player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
                    cancel();
                }
            }
        }.runTaskTimer(SMPPlugin.getInstance(), 0L, 20L);
    }
}