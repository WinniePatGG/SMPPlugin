package de.winniepat.SMPPlugin.listeners;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;

public class FirstPlayerJoinTracker implements Listener {

    String longText = "Einst lebten alle friedlich beisammen in der Stadt Lunaris. " +
            "Doch eines Abends, als der Blutmond über die Stadt zog, wurde Lunaris von unzähligen Monstern attackiert und fast vollständig zerstört. " +
            "Die tapferen Bewohner versuchten mit aller Kraft der Attacke standzuhalten – doch es war vergebens. Die Monster waren einfach zu stark, und es waren zu viele. " +
            "Die Stadtbewohner konnten nichts ausrichten. Alles, was blieb, war das Rathaus am Marktplatz und ein paar Überreste der Häuser… " +
            "Doch die Hoffnung besteht, dass die Stadt Lunaris bald wieder in vollem Glanz erstrahlen kann und genauso belebt wird, wie sie es einst war.";

    private final JavaPlugin plugin;
    private final File file;
    private final Gson gson = new Gson();
    private final Set<UUID> joinedPlayers = new HashSet<>();

    public FirstPlayerJoinTracker(JavaPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "players.json");
        load();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        if (joinedPlayers.add(uuid)) {
            ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
            BookMeta bookMeta = (BookMeta) book.getItemMeta();
            bookMeta.setItemName("...");
            bookMeta.setAuthor("Unknown Stranger");
            bookMeta.setPages(splitIntoPages(longText, 250));
            book.setItemMeta(bookMeta);

            event.getPlayer().getInventory().addItem(book);
            save();
        }
    }

    public void load() {
        if (!file.exists()) {
            plugin.getDataFolder().mkdirs();
            save();
            return;
        }

        try (FileReader reader = new FileReader(file)) {
            Set<String> loaded = gson.fromJson(reader, new TypeToken<Set<String>>() {}.getType());
            if (loaded != null) {
                for (String id : loaded) {
                    joinedPlayers.add(UUID.fromString(id));
                }
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to load players.json: " + e.getMessage());
        }
    }

    public void save() {
        try (FileWriter writer = new FileWriter(file)) {
            Set<String> ids = new HashSet<>();
            for (UUID uuid : joinedPlayers) {
                ids.add(uuid.toString());
            }
            gson.toJson(ids, writer);
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to save players.json: " + e.getMessage());
        }
    }

    private static List<String> splitIntoPages(String text, int maxLength) {
        List<String> pages = new ArrayList<>();
        while (text.length() > 0) {
            int end = Math.min(maxLength, text.length());
            pages.add(text.substring(0, end));
            text = text.substring(end);
        }
        return pages;
    }
}
