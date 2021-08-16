package me.customenchants.test.Enchantments.CustomEvents;

import me.customenchants.test.CustomEnchants;
import me.customenchants.test.Enchantments.utils.EnchantCreator;
import me.customenchants.test.Enchantments.utils.math.Line;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class SurroundBlocks implements Listener {
    public CustomEnchants plugin;

    public final EnchantCreator.EventTypes id;

    public List<Location> banBlocks = new ArrayList<>();

    public SurroundBlocks(CustomEnchants pl, EnchantCreator.EventTypes i) {
        this.plugin = pl;
        this.id = i;
    }

    public void setBlockAround(Location b, final LivingEntity ent, Material mat, final Player p) {
        final HashMap<Location, Material> hold = new HashMap<>();
        hold.put(ent.getLocation().clone().subtract(0.0D, 1.0D, 0.0D), ent.getLocation().clone().subtract(0.0D, 1.0D, 0.0D).getBlock().getType());
        ent.getLocation().clone().subtract(0.0D, 1.0D, 0.0D).getBlock().setType(mat);
        this.banBlocks.add(ent.getLocation().clone().subtract(0.0D, 1.0D, 0.0D));
        Location b1 = ent.getLocation().clone().clone().add(1.0D, 0.0D, 0.0D);
        Location b2 = ent.getLocation().clone().clone().add(0.0D, 0.0D, 1.0D);
        Location b3 = ent.getLocation().clone().clone().add(0.0D, 0.0D, -1.0D);
        Location b4 = ent.getLocation().clone().clone().add(-1.0D, 0.0D, 0.0D);
        Location b5 = b1.clone().add(0.0D, 1.0D, 0.0D);
        Location b6 = b2.clone().add(0.0D, 1.0D, 0.0D);
        Location b7 = b3.clone().add(0.0D, 1.0D, 0.0D);
        Location b8 = b4.clone().add(0.0D, 1.0D, 0.0D);
        Location b9 = ent.getLocation().clone();
        Location b10 = b9.clone().add(0.0D, 1.0D, 0.0D);
        final List<Location> loc = Arrays.asList(new Location[] { b1, b2, b3, b4, b5, b6, b7, b8 });
        loc.stream().map(e -> Boolean.valueOf(this.banBlocks.add(e)));
        hold.put(ent.getLocation().clone().add(0.0D, 2.0D, 0.0D), ent.getLocation().clone().clone().add(0.0D, 2.0D, 0.0D).getBlock().getType());
        ent.getLocation().clone().add(0.0D, 2.0D, 0.0D).getBlock().setType(mat);
        this.banBlocks.add(ent.getLocation().clone().add(0.0D, 2.0D, 0.0D));
        hold.put(b9, b9.getBlock().getType());
        b9.getBlock().setType(Material.AIR);
        hold.put(b10, b10.getBlock().getType());
        b9.getBlock().setType(Material.AIR);
        loc.forEach(l -> {
            hold.put(l, l.getBlock().getType());
            l.getBlock().setType(mat);
        });
        (new BukkitRunnable() {
            int c = 0;

            public void run() {
                this.c++;
                Line.draw(p,ent,Particle.FLAME);
                if (this.c > 240) {
                    cancel();
                    hold.keySet().forEach(y -> y.getBlock().setType((Material)hold.get(y)));
                    loc.stream().map(e -> Boolean.valueOf(SurroundBlocks.this.banBlocks.remove(e)));
                }
            }
        }).runTaskTimer(plugin, 0L, 2L);
    }

    @EventHandler
    public void entityAttacc(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player))
            return;
        final Player p = (Player)e.getEntity();
        final LivingEntity ent = (LivingEntity)e.getDamager();
        (new BukkitRunnable() {
            int counter = 0;

            public void run() {
                ent.setVelocity((new Vector(0, 2, 0)).normalize().multiply(2).add(new Vector(p.getLocation().getDirection().subtract(ent.getLocation().getDirection()).getX(), 0.0D, p.getLocation().getDirection().subtract(ent.getLocation().getDirection()).getZ())));
                this.counter++;
                if (this.counter >= 3) {
                    cancel();
                    ent.setVelocity(ent.getVelocity().multiply(0));
                    Location l = ent.getLocation().clone().add(0.0D, -1.0D, 0.0D);
                    ent.teleport(l.clone().getBlock().getLocation().clone().add(0.5D, 0.0D, 0.5D));
                    SurroundBlocks.this.setBlockAround(l, ent, Material.IRON_BLOCK, p);
                }
            }
        }).runTaskTimer(plugin, 0L, 2L);
    }
}
