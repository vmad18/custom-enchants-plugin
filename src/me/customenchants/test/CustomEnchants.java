package me.customenchants.test;

import me.customenchants.test.Enchantments.CustomEvents.*;
import me.customenchants.test.Enchantments.CustomItems.ChainItem;
import me.customenchants.test.Enchantments.utils.EnchantCreator;
import me.customenchants.test.Enchantments.utils.EnchantmentHandler;
import me.customenchants.test.Enchantments.utils.Sources;
import me.customenchants.test.Enchantments.CustomItems.Gems;
import me.customenchants.test.Enchantments.utils.EnchantUtils.MOB_CREATOR;
import me.customenchants.test.Enchantments.utils.EnchantUtils.POTION_MAKER;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author v18
 *
 **/

public class CustomEnchants extends JavaPlugin implements CommandExecutor {
    //singleton
    private static CustomEnchants plugin;

    private EnchantmentHandler ench;

    public Sources source = new Sources();

    private List<String> enchantList = new ArrayList<>();

    public static CustomEnchants getInstance() {
        return plugin;
    }

    @Override
    public void onEnable() {
        plugin = this;
        ench = new EnchantmentHandler();
        this.getServer().getPluginManager().registerEvents(new GameSettings(), this);
        this.getServer().getPluginManager().registerEvents(new ApplyPotionEffectsEvent(EnchantCreator.EventTypes.POTIONS_EFFECT), this);
        this.getServer().getPluginManager().registerEvents(new LightningStorm(EnchantCreator.EventTypes.LIGHTNING_STORM), this);
        this.getServer().getPluginManager().registerEvents(new ArrowBarrage(EnchantCreator.EventTypes.ARROW_DEFENSE), this);
        this.getServer().getPluginManager().registerEvents(new ThrowIce(EnchantCreator.EventTypes.ICE_THROW), this);
        this.getServer().getPluginManager().registerEvents(new DoubleHit(EnchantCreator.EventTypes.DOUBLE),this);
        this.getServer().getPluginManager().registerEvents(new ElytraBoost(EnchantCreator.EventTypes.ELYTRA_FLY),this);
        this.getServer().getPluginManager().registerEvents(new StealHealthEvent(EnchantCreator.EventTypes.STEAL_HEALTH),this);
        this.getServer().getPluginManager().registerEvents(new DamageReduction(EnchantCreator.EventTypes.DAMAGE_REDUCE),this);
        this.getServer().getPluginManager().registerEvents(new EffectEnemyEvent(EnchantCreator.EventTypes.EFFECT_ENEMY, EnchantCreator.EventTypes.GODS_TOUCH),this);
        this.getServer().getPluginManager().registerEvents(new ExplosiveHit(EnchantCreator.EventTypes.EXPLOSIVE_HIT),this);
        this.getServer().getPluginManager().registerEvents(new SummonMob(EnchantCreator.EventTypes.MOB_SPAWN),this);
        this.getServer().getPluginManager().registerEvents(new LiftBlocks(EnchantCreator.EventTypes.BLOCK_LIFT),this);
        this.getServer().getPluginManager().registerEvents(new ChainPlayer(EnchantCreator.EventTypes.CHAIN_PLAYER,this,source),this);
        this.getServer().getPluginManager().registerEvents(new SoulBurst(EnchantCreator.EventTypes.SOUL_BURST),this);
        register();

        for(String i: EnchantmentHandler.getInstance().enchantTypes.keySet()){
            enchantList.add(i);
        }
        Bukkit.broadcastMessage(ChatColor.BLUE + enchantList.toString());
    }

    @Override
    public void onDisable() {}

