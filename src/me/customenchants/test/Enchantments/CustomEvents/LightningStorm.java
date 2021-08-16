package me.customenchants.test.Enchantments.CustomEvents;

import me.customenchants.test.CustomEnchants;
import me.customenchants.test.Enchantments.utils.EnchantCreator;
import me.customenchants.test.Enchantments.utils.EnchantmentHandler;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Random;

public class LightningStorm implements Listener {

    public EnchantCreator.EventTypes id;

    public LightningStorm(EnchantCreator.EventTypes type) {
        id = type;
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player)) {
            return;
        }
        Player p = (Player) e.getDamager();
        LivingEntity damaged = (LivingEntity) e.getEntity();
        ItemStack item = p.getItemInHand();
        if (!(item.getItemMeta().getLore() == null)) {
            for (String i : item.getItemMeta().getLore()) {
                String[] splitString = EnchantmentHandler.getInstance().ParseEnchant(ChatColor.stripColor(i));
                String enchantName = ChatColor.stripColor(splitString[0]);
                int lvl = Integer.parseInt(ChatColor.stripColor(splitString[1]));
                if (EnchantmentHandler.getInstance().enchantTypes.containsKey(enchantName)) {
                    if (EnchantmentHandler.getInstance().enchantTypes.get(enchantName).getTypeEvent() == id) {
                        Random r = new Random();
                        int value = r.nextInt(20);
                        if (value < lvl) {
                            new BukkitRunnable() {
                                int counter = 0;
                                @Override
                                public void run() {
                                    p.getWorld().strikeLightningEffect(damaged.getLocation().add(new Vector(0, -1, 0)));
                                    if (!(damaged.isDead())) {
                                        damaged.damage(Math.floor(lvl * 2.025));
                                    }
                                    p.playSound(damaged.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 3.0F, 0.5F);
                                    counter++;

                                    if (counter == lvl - 1 || damaged.isDead()) {
                                        cancel();
                                    }
                                }
                            }.runTaskTimer(CustomEnchants.getPlugin(CustomEnchants.class), 0, 20);
                        }
                    }
                }
            }
        }
    }
}
