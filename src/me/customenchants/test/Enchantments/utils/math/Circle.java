package me.customenchants.test.Enchantments.utils.math;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;

public class Circle {
    public static void drawCircle(LivingEntity ent, Particle p, double r, double h) {
        double i;
        for (i = 0.0D;i<=2*Math.PI; i += Math.PI/4) {
            Location loc = ent.getLocation().add(r * Math.cos(i), h, r * Math.sin(i));
            ent.getWorld().spawnParticle(p, loc, 0);
        }
    }
}
