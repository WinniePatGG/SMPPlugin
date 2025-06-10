package de.winniepat.SMPPlugin.starter;

import de.winniepat.SMPPlugin.SMPPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class StartCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("start")) {
            Location spawn = new Location(Bukkit.getWorlds().get(0), 0, Bukkit.getWorlds().get(0).getHighestBlockYAt(0, 0) + 1, 0);

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!player.isOp()) {
                    player.teleport(spawn);
                    SMPPlugin.frozenPlayers.add(player.getUniqueId());
                }
            }

            new BukkitRunnable() {
                int countdown = 3;

                @Override
                public void run() {
                    if (countdown > 0) {
                        Bukkit.broadcastMessage(ChatColor.YELLOW + "Starts in " + countdown + "...");
                        countdown--;
                    } else {
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            SMPPlugin.frozenPlayers.remove(player.getUniqueId());
                            player.sendTitle(ChatColor.GREEN + "Have Fun", ChatColor.YELLOW + "-SMP-", 10, 40, 10);
                        }
                        cancel();
                    }
                }
            }.runTaskTimer(SMPPlugin.getInstance(), 0, 20);

            return true;
        }

        sender.sendMessage(ChatColor.RED + "Usage: /smp start");
        return true;
    }

}
