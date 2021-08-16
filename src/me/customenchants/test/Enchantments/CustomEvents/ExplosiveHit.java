package me.customenchants.test.Enchantments.CustomEvents;

import me.customenchants.test.Enchantments.utils.EnchantCreator;
import me.customenchants.test.Enchantments.utils.EnchantmentHandler;
import me.customenchants.test.Enchantments.utils.ParsedEnchant;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Random;

public class ExplosiveHit implements Listener {

    private final EnchantCreator.EventTypes id;

    public ExplosiveHit(EnchantCreator.EventTypes i){
        this.id = i;
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent e){
        if(!(e.getDamager() instanceof Player)){
            return;
        }

        Player damager = (Player) e.getDamager();
        Entity damaged = e.getEntity();
        ItemStack item = damager.getItemInHand();
        ItemMeta itemMeta = item.getItemMeta();
        if(itemMeta.getLore() != null){
            List<ParsedEnchant> check = EnchantmentHandler.getInstance().checkEnchant(itemMeta.getLore(),id);
            if(!(check.isEmpty())){
                Random r = new Random();
                int rand_num = r.nextInt(100);

                if(rand_num<(EnchantmentHandler.getInstance().enchantTypes.get(check.get(0).getS()).getProcChance()+(check.get(0).getI()*3)) && !(damaged.isDead())) {
                    LivingEntity ent = (LivingEntity) damaged;
                    ent.getWorld().createExplosion(ent.getLocation().add(0,1,0),0,true);
                    ent.damage(check.get(0).getI()*2);
                    Vector vec = damager.getLocation().getDirection();
                    ent.setVelocity(vec.add(new Vector(damager.getLocation().getDirection().getX() / 10, 1, damager.getLocation().getDirection().getZ() / 10)).normalize().multiply(1.5));
                }
            }
        }
    }

}
