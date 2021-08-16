package me.customenchants.test.Enchantments.utils;

public class ParsedEnchant {

    private final boolean b;
    private final String s;
    private final Integer i;

    public ParsedEnchant(boolean bool, String str, Integer val){
        this.b = bool;
        this.s = str;
        this.i = val;
    }

    public String getS() {
        return s;
    }

    public boolean getB(){
        return b;
    }

    public Integer getI(){ return i; }
}