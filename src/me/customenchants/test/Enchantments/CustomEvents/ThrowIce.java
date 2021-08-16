package me.customenchants.test.Enchantments.CustomEvents;

import me.customenchants.test.CustomEnchants;
import me.customenchants.test.Enchantments.utils.EnchantCreator;
import me.customenchants.test.Enchantments.utils.EnchantmentHandler;
import me.customenchants.test.Enchantments.utils.ParsedEnchant;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class ThrowIce implements Listener {
    //ice yeeter

    private final EnchantCreator.EventTypes id;

    public HashMap<UUID, Long> coolDown = new HashMap<>();

    public ThrowIce(EnchantCreator.EventTypes e) {
        this.id = e;
    }

    public List<Block> getNearbyBlocks(Location loc, int radius) {
        List<Block> blocks = new ArrayList<Block>();
        for (int x = loc.getBlockX() - radius; x <= loc.getBlockX() + radius; x++) {
            for (int z = loc.getBlockZ() - radius; z <= loc.getBlockZ() + radius; z++) {
                blocks.add(loc.getWorld().getHighestBlockAt(x, z).getLocation().subtract(0, 0, 0).getBlock());
            }
        }
        return blocks;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            ItemStack item = p.getItemInHand();
            ItemMeta itemMeta = item.getItemMeta();
            if(!(itemMeta == null)) {
                if ((itemMeta.hasLore())) {
                    List<ParsedEnchant> check = EnchantmentHandler.getInstance().checkEnchant(itemMeta.getLore(), id);
                    if (!(check.isEmpty())) {
                        if (!(coolDown.containsKey(p.getUniqueId()))) {
                            Vector dir = p.getEyeLocation().getDirection();

                            ArmorStand armor;
                            armor = (ArmorStand) p.getEyeLocation().getWorld().spawnEntity(p.getLocation().add(0, 2, 0), EntityType.ARMOR_STAND);
                            armor.setGravity(false);
                            armor.setGravity(true);
                            armor.setHelmet(new ItemStack(Material.BLUE_ICE));
                            armor.setInvulnerable(true);
                            armor.setVisible(false);
                            armor.setSmall(true);
                            armor.setBasePlate(false);
                            dir.multiply(.9);
                            final boolean[] flag = {false};

                            coolDown.put(p.getUniqueId(), System.currentTimeMillis() + 60000*5);

                            new BukkitRunnable() {
                                int counter = 0;
                                List<Block> blcks;
                                HashMap<Location, Material> oldBlocks = new HashMap<>();

                                @Override
                                public void run() {
                                    if (counter >= 10) {
                                        armor.remove();
                                        cancel();
                                        for (Location loc : oldBlocks.keySet()) {
                                            loc.getBlock().setType(oldBlocks.get(loc));
                                        }
                                    }
                                    counter++;

                                    dir.subtract(new Vector(0, .125, 0));
                                    Vector vec = dir;

                                    if (armor.isOnGround() && !flag[0]) {
                                        armor.setVelocity(dir.normalize().multiply(0));

                                        for (Entity i : armor.getNearbyEntities(5, 5, 5)) {
                                            if (armor.getLocation().distance(i.getLocation()) <= 2.5 && !(i.equals(p))) {
                                                LivingEntity ent = (LivingEntity) i;
                                                ent.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 160, 7, false, false, false));
                                                ent.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 160, 7, false, false, false));
                                                if (ent instanceof Player) {
                                                    ent.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&l*** A Shiver is Sent Down Your Spine ***"));
                                                }
                                                ent.damage(4);
                                                ent.setVelocity(dir.add(new Vector(p.getLocation().getDirection().getX() / 10, 1.5, p.getLocation().getDirection().getZ() / 10)).normalize().multiply(2));
                                            }
                                        }

                                        blcks = getNearbyBlocks(armor.getLocation(), 3);
                                        for (Block block : blcks) {
                                            if (block.getType().equals(Material.BLUE_ICE)) {
                                                continue;
                                            }
                                            oldBlocks.put(block.getLocation(), block.getType());
                                            Block blcktype = block;
                                            blcktype.setType(Material.BLUE_ICE);
                                        }

                                        counter -= 5;
                                        flag[0] = true;
                                    } else if (!flag[0]) {
                                        armor.setVelocity(vec.normalize());
                                        for (Entity i : armor.getNearbyEntities(5, 5, 5)) {
                                            if (armor.getLocation().distance(i.getLocation()) <= 2 && !(i.equals(p))) {
                                                LivingEntity ent = (LivingEntity) i;
                                                ent.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 5, false, false, false));
                                                ent.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 100, 5, false, false, false));
                                            }
                                        }
                                    }
                                }
                            }.runTaskTimer(CustomEnchants.getInstance(), 0, 2);
                        } else {
                            p.sendMessage(ChatColor.BLUE + "Sorry, your " + ChatColor.LIGHT_PURPLE + "Mythical Ability " + ChatColor.BLUE + "is on a cool down.");
                            p.sendMessage(ChatColor.RED + String.valueOf((int) ((coolDown.get(p.getUniqueId()) - System.currentTimeMillis()) / 60000)) + ChatColor.AQUA + " minutes left.");
                        }
                    }
                }
            }
        }


        if (coolDown.containsKey(p.getUniqueId())) {
            if(coolDown.get(p.getUniqueId())-System.currentTimeMillis() <= 0){
                coolDown.remove(p.getUniqueId());
            }
        }
    }
}
