package de.winniepat.SMPPlugin;

import de.winniepat.SMPPlugin.blackmarket.*;
import de.winniepat.SMPPlugin.blockelevator.*;
import de.winniepat.SMPPlugin.bloodmoon.*;
import de.winniepat.SMPPlugin.commands.*;
import de.winniepat.SMPPlugin.listeners.*;
import de.winniepat.SMPPlugin.polls.*;
import de.winniepat.SMPPlugin.report.*;
import de.winniepat.SMPPlugin.report.commands.*;
import de.winniepat.SMPPlugin.starter.MoveEvent;
import de.winniepat.SMPPlugin.starter.StartCommand;
import de.winniepat.SMPPlugin.suggestions.*;
import de.winniepat.SMPPlugin.suggestions.commands.*;
import de.winniepat.SMPPlugin.waypoints.*;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.*;
import org.bukkit.configuration.file.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.sql.SQLException;
import java.util.*;

public final class SMPPlugin extends JavaPlugin {
    private static SMPPlugin instance;
    private WaypointsDatabase waypointsDatabase;
    private ReportDatabase reportDatabase;
    private SuggestionManager suggestionManager;
    CodingCommand codingCommand = new CodingCommand(this, this);
    private FileConfiguration messages;
    private BloodmoonManager bloodmoonManager;
    public static Set<UUID> frozenPlayers = new HashSet<>();

