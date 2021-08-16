package me.customenchants.test.Enchantments.CustomEvents;

import me.customenchants.test.CustomEnchants;
import me.customenchants.test.Enchantments.utils.EnchantCreator;
import me.customenchants.test.Enchantments.utils.EnchantmentHandler;
import me.customenchants.test.Enchantments.utils.ParsedEnchant;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Random;


public class ArrowBarrage implements Listener {


    private final EnchantCreator.EventTypes id;

    public ArrowBarrage(EnchantCreator.EventTypes i) {
        this.id = i;
    }

    public void arrowThing(Player p, double x, double y, double z, int i) {
        Arrow arr = p.launchProjectile(Arrow.class);
        Vector dir = p.getLocation().getDirection();

        dir.setX(Math.cos(i));

        //Tan of Pitch is vector
        dir.setY(0);
        dir.setZ(Math.sin(i));
        arr.setVelocity(dir.normalize().multiply(.85));
        arr.setDamage(7);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (arr.isDead() || arr.isInBlock() || arr.isOnGround()) {
                    cancel();
                    arr.remove();
                }
            }
        }.runTaskTimer(CustomEnchants.getInstance(), 0, 0);
    }

    @EventHandler
    public void onDamageEvent(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) {
            return;
        }

        if (e.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            return;
        }

        Player p = (Player) e.getEntity();
        int radius = 6;
        int circumfrence = (int) (2 * radius * Math.PI);

        Location loc = p.getLocation();
        ItemStack[] armorItems = p.getInventory().getArmorContents();

        Random r = new Random();
        int rand_int = r.nextInt(100);

        for (ItemStack armor : armorItems) {
            if (!(armor == null)) {
                if (!(armor.getItemMeta().getLore() == null)) {
                    List<ParsedEnchant> check = EnchantmentHandler.getInstance().checkEnchant(armor.getItemMeta().getLore(), id);
                    if (!(check.isEmpty())) {
                        if (EnchantmentHandler.getInstance().enchantTypes.get(check.get(0).getS()).getProcChance() > rand_int) {
                            for (int i = 0; i <= circumfrence / 2; i++) {
                                Location lec = new Location(p.getWorld(), loc.getX() + radius * Math.cos(i), loc.getY(), loc.getZ() + radius * Math.sin(i), p.getLocation().getYaw(), p.getLocation().getPitch());
                                arrowThing(p, lec.getX(), lec.getY(), lec.getZ(), i);
                                p.playSound(p.getLocation(), Sound.BLOCK_ANCIENT_DEBRIS_FALL, 2F, 0.5F);
                            }
                        }
                    }
                }
            }
        }
    }
}
