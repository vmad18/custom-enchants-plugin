package me.customenchants.test.Enchantments.utils;

import me.customenchants.test.Enchantments.utils.EnchantUtils.MOB_CREATOR;
import me.customenchants.test.Enchantments.utils.EnchantUtils.POTION_MAKER;
import net.minecraft.server.v1_16_R2.ParticleType;
import org.bukkit.Material;
import java.util.List;
import java.util.Map;

public class EnchantCreator {
    public enum EventTypes {
        POTIONS_EFFECT,
        LIGHTNING_STORM,
        ARROW_DEFENSE,
        ICE_THROW,
        DOUBLE,
        ELYTRA_FLY,
        STEAL_HEALTH,
        DAMAGE_REDUCE,
        EFFECT_ENEMY,
        ANTI_POTION,
        GODS_TOUCH,
        FIENDS_GRIP,
        SOUL_BURST,
        EXPLOSIVE_HIT,
        MOB_SPAWN,
        BLOCK_LIFT,
        CHAIN_PLAYER
    }

    public enum Rarity {
        GOD_LIKE("&b&l"),
        MYTHIC("&d"),
        LEGENDARY("&6"),
        RARE("&9");
        private final String id;
        Rarity(String s) {
            this.id = s;
        }
        public String returnColor() {
            return id;
        }
    }

    private final String enchantName;
    private final String loreName;
    private final EventTypes typeEvent;
    private final List<Material> material;
    private final boolean isArmor;
    private final Rarity rarity;
    private final int procChance;
    private final List<Integer> durations;
    private final int coolDown;
    private final MOB_CREATOR mob;
    private final POTION_MAKER pot;
    private Map<ParticleType,Integer> potionEffect;

    public EnchantCreator(EventTypes event, List<Material> mat, boolean armor, String name, String lore, POTION_MAKER potMake, Rarity rare, Integer chance, List<Integer> dur, int cd, MOB_CREATOR m) {
        this.enchantName = name;
        this.loreName = lore;
        this.typeEvent = event;
        this.material = mat;
        this.isArmor = armor;
        this.rarity = rare;
        this.procChance = chance;
        this.durations = dur;
        this.coolDown = cd;
        this.mob = m;
        this.pot = potMake;
        //this.potionEffect = particle;
    }

    public String getEnchantName() {
        return enchantName;
    }
    public String getLoreName() {
        return loreName;
    }
    public EventTypes getTypeEvent() { return typeEvent; }
    public List<Material> getMaterial() {
        return material;
    }
    public Rarity getRarity() {
        return rarity;
    }
    public boolean isArmor() { return isArmor; }
    public Integer getProcChance() { return procChance; }
    public List<Integer> getDurations() { return durations; }
    public int getCoolDown() {
        return coolDown;
    }
    public POTION_MAKER getPot() { return pot; }
    public MOB_CREATOR getMob() {
        return mob;
    }
}