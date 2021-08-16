package me.customenchants.test.Enchantments.CustomEvents;

import me.customenchants.test.CustomEnchants;
import me.customenchants.test.Enchantments.utils.EnchantCreator;
import me.customenchants.test.Enchantments.utils.EnchantmentHandler;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class StealHealthEvent implements Listener {

    private final EnchantCreator.EventTypes id;

    public StealHealthEvent(EnchantCreator.EventTypes i) {
        this.id = i;
    }

    @EventHandler
    public void stealHealth(EntityDamageByEntityEvent e) {
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
                        int value = r.nextInt(100);
                        if (value < EnchantmentHandler.getInstance().enchantTypes.get(enchantName).getProcChance()+(lvl*2)) {
                            damaged.damage(lvl*2);
                            if(p.getHealth()+lvl > 20){
                                p.setHealth(20);
                            }else{
                                p.setHealth(p.getHealth()+lvl);
                            }
                            p.damage(lvl);
                            p.spawnParticle(Particle.HEART, damaged.getLocation().getX(), damaged.getLocation().getY()+1, damaged.getLocation().getZ(), 1, 0, 0, 0);
                            new BukkitRunnable() {
                                int counter = 0;
                                Location loc,particlePos1,particlePos2,newPart;
                                @Override
                                public void run() {
                                    if(counter == 3){
                                        cancel();
                                    }
                                    loc = damaged.getLocation();
                                    p.spawnParticle(Particle.HEART,loc.clone().add(0,1.5,0),0);

                                    counter++;
                                }


                            }.runTaskTimer(CustomEnchants.getInstance(), 0, 1);
                        }
                    }
                }
            }
        }
    }
}
