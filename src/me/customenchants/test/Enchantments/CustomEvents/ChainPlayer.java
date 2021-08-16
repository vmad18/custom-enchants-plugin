package me.customenchants.test.Enchantments.CustomEvents;

import me.customenchants.test.Enchantments.utils.Sources;
import me.customenchants.test.CustomEnchants;
import me.customenchants.test.Enchantments.CustomItems.ChainItem;
import me.customenchants.test.Enchantments.utils.EnchantCreator;
import me.customenchants.test.Enchantments.utils.math.Circle;
import me.customenchants.test.Enchantments.utils.math.Line;
import net.minecraft.server.v1_16_R2.PacketPlayOutEntityDestroy;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.Optional;

public class ChainPlayer implements Listener {

    public final EnchantCreator.EventTypes id;

    public CustomEnchants plugin;

    public Sources source;

    public ChainPlayer(EnchantCreator.EventTypes i, CustomEnchants p, Sources s) {
        id = i;
        plugin = p;
        source = s;
    }


    @EventHandler
    public void bowHit(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Arrow))
            return;
        Arrow arr = (Arrow) e.getDamager();
        if (!this.source.hasArrowItem(arr))
            return;
        if (!(e.getEntity() instanceof LivingEntity) || e.getEntity() instanceof ArmorStand)
            return;
        if (this.source.isChained((LivingEntity) e.getEntity())) {
            e.setCancelled(true);
            return;
        }
        final LivingEntity ent = (LivingEntity) e.getEntity();
        final Player p = (Player) ((Arrow) e.getDamager()).getShooter();
        double dist = e.getDamager().getLocation().distance(ent.getLocation());
        Vector vect = p.getLocation().clone().toVector().subtract(ent.getLocation().toVector()).normalize().multiply(5);
        vect.setY(vect.getY() + 1.0D + Math.abs(p.getLocation().getY() - ent.getLocation().getY()));
        ent.setVelocity(vect);
        source.removeArrowItem(arr);
        source.addChained(ent, p);
        new BukkitRunnable() {
            public void run() {
                chainUp(p, ent);
                cancel();
            }
        }.runTaskTimer(plugin, 5L, 0L);
    }

    public void chainUp(final Player p, final LivingEntity subject) {
        Vector dirLoc = p.getLocation().getDirection();
        Vector eyeLoc = p.getEyeLocation().toVector();
        final LivingEntity finalSubject = subject;
        playParticle(subject);
        (new BukkitRunnable() {
            int c;

            public void run() {
                Line.draw(p, subject, Particle.CRIT_MAGIC);
                if (p.getLocation().distance(finalSubject.getLocation()) > 8.0D && p.getLocation().distance(finalSubject.getLocation()) <= 15.0D) {
                    finalSubject.setVelocity(p.getLocation().toVector().subtract(finalSubject.getLocation().toVector()).normalize().multiply(p.getLocation().distance(finalSubject.getLocation()) * 7.0D / 25.0D));
                } else if (p.getLocation().distance(finalSubject.getLocation()) > 15.0D) {
                    finalSubject.teleport(p.getLocation().subtract(p.getLocation().getDirection().normalize().multiply(2).setY(0)));
                }
                this.c++;
                if (this.c >= 720 || subject.isDead() || subject.getHealth() <= 0.0D) {
                    cancel();
                    source.removeChained(subject);
                }
            }
        }).runTaskTimer(plugin, 0L, 2L);
    }

    public void playParticle(final LivingEntity ent) {
        (new BukkitRunnable() {
            double phi;

            double h;

            public void run() {
                Circle.drawCircle(ent, Particle.CRIT_MAGIC, 0.5D, 0.5D);
                Circle.drawCircle(ent, Particle.CRIT_MAGIC, 0.5D, 1.0D);
                Circle.drawCircle(ent, Particle.CRIT_MAGIC, 0.5D, 1.5D);
                for (double i = 0.0D; i < this.phi; i += Math.PI / 16) {
                    this.h += 0.05D;
                    Location loc = ent.getLocation().clone().add(Math.cos(i), -1.0D + this.h, Math.sin(i));
                    ent.getWorld().spawnParticle(Particle.FLAME, loc, 0);
                }
                this.h = 0.0D;
                this.phi += Math.PI / 16;
                if (this.phi > 4 * Math.PI || !source.isChained(ent))
                    cancel();
            }
        }).runTaskTimer(plugin, 0L, 0L);
        (new BukkitRunnable() {
            double phi, h;
            double sub = 0.1D;
            double sub2 = 0.026D;
            double r = 0.75D;
            final double adder = 3 * Math.PI / 4;

            public void run() {
                Circle.drawCircle(ent, Particle.CRIT_MAGIC, 0.5D, 0.5D);
                Circle.drawCircle(ent, Particle.CRIT_MAGIC, 0.5D, 1.0D);
                Circle.drawCircle(ent, Particle.CRIT_MAGIC, 0.5D, 1.5D);
                for (int i = 0; i < 3; i++) {
                    Location loc = ent.getLocation().clone().add(this.r * Math.cos(this.phi + adder * i), this.h, this.r * Math.sin(this.phi + adder * i));
                    ent.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, loc, 0);
                }
                this.phi += Math.PI / 16;
                this.h += this.sub;
                this.r -= this.sub2;
                if (this.h > 2.5D) {
                    this.sub *= -1.0D;
                    this.sub2 *= -1.0D;
                } else if (this.h <= 0.0D) {
                    this.sub *= -1.0D;
                    this.sub2 *= -1.0D;
                }
                if (ent.isDead() || !source.isChained(ent))
                    cancel();
            }
        }).runTaskTimer(plugin, 60L, 2L);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        final Player p = e.getPlayer();
        if (!this.source.getChains().stream().anyMatch(chainItem -> chainItem.getItem().equals(e.getItem())))
            return;
        Optional<ChainItem> item = this.source.getChains().stream().filter(chainItem -> chainItem.getItem().equals(e.getItem())).findFirst();
        ChainItem item1 = item.get();

        final ArmorStand arm = (ArmorStand) p.getWorld().spawnEntity(p.getLocation().subtract(0,0,0), EntityType.ARMOR_STAND);

        ArmorStand arm1 = (ArmorStand) p.getWorld().spawnEntity(p.getLocation().subtract(0,0,0), EntityType.ARMOR_STAND);


        arm1.setArms(true);
        arm1.setGravity(false);
        arm1.setBasePlate(false);
        arm1.setRotation(90.0F + p.getLocation().getYaw(), 0.0F);
        EulerAngle a1 = new EulerAngle(((e.getPlayer().getLocation().getPitch())*Math.PI/180), 0, 0);
        EulerAngle b1 = new EulerAngle(Math.PI, 0, 0);
        EulerAngle c1 = new EulerAngle(Math.PI, 0, 0);
        EulerAngle d1 = new EulerAngle((e.getPlayer().getLocation().getPitch()-180) * Math.PI/180, 0, 0);
        EulerAngle e11 = new EulerAngle(0, 0, Math.PI/2);

        arm1.setBasePlate(false);

        arm.setArms(true);
        arm.setGravity(false);
        arm.setItemInHand(new ItemStack(Material.NETHERITE_AXE));
        arm.setBasePlate(false);
        arm.setRotation(90.0F + p.getLocation().getYaw(), 0.0F);
        EulerAngle a = new EulerAngle(((e.getPlayer().getLocation().getPitch())*Math.PI/180), 0, 0);
        EulerAngle b = new EulerAngle(Math.PI, 0, 0);
        EulerAngle c = new EulerAngle(Math.PI, 0, 0);
        EulerAngle d = new EulerAngle((e.getPlayer().getLocation().getPitch()-180) * Math.PI/180, 0, 0);
        EulerAngle e1 = new EulerAngle(0, 0, Math.PI/2);

        arm.setBasePlate(false);
        arm.setRightArmPose(a);
        arm.setRightLegPose(b);
        arm.setLeftLegPose(c);
        arm.setBodyPose(d);
        arm.setLeftArmPose(e1);


        Projectile projectile = e.getPlayer().getWorld().spawn(e.getPlayer().getEyeLocation().add(p.getLocation().getDirection().getX(), p.getLocation().getDirection().getY(), p.getLocation().getDirection().getZ()), Arrow.class);
        final Arrow arr = (Arrow) projectile;
        arr.setVelocity(p.getLocation().getDirection().normalize().multiply(2));
        //arr.setColor(item1.getGem().getColor());
        arr.setDamage(2.0D);
        arr.setShooter(p);


        for(Player w : Bukkit.getServer().getOnlinePlayers()) {
            PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(arr.getEntityId());
            ((CraftPlayer) w).getHandle().playerConnection.sendPacket(packet);
        }

        final Bat bat = p.getWorld().spawn(p.getEyeLocation(), Bat.class);
        bat.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 100000, 100000));
        bat.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100000, 100000));
        bat.setAware(false);
        bat.setInvulnerable(true);
        bat.setAI(false);
        bat.setInvulnerable(true);
        bat.setSilent(true);
        bat.setLeashHolder(arm1);


        BoundingBox box = arm.getBoundingBox();

        Vector vect = p.getLocation().getDirection().normalize().multiply(3);

        this.source.addArrowItem(arr);

        new BukkitRunnable() {
            public void run() {
                bat.teleport(p.getEyeLocation());


                // - (Math.PI/180)*arr.getLocation().getPitch()
                //3*Math.PI/2
                arm.setRightArmPose(new EulerAngle(0.0D, (arr.getLocation().clone().getYaw() + 90) * Math.PI / 180, (arr.getLocation().getPitch()) * Math.PI / 180));

                arm1.setRightArmPose(new EulerAngle(0.0D, (arr.getLocation().getYaw() + 270) * Math.PI / 180, (arr.getLocation().getPitch() - 90) * Math.PI / 180));


                Location loc = arr.getLocation().clone().subtract(0, 0, 0);
                loc.setYaw(arm.getLocation().getYaw());
                loc.setPitch(arm.getLocation().getPitch());
                arm1.teleport(loc);

                //-.35
                Vector orthogonal = arm1.getLocation().getDirection().clone().getCrossProduct(new Vector(0, 1, 0)).normalize().multiply(-2);
                Location l = arm1.getLocation().clone().add(orthogonal);
                arm.teleport(l);


                EulerAngle a = new EulerAngle(((arr.getLocation().getPitch()) * Math.PI / 180), 0, 0);
                EulerAngle b = new EulerAngle(Math.PI, 0, 0);
                EulerAngle c = new EulerAngle(Math.PI, 0, 0);
                EulerAngle d = new EulerAngle((arr.getLocation().getPitch() - 180) * Math.PI / 180, 0, 0);
                EulerAngle e1 = new EulerAngle(0, 0, Math.PI / 2);

                arm.setRightArmPose(a);
                arm.setRightLegPose(b);
                arm.setLeftLegPose(c);
                arm.setBodyPose(d);
                arm.setLeftArmPose(e1);


/*                arm1.setRightArmPose(a);
                arm1.setRightLegPose(b);
                arm1.setLeftLegPose(c);
                arm1.setBodyPose(d);
                arm1.setLeftArmPose(e1);*/


                if (arr.getLocation().distance(p.getLocation()) > 80.0D) {
                    cancel();
                    bat.remove();
                    arr.remove();
                }
                if (arr.isOnGround() || arr.isInBlock() || arr.isDead()) {
                    if (arr.isInBlock() || arr.isOnGround()) {
                        arr.getWorld().spawnParticle(Particle.REDSTONE, arr.getLocation(), 0, new Particle.DustOptions(Color.FUCHSIA, 1.0F));
                        Vector o = p.getLocation().toVector();
                        Vector s = arr.getLocation().toVector();
                        Vector vect = s.clone().subtract(o);
                        if (p.getLocation().distance(arr.getLocation()) > 30.0D) {
                            vect.normalize().multiply(5);
                        } else if (p.getLocation().distance(arr.getLocation()) > 20.0D && p.getLocation().distance(arr.getLocation()) < 30.0D) {
                            vect.normalize().multiply(3.25D);
                        } else {
                            vect.normalize().multiply(2.25D);
                        }
                        p.setVelocity(vect);
                    }
                    cancel();
                    bat.remove();
                    arr.remove();
                }
            }
        }.runTaskTimer(plugin, 0L, 0L);
    }

    public LivingEntity getEnts(ArmorStand arm, Player p, Bat b) {

        for (Entity ent : arm.getNearbyEntities(5, 5, 5)) {
            if (!(ent instanceof LivingEntity)) continue;
            if (ent.equals(p)) continue;
            if (ent instanceof ArmorStand) continue;
            if (ent.equals(b)) {
                Bukkit.broadcastMessage(b.getType().toString());
                continue;
            }
            if (arm.getBoundingBox().overlaps(ent.getBoundingBox())) {
                Bukkit.broadcastMessage(ChatColor.YELLOW + ent.getType().toString());
                return (LivingEntity) ent;
            }
        }
        return null;
    }
}
