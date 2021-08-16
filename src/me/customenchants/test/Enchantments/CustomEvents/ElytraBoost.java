package me.customenchants.test.Enchantments.CustomEvents;

import me.customenchants.test.Items.CustomElytra;
import me.customenchants.test.CustomEnchants;
import me.customenchants.test.Enchantments.utils.EnchantCreator;
import me.customenchants.test.Enchantments.utils.EnchantmentHandler;
import me.customenchants.test.Enchantments.utils.ParsedEnchant;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ElytraBoost implements Listener {
    private final EnchantCreator.EventTypes id;

    public ElytraBoost(EnchantCreator.EventTypes i) {
        this.id = i;
    }

    public HashMap<UUID, ItemStack> itemHold = new HashMap<>();


    public HashMap<UUID, Long> coolDown = new HashMap<>();


    public void runElytraFlight(Player p, int timer, double speed) {
        boolean[] setGlide = {true};
        boolean[] warning = {false};
        new BukkitRunnable() {
            int counter = 0;

            @Override
            public void run() {

                if (counter >= timer) {
                    cancel();
                    p.setVelocity(p.getLocation().getDirection().normalize().multiply(0));
                    if (itemHold.containsKey(p.getUniqueId())) {
                        p.getInventory().setItem(38, itemHold.get(p.getUniqueId()));
                    }
                    coolDown.put(p.getUniqueId(), System.currentTimeMillis() + 300000);
                } else {
                    counter++;
                }

                if (timer - counter <= 40 && !warning[0]) {
                    p.sendMessage(ChatColor.RED + "5 Seconds of Flight Left!");
                    warning[0] = true;
                }

                Vector vec = p.getLocation().getDirection();

                p.setVelocity(vec.normalize().multiply(speed));


                if (!p.isOnGround()) {
                    if (setGlide[0]) {
                        p.setGliding(true);
                        setGlide[0] = false;
                    }

                    if (p.isGliding()) {
                        if (p.isOnGround() && !p.isDead()) {
                            cancel();
                            p.setVelocity(vec.normalize().multiply(0));
                            p.setVelocity(p.getLocation().getDirection().normalize().multiply(0));
                            p.getInventory().setItem(38, itemHold.get(p.getUniqueId()));
                            coolDown.put(p.getUniqueId(), System.currentTimeMillis() + 300000);
                        } else if (p.isDead()) {
                            cancel();
                        }
                    } else if (!p.isGliding() && !p.isDead()) {
                        cancel();
                        p.setVelocity(vec.normalize().multiply(0));
                        p.setVelocity(p.getLocation().getDirection().normalize().multiply(0));
                        p.getInventory().setItem(38, itemHold.get(p.getUniqueId()));
                        coolDown.put(p.getUniqueId(), System.currentTimeMillis() + 300000);
                    } else if (p.isDead()) {
                        cancel();
                    }


                } else if ((!p.isGliding()) || (p.isOnGround())) {
                    cancel();
                    p.setVelocity(p.getLocation().getDirection().normalize().multiply(0));
                    p.getInventory().setItem(38, itemHold.get(p.getUniqueId()));
                    coolDown.put(p.getUniqueId(), System.currentTimeMillis() + 300000);
                }
                if (p.isDead()) {
                    p.getInventory().clear();
                }
                counter++;
            }
        }.runTaskTimer(CustomEnchants.getInstance(), 0, 5);


    }

    @EventHandler
    public void onShift(PlayerToggleSneakEvent e) {
        Player p = e.getPlayer();
        final boolean[] flag = {false};
        final boolean[] setGlide = {false};
        boolean[] stop = {false};
        CustomElytra ely = new CustomElytra();
        Double[] speeds = {1.0, 1.5, 2.25};
        if (e.isSneaking()) {
            if (!(p.getInventory().getItem(38) == null)) {
                if (!(Objects.requireNonNull(p.getInventory().getItem(38)).getItemMeta() == null)) {
                    if ((Objects.requireNonNull(p.getInventory().getItem(38)).getItemMeta().hasLore())) {
                        List<ParsedEnchant> check = EnchantmentHandler.getInstance().checkEnchant(p.getInventory().getItem(38).getItemMeta().getLore(), id);
                        if (!(check.isEmpty())) {
                            if (!coolDown.containsKey(p.getUniqueId())) {
                                new BukkitRunnable() {
                                    int counter = 0;

                                    @Override
                                    public void run() {
                                        if (flag[0] && !(stop[0]) && counter >= 250) {
                                            cancel();
                                            runElytraFlight(p, 4 * (75 + (10 * check.get(0).getI())), speeds[check.get(0).getI() - 1]);
                                            flag[0] = false;
                                        } else if (flag[0] && !(stop[0])) {
                                            counter++;
                                        }

                                        //Shift Check
                                        if (p.isSneaking() && counter >= 240 && !flag[0] && !(stop[0]) && !p.isGliding()) {

                                            p.setVelocity(new Vector(0, 10, 0).normalize().multiply(2));
                                            e.setCancelled(true);
                                            p.setSneaking(false);
                                            flag[0] = true;
                                            itemHold.put(p.getUniqueId(), p.getInventory().getItem(38));
                                            p.getInventory().setItem(38, ely.getElytra());

                                            p.sendMessage(ChatColor.DARK_AQUA + "Have a safe flight ^_^!");

                                        } else if (p.isSneaking() && counter < 240 && !(stop[0]) && !p.isGliding()) {
                                            counter++;
                                            if (counter % 20 == 0) {
                                                p.sendMessage(ChatColor.BLUE + "Launching in: " + (12 - counter / 20));
                                            }
                                        } else if (!(p.isSneaking()) && counter < 240 && !flag[0]) {
                                            cancel();
                                            stop[0] = true;
                                            p.sendMessage(ChatColor.RED + "Aborted.");
                                        }

                                        if (stop[0]) {
                                            cancel();
                                        }
                                    }
                                }.runTaskTimer(CustomEnchants.getInstance(), 0, 0);
                            } else if (coolDown.get(p.getUniqueId()) - System.currentTimeMillis() <= 0) {
                                coolDown.remove(p.getUniqueId());
                            } else {
                                p.sendMessage(ChatColor.BLUE + "Sorry, your " + ChatColor.LIGHT_PURPLE + "Mythical Ability " + ChatColor.BLUE + "is on a cool down.");
                                p.sendMessage(ChatColor.RED + String.valueOf((int) ((coolDown.get(p.getUniqueId()) - System.currentTimeMillis()) / 60000)) + ChatColor.AQUA + " minutes left.");
                            }
                        }
                    }
                }
            }
        }
    }
}
