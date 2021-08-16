package me.customenchants.test.Enchantments.CustomEvents;

import me.customenchants.test.CustomEnchants;
import me.customenchants.test.Enchantments.utils.EnchantCreator;
import me.customenchants.test.Enchantments.utils.EnchantmentHandler;
import me.customenchants.test.Enchantments.utils.ParsedEnchant;
import me.customenchants.test.Enchantments.utils.EnchantUtils.MOB_CREATOR;
import org.bukkit.ChatColor;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SummonMob implements Listener {
    public final EnchantCreator.EventTypes id;

    public SummonMob(EnchantCreator.EventTypes i) {
        this.id = i;
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent e) {

        if (!(e.getEntity() instanceof Player)) {
            return;
        }

        Entity damager = e.getDamager();
        Player damaged = (Player) e.getEntity();

        List<ParsedEnchant> enchants = EnchantmentHandler.getInstance().armor(damaged.getInventory().getArmorContents(), id);
        if (enchants != null) {
            Random r = new Random();
            int rand_num = r.nextInt(100);
            if (rand_num < (EnchantmentHandler.getInstance().enchantTypes.get(enchants.get(0).getS()).getProcChance() + (enchants.get(0).getI() * 1.25)) && !(damaged.isDead())) {
                MOB_CREATOR mob = EnchantmentHandler.getInstance().enchantTypes.get(enchants.get(0).getS()).getMob();
                //Implement more mobs spawn soon; done for now
                List<Creature> ents = new ArrayList<>();

                for(int i=0;i<mob.getCount();i++){
                    Entity el = damaged.getWorld().spawnEntity(damaged.getLocation().add(0,10,0), mob.getType());
                    if (!(el instanceof Creature)) {
                        return;
                    }
                    ents.add((Creature) el);
                }

                for(Creature cr:ents){
                    cr.attack(damager);
                    cr.setMaxHealth(200);
                    cr.setHealth(200);
                    cr.setTarget((LivingEntity) damager);
                    cr.setCustomName(ChatColor.AQUA + damaged.getDisplayName()+"'s "+ ChatColor.LIGHT_PURPLE + cr.getHealth());
                }

                new BukkitRunnable() {
                    int counter=0;
                    @Override
                    public void run() {
                        for(Creature cr:ents) {
                            cr.setCustomName(ChatColor.AQUA + damaged.getDisplayName() + "'s " + ChatColor.LIGHT_PURPLE + ((int) cr.getHealth()));
                        }
                        if(!(damager.isDead())){
                            for(Creature cr:ents) {
                                cr.setTarget((LivingEntity) damager);
                            }
                        }
                        if(counter>=(19+enchants.get(0).getI()) || damager.isDead()){
                            cancel();
                            for(Creature cr:ents) {
                                cr.remove();
                            }
                        }
                        counter++;
                    }
                }.runTaskTimer(CustomEnchants.getInstance(), 0, 10);

            }
        }

    }
}


