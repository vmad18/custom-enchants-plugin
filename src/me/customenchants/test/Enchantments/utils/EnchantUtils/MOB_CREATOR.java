package me.customenchants.test.Enchantments.utils.EnchantUtils;

import org.bukkit.entity.EntityType;

public class MOB_CREATOR {

    private final EntityType type;

    private final int count;

    //private final Entity e;
    public MOB_CREATOR(EntityType typ,int i){
        this.type = typ;
        this.count = i;
        //this.e = l;
    }

    public EntityType getType() {
        return type;
    }

    public int getCount() {
        return count;
    }

//    public Entity getE() {
//        return e;
//    }

}
