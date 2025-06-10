package de.winniepat.SMPPlugin.blockelevator;
import de.winniepat.SMPPlugin.SMPPlugin;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class PlayerSneakListener implements Listener {

    @EventHandler
    public void onPlayerSneak(PlayerToggleSneakEvent event) {
        if (!event.isSneaking()) return;

        Player player = event.getPlayer();

        // Always check the block **below** the player's feet
        Block blockBelow = player.getLocation().subtract(0, 0.1, 0).getBlock();

        if (blockBelow.getType() == Material.CRYING_OBSIDIAN) {
            if (!CooldownManager.isOnCooldown(player.getUniqueId())) {
                for (int y = blockBelow.getY() - 1; y >= 0; y--) {
                    Block below = player.getWorld().getBlockAt(blockBelow.getX(), y, blockBelow.getZ());
                    if (below.getType() == Material.CRYING_OBSIDIAN) {
                        player.teleport(new Location(player.getWorld(), below.getX() + 0.5, y + 1, below.getZ() + 0.5));
                        player.playSound(player.getLocation(), Sound.BLOCK_PISTON_CONTRACT, 1f, 1f);
                        player.getWorld().spawnParticle(Particle.CLOUD, player.getLocation(), 20, 0.3, 0.3, 0.3);
                        CooldownManager.setCooldown(player.getUniqueId());
                        return;
                    }
                }
                player.sendMessage(ChatColor.YELLOW + "No elevator block below!");
            } else if (!player.hasMetadata("sneakCooldownNotified")) {
                player.sendMessage(ChatColor.RED + "Elevator is cooling down!");
                player.setMetadata("sneakCooldownNotified", new FixedMetadataValue(SMPPlugin.getInstance(), true));
                Bukkit.getScheduler().runTaskLater(SMPPlugin.getInstance(), () -> {
                    player.removeMetadata("sneakCooldownNotified", SMPPlugin.getInstance());
                }, 20L);
            }
        }
    }
}
