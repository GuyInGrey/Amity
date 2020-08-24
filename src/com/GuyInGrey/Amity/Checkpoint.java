package com.GuyInGrey.Amity;

import java.text.DateFormatSymbols;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import net.md_5.bungee.api.ChatColor;

public class Checkpoint implements Listener
{
  Map<UUID, Location> Checkpoints = new HashMap<UUID, Location>();

  @EventHandler
  public void onPlayerInteract(PlayerInteractEvent e)
  {
    if (e.getClickedBlock() != null && e.getClickedBlock().getType() == Material.BELL)
    {
      Location l = e.getPlayer().getLocation();
      Checkpoints.put(e.getPlayer().getUniqueId(), l);
      e.getPlayer().sendMessage(ChatColor.AQUA + "Checkpoint set! Use your checkpoint item to get back.");
      return;
    }

    ItemMeta item = e.getPlayer().getInventory().getItemInMainHand().getItemMeta();
    if (item != null)
    {
      String name = item.getDisplayName();
      if (name != null && name.toLowerCase().contains("checkpoint"))
      {
        Player p = e.getPlayer();

        if (Checkpoints.containsKey(p.getUniqueId()))
        {
          Back(p);
        } else
        {
          p.sendMessage(ChatColor.RED + "You do not have a checkpoint set.");
        }
      }
    }
  }

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent e)
  {
    Checkpoints.remove(e.getPlayer().getUniqueId());
    stopSounds(e.getPlayer());

    String name = e.getPlayer().getName();
  }

  @EventHandler
  public void onHit(EntityDamageEvent event)
  {
    if (event.getEntity() instanceof Player)
    {
      event.setCancelled(true);
      if (event.getCause() == DamageCause.HOT_FLOOR)
      {
        Back((Player) event.getEntity());
      }
    }
  }

  @EventHandler
  public void onPlayerMove(PlayerMoveEvent e)
  {
    if (e.getPlayer().getLocation().add(0, -1, 0).getBlock().getType() == Material.BEDROCK)
    {
      if (Checkpoints.containsKey(e.getPlayer().getUniqueId()))
      {
        Location l1 = Checkpoints.get(e.getPlayer().getUniqueId()).getBlock().getLocation();
        Location l2 = e.getPlayer().getLocation().getBlock().getLocation();
        if (!(l1.getX() == l2.getX() && l1.getX() == l2.getX() && l1.getX() == l2.getX()))
        {
          Checkpoints.put(e.getPlayer().getUniqueId(), e.getPlayer().getLocation());
          e.getPlayer().sendMessage(ChatColor.AQUA + "Checkpoint set! Use your checkpoint item to get back.");
        }
      } else
      {
        Checkpoints.put(e.getPlayer().getUniqueId(), e.getPlayer().getLocation());
        e.getPlayer().sendMessage(ChatColor.AQUA + "Checkpoint set! Use your checkpoint item to get back.");
      }
    }

    if (e.getPlayer().getLocation().getY() < 1)
    {
      Back(e.getPlayer());
    } else if (e.getPlayer().isSneaking())
    {
      if (e.getPlayer().getLocation().add(new Vector(0, -1, 0)).getBlock().getType() == Material.MAGMA_BLOCK)
      {
        e.getPlayer().setSneaking(false);
      }
    }
  }

  private static final Sound[] SOUNDS = Sound.values();

  public void stopSounds(Player player)
  {
    for (Sound sound : SOUNDS)
    {
      player.stopSound(sound);
    }
  }

  public void Back(Player sender)
  {
    sender.setVelocity(new Vector(0, 0, 0));

    new BukkitRunnable()
    {
      @Override
      public void run()
      {
        sender.teleport(Checkpoints.get(sender.getUniqueId()));
      }
    }.runTaskLater(Amity.instance, 2);

    sender.sendMessage(ChatColor.AQUA + "Teleported you to checkpoint!");

    sender.setFireTicks(0);
    sender.setHealth(20.0);
  }

  public ItemStack GetReward(String playerName)
  {
    Calendar cal = Calendar.getInstance();
    int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
    int month = cal.get(Calendar.MONTH);
    int year = cal.get(Calendar.YEAR);
    String date = dayOfMonth + " " + getMonth(month) + ", " + year;

    ItemStack reward = new ItemStack(Material.DIAMOND_HOE, 1);
    ItemMeta meta = reward.getItemMeta();
    meta.setDisplayName(ChatColor.DARK_BLUE + "" + ChatColor.BOLD + "Hoe Of Undying Fortune");
    meta.setLore(Arrays.asList(ChatColor.WHITE + "May the strength be with you,",
        ChatColor.WHITE + "to leap over all bounderies,", ChatColor.WHITE + "to expand all limits,",
        ChatColor.WHITE + "and to achieve all desires.", ChatColor.GOLD + "Achived by " + playerName + ",",
        ChatColor.GOLD + "on " + date));
    reward.setItemMeta(meta);
    reward.addEnchantment(Enchantment.MENDING, 1);
    reward.addEnchantment(Enchantment.DURABILITY, 3);

    return reward;
  }

  public String getMonth(int month)
  {
    return new DateFormatSymbols().getMonths()[month];
  }
}