package me.customenchants.test.Enchantments.CustomEvents;

import me.customenchants.test.Enchantments.utils.EnchantCreator;
import me.customenchants.test.Enchantments.utils.EnchantmentHandler;
import me.customenchants.test.Enchantments.utils.EnchantUtils.POTION_MAKER;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class EffectEnemyEvent implements Listener {

    private final EnchantCreator.EventTypes id;

    private final EnchantCreator.EventTypes id2;

    private HashMap<UUID, Long> coolDown = new HashMap<>();
    private HashMap<UUID, Long> enchantEnable = new HashMap<>();


    public EffectEnemyEvent(EnchantCreator.EventTypes i, EnchantCreator.EventTypes i2) {
        this.id = i;
        this.id2 = i2;
    }

    public void effectPlayer(LivingEntity ent, POTION_MAKER a, int lvl, List<Integer> duration) {
        boolean[] flag = {false};
        for (int i = 0; i < a.getPot().size(); i++) {
            if (ent instanceof Player) {
                Player p = (Player) ent;
                ItemStack[] armor = p.getInventory().getArmorContents();
                for (ItemStack arm : armor) {
                    if (arm.getItemMeta() != null) {
                        if (arm.getItemMeta().hasLore()) {
                            for (String str : arm.getItemMeta().getLore()) {
                                String[] splitString = EnchantmentHandler.getInstance().ParseEnchant(str);
                                String enchantName = splitString[0];
                                if (EnchantmentHandler.getInstance().containsEnchant(enchantName)) {
                                    if (EnchantmentHandler.getInstance().getObj(enchantName).getTypeEvent().equals(EnchantCreator.EventTypes.ANTI_POTION)) {
                                        if (EnchantmentHandler.getInstance().getObj(enchantName).getPot().getPot().contains(a.getPot())) {
                                            flag[0] = true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (!flag[0]) {
                ent.addPotionEffect(new PotionEffect(a.getPot().get(i), duration.get(i) * 20, (int) Math.floor(a.getAmplifier().get(i) * (1 + .25 * lvl)), false, false, false));
            } else {
                flag[0] = false;
            }
        }
    }

    public void removeEffects(Player p, List<PotionEffectType> eff) {
        for (PotionEffectType i : eff) {
            p.removePotionEffect(i);
        }
    }

    public boolean enemyCheck(LivingEntity ent) {

        if (!(ent instanceof Player)) {
            return false;
        }
        Player p = (Player) ent;
        ItemStack[] armor = p.getInventory().getArmorContents();
        if (!(coolDown.containsKey(p.getUniqueId())) && !(enchantEnable.containsKey(p.getUniqueId()))) {
            for (ItemStack arm : armor) {
                if (arm != null) {
                    if (arm.getType() != Material.AIR) {
                        if (arm.getItemMeta() != null) {
                            if (arm.getItemMeta().hasLore()) {
                                for (String str : arm.getItemMeta().getLore()) {
                                    String[] splitString = EnchantmentHandler.getInstance().ParseEnchant(str);
                                    String enchantName = splitString[0];
                                    int lvl = Integer.valueOf(splitString[1]);
                                    if (EnchantmentHandler.getInstance().containsEnchant(enchantName)) {
                                        if (EnchantmentHandler.getInstance().getObj(enchantName).getTypeEvent() == id2) {
                                            Random rand = new Random();
                                            int rand_int = rand.nextInt(100);
                                            if (rand_int < EnchantmentHandler.getInstance().getObj(enchantName).getProcChance()) {
                                                coolDown.put(p.getUniqueId(), System.currentTimeMillis() + 60000 * EnchantmentHandler.getInstance().getObj(enchantName).getCoolDown());
                                                enchantEnable.put(p.getUniqueId(), Integer.valueOf(String.valueOf(EnchantmentHandler.getInstance().getObj(enchantName).getDurations())) * 1000 + System.currentTimeMillis());
                                                return true;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else if ((coolDown.get(p.getUniqueId()) - System.currentTimeMillis()) <= 0) {
            coolDown.remove(p.getUniqueId());
        }

        return false;
    }

    @EventHandler
    public void playerEffectEnemy(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player)) {
            return;
        }
        Player p = (Player) e.getDamager();
        LivingEntity damaged = (LivingEntity) e.getEntity();
        ItemStack item = p.getItemInHand();
        if (!(item.getItemMeta() == null)) {
            if (item.getItemMeta().hasLore()) {
                for (String str : item.getItemMeta().getLore()) {
                    String[] splitString = EnchantmentHandler.getInstance().ParseEnchant(str);
                    String enchantName = splitString[0];
                    int lvl = Integer.valueOf(splitString[1]);
                    if (EnchantmentHandler.getInstance().containsEnchant(enchantName)) {
                        if (EnchantmentHandler.getInstance().getObj(enchantName).getTypeEvent() == id) {
                            Random r = new Random();
                            int rand_int = r.nextInt(100);
                            if (rand_int < EnchantmentHandler.getInstance().getObj(enchantName).getProcChance()) {
                                enemyCheck(damaged);
                                if (enchantEnable.containsKey(damaged.getUniqueId())) {
                                    if (!(enchantEnable.get(damaged.getUniqueId()) - System.currentTimeMillis() <= 0)) {
                                            return;
                                    }else{
                                        enchantEnable.remove(damaged.getUniqueId());
                                    }
                                }
                                effectPlayer(damaged, EnchantmentHandler.getInstance().getObj(enchantName).getPot(), lvl, EnchantmentHandler.getInstance().getEnchant(enchantName).getDurations());
                            }
                        }
                    }
                }
            }
        }
    }
}
