package me.customenchants.test.Enchantments.CustomItems;

import java.util.List;

import me.customenchants.test.CustomEnchants;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.tags.ItemTagType;
import org.bukkit.plugin.Plugin;

public class ChainItem {
    private ItemStack item;

    private Gems gem;

    public CustomEnchants plugin;

    public ChainItem(ItemStack i, Gems g, CustomEnchants p, String name) {
        this.gem = g;
        this.item = i;
        this.plugin = p;
        setName(name);
    }

    public void addLore(String s) {
        ItemMeta itemMeta = getItem().getItemMeta();
        List<String> currentLore = itemMeta.getLore();
        currentLore.add(s);
        itemMeta.setLore(currentLore);
        getItem().setItemMeta(itemMeta);
    }

    public void setName(String s) {
        ItemMeta itemMeta = getItem().getItemMeta();
        itemMeta.setDisplayName(getGem().getColor() + getGem().setName(s));
        getItem().setItemMeta(itemMeta);
    }

    public ItemStack getItem() {
        return this.item;
    }

    public Gems getGem() {
        return this.gem;
    }

    public void setTag() {
        ItemMeta itemMeta = getItem().getItemMeta();
        NamespacedKey key = new NamespacedKey((Plugin)this.plugin, getGem().getType().getS());
        itemMeta.getCustomTagContainer().setCustomTag(key, ItemTagType.STRING, getGem().getName());
        getItem().setItemMeta(itemMeta);
    }

    public boolean isItem(ItemStack i) {
        return i.equals(getItem());
    }
}
