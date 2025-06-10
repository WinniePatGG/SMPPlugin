package de.winniepat.SMPPlugin.suggestions;

import de.winniepat.SMPPlugin.SMPPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class SuggestionGui implements Listener {

    private final SuggestionManager manager;
    private final Map<UUID, Integer> pageMap = new HashMap<>();
    private final SMPPlugin smpPlugin;

    public SuggestionGui(SuggestionManager manager, SMPPlugin smpPlugin) {
        this.smpPlugin = smpPlugin;
        this.manager = manager;
    }

    public void open(Player player, SuggestionStatus filter) {
        open(player, filter, pageMap.getOrDefault(player.getUniqueId(), 0));
    }

    public void open(Player player, SuggestionStatus filter, int page) {
        List<Suggestion> suggestions = new ArrayList<>(manager.getAllSuggestions());
        if (filter != null) suggestions.removeIf(s -> s.getStatus() != filter);

        int itemsPerPage = 45;
        int totalPages = (int) Math.ceil(suggestions.size() / (double) itemsPerPage);
        page = Math.max(0, Math.min(page, totalPages - 1));
        pageMap.put(player.getUniqueId(), page);

        Inventory inv = Bukkit.createInventory(null, 54, "§8Suggestions §7Page " + (page + 1));

        int start = page * itemsPerPage;
        int end = Math.min(start + itemsPerPage, suggestions.size());
        List<Suggestion> pageItems = suggestions.subList(start, end);

        int i = 0;
        for (Suggestion s : pageItems) {
            ItemStack paper = new ItemStack(Material.PAPER);
            ItemMeta meta = paper.getItemMeta();
            meta.setDisplayName("§e[#" + s.getId() + "] §f" + s.getTitle());
            List<String> lore = new ArrayList<>();
            lore.add("§7Von: §a" + s.getPlayerName());
            lore.add("§7Status: §b" + s.getStatus().name());
            lore.add("§8" + s.getContent());
            lore.add("§7» §dShift-Leftclick: review");
            lore.add("§7» §aLeftclick: accept");
            lore.add("§7» §cRightclick: deny");
            lore.add("§7» §eShift-Rightclick: delete");
            meta.setLore(lore);
            paper.setItemMeta(meta);
            inv.setItem(i++, paper);
        }

        inv.setItem(45, createControlItem(Material.ARROW, "§7« Bacl", "page:prev"));
        inv.setItem(46, createFilterItem(Material.GREEN_WOOL, "§aAccepted", SuggestionStatus.ACCEPTED));
        inv.setItem(47, createFilterItem(Material.YELLOW_WOOL, "§eReview", SuggestionStatus.REVIEW));
        inv.setItem(48, createFilterItem(Material.RED_WOOL, "§cDenied", SuggestionStatus.REJECTED));
        inv.setItem(49, createFilterItem(Material.GRAY_WOOL, "§7Show All", SuggestionStatus.NONE));
        inv.setItem(53, createControlItem(Material.ARROW, "§7Next Page »", "page:next"));

        player.openInventory(inv);
    }

    private ItemStack createFilterItem(Material mat, String name, SuggestionStatus status) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        if (status != null) meta.setLore(Collections.singletonList("filter:" + status.name()));
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createControlItem(Material mat, String name, String tag) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Collections.singletonList(tag));
        item.setItemMeta(meta);
        return item;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!e.getView().getTitle().startsWith("§8Suggestions")) return;
        e.setCancelled(true);
        ItemStack clicked = e.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta()) return;

        Player p = (Player) e.getWhoClicked();
        ItemMeta meta = clicked.getItemMeta();
        String name = meta.getDisplayName();
        List<String> lore = meta.getLore();

        if (lore == null || lore.isEmpty()) return;
        String tag = lore.get(0);

        if (tag.startsWith("page:")) {
            int currentPage = pageMap.getOrDefault(p.getUniqueId(), 0);
            int newPage = tag.equals("page:next") ? currentPage + 1 : currentPage - 1;
            open(p, null, newPage);
            return;
        }

        if (tag.startsWith("filter:")) {
            String statusName = tag.substring(7);
            SuggestionStatus status = SuggestionStatus.valueOf(statusName);
            open(p, status, 0);
            return;
        }

        if (!name.startsWith("§e[#")) return;
        int id;
        try {
            id = Integer.parseInt(name.split("#")[1].split("]")[0]);
        } catch (Exception ex) {
            p.sendMessage(smpPlugin.getMessage("suggestiongui.processing_error", Map.of("player", p.getName())));
            return;
        }

        Suggestion suggestion = manager.getSuggestionById(id);
        if (suggestion == null) {
            p.sendMessage("§cSuggestion not found.");
            return;
        }

        switch (e.getClick()) {
            case LEFT -> {
                manager.updateStatus(id, SuggestionStatus.ACCEPTED);
                p.sendMessage("§aSuggestion §e#" + id + " §aaccepted.");
            }
            case RIGHT -> {
                manager.updateStatus(id, SuggestionStatus.REJECTED);
                p.sendMessage("§cSuggestion §e#" + id + " §cdenied.");
            }
            case SHIFT_RIGHT -> {
                manager.deleteSuggestion(id);
                p.sendMessage("§eSuggestion §7#" + id + " deleted.");
            }
            case SHIFT_LEFT -> {
                manager.updateStatus(id, SuggestionStatus.REVIEW);
                p.sendMessage("§aSuggestion §e#" + id + " §awill be reviewedd.");
            }
        }

        open(p, null, pageMap.getOrDefault(p.getUniqueId(), 0));
    }
}
