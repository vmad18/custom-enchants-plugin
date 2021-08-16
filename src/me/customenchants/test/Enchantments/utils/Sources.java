package me.customenchants.test.Enchantments.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import me.customenchants.test.Enchantments.CustomItems.ChainItem;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class Sources {
    private final List<Arrow> arrows = new ArrayList<>();

    private final List<ChainItem> chains = new ArrayList<>();

    private final HashMap<LivingEntity, Player> chained = new HashMap<>();

    private final HashMap<UUID, Long> hasTouch = new HashMap<>();

    //Arrow Items
    public void addArrowItem(Arrow i) {
        this.arrows.add(i);
    }

    public void removeArrowItem(Arrow i) {
        if (hasArrowItem(i))
            this.arrows.remove(i);
    }

    public boolean hasArrowItem(Arrow i) {
        return this.arrows.contains(i);
    }

    //Chain Items Getters and Setters
    public void addChainItems(ChainItem c) {
        this.chains.add(c);
    }

    public void removeChainItems(ChainItem c) {
        if (hasChainItems(c))
            this.chains.remove(c);
    }

    public boolean hasChainItems(ChainItem c) {
        return this.chains.contains(c);
    }

    public List<ChainItem> getChains() {
        return this.chains;
    }

    //Chained Players Getters and Setters
    public void addChained(LivingEntity ent, Player pl) {
        this.chained.put(ent, pl);
    }

    public void removeChained(LivingEntity p) { if (isChained(p)) this.chained.remove(p);}

    public boolean isChained(LivingEntity p) {
        return this.chained.containsKey(p);
    }

    //God's Touch Getters and Setters
    public void putTouch(UUID uuid,Long l){
        hasTouch.put(uuid,l);
    }

    public void removeTouch(UUID uuid){
        if(hasTouch(uuid))
            hasTouch.remove(uuid);
    }

    public boolean hasTouch(UUID uuid){
        return hasTouch.containsKey(uuid);
    }
}
