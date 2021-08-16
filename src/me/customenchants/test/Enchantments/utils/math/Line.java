package me.customenchants.test.Enchantments.utils.math;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Line {
    public static void draw(Player p, LivingEntity ent, Particle l) {
        Location o = p.getEyeLocation().subtract(0.0D, 0.75D, 0.0D);
        Location dir = ent.getEyeLocation();
        Vector eb = o.clone().toVector().subtract(dir.clone().toVector());
        eb.normalize();
        double dest = o.distance(dir) - 1.0D;
        for (int i = 0; i < dest; i++) {
            Location a = dir.add(eb);
            p.spawnParticle(l, a, 0);
        }
    }
}
