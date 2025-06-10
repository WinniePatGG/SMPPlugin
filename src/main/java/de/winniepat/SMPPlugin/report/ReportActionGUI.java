package de.winniepat.SMPPlugin.report;

import de.winniepat.SMPPlugin.SMPPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Map;

public class ReportActionGUI {

    private final SMPPlugin smpPlugin;

    public ReportActionGUI(SMPPlugin smpPlugin) {
        this.smpPlugin = smpPlugin;
    }

    public static void open(JavaPlugin plugin, ReportDatabase db, Player p, String reportId, SMPPlugin smpPlugin) {
        Map<String, String> report = db.getReportById(reportId);
        if (report == null) {
            p.sendMessage(smpPlugin.getMessage("command.reportactiongui.not_found", Map.of("player", p.getName())));
            return;
        }

        Inventory gui = Bukkit.createInventory(p, 27, "§eActions for the report #" + reportId);

        ItemStack info = new ItemStack(Material.BOOK);
        ItemMeta im = info.getItemMeta();
        im.setDisplayName("§bReport Details");
        im.setLore(List.of(
                "§7Victim: §f" + report.get("reported"),
                "§7By: §a" + report.get("reporter"),
                "§7Reason: §f" + report.get("reason"),
                "§8ID: " + reportId
        ));
        info.setItemMeta(im);
        gui.setItem(11, info);

        ItemStack delete = new ItemStack(Material.BARRIER);
        ItemMeta dm = delete.getItemMeta();
        dm.setDisplayName("§cReport löschen");
        delete.setItemMeta(dm);
        gui.setItem(13, delete);

        ItemStack ban = new ItemStack(Material.IRON_SWORD);
        ItemMeta bm = ban.getItemMeta();
        bm.setDisplayName("§4Ban Victim");
        bm.setLore(List.of("§7Reason: §cSecurity Ban"));
        ban.setItemMeta(bm);
        gui.setItem(15, ban);

        p.openInventory(gui);

        Bukkit.getPluginManager().registerEvents(new org.bukkit.event.Listener() {
            @EventHandler
            public void onClick(InventoryClickEvent e) {
                if (!e.getView().getTitle().equals("§eActions for report #" + reportId)) return;
                if (!(e.getWhoClicked() instanceof Player clicker)) return;

                ItemStack clicked = e.getCurrentItem();
                if (clicked == null || clicked.getType() == Material.AIR) return;

                if (clicked.getType() == Material.BARRIER) {
                    db.deleteReportById(reportId);
                    clicker.closeInventory();
                    clicker.playSound(clicker.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 0.5f);
                    clicker.sendMessage(smpPlugin.getMessage("reportactiongui.successful_delete", Map.of("player", clicker.getName())));
                    HandlerList.unregisterAll(this);
                }

                if (clicked.getType() == Material.IRON_SWORD) {
                    String target = report.get("reported");
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                            "ban " + target + " Security Ban");
                    clicker.closeInventory();
                    clicker.sendMessage("§cPlayer was permanently banned.");
                    clicker.sendMessage(smpPlugin.getMessage("reportactiongui.successful_ban", Map.of("player", clicker.getName())));
                    HandlerList.unregisterAll(this);
                }
                e.setCancelled(true);
            }
        }, plugin);
    }
}