    @Override
    public void onEnable() {
        saveDefaultMessages();
        loadMessages();
        instance = this;

        File dbFile = new File(getDataFolder(), "suggestions.db");
        try {
            this.suggestionManager = new SuggestionManager(dbFile);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        registerWaypoints();
        getLogger().info("Waypoints registered");
        registerReportDatabase();
        getLogger().info("Reports registered");
        registerBloodMoon();
        getLogger().info("BloodMoon registered");
        registerCommands();
        getLogger().info("Commands registered");
        registerListeners();
        getLogger().info("Listener registered");
        registerPollsystem();
        getLogger().info("PollSystem registered");
        registerSuggestionDatabase();
        getLogger().info("Suggestion Database registered");
    }

    @Override
    public void onDisable() {
        getLogger().warning("Ach Leck Eier!");
        bloodmoonManager.endBloodmoon();
    }

    private void registerCommands() {
        Objects.requireNonNull(getCommand("trash")).setExecutor(new TrashCommand(this, this));
        Objects.requireNonNull(getCommand("report")).setExecutor(new ReportCommand(reportDatabase, this));
        Objects.requireNonNull(getCommand("reports")).setExecutor(new ReportsCommand(this, reportDatabase, this));
        Objects.requireNonNull(getCommand("suggestion")).setExecutor(new SuggestionCommand(suggestionManager, this, this));
        Objects.requireNonNull(getCommand("suggestions")).setExecutor(new SuggestionListCommand(suggestionManager));
        Objects.requireNonNull(getCommand("suggestionstatus")).setExecutor(new SuggestionStatusCommand(suggestionManager, this));
        Objects.requireNonNull(getCommand("coding")).setExecutor(new CodingCommand(this, this));
        Objects.requireNonNull(getCommand("suggestionsgui")).setExecutor(this::handleGuiCommand);
        Objects.requireNonNull(getCommand("bloodmoon")).setExecutor(new BloodmoonCommand(bloodmoonManager));
        Objects.requireNonNull(getCommand("blackmarket")).setExecutor(new BlackMarketCommand(this));
        Objects.requireNonNull(getCommand("getwaypoint")).setExecutor(new WaypointCommands(waypointsDatabase, this));
        Objects.requireNonNull(getCommand("addwaypoint")).setExecutor(new WaypointCommands(waypointsDatabase, this));
        Objects.requireNonNull(getCommand("smp")).setExecutor(new StartCommand());
        Objects.requireNonNull(getCommand("spawn")).setExecutor(new SpawnCommand());
    }

    private boolean handleGuiCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            new SuggestionGui(suggestionManager, this).open(player, null);
        } else {
            sender.sendMessage("§cOnly players can open the GUI.");
        }
        return true;
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new CodingCleanupListener(codingCommand), this);
        getServer().getPluginManager().registerEvents(new BlackMarketEffectListener(this), this);
        getServer().getPluginManager().registerEvents(new WaypointListener(waypointsDatabase, this), this);
        getServer().getPluginManager().registerEvents(new PlayerJumpListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerSneakListener(), this);
        getServer().getPluginManager().registerEvents(new MoveEvent(), this);
        getServer().getPluginManager().registerEvents(new QuitLightningListener(), this);
        Bukkit.getPluginManager().registerEvents(new SuggestionGui(suggestionManager, this), this);
        Bukkit.getPluginManager().registerEvents(new FirstPlayerJoinTracker(this), this);
    }

    private void registerPollsystem() {
        File pollDataFile = new File(getDataFolder(), "polls.yml");
        PollManager pollManager = new PollManager(pollDataFile);
        if (!pollDataFile.getParentFile().exists()) {
            pollDataFile.getParentFile().mkdirs();
        }
        Objects.requireNonNull(getCommand("pollstart")).setExecutor(new PollCommand(pollManager, this));
        Objects.requireNonNull(getCommand("pollvote")).setExecutor((sender, cmd, label, args) -> {
            if (sender instanceof Player player) {
                new PollVoteGUI(pollManager, this).openVoteGUI(player);
            }
            return true;
        });
        Objects.requireNonNull(getCommand("pollresults")).setExecutor((sender, cmd, label, args) -> {
            if (sender instanceof Player player && player.hasPermission("poll.view")) {
                new PollResultGUI(pollManager).openResultsGUI(player);
            }
            return true;
        });
        getServer().getPluginManager().registerEvents(new PollVoteGUI(pollManager, this), this);
    }

    private void registerSuggestionDatabase() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        File dbFile = new File(getDataFolder(), "suggestions.db");
        try {
            suggestionManager = new SuggestionManager(dbFile);
        } catch (SQLException e) {
            getLogger().severe("Couldn't initialize the Suggestion Database.");
            e.printStackTrace();
            return;
        }
        getLogger().info("Suggestion Database connected successfully");
    }

    private void saveDefaultMessages() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        File file = new File(getDataFolder(), "messages.yml");
        if (!file.exists()) {
            saveResource("messages.yml", false);
        }
    }

    private void loadMessages() {
        File file = new File(getDataFolder(), "messages.yml");
        messages = YamlConfiguration.loadConfiguration(file);

        Bukkit.getLogger().info("[DEBUG] Loaded keys: " + messages.getKeys(true));
    }

    public String getMessage(String key, Map<String, String> placeholders) {
        if (!messages.contains(key)) {
            getLogger().warning("Missing message key: " + key);
            getLogger().warning("Available keys: " + messages.getKeys(true));
            return ChatColor.RED + "Message not found: " + key;
        }

        String msg = messages.getString(key);

        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            msg = msg.replace("%" + entry.getKey() + "%", entry.getValue());
        }

        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    private void checkForElevator(Player player) {
        Block blockBelow = player.getLocation().subtract(0, 0.1, 0).getBlock();
        if (blockBelow.getType() != Material.LIGHT_WEIGHTED_PRESSURE_PLATE) return;

        // Handle sneaking (go down)
        if (player.isSneaking() && !CooldownManager.isOnCooldown(player.getUniqueId())) {
            for (int y = blockBelow.getY() - 1; y >= 0; y--) {
                Block below = player.getWorld().getBlockAt(blockBelow.getX(), y, blockBelow.getZ());
                if (below.getType() == Material.LIGHT_WEIGHTED_PRESSURE_PLATE) {
                    player.teleport(new Location(player.getWorld(), below.getX() + 0.5, y + 1, below.getZ() + 0.5));
                    player.playSound(player.getLocation(), Sound.BLOCK_PISTON_CONTRACT, 1f, 1f);
                    player.getWorld().spawnParticle(Particle.CLOUD, player.getLocation(), 20, 0.3, 0.3, 0.3);
                    CooldownManager.setCooldown(player.getUniqueId());
                    return;
                }
            }
            player.sendMessage(ChatColor.YELLOW + "No elevator block below!");
            CooldownManager.setCooldown(player.getUniqueId());
        }

        if (player.getVelocity().getY() > 0.2 && !CooldownManager.isOnCooldown(player.getUniqueId())) {
            for (int y = blockBelow.getY() + 2; y <= player.getWorld().getMaxHeight(); y++) {
                Block above = player.getWorld().getBlockAt(blockBelow.getX(), y, blockBelow.getZ());
                if (above.getType() == Material.LIGHT_WEIGHTED_PRESSURE_PLATE) {
                    player.teleport(new Location(player.getWorld(), above.getX() + 0.5, y + 1, above.getZ() + 0.5));
                    player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
                    player.getWorld().spawnParticle(Particle.PORTAL, player.getLocation(), 30, 0.5, 1, 0.5);
                    CooldownManager.setCooldown(player.getUniqueId());
                    return;
                }
            }
            player.sendMessage(ChatColor.YELLOW + "No elevator block above!");
            CooldownManager.setCooldown(player.getUniqueId());
        }
    }

    private void registerBloodMoon() {
        this.bloodmoonManager = new BloodmoonManager(this);
        getServer().getPluginManager().registerEvents(new BloodmoonListener(bloodmoonManager, this), this);
        new BloodmoonTask(this, bloodmoonManager).start();

    }

    private void registerWaypoints() {
        reportDatabase = new ReportDatabase(this);
    }

    private void registerReportDatabase() {
        reportDatabase = new ReportDatabase(this);
    }

    public static SMPPlugin getInstance() {
        return instance;
    }

}
