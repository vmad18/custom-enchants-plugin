package me.customenchants.test.Enchantments.CustomItems;

import java.util.ArrayList;
import org.bukkit.Color;

public class Gems {
    private Types type;

    private String name;

    private Color color;

    private ArrayList<String> lore;

    public enum Types {
        LIGHTNING("Lightning"),
        FIRE("Ignition"),
        SOUL("Steal-Health"),
        NONE("");

        private String s;

        Types(String s) {
            this.s = s;
        }

        public String getS() {
            return this.s;
        }
    }

    public Gems(Types t, String n, Color c) {
        this.type = t;
        this.name = n;
        this.color = c;
    }

    public Types getType() {
        return this.type;
    }

    public String getName() {
        return this.name;
    }

    public String setName(String a) {
        return getName() + " " + a;
    }

    public Color getColor() {
        return this.color;
    }
}
