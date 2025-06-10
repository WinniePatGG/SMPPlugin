package de.winniepat.SMPPlugin.blackmarket;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public class BlackMarketOffer {
    public final ItemStack item;
    public final List<ItemStack> price;
    public final String catchId;

    public BlackMarketOffer(ItemStack item, List<ItemStack> price, String catchId) {
        this.item = item;
        this.price = price;
        this.catchId = catchId;
    }
}
