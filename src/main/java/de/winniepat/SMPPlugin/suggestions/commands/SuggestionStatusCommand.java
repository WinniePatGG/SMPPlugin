package de.winniepat.SMPPlugin.suggestions.commands;

import de.winniepat.SMPPlugin.SMPPlugin;
import de.winniepat.SMPPlugin.suggestions.Suggestion;
import de.winniepat.SMPPlugin.suggestions.SuggestionManager;
import de.winniepat.SMPPlugin.suggestions.SuggestionStatus;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class SuggestionStatusCommand implements CommandExecutor {

    private final SuggestionManager manager;
    private final SMPPlugin smpPlugin;

    public SuggestionStatusCommand(SuggestionManager manager, SMPPlugin smpPlugin) {
        this.smpPlugin = smpPlugin;
        this.manager = manager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(smpPlugin.getMessage("command.suggestionstatus.usage", Map.of("player", sender.getName())));
            return true;
        }

        SuggestionStatus status;
        switch (args[0].toLowerCase()) {
            case "accept" -> status = SuggestionStatus.ACCEPTED;
            case "reject" -> status = SuggestionStatus.REJECTED;
            case "review" -> status = SuggestionStatus.REVIEW;
            default -> {
                sender.sendMessage("§cInvalid Status: " + args[0]);
                return true;
            }
        }

        try {
            int id = Integer.parseInt(args[1]);
            Suggestion suggestion = manager.getSuggestionById(id);
            if (suggestion == null) {
                sender.sendMessage("§cNo suggestion found with ID " + id);
                return true;
            }
            manager.updateStatus(id, status);
            sender.sendMessage("§aSuggestion #" + id + " was set to " + status);

            Player player = Bukkit.getPlayer(suggestion.getPlayerUuid());
            if (player != null && player.isOnline()) {
                player.sendMessage("§aYour suggestion  (#" + id + ") was set to §e" + status);
            }

        } catch (NumberFormatException e) {
            sender.sendMessage("§cInvalid ID: " + args[1]);
        }
        return true;
    }
}