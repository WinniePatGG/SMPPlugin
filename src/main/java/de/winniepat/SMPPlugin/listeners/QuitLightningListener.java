package de.winniepat.SMPPlugin.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuitLightningListener implements Listener {
    @EventHandler
    public void playerQuitListener(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (player.isOp()) {
            player.getWorld().strikeLightningEffect(player.getLocation());
        }
    }
}
