package de.winniepat.SMPPlugin.polls;

import de.winniepat.SMPPlugin.SMPPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class PollCommand implements CommandExecutor {

    private final PollManager manager;
    private final SMPPlugin smpPlugin;

    public PollCommand(PollManager manager, SMPPlugin smpPlugin) {
        this.manager = manager;
        this.smpPlugin = smpPlugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(smpPlugin.getMessage("command.poll.usage", Map.of("player", sender.getName())));
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(smpPlugin.getMessage("error.no_player", Map.of("player", sender.getName())));
            return true;
        }

        if (!player.hasPermission("poll.start")) {
            sender.sendMessage(smpPlugin.getMessage("command.error.no_permission", Map.of("player", sender.getName())));
            return true;
        }

        if (manager.hasActivePoll()) {
            sender.sendMessage(smpPlugin.getMessage("command.poll.already_active", Map.of("player", sender.getName())));
            return true;
        }

        if (args[0].startsWith("\"") && args[0].endsWith("\"")) {
            String question = args[0].substring(1, args[0].length() - 1);
            long duration;
            try {
                duration = Long.parseLong(args[1]);
            } catch (NumberFormatException e) {
                sender.sendMessage(smpPlugin.getMessage("command.poll.invalid_time", Map.of("player", sender.getName())));
                return true;
            }
            List<String> options = Arrays.asList(Arrays.copyOfRange(args, 2, args.length));
            manager.startPoll(question, options, player.getName(), duration);
            Bukkit.broadcastMessage("§6Poll §e" + question + " §7(started " + duration + "s)");

            for (Player online : Bukkit.getOnlinePlayers()) {
                online.sendTitle(
                        "§6New Poll!",
                        "§7Use §e/vote §7to vote | " + duration + " Seconds left",
                        10, 60, 10
                );
            }
            return true;
        } else {
            sender.sendMessage(smpPlugin.getMessage("command.poll.usage", Map.of("player", sender.getName())));
            return true;
        }
    }
}
