package me.customenchants.test.Enchantments.utils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class EnchantmentHandler {
    public static EnchantmentHandler instance;

    public EnchantmentHandler() {
        instance = this;
    }

    public static EnchantmentHandler getInstance() {
        return instance;
    }

    public HashMap<String, EnchantCreator> enchantTypes = new HashMap<>();

    public void registerEnchant(EnchantCreator source) {
        enchantTypes.put(source.getEnchantName(), source);
    }

    public boolean containsEnchant(String str) {
        if (enchantTypes.containsKey(str))
            return true;
        return false;
    }

    public EnchantCreator getEnchant(String str) {
        if (containsEnchant(str))
            return enchantTypes.get(str);
        return null;
    }

    public EnchantCreator getObj(String str) {
        return enchantTypes.get(str);
    }

    public String[] ParseEnchant(String a) {
        return ChatColor.stripColor(a).split(" ");
    }

    public List<ParsedEnchant> checkEnchant(List<String> lore, EnchantCreator.EventTypes id) {
        HashMap<String, Integer> enchantIndicies = new HashMap<>();
        ArrayList<ParsedEnchant> members = new ArrayList<>();
        int lvl;
        for (String str : lore) {
            String[] splitString = EnchantmentHandler.getInstance().ParseEnchant(str);
            String enchantName = splitString[0];
            lvl = Integer.valueOf(splitString[1]);
            if (EnchantmentHandler.getInstance().containsEnchant(enchantName)) {
                if (EnchantmentHandler.getInstance().getObj(enchantName).getTypeEvent().equals(id)) {
                    enchantIndicies.put(enchantName, lvl);
                    members.add(new ParsedEnchant(true, enchantName, lvl));
                }
            }
        }
        return members;
    }

    public List<ParsedEnchant> armor(ItemStack[] armorItems, EnchantCreator.EventTypes id) {
        for (ItemStack armor : armorItems) {
            if (!(armor == null)) {
                if (!(armor.getItemMeta().getLore() == null)) {
                    List<ParsedEnchant> check = EnchantmentHandler.getInstance().checkEnchant(armor.getItemMeta().getLore(), id);
                    if (!(check.isEmpty())) {
                        return check;
                    }
                }
            }
        }
        return null;
    }

    public String addName(EnchantCreator.Rarity rarity, String enchantName, Integer level) {
        return ChatColor.translateAlternateColorCodes('&', rarity.returnColor() + enchantName + " " + level);
    }

    public ItemStack applyEnchant(ItemStack item, Integer level, String name, Player p) {
        if (!(enchantTypes.get(name).getMaterial().contains(item.getType()))) {
            p.sendMessage(ChatColor.DARK_RED + "Hello player, you are unable to add that enchantment!");
            return item;
        }
        if (enchantTypes.containsKey(name)) {
            if (enchantTypes.get(name).getRarity().equals(EnchantCreator.Rarity.MYTHIC)) {
                if (!(item.getItemMeta().getLore() == null)) {
                    for (String str : item.getItemMeta().getLore()) {
                        String enchantName = ParseEnchant(str)[0];
                        if (enchantTypes.get(enchantName).getRarity().equals(EnchantCreator.Rarity.MYTHIC) && !(enchantName.equals(name))) {
                            p.sendMessage(ChatColor.DARK_AQUA + "You can only have one Mythical Enchant");
                            return item;
                        }
                    }
                }
            }
        }

        ItemMeta itemMeta = item.getItemMeta();
        ArrayList<String> lore = new ArrayList();

        if (!(itemMeta.getLore() == null)) {
            lore.addAll(itemMeta.getLore());
            for (String str : lore) {
                String[] splitString = ParseEnchant(str);
                String enchantName = splitString[0];
                Integer lvl = Integer.valueOf(splitString[1]);
                boolean b = name.equals(enchantName);
                if (b && level.equals(lvl)) {
                    lore.set(lore.indexOf(str), (addName(enchantTypes.get(name).getRarity(), enchantTypes.get(name).getEnchantName(), level + 1)));
                    itemMeta.setLore(lore);
                    item.setItemMeta(itemMeta);
                    return item;
                } else if (b && level > lvl) {
                    lore.set(lore.indexOf(str), (addName(enchantTypes.get(name).getRarity(), enchantTypes.get(name).getEnchantName(), level)));
                    itemMeta.setLore(lore);
                    item.setItemMeta(itemMeta);
                    return item;
                } else if (b && level < lvl) {
                    p.sendMessage(ChatColor.DARK_RED + "Levels Can't go Down");
                    return item;
                }

            }
        }


        lore.add(addName(enchantTypes.get(name).getRarity(), enchantTypes.get(name).getEnchantName(), level));
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
}
