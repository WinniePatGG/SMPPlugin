package de.winniepat.SMPPlugin.suggestions.commands;

import de.winniepat.SMPPlugin.suggestions.Suggestion;
import de.winniepat.SMPPlugin.suggestions.SuggestionManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class SuggestionListCommand implements CommandExecutor {

    private final SuggestionManager manager;

    public SuggestionListCommand(SuggestionManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
        for (Suggestion suggestion : manager.getAllSuggestions()) {
            sender.sendMessage("§7[#" + suggestion.getId() + "] §e" + suggestion.getTitle() +
                    " §8von §6" + suggestion.getPlayerName() + " §8(§f" + suggestion.getStatus() + "§8)");
        }
        return true;
    }
}
