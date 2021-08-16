package me.customenchants.test.Enchantments.CustomEvents;

import me.customenchants.test.Enchantments.utils.EnchantCreator;
import me.customenchants.test.Enchantments.utils.EnchantmentHandler;
import me.customenchants.test.Enchantments.utils.ParsedEnchant;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class DamageReduction implements Listener {

    private final EnchantCreator.EventTypes id;

    public DamageReduction(EnchantCreator.EventTypes i) {
        this.id = i;
    }

    @EventHandler
    public void reduceDamage(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player)) {
            return;
        }

        LivingEntity damaged = (LivingEntity) e.getEntity();
        ItemStack[] armorItems = ((Player) damaged).getInventory().getArmorContents();
        for (ItemStack armor : armorItems) {
            if (armor != null) {
                if (armor.getType() != Material.AIR) {
                    if (armor.hasItemMeta()) {
                        if (armor.getItemMeta().hasLore()) {
                            List<ParsedEnchant> check = EnchantmentHandler.getInstance().checkEnchant(armor.getItemMeta().getLore(), id);
                            if (!(check.isEmpty())) {
                                e.setDamage(((double)(100 - (check.get(0).getI() * 3))/100) * e.getDamage());
                            }
                        }
                    }
                }
            }
        }
    }

}