    public void register() {
        EnchantmentHandler.getInstance().registerEnchant(new EnchantCreator(EnchantCreator.EventTypes.POTIONS_EFFECT, Arrays.asList(Material.NETHERITE_AXE, Material.DIAMOND_SWORD), false, "Strength", "Gives Strength", new POTION_MAKER(Arrays.asList(PotionEffectType.INCREASE_DAMAGE, PotionEffectType.JUMP, PotionEffectType.SPEED, PotionEffectType.ABSORPTION), Arrays.asList(1, 2, 2, 1)), EnchantCreator.Rarity.LEGENDARY, 0,null,0,null));
        EnchantmentHandler.getInstance().registerEnchant(new EnchantCreator(EnchantCreator.EventTypes.POTIONS_EFFECT, Arrays.asList(Material.NETHERITE_CHESTPLATE, Material.DIAMOND_CHESTPLATE), true, "Overload", "Gives More Health", new POTION_MAKER(Arrays.asList(PotionEffectType.ABSORPTION, PotionEffectType.REGENERATION, PotionEffectType.SATURATION), Arrays.asList(1, 2, 1)), EnchantCreator.Rarity.LEGENDARY, 0,null,0,null));
        EnchantmentHandler.getInstance().registerEnchant(new EnchantCreator(EnchantCreator.EventTypes.LIGHTNING_STORM, Arrays.asList(Material.DIAMOND_SWORD), false, "Thunderous-Wrath", "Strikes Lightning", null, EnchantCreator.Rarity.RARE, 0,null,0,null));
        EnchantmentHandler.getInstance().registerEnchant(new EnchantCreator(EnchantCreator.EventTypes.ARROW_DEFENSE, Arrays.asList(Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.NETHERITE_CHESTPLATE), true, "Arrow-Barrage", "Summons Arrows Around", null, EnchantCreator.Rarity.MYTHIC, 20,null,0,null));
        EnchantmentHandler.getInstance().registerEnchant(new EnchantCreator(EnchantCreator.EventTypes.ICE_THROW,Arrays.asList(Material.DIAMOND_SWORD,Material.NETHERITE_SWORD),false,"Ice-Logger","Throws Ice at Enemies, cool-down tho",null, EnchantCreator.Rarity.MYTHIC,0,null,0,null));
        EnchantmentHandler.getInstance().registerEnchant(new EnchantCreator(EnchantCreator.EventTypes.DOUBLE,Arrays.asList(Material.DIAMOND_SWORD,Material.NETHERITE_SWORD),false,"Double-Strike","Chance for DOUBLE HIT",null, EnchantCreator.Rarity.LEGENDARY,25,null,0,null));
        EnchantmentHandler.getInstance().registerEnchant(new EnchantCreator(EnchantCreator.EventTypes.ELYTRA_FLY, Arrays.asList(Material.DIAMOND_CHESTPLATE,Material.NETHERITE_CHESTPLATE,Material.IRON_CHESTPLATE),true,"Armored-Elytra","The ability to fly away from your enemies",null,EnchantCreator.Rarity.MYTHIC,0,null,0,null));
        EnchantmentHandler.getInstance().registerEnchant(new EnchantCreator(EnchantCreator.EventTypes.STEAL_HEALTH, Arrays.asList(Material.DIAMOND_SWORD,Material.NETHERITE_SWORD),false,"Life-Steal","Take Health from your Enemy",null,EnchantCreator.Rarity.LEGENDARY,10,null,0,null));
        EnchantmentHandler.getInstance().registerEnchant(new EnchantCreator(EnchantCreator.EventTypes.DAMAGE_REDUCE, Arrays.asList(Material.NETHERITE_CHESTPLATE, Material.DIAMOND_CHESTPLATE, Material.NETHERITE_LEGGINGS),true,"Armored","Decreases Damage Taken",null,EnchantCreator.Rarity.LEGENDARY,0,null,0,null));
        EnchantmentHandler.getInstance().registerEnchant(new EnchantCreator(EnchantCreator.EventTypes.EFFECT_ENEMY, Arrays.asList(Material.DIAMOND_SWORD,Material.NETHERITE_SWORD),false,"Wither","Give Wither to enemy", new POTION_MAKER(Arrays.asList(PotionEffectType.WITHER),Arrays.asList(1)),EnchantCreator.Rarity.RARE,50,Arrays.asList(8),0,null));
        EnchantmentHandler.getInstance().registerEnchant(new EnchantCreator(EnchantCreator.EventTypes.GODS_TOUCH, Arrays.asList(Material.NETHERITE_CHESTPLATE, Material.DIAMOND_CHESTPLATE),true,"Gods-Touch","Takes away all Potion effects",new POTION_MAKER(Arrays.asList(PotionEffectType.WITHER),Arrays.asList(1)),EnchantCreator.Rarity.MYTHIC,50,Arrays.asList(8),60,null));
        EnchantmentHandler.getInstance().registerEnchant(new EnchantCreator(EnchantCreator.EventTypes.EXPLOSIVE_HIT, Arrays.asList(Material.DIAMOND_SWORD,Material.NETHERITE_SWORD),false,"Explosive-Hit","Implode your enemy",null,EnchantCreator.Rarity.RARE,15,Arrays.asList(8),0,null));
        EnchantmentHandler.getInstance().registerEnchant(new EnchantCreator(EnchantCreator.EventTypes.ANTI_POTION, Arrays.asList(Material.NETHERITE_CHESTPLATE, Material.DIAMOND_CHESTPLATE),true,"Anti-Oxidant","Resiliant to Poision",new POTION_MAKER(Arrays.asList(PotionEffectType.POISON),Arrays.asList(1)),EnchantCreator.Rarity.LEGENDARY,50,Arrays.asList(8),60,null));
        EnchantmentHandler.getInstance().registerEnchant(new EnchantCreator(EnchantCreator.EventTypes.EFFECT_ENEMY, Arrays.asList(Material.DIAMOND_SWORD,Material.NETHERITE_SWORD),false,"Poisoned-Blade","Give Poison to enemy",new POTION_MAKER(Arrays.asList(PotionEffectType.POISON),Arrays.asList(2)),EnchantCreator.Rarity.RARE,15,Arrays.asList(5),0,null));
        EnchantmentHandler.getInstance().registerEnchant(new EnchantCreator(EnchantCreator.EventTypes.EFFECT_ENEMY, Arrays.asList(Material.DIAMOND_SWORD,Material.NETHERITE_SWORD),false,"Blinding-Light","Give Blindness to enemy",new POTION_MAKER(Arrays.asList(PotionEffectType.BLINDNESS),Arrays.asList(2)),EnchantCreator.Rarity.RARE,10,Arrays.asList(4),0,null));
        EnchantmentHandler.getInstance().registerEnchant(new EnchantCreator(EnchantCreator.EventTypes.EFFECT_ENEMY, Arrays.asList(Material.DIAMOND_SWORD,Material.NETHERITE_SWORD),false,"Chains","Give Blindness to enemy",new POTION_MAKER(Arrays.asList(PotionEffectType.SLOW),Arrays.asList(2)),EnchantCreator.Rarity.RARE,10,Arrays.asList(4),0,null));
        EnchantmentHandler.getInstance().registerEnchant(new EnchantCreator(EnchantCreator.EventTypes.MOB_SPAWN, Arrays.asList(Material.DIAMOND_CHESTPLATE),true,"Iron-Savior","Summon iron golem", null,EnchantCreator.Rarity.LEGENDARY,10,null,0,new MOB_CREATOR(EntityType.IRON_GOLEM,2)));
        EnchantmentHandler.getInstance().registerEnchant(new EnchantCreator(EnchantCreator.EventTypes.BLOCK_LIFT, Arrays.asList(Material.DIAMOND_CHESTPLATE),true,"Nature's-Touch","Absorb the Essence of Nature", null,EnchantCreator.Rarity.LEGENDARY,40,null,0,null));
        EnchantmentHandler.getInstance().registerEnchant(new EnchantCreator(EnchantCreator.EventTypes.SOUL_BURST, Arrays.asList(Material.DIAMOND_CHESTPLATE), true, "Soul-Burst", "Unleash your inner rage", new POTION_MAKER(Arrays.asList(PotionEffectType.INCREASE_DAMAGE),Arrays.asList(2)),EnchantCreator.Rarity.GOD_LIKE, 0, null, 0, null));
    }


    public boolean onCommand(CommandSender sender, Command cmd, String label, String args[]) {
        Player p = (Player) sender;
        if(!(sender instanceof Player)) {
            return true;
        }
        if(label.equalsIgnoreCase("applyEnchant")){
            if(enchantList.contains(args[0])){
                Bukkit.broadcastMessage(ChatColor.BLUE + enchantList.toString());
                p.setItemInHand(EnchantmentHandler.getInstance().applyEnchant(p.getItemInHand(), Integer.valueOf(args[1]) ,args[0], p));
                return true;
            }
        }
        if (label.equalsIgnoreCase("getchain")) {
            ChainItem item = new ChainItem(new ItemStack(Material.CHAIN), new Gems(Gems.Types.LIGHTNING, "Lightning", Color.AQUA), plugin, "God's Chains");
            this.source.addChainItems(item);
            p.getInventory().addItem(new ItemStack[] { item.getItem() });
            return true;
        }
        return false;
    }

}

