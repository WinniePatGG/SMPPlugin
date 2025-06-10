package de.winniepat.SMPPlugin.report.commands;

import de.winniepat.SMPPlugin.SMPPlugin;
import de.winniepat.SMPPlugin.report.ReportDatabase;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class ReportCommand implements CommandExecutor {

    private final ReportDatabase db;
    private final SMPPlugin plugin;

    public ReportCommand(ReportDatabase db, SMPPlugin plugin) {
        this.db = db;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String @NotNull [] args) {
        if (!(sender instanceof Player p)) {
            return true;
        }

        if (args.length < 2) {
            p.sendMessage(plugin.getMessage("command.report.usage", Map.of("player", sender.getName())));
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null || target == p) {
            sender.sendMessage(plugin.getMessage("command.report.invalid_player", Map.of("player", sender.getName())));
            return true;
        }

        String reason = String.join(" ", args).substring(args[0].length()).trim();
        if (reason.length() < 4) {
            sender.sendMessage(plugin.getMessage("command.report.reason", Map.of("player", sender.getName())));
            return true;
        }

        db.insertReport(p.getName(), target.getName(), reason);
        sender.sendMessage(plugin.getMessage("command.report.success", Map.of("player", sender.getName())));
        return true;
    }
}
