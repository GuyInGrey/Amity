package com.GuyInGrey.Amity;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent.Status;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import com.GuyInGrey.DungeonGenerator.DungeonCommand;
import com.GuyInGrey.DungeonGenerator.DungeonGenerator;

import github.scarsz.discordsrv.util.DiscordUtil;

public class Amity extends JavaPlugin implements Listener, CommandExecutor
{
  public static Amity instance;
  Location lobby;
  Location lobby1;
  Location lobby2;
  static String ChannelID = "743970937052987492";

  @Override
  public void onEnable()
  {
    lobby = new Location(Bukkit.getWorlds().get(0), 117.5, 103, 94.5, 135, 0);
    lobby1 = new Location(Bukkit.getWorlds().get(0), 154, 190, -6);
    lobby2 = new Location(Bukkit.getWorlds().get(0), 50, 78, 138);
    instance = this;

    getServer().getPluginManager().registerEvents(new ChairSit(), this);
    getServer().getPluginManager().registerEvents(new Tag(), this);
    // getServer().getPluginManager().registerEvents(new Music(), this);
    getServer().getPluginManager().registerEvents(new Buttons(), this);
    getServer().getPluginManager().registerEvents(new Checkpoint(), this);
    getServer().getPluginManager().registerEvents(this, this);

    this.getCommand("lobby").setExecutor(new CmdLobby());
    this.getCommand("maze").setExecutor(new MazeCommand());
    this.getCommand("cellborder").setExecutor(new CellBorderCommand());
    this.getCommand("pack").setExecutor(this);
    this.getCommand("dungeon").setExecutor(new DungeonCommand());

    DungeonGenerator.LoadStructures();

    new BukkitRunnable()
    {
      @Override
      public void run()
      {
        Tag.instance.Tick();
        MazeCommand.instance.Tick();
        Tick();
      }
    }.runTaskTimer(this, 0L, 1L);
  }

  @Override
  public void onDisable()
  {
  }

  public void Tick()
  {
    for (Player p : Bukkit.getOnlinePlayers())
    {
      if (!InLobby(p))
      {
        return;
      }

      // No effects in lobby
      for (PotionEffect pe : p.getActivePotionEffects())
      {
        p.removePotionEffect(pe.getType());
      }
    }
  }

  public boolean InLobby(Entity e)
  {
    return Helper.PlayerInArea(e, lobby1, lobby2);
  }

  public void TeleportPlayer(Player p, Location newLoc)
  {
    p.getWorld().playSound(p.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 1, 1);
    p.getWorld().spawnParticle(Particle.TOTEM, p.getLocation(), 100, 0.2, 0, 0.2);

    Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable()
    {
      @Override
      public void run()
      {
        p.teleport(newLoc, TeleportCause.PLUGIN);
      }
    }, 1L);

    Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable()
    {
      @Override
      public void run()
      {
        newLoc.getWorld().playSound(newLoc, Sound.BLOCK_BEACON_POWER_SELECT, 1, 1);
        newLoc.getWorld().spawnParticle(Particle.TOTEM, newLoc, 100, 0.2, 0, 0.2);
      }
    }, 2L);
  }

  public void BtnLobby(Player p)
  {
    TeleportPlayer(p, lobby);
  }

  public static void Broadcast(String message)
  {
    Bukkit.broadcastMessage(message);
    message = message.replace("[", "").replace("]", "");
    message = message.replace("TAG", ":tag:");
    DiscordUtil.sendMessage(DiscordUtil.getTextChannelById(ChannelID), message);
  }

  @EventHandler
  public void onDrop(PlayerDropItemEvent e)
  {
    if (e.getPlayer().getGameMode() == GameMode.ADVENTURE)
    {
      e.setCancelled(true);
    }
  }

  @EventHandler
  public void onPickup(EntityPickupItemEvent e)
  {
    if (e.getEntity().getType() == EntityType.PLAYER)
    {
      if (((Player) e.getEntity()).getGameMode() == GameMode.ADVENTURE)
      {
        e.setCancelled(true);
      }
    }
  }

  @EventHandler
  public void onInventoryMove(InventoryClickEvent e)
  {
    if (e.getWhoClicked().getGameMode() == GameMode.ADVENTURE)
    {
      e.setCancelled(true);
    }
  }

  @EventHandler
  public void playerJoin(PlayerJoinEvent e)
  {
    e.getPlayer().teleport(lobby);

//		Bukkit.getScheduler().scheduleSyncDelayedTask(Amity.instance, new Runnable() {
//		    @Override
//		    public void run() {
//				e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.MUSIC_CREDITS, SoundCategory.AMBIENT,  100000, 1);
//		    }
//		}, 30 * 20);

    Pack(e.getPlayer());
  }

  public void Pack(Player p)
  {
    // Direct download URL
    String pack = "https://guyingrey.github.io/New_Wayukian_Java.zip";
    try
    {
      p.setResourcePack(pack, createSha1(
          new File(Amity.instance.getDataFolder().getAbsolutePath() + File.separator + "New_Wayukian_Java.zip")));
    } catch (Exception e)
    {
    }

    Status s = p.getResourcePackStatus();
    if (s == null || s == Status.DECLINED || s == Status.FAILED_DOWNLOAD)
    {
      p.sendMessage("Attemping resource pack loading...");
    } else
    {
      p.sendMessage("You have the resource pack already! Reloading it for you.");
    }
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
  {
    Pack((Player) sender);
    return true;
  }

  public byte[] createSha1(File file) throws Exception
  {
    MessageDigest digest = MessageDigest.getInstance("SHA-1");
    InputStream fis = new FileInputStream(file);
    int n = 0;
    byte[] buffer = new byte[8192];
    while (n != -1)
    {
      n = fis.read(buffer);
      if (n > 0)
      {
        digest.update(buffer, 0, n);
      }
    }
    fis.close();
    return digest.digest();
  }
}