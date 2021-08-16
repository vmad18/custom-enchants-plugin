package me.customenchants.test;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Random;

public class GameSettings implements Listener {

    @EventHandler
    public void EntityHit(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player) && !(e.getDamager() instanceof Arrow)) {
            return;
        }

        Random r = new Random();
        double ra = (r.nextInt(25) / 16.6) + .5;
        final double fa = ((Math.random() * 7) + 1) / ((double) 10);


        if (!(e.getDamager() instanceof Arrow)) {
            Player damager = (Player) e.getDamager();
        } else {
            Arrow damager = (Arrow) e.getDamager();
        }

        LivingEntity damaged = (LivingEntity) e.getEntity();


        if ((damaged instanceof ArmorStand)) {
            e.setCancelled(true);
        }
        Location damageLoc = damaged.getLocation().add(new Vector(fa, ra, fa));
        ArmorStand armor = (ArmorStand) damaged.getWorld().spawnEntity(damageLoc, EntityType.ARMOR_STAND);
        armor.setVisible(false);
        if (!(damaged.isDead())) {
            new BukkitRunnable() {

                int counter = 0;

                @Override
                public void run() {
                    armor.setSmall(true);
                    armor.setMarker(true);
                    armor.setCustomNameVisible(true);
                    armor.setInvulnerable(true);
                    armor.setCustomName(ChatColor.AQUA + Integer.toString((int) e.getDamage()));

                    counter++;
                    try {
                        Thread.sleep(0);

                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    if (counter == 2) {
                        try {
                            Thread.sleep(0);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                        cancel();
                        armor.remove();
                    }

                }

            }.runTaskTimer(CustomEnchants.getPlugin(CustomEnchants.class), 0, 10L);
        }

    }
}
