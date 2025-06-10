package de.winniepat.SMPPlugin.blackmarket;

import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class BlackMarketTradeHandler {

    public static void applyTrades(Villager v, JavaPlugin plugin) {
        List<MerchantRecipe> recipes = new ArrayList<>();

        for (BlackMarketOffer offer : BlackMarketItems.getRandomOffers(plugin, 3)) {
            MerchantRecipe recipe = new MerchantRecipe(offer.item, 9999);
            recipe.setUses(0);
            recipe.setExperienceReward(false);
            for (ItemStack ingredient : offer.price) {
                recipe.addIngredient(ingredient);
            }
            recipes.add(recipe);
        }

        v.setRecipes(recipes);
    }


}
