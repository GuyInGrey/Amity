package com.GuyInGrey.Amity;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import github.scarsz.discordsrv.util.DiscordUtil;

public class Amity extends JavaPlugin implements Listener
{
	public static Amity instance;
	Location lobby;
	static String ChannelID = "743970937052987492";
	
	@Override
	public void onEnable()
	{
		lobby = new Location(Bukkit.getWorlds().get(0), 0.5, 80, 0.5, 180, 0);
		instance = this;
		
		getServer().getPluginManager().registerEvents(new ChairSit(), this);
		getServer().getPluginManager().registerEvents(new Tag(), this);
		getServer().getPluginManager().registerEvents(new Music(), this);
		getServer().getPluginManager().registerEvents(new Buttons(), this);
		getServer().getPluginManager().registerEvents(this, this);
		
		this.getCommand("lobby").setExecutor(new CmdLobby());
		
		new BukkitRunnable () {
		    @Override
		    public void run() {
		    	Tag.instance.Tick();
		    }
		}.runTaskTimer(this, 0L, 1L);
	}
	 
	@Override
	public void onDisable() { }
	
	public void TeleportPlayer(Player p, Location newLoc)
	{
		p.getWorld().playSound(p.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 1, 1);
		p.getWorld().spawnParticle(Particle.TOTEM, p.getLocation(), 100, 0.2, 0, 0.2);
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
		    @Override
		    public void run() {
				p.teleport(newLoc, TeleportCause.PLUGIN);
		    }
		}, 1L);
		
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
		    @Override
		    public void run() {
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
			if (((Player)e.getEntity()).getGameMode() == GameMode.ADVENTURE)
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
}