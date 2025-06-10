package de.winniepat.SMPPlugin.blockelevator;

import de.winniepat.SMPPlugin.SMPPlugin;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class PlayerJumpListener implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        // Check if player moves upwards
        if (event.getFrom().getY() < event.getTo().getY()) {
            // Always check the block below the player's feet
            Block blockBelow = player.getLocation().subtract(0, 0.1, 0).getBlock();

            if (blockBelow.getType() == Material.CRYING_OBSIDIAN) {
                if (!CooldownManager.isOnCooldown(player.getUniqueId())) {
                    for (int y = blockBelow.getY() + 2; y <= player.getWorld().getMaxHeight(); y++) {
                        Block above = player.getWorld().getBlockAt(blockBelow.getX(), y, blockBelow.getZ());
                        if (above.getType() == Material.CRYING_OBSIDIAN) {
                            player.teleport(new Location(player.getWorld(), above.getX() + 0.5, y + 1, above.getZ() + 0.5));
                            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
                            player.getWorld().spawnParticle(Particle.PORTAL, player.getLocation(), 30, 0.5, 1, 0.5);
                            CooldownManager.setCooldown(player.getUniqueId());
                            return;
                        }
                    }
                    player.sendMessage(ChatColor.YELLOW + "No elevator block above!");
                } else if (!player.hasMetadata("jumpCooldownNotified")) {
                    player.sendMessage(ChatColor.RED + "Elevator is cooling down!");
                    player.setMetadata("jumpCooldownNotified", new FixedMetadataValue(SMPPlugin.getInstance(), true));
                    Bukkit.getScheduler().runTaskLater(SMPPlugin.getInstance(), () -> {
                        player.removeMetadata("jumpCooldownNotified", SMPPlugin.getInstance());
                    }, 20L);
                }
            }
        }
    }
}
