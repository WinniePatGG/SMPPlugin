package de.winniepat.SMPPlugin.listeners;

import de.winniepat.SMPPlugin.commands.CodingCommand;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class CodingCleanupListener implements Listener {

    private final CodingCommand codingCommand;

    public CodingCleanupListener(CodingCommand codingCommand) {
        this.codingCommand = codingCommand;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();

        if (codingCommand.getAnimationTasks().containsKey(uuid)) {
            codingCommand.getAnimationTasks().get(uuid).cancel();
            codingCommand.getAnimationTasks().remove(uuid);
        }

        if (codingCommand.getCodingStands().containsKey(uuid)) {
            ArmorStand stand = codingCommand.getCodingStands().remove(uuid);
            if (stand != null && !stand.isDead()) {
                stand.remove();
            }
        }
    }
}
