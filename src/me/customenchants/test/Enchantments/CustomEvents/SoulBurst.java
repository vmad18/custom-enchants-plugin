package me.customenchants.test.Enchantments.CustomEvents;

import me.customenchants.test.CustomEnchants;
import me.customenchants.test.Enchantments.utils.EnchantCreator;
import me.customenchants.test.Enchantments.utils.EnchantUtils.POTION_MAKER;
import me.customenchants.test.Enchantments.utils.EnchantmentHandler;
import me.customenchants.test.Enchantments.utils.ParsedEnchant;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class SoulBurst implements Listener {

    private final EnchantCreator.EventTypes id;

    private final HashMap<Player, Integer> hitCount = new HashMap<>();

    private final HashMap<Player, Long> coolDown = new HashMap<>();

    private final ArrayList<Player> soulBurst = new ArrayList<>();

    public SoulBurst(EnchantCreator.EventTypes i) {
        this.id = i;
    }

    private void timer(Player p, POTION_MAKER ec, int count){
        new BukkitRunnable(){
            int counter = 0;
            @Override
            public void run() {
                Bukkit.broadcastMessage(String.valueOf(counter));
                counter++;
                if(counter>=2*count){
                    cancel();
                    soulBurst.remove(p);
                    ApplyPotionEffectsEvent.removeEffects(p,ec);
                    coolDown.put(p, 180000L);
                }
            }
        }.runTaskTimer(CustomEnchants.getInstance(), 0, 1*20);
    }

    private void restoreEffects(Player p){
        for (ItemStack i : p.getInventory().getArmorContents()) {
            if (!(i == null)) {
                if (!(i.getItemMeta().getLore() == null)) {
                    for (String x : i.getItemMeta().getLore()) {
                        String[] splitString = EnchantmentHandler.getInstance().ParseEnchant(ChatColor.stripColor(x));
                        String enchantName = splitString[0];
                        int lvl = Integer.parseInt(splitString[1]);
                        if (EnchantmentHandler.getInstance().enchantTypes.containsKey(enchantName)) {
                            if (EnchantmentHandler.getInstance().enchantTypes.get(enchantName).getTypeEvent().equals(EnchantCreator.EventTypes.POTIONS_EFFECT)) {
                                ApplyPotionEffectsEvent.effectPlayer(p, EnchantmentHandler.getInstance().getObj(enchantName).getPot(), lvl);
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent e){

        if(!(e.getEntity() instanceof Player)) return;

        Player p = (Player) e.getEntity();


        List<ParsedEnchant> enchantList = EnchantmentHandler.getInstance().armor(p.getInventory().getArmorContents(), id);


        if(enchantList == null) return;

        if(!(hitCount.containsKey(p))){
            if(!(coolDown.containsKey(p)))
                hitCount.put(p,0);
            else if(coolDown.get(p)<System.currentTimeMillis()){
                hitCount.put(p,0);
                coolDown.remove(p);
            }
        }else if(10/enchantList.get(0).getI()>=hitCount.get(p) && !(soulBurst.contains(p))){
            EnchantCreator ec = EnchantmentHandler.getInstance().getObj(enchantList.get(0).getS());

            hitCount.put(p,0);

            ApplyPotionEffectsEvent.effectPlayer(p, ec.getPot(), enchantList.get(0).getI()+7);

            soulBurst.add(p);

            timer(p, ec.getPot(), 14+2*enchantList.get(0).getI());

            Location loc = p.getLocation();

            new BukkitRunnable(){
                double phi=0;
                public void run(){

                    double yaw = p.getLocation().getYaw()*Math.PI/180-Math.PI/2;

                    p.getLocation().getWorld().spawnParticle(Particle.LAVA,p.getLocation().clone().add(0,2,0),0);

                    p.getLocation().getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, p.getLocation().clone().add(Math.cos(phi+yaw),1+Math.sin(phi),Math.sin(phi+yaw)),0);

                    p.getLocation().getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, p.getLocation().clone().add(Math.cos(phi+Math.PI+yaw),1+Math.sin(phi),Math.sin(phi+Math.PI+yaw)),0);

                    phi+=Math.PI/16;

                    if(!soulBurst.contains(p)){
                        cancel();
                    }
                }
            }.runTaskTimer(CustomEnchants.getInstance(), 0, 1);


            new BukkitRunnable(){
                int r=1;
                public void run(){
                    for(double i=0;i<2*Math.PI;i+=Math.PI/32){
                        Location loc1 = loc.clone().add((r)*Math.cos(i),1,(r)*Math.sin(i));
                        Location loc2 = loc.clone().add((r+1)*Math.cos(i),1,(r+1)*Math.sin(i));
                        Location loc3 = loc.clone().add((r+2)*Math.cos(i),1,(r+2)*Math.sin(i));
                        Location loc4 = loc.clone().add((r+3)*Math.cos(i),1,(r+3)*Math.sin(i));

                        loc1.getWorld().spawnParticle(Particle.FLAME, loc1, 0);
                        loc1.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, loc2, 0);
                        loc1.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, loc3, 0);
                        loc1.getWorld().spawnParticle(Particle.FLAME, loc4, 0);

                        Collection<Entity> ents = loc1.getWorld().getNearbyEntities(loc4,2,1,2);
                        for(Entity e : ents){
                            if(e.equals(p)) continue;

                            Vector vel = e.getLocation().toVector().clone().subtract(p.getLocation().toVector());
                            vel = vel.normalize().multiply(3).setY(1.5);
                            e.setVelocity(vel);
                        }
                    }
                    r++;
                    if(r==9){
                        for(double i=0;i<2*Math.PI;i+=Math.PI/8){
                            Location loc1 = loc.clone().add((r)*Math.cos(i),1,(r)*Math.sin(i));
                            loc1.getWorld().strikeLightning(loc1.clone().subtract(0,1,0));
                        }
                        cancel();
                    }
                }
            }.runTaskTimer(CustomEnchants.getInstance(), 0, 3);

        }else if(coolDown.get(p) <= System.currentTimeMillis() && !(soulBurst.contains(p))){
            Bukkit.broadcastMessage(String.valueOf(coolDown.get(p)));
            hitCount.put(p,hitCount.get(p)+1);
            Bukkit.broadcastMessage(String.valueOf(hitCount.get(p)));
        }
    }
}
