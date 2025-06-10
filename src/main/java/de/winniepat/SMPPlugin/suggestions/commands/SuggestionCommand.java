package de.winniepat.SMPPlugin.suggestions.commands;

import de.winniepat.SMPPlugin.SMPPlugin;
import de.winniepat.SMPPlugin.suggestions.Suggestion;
import de.winniepat.SMPPlugin.suggestions.SuggestionManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SuggestionCommand implements CommandExecutor {

    private final SuggestionManager manager;
    private final SMPPlugin smpPlugin;
    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private final long COOLDOWN_MILLIS = 60 * 60 * 1000;

    private final File cooldownFile;
    private final YamlConfiguration cooldownData;

    public SuggestionCommand(SuggestionManager manager, Plugin plugin, SMPPlugin smpPlugin) {
        this.manager = manager;
        this.smpPlugin = smpPlugin;

        this.cooldownFile = new File(plugin.getDataFolder(), "suggestion_cooldowns.yml");
        if (!cooldownFile.exists()) {
            try {
                cooldownFile.getParentFile().mkdirs();
                cooldownFile.createNewFile();
                System.out.println("[Suggestion] Created cooldowns.yml at: " + cooldownFile.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        this.cooldownData = YamlConfiguration.loadConfiguration(cooldownFile);

        for (String key : cooldownData.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                long timestamp = cooldownData.getLong(key);
                cooldowns.put(uuid, timestamp);
            } catch (IllegalArgumentException ignored) {}
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(smpPlugin.getMessage("command.error.no_player", Map.of("player", sender.getName())));
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(smpPlugin.getMessage("command.suggestion.usage", Map.of("player", player.getName())));
            return true;
        }

        UUID uuid = player.getUniqueId();
        long now = System.currentTimeMillis();

        if (cooldowns.containsKey(uuid)) {
            long lastUsed = cooldowns.get(uuid);
            long timeLeft = COOLDOWN_MILLIS - (now - lastUsed);
            if (timeLeft > 0) {
                player.sendMessage("§cPlease wait §e" + (timeLeft / 1000) + " seconds§c, before you send another Suggestion.");
                return true;
            }
        }

        String input = String.join(" ", args);
        Matcher matcher = Pattern.compile("^\"([^\"]{1,50})\"\\s+(.{1,300})$").matcher(input);

        if (!matcher.matches()) {
            player.sendMessage(smpPlugin.getMessage("command.suggestion.usage", Map.of("player", player.getName())));
            return true;
        }

        String title = matcher.group(1).trim();
        String content = matcher.group(2).trim();

        Suggestion suggestion = manager.addSuggestion(uuid, player.getName(), title, content);
        if (suggestion != null) {
            player.sendMessage("§aYour Suggestion was submitted with the ID §e#" + suggestion.getId());
            cooldowns.put(uuid, now);
            cooldownData.set(uuid.toString(), now);
            try {
                cooldownData.save(cooldownFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            player.sendMessage(smpPlugin.getMessage("command.suggestion.fail", Map.of("player", player.getName())));
        }

        return true;
    }
}
