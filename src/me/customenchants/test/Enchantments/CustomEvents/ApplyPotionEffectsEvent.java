package me.customenchants.test.Enchantments.CustomEvents;

import me.customenchants.test.Items.CustomElytra;
import me.customenchants.test.Enchantments.utils.EnchantCreator;
import me.customenchants.test.Enchantments.utils.EnchantmentHandler;
import me.customenchants.test.Enchantments.utils.EnchantUtils.POTION_MAKER;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class ApplyPotionEffectsEvent implements Listener {
    private EnchantCreator.EventTypes id;

    public ApplyPotionEffectsEvent(EnchantCreator.EventTypes type) {
        id = type;
        armors.put("Helmet", Arrays.asList(Material.NETHERITE_HELMET, Material.DIAMOND_HELMET, Material.LEATHER_HELMET,
                Material.IRON_HELMET));
        armors.put("Chestplate", Arrays.asList(Material.NETHERITE_CHESTPLATE, Material.DIAMOND_CHESTPLATE, Material.LEATHER_CHESTPLATE,
                Material.IRON_CHESTPLATE));
        armors.put("Leggings", Arrays.asList(Material.NETHERITE_LEGGINGS, Material.DIAMOND_LEGGINGS, Material.LEATHER_LEGGINGS,
                Material.IRON_LEGGINGS));
        armors.put("Boots", Arrays.asList(Material.NETHERITE_BOOTS, Material.DIAMOND_BOOTS, Material.LEATHER_BOOTS,
                Material.IRON_BOOTS));
    }

    private HashMap<String, List<Material>> armors = new HashMap<>();

    private List<Material> weapons = Arrays.asList(Material.DIAMOND_SWORD, Material.NETHERITE_SWORD, Material.IRON_SWORD, Material.WOODEN_AXE, Material.NETHERITE_AXE, Material.DIAMOND_AXE, Material.IRON_AXE);

    public static void effectPlayer(Player p, POTION_MAKER a, int lvl) {
        for (int i = 0; i < a.getPot().size(); i++) {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&l[+]" + "&7" + a.getPot().get(i).getName()));
            p.addPotionEffect(new PotionEffect(a.getPot().get(i), 99999, (int) Math.floor(a.getAmplifier().get(i) * (1 + .25 * lvl)), false, false, false));
        }
    }

    public static void removeEffects(Player p, POTION_MAKER eff) {
        for (PotionEffectType i : eff.getPot()) {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&l[-]" + "&7" + i.getName()));
            p.removePotionEffect(i);
        }
    }

    @EventHandler
    public void InventorySwitchItem(PlayerItemHeldEvent e) {
        ItemStack new_current = e.getPlayer().getInventory().getItem(e.getNewSlot());
        ItemStack prev = e.getPlayer().getInventory().getItem(e.getPreviousSlot());

        if (!(prev == null)) {
            if (!(prev.getItemMeta().getLore() == null)) {
                for (String i : prev.getItemMeta().getLore()) {
                    String[] splitString = EnchantmentHandler.getInstance().ParseEnchant(ChatColor.stripColor(i));
                    String enchantName = splitString[0];
                    int lvl = Integer.parseInt(splitString[1]);
                    if (EnchantmentHandler.getInstance().enchantTypes.containsKey(enchantName)) {
                        if ((EnchantmentHandler.getInstance().enchantTypes.get(enchantName).getTypeEvent() == id) && !(EnchantmentHandler.getInstance().enchantTypes.get(enchantName).isArmor())) {
                            removeEffects(e.getPlayer(), EnchantmentHandler.getInstance().getObj(enchantName).getPot());
                        }
                    }
                }
            }
        }
        if (!(new_current == null)) {
            if (!(new_current.getItemMeta().getLore() == null)) {
                for (String i : new_current.getItemMeta().getLore()) {
                    String[] splitString = EnchantmentHandler.getInstance().ParseEnchant(ChatColor.stripColor(i));
                    String enchantName = splitString[0];
                    int lvl = Integer.parseInt(splitString[1]);
                    if (EnchantmentHandler.getInstance().enchantTypes.containsKey(enchantName)) {
                        if (EnchantmentHandler.getInstance().enchantTypes.get(enchantName).getTypeEvent() == id && !(EnchantmentHandler.getInstance().enchantTypes.get(enchantName).isArmor() == true)) {
                            effectPlayer(e.getPlayer(), EnchantmentHandler.getInstance().getObj(enchantName).getPot(), lvl);
                            return;
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void InventoryClick(InventoryClickEvent e) {
        List<Integer> bob = Arrays.asList(39, 38, 37, 36);
        Player p = (Player) e.getWhoClicked();
        CustomElytra ely = new CustomElytra();
        if (e.getCurrentItem().equals(ely)) {
            e.setCancelled(true);
            return;
        }

        if ((bob.contains(e.getSlot())) && e.getClick() == ClickType.SHIFT_LEFT) {
            Bukkit.broadcastMessage("SHIFTED");
            for (String arm : armors.keySet()) {
                Bukkit.broadcastMessage("SHIFTED2");
                if ((armors.get(arm).contains(e.getCursor().getType()))) {
                    if ((e.getCurrentItem().getType() == Material.AIR)) {
                        if (!(e.getCursor().getItemMeta().getLore() == null)) {
                            for (String i : e.getCursor().getItemMeta().getLore()) {
                                String[] splitString = EnchantmentHandler.getInstance().ParseEnchant(ChatColor.stripColor(i));
                                String enchantName = splitString[0];
                                int lvl = Integer.parseInt(splitString[1]);
                                if ((EnchantmentHandler.getInstance().enchantTypes.containsKey(enchantName))) {
                                    if (EnchantmentHandler.getInstance().enchantTypes.get(enchantName).getTypeEvent().equals(id)) {
                                        effectPlayer(p, EnchantmentHandler.getInstance().getObj(enchantName).getPot(), lvl);
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (bob.contains(e.getSlot())) {
            if (!(e.getCursor().getType() == Material.AIR)) {
                if (!(e.getCursor().getItemMeta().getLore() == null)) {
                    for (String i : e.getCursor().getItemMeta().getLore()) {
                        String[] splitString = EnchantmentHandler.getInstance().ParseEnchant(ChatColor.stripColor(i));
                        String enchantName = splitString[0];
                        int lvl = Integer.parseInt(splitString[1]);
                        if ((EnchantmentHandler.getInstance().containsEnchant(enchantName))) {
                            if (EnchantmentHandler.getInstance().getObj(enchantName).getTypeEvent().equals(id)) {
                                Bukkit.broadcastMessage("THIS One 2");
                                effectPlayer(p, EnchantmentHandler.getInstance().getObj(enchantName).getPot(), lvl);
                                return;
                            }
                        }
                    }
                }
            }
        } else {
            if (!(e.getCursor().getType() == Material.AIR)) {
                if (!(e.getCursor().getItemMeta().getLore() == null)) {
                    for (String i : e.getCursor().getItemMeta().getLore()) {
                        String[] splitString = EnchantmentHandler.getInstance().ParseEnchant(ChatColor.stripColor(i));
                        String enchantName = splitString[0];
                        int lvl = Integer.parseInt(splitString[1]);
                        if ((EnchantmentHandler.getInstance().containsEnchant(enchantName))) {
                            if (EnchantmentHandler.getInstance().getObj(enchantName).getTypeEvent().equals(id)) {
                                if (!(EnchantmentHandler.getInstance().getObj(enchantName).isArmor())) {
                                    effectPlayer(p, EnchantmentHandler.getInstance().getObj(enchantName).getPot(), lvl);
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }

        if (bob.contains(e.getSlot()) && e.getClick() == ClickType.SHIFT_LEFT) {
            for (String arm : armors.keySet()) {
                if ((armors.get(arm).contains(e.getCurrentItem().getType()))) {
                    if (!(e.getCurrentItem().getType() == Material.AIR)) {
                        if (!(e.getCurrentItem().getItemMeta().getLore() == null)) {
                            for (String i : e.getCurrentItem().getItemMeta().getLore()) {
                                String[] splitString = EnchantmentHandler.getInstance().ParseEnchant(ChatColor.stripColor(i));
                                String enchantName = splitString[0];
                                int lvl = Integer.parseInt(splitString[1]);
                                if ((EnchantmentHandler.getInstance().containsEnchant(enchantName))) {
                                    if (EnchantmentHandler.getInstance().getObj(enchantName).getTypeEvent().equals(id)) {
                                        Bukkit.broadcastMessage("THIS One 1");
                                        removeEffects(p, EnchantmentHandler.getInstance().getObj(enchantName).getPot());
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else if (!(bob.contains(e.getSlot()) && e.getClick() == ClickType.SHIFT_LEFT)) {
            if (!(e.getCurrentItem().getType() == Material.AIR)) {
                if (!(e.getCurrentItem().getItemMeta().getLore() == null)) {
                    for (String i : e.getCurrentItem().getItemMeta().getLore()) {
                        String[] splitString = EnchantmentHandler.getInstance().ParseEnchant(ChatColor.stripColor(i));
                        String enchantName = splitString[0];
                        int lvl = Integer.parseInt(splitString[1]);
                        if ((EnchantmentHandler.getInstance().containsEnchant(enchantName))) {
                            if (EnchantmentHandler.getInstance().getObj(enchantName).getTypeEvent().equals(id)) {
                                if (!(EnchantmentHandler.getInstance().getObj(enchantName).isArmor())) {
                                    removeEffects(p, EnchantmentHandler.getInstance().getObj(enchantName).getPot());
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }


        if (bob.contains(e.getSlot())) {
            if (!(e.getCurrentItem().getType() == Material.AIR)) {
                if (!(e.getCurrentItem().getItemMeta().getLore() == null)) {
                    for (String i : e.getCurrentItem().getItemMeta().getLore()) {
                        String[] splitString = EnchantmentHandler.getInstance().ParseEnchant(ChatColor.stripColor(i));
                        String enchantName = splitString[0];
                        int lvl = Integer.parseInt(splitString[1]);
                        if ((EnchantmentHandler.getInstance().containsEnchant(enchantName))) {
                            if (EnchantmentHandler.getInstance().getObj(enchantName).getTypeEvent().equals(id)) {
                                Bukkit.broadcastMessage("THIS One 51");
                                removeEffects(p, EnchantmentHandler.getInstance().getObj(enchantName).getPot());
                                return;
                            }
                        }
                    }
                }
            }
        } else {
            if (!(e.getCurrentItem().getType() == Material.AIR)) {
                if (!(e.getCurrentItem().getItemMeta().getLore() == null)) {
                    for (String i : e.getCurrentItem().getItemMeta().getLore()) {
                        String[] splitString = EnchantmentHandler.getInstance().ParseEnchant(ChatColor.stripColor(i));
                        String enchantName = splitString[0];
                        int lvl = Integer.parseInt(splitString[1]);
                        if ((EnchantmentHandler.getInstance().containsEnchant(enchantName))) {
                            if (EnchantmentHandler.getInstance().getObj(enchantName).getTypeEvent().equals(id)) {
                                if (!(EnchantmentHandler.getInstance().getObj(enchantName).isArmor())) {
                                    removeEffects(p, EnchantmentHandler.getInstance().getObj(enchantName).getPot());
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        for (ItemStack i : p.getInventory().getArmorContents()) {
            if (!(i == null)) {
                if (!(i.getItemMeta().getLore() == null)) {
                    for (String x : i.getItemMeta().getLore()) {
                        String[] splitString = EnchantmentHandler.getInstance().ParseEnchant(ChatColor.stripColor(x));
                        String enchantName = splitString[0];
                        int lvl = Integer.parseInt(splitString[1]);
                        if (EnchantmentHandler.getInstance().enchantTypes.containsKey(enchantName)) {
                            if (EnchantmentHandler.getInstance().enchantTypes.get(enchantName).getTypeEvent().equals(id)) {
                                effectPlayer(p, EnchantmentHandler.getInstance().getObj(enchantName).getPot(), lvl);
                            }
                        }
                    }
                }
            }
        }
        return;
    }

    @EventHandler
    public void onInteractEvent(PlayerInteractEvent e) {
        Player p = e.getPlayer();

        CustomElytra ely = new CustomElytra();
        if (Objects.equals(e.getItem(), ely)) {
            e.setCancelled(true);
            return;
        }

        if ((e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            ItemStack item = e.getItem();
            ItemStack[] ffsList = p.getInventory().getArmorContents();
            for (ItemStack item2 : ffsList) {
                if (!(item2 == null)) {
                    for (String arm : armors.keySet()) {
                        if (!(item2.getType() == Material.AIR)) {
                            if (armors.get(arm).contains(item2.getType()) && armors.get(arm).contains(item.getType()) && !(weapons.contains(item.getType()))) {
                                return;
                            }
                        }
                    }
                }
            }
            if (item != null) {
                if (item.getItemMeta().getLore() != null) {
                    for (String str : item.getItemMeta().getLore()) {
                        String[] splitString = EnchantmentHandler.getInstance().ParseEnchant(ChatColor.stripColor(str));
                        String enchantName = splitString[0];
                        int lvl = Integer.parseInt(splitString[1]);
                        if (EnchantmentHandler.getInstance().enchantTypes.containsKey(enchantName)) {
                            if (EnchantmentHandler.getInstance().enchantTypes.get(enchantName).getTypeEvent() == id && EnchantmentHandler.getInstance().enchantTypes.get(enchantName).isArmor()) {
                                effectPlayer(p, EnchantmentHandler.getInstance().getObj(enchantName).getPot(), lvl);
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent e) {
        Player p = e.getPlayer();
        ItemStack item = (ItemStack) e.getItemDrop();
        if (item.getItemMeta() != null) {
            if (item.getItemMeta().getLore() != null) {
                for (String str : item.getItemMeta().getLore()) {
                    String[] splitString = EnchantmentHandler.getInstance().ParseEnchant(ChatColor.stripColor(str));
                    String enchantName = splitString[0];
                    int lvl = Integer.parseInt(splitString[1]);
                    if (EnchantmentHandler.getInstance().enchantTypes.containsKey(enchantName)) {
                        if (EnchantmentHandler.getInstance().enchantTypes.get(enchantName).getTypeEvent().equals(id)) {
                            removeEffects(p, EnchantmentHandler.getInstance().getObj(enchantName).getPot());
                            return;
                        }
                    }
                }
            }
        }
    }
}