package me.customenchants.test.Enchantments.CustomEvents;

import me.customenchants.test.CustomEnchants;
import me.customenchants.test.Enchantments.utils.EnchantCreator;
import me.customenchants.test.Enchantments.utils.EnchantmentHandler;
import me.customenchants.test.Enchantments.utils.ParsedEnchant;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.stream.Collectors;

public class LiftBlocks implements Listener {

    private final EnchantCreator.EventTypes id;

    public HashMap<Player, HashMap<ArmorStand, Block>> blockTyp = new HashMap<>();

    public HashMap<Player, HashMap<ArmorStand, Double>> armorstandLocs = new HashMap<>();

    public LiftBlocks(EnchantCreator.EventTypes i) {
        this.id = i;
    }


    public boolean armorSpawn(boolean b, Player damaged, double phi, boolean isSpawn) {
        ItemStack[] armorItems = damaged.getInventory().getArmorContents();
        for (ItemStack armors : armorItems) {
            if (!(armors == null)) {
                if (!(armors.getItemMeta().getLore() == null)) {
                    List<ParsedEnchant> check = EnchantmentHandler.getInstance().checkEnchant(armors.getItemMeta().getLore(), id);
                    if (check.get(0).getB()) {
                        if (!b) {
                            if(!(isSpawn)) {
                                for (ArmorStand a : blockTyp.get(damaged).keySet()) {
                                    a.setHelmet(new ItemStack(blockTyp.get(damaged).get(a).getType()));
                                }
                            }
                            for (ArmorStand a : armorstandLocs.get(damaged).keySet()) {
                                double rad = armorstandLocs.get(damaged).get(a);
                                a.teleport(damaged.getLocation());
                                a.teleport(damaged.getLocation().add(2 * Math.cos(phi + rad), 0, 2 * Math.sin(phi + rad)));
                                damaged.sendMessage(damaged.getLocation().getX() + " " + damaged.getLocation().getZ());
                                damaged.sendMessage(ChatColor.RED + " " + damaged.getLocation().getX()+2*Math.cos(phi+rad) + " " + damaged.getLocation().getZ() + Math.sin(phi + rad));
                                //a.teleport(damaged.getLocation().add(3*Math.cos(phi), 0, 3*Math.sin(phi)));
                            }
                            return true;
                        }
                    }
                }
            }
        }
        for(ArmorStand arm: armorstandLocs.get(damaged).keySet()){
            arm.setHelmet(null);
        }
        return false;
    }


    public void runFunc(Player damaged) {
        new BukkitRunnable() {
            int counter = 0;
            double phi = 0;
            boolean bool = false;
            boolean hell = false;

            public void run() {
                if(!bool) {
                    hell = armorSpawn(bool, damaged, phi, hell);
                }
                phi += Math.PI / 16;
                if ((damaged.isSneaking()) && !(bool) && hell) {
                    for (ArmorStand a : armorstandLocs.get(damaged).keySet()) {
                        //a.teleport(new Location(a.getWorld(), a.getLocation().getX(), a.getLocation().getY(), a.getLocation().getZ()));
                        a.setGravity(true);
                        bool = true;
                        new BukkitRunnable() {
                            int counter = 0;
                            Vector vec = damaged.getLocation().toVector();

                            public void run() {
                                Vector vect = a.getLocation().toVector().subtract(vec).normalize().multiply(1.25);
                                vect.setY(-.2);
                                a.setVelocity(vect);

                                RayTraceResult ray = a.rayTraceBlocks(.5,FluidCollisionMode.SOURCE_ONLY);


                                Collection<Entity> hold = a.getWorld().getNearbyEntities(a.getLocation(), 2, 2, 2);
                                List<Entity> near = hold.stream().collect(Collectors.toList());
                                for (Entity ent : near) {
                                    if (a.getLocation().distance(ent.getLocation()) <= 2 && ent != damaged && !(ent instanceof ArmorStand)) {
                                        ent.setVelocity(ent.getLocation().toVector().subtract(a.getLocation().toVector()).add(new Vector(a.getVelocity().getX(), 3, a.getVelocity().getZ())).normalize().multiply(1.5));
                                        damaged.getWorld().strikeLightningEffect(ent.getLocation().add(new Vector(0, -1, 0)));
                                        damaged.playSound(ent.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 3.0F, 0.5F);
                                        damaged.setFireTicks(10);
                                        damaged.damage(1.5);
                                        ((LivingEntity) ent).damage(2 + (armorstandLocs.get(damaged).keySet().size()) / 8);
                                    }
                                }
                                if (counter == 10 || !(ray==null)) {
                                    cancel();
                                    a.remove();
                                    armorstandLocs.get(damaged).remove(a);
                                    bool = false;
                                }
                                counter++;
                            }
                        }.runTaskTimer(CustomEnchants.getInstance(), 0, 5);
                    }
                }
            }
        }.runTaskTimer(CustomEnchants.getInstance(), 0, 5);


    }


    @EventHandler
    public void onDamageEvent(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) {
            return;
        }
        Player damaged = ((Player) e.getEntity());
        ItemStack[] armorItems = damaged.getInventory().getArmorContents();
        Random ra = new Random();
        int rand_int = ra.nextInt(100);
        for (ItemStack armors : armorItems) {
            if (!(armors == null)) {
                if (!(armors.getItemMeta().getLore() == null)) {
                    List<ParsedEnchant> check = EnchantmentHandler.getInstance().checkEnchant(armors.getItemMeta().getLore(), id);
                    if (!(check.isEmpty())) {
                        if (EnchantmentHandler.getInstance().enchantTypes.get(check.get(0).getS()).getProcChance()*(check.get(0).getI()/2) > rand_int) {
                            Random raf = new Random();
                            int ran = raf.nextInt(10);
                            if (!armorstandLocs.containsKey(damaged)) {
                                armorstandLocs.put(damaged, new HashMap<ArmorStand, Double>());
                                runFunc(damaged);
                            }

                            if(!(blockTyp.containsKey(damaged))){
                                blockTyp.put(damaged,new HashMap<>());
                            }

                            if (check.get(0).getI() >= armorstandLocs.get(damaged).size()) {
                                Random r = new Random();
                                int degree = r.nextInt(180);
                                double rad = (degree) * (Math.PI / 180);
                                Bukkit.broadcastMessage(degree + " " + rad);
                                Location loc = damaged.getLocation().add(2 * Math.cos(rad), 0, 2 * Math.sin(rad));

                                if (!(damaged.getLocation().subtract(0,1,0).getBlock().getType().equals(Material.AIR))) {
                                    ArmorStand armor = (ArmorStand) damaged.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
                                    armor.setHelmet(new ItemStack(damaged.getLocation().subtract(0,1,0).getBlock().getType()));
                                    armor.setVisible(false);
                                    armor.setGravity(false);
                                    armor.setMarker(false);
                                    armorstandLocs.get(damaged).put(armor, rad);

                                    blockTyp.get(damaged).put(armor,loc.getBlock());
                                    Bukkit.broadcastMessage(armorstandLocs.toString() + " " + ChatColor.YELLOW + rad + " " + 2*Math.cos(rad) +  " " + 2*Math.sin(rad));
                                }
                            }
                        }
                    }
                    return;
                }
            }
        }
    }
}
