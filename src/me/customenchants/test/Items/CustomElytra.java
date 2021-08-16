package me.customenchants.test.Items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CustomElytra {

    private ItemStack elytra = new ItemStack(Material.ELYTRA);

    public CustomElytra(){
        ItemMeta itemMeta = elytra.getItemMeta();
        itemMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "Armored-Elytra");
        itemMeta.setUnbreakable(true);
        itemMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL,5,true);
        elytra.setItemMeta(itemMeta);
    }

    public ItemStack getElytra(){
        return elytra;
    }
}
