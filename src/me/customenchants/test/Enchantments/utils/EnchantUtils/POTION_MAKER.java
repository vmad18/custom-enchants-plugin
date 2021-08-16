package me.customenchants.test.Enchantments.utils.EnchantUtils;

import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class POTION_MAKER {
    private final List<PotionEffectType> pot;
    private final List<Integer> amplifier;

    public POTION_MAKER(List<PotionEffectType> p, List<Integer> c){
        this.pot = p;
        this.amplifier = c;
    }

    public List<PotionEffectType> getPot() {
        return pot;
    }

    public List<Integer> getAmplifier() {
        return amplifier;
    }
}
