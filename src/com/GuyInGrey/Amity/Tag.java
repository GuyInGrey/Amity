package com.GuyInGrey.Amity;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;
import org.bukkit.ChatColor;

public class Tag implements Listener
{
	public static Tag instance;
	Random random;
	
	HashMap<UUID, TagPlayer> Players;
	UUID It;
	boolean GameRunning;
	boolean CountingDown;
	boolean itWaiting = false;
	boolean gameJustStarted = false;
	String tagPrefix = "[" + ChatColor.AQUA + "TAG" + ChatColor.WHITE + "] " + ChatColor.GOLD;
	
	Location gameBox1;
	Location gameBox2;
	Location lobby1;
	Location lobby2;
	Location lobby;
	Location tagStart;
	Location lever_blindness;
	Location lever_arrow;
	
	public Tag()
	{
		gameBox1 = new Location(Bukkit.getWorlds().get(0), 37, 255, -161);
		gameBox2 = new Location(Bukkit.getWorlds().get(0), -70, 58, -18);
		lobby1 = new Location(Bukkit.getWorlds().get(0), 34, 124, -88);
		lobby2 = new Location(Bukkit.getWorlds().get(0), 45, 111, -76);
		lobby = new Location(Bukkit.getWorlds().get(0), 41.45, 112.1, -81.5, 90, 25);
		tagStart = new Location(Bukkit.getWorlds().get(0), -1, 78, -39, -180, 0);
		lever_blindness = new Location(Bukkit.getWorlds().get(0), 41, 114, -84);
		lever_arrow = new Location(Bukkit.getWorlds().get(0), 40, 114, -84);
		
		random = new Random();
		Players = new HashMap<UUID, TagPlayer>();
		instance = this;
	}
	
	public void Tick()
	{
		for (Player p : Bukkit.getOnlinePlayers())
		{
			UUID id = p.getUniqueId();
			
			// Manage teams.
			Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
			Team itTeam = scoreboard.getTeam("It");
			Team everyoneTeam = scoreboard.getTeam("Everyone");
			if (It == p.getPlayer().getUniqueId() && GameRunning)
			{
				itTeam.addEntry(p.getName());
				everyoneTeam.removeEntry(p.getName());
			}
			else
			{
				itTeam.removeEntry(p.getName());
				everyoneTeam.addEntry(p.getName());
			}
			p.setScoreboard(scoreboard);
			
			// Players in game shouldn't leave the game area.
			if ((IsInGame(p) && !Helper.PlayerInArea(p, gameBox1, gameBox2) && !(p.getUniqueId().equals(It) && itWaiting)) ||
					(p.getGameMode() == GameMode.SPECTATOR && !Helper.PlayerInArea(p, gameBox1, gameBox2)))
			{ Amity.instance.TeleportPlayer(p, tagStart); }

			// This process only applies to people in the tag zones.
			if (!InGameArea(p)) { return; }
			
			// Players not in game shouldn't be in the game area.
			if (!IsInGame(p) && GameRunning && Helper.PlayerInArea(p, gameBox1, gameBox2) && p.getGameMode() != GameMode.SPECTATOR)
			{ Amity.instance.TeleportPlayer(p, lobby); }
			
			// If you're not in a game, clear your inventory.
			if (!IsInGame(p) && p.getGameMode() == GameMode.ADVENTURE) { p.getInventory().clear(); }
			
			// If you're riding an arrow, spawn Note particles as a trail.
			if (p.getVehicle() != null) { p.getWorld().spawnParticle(Particle.NOTE, p.getLocation(), 1); }
			
			// Blindness option
			if (Helper.GetLever(lever_blindness))
			{ p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 1000000, 1, false, false, false)); }
			else { p.removePotionEffect(PotionEffectType.BLINDNESS); }
			
			if (!IsInGame(p)) { continue; }

			if (Helper.GetLever(lever_arrow))
			{
				// Arrow
				if (p.getInventory().getItem(5) == null)
				{
					if (Players.get(id).TicksUntilArrow == -1) { Players.get(id).TicksUntilArrow = 10 * 20; }
					Players.get(id).TicksUntilArrow = Players.get(id).TicksUntilArrow - 1;
					if (Players.get(id).TicksUntilArrow == 0)
					{
						p.getInventory().setItem(5, new ItemStack(Material.ARROW, 1));
					}
					
					int ticks = Players.get(id).TicksUntilArrow;
					
					p.setLevel((ticks - (ticks % 20)) / 20);
					float exp = (float)(ticks % 20) / 20f;
					p.setExp(exp < 0 ? 0 : exp > 1 ? 1 : exp);
				}
			}
			
			// Snowball
			if (p.getInventory().getItem(0) == null && p.getUniqueId() == It)
			{
				if (Players.get(id).TicksUntilSnowball == -1) { Players.get(id).TicksUntilSnowball = 1 * 20; }
				Players.get(id).TicksUntilSnowball -= 1;
				if (Players.get(id).TicksUntilSnowball == 0)
				{
					p.getInventory().setItem(0, Snowball());
				}
			}
			
			// Clock
			float seconds = ((float)Players.get(id).TicksItLeft / 20f) % 60f;
			float minutes = (((float)Players.get(id).TicksItLeft / 20f) - ((float)Players.get(id).TicksItLeft / 20f) % 60f) / 60f;
			p.getInventory().setItem(7, Clock((int)seconds, "Seconds Remaining"));
			p.getInventory().setItem(6, Clock((int)minutes, "Minutes Remaining"));
			if (It == p.getUniqueId() && !itWaiting)
			{
				Players.get(id).TicksItLeft -=1;
			}
			if (Players.get(id).TicksItLeft <= 0)
			{
				Players.remove(p.getUniqueId());
				Elimination(p, Bukkit.getPlayer((UUID)Players.keySet().toArray()[0]));
			}
		}
	}
	
	// Prevent trampling farmland
	@EventHandler
	public void onEntityInteract(EntityInteractEvent event) 
	{
		if (!InGameArea(event.getEntity())) { return; }
		if (event.getBlock().getType() == Material.FARMLAND && event.getEntity() instanceof LivingEntity)
		{ event.setCancelled(true); }
	}
	
	// Prevent trampling farmland
    @EventHandler(priority = EventPriority.NORMAL)
    public void OnPlayerInteractEvent(PlayerInteractEvent event) 
    {
		if (!InGameArea(event.getPlayer())) { return; }
    	if (event.getClickedBlock() == null) { return; }
        Block soilBlock = event.getClickedBlock();
        if ((event.getAction() == Action.PHYSICAL)) {
            if (soilBlock.getType() == Material.FARMLAND) {
                event.setCancelled(true);
            }
        }
    }
	
	@EventHandler
	public void onProjectileHit(ProjectileHitEvent e)
	{
		if (!InGameArea(e.getEntity())) { return; }
		Location l = e.getEntity().getLocation();
		
		if (e.getEntity().getType() == EntityType.ARROW)
		{
			e.getEntity().remove();
			if (e.getEntity().getPassengers().size() > 0)
			{
				Player p = (Player)e.getEntity().getShooter();
				Location l2 = new Location(l.getWorld(), Math.floor(l.getX()) + 0.5, l.getY(), Math.floor(l.getZ()) + 0.5);
				l2.setDirection(p.getLocation().getDirection());
				p.teleport(l2);
				p.playSound(l2, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
			}
		}
		else if (e.getEntity().getType() == EntityType.SNOWBALL)
		{
			if (e.getHitEntity() != null && e.getHitEntity().getType() == EntityType.PLAYER)
			{
				Player itPlayer = Bukkit.getPlayer(It);
				Player hitPlayer = ((Player)e.getHitEntity());
				
				if ((Player)(e.getEntity().getShooter()) == itPlayer)
				{
					if (hitPlayer != itPlayer)
					{
						if (Players.get(hitPlayer.getUniqueId()).Lives <= 1)
						{
							Elimination(hitPlayer, itPlayer);
						}
						else
						{
							It = hitPlayer.getUniqueId();
							Amity.Broadcast(tagPrefix + hitPlayer.getDisplayName() + " is it!");
							
							Players.get(It).TicksUntilSnowball = 5 * 20;
							Players.get(itPlayer.getUniqueId()).TicksUntilArrow = 5;
							Players.get(itPlayer.getUniqueId()).TicksItLeft = Players.size() * 20 * 60;
							Players.get(It).Lives -= 1;

							hitPlayer.getInventory().setItem(8, Totem(Players.get(It).Lives));
							
							Location l2 = hitPlayer.getLocation();
							hitPlayer.getWorld().strikeLightning(l2);
							hitPlayer.getWorld().playSound(l2, Sound.BLOCK_BEACON_DEACTIVATE, 100, 2);
						}
					}
				}
			}
			else	
			{
				l.getWorld().spawnParticle(Particle.VILLAGER_ANGRY, l, 10, 0.3, 0.3, 0.3);
				l.getWorld().playSound(l, Sound.ENTITY_SLIME_SQUISH, 1, 1);
			}

		}
	}
	
	@EventHandler
	public void onProjectileLaunch(ProjectileLaunchEvent e)
	{
		if (!InGameArea(e.getEntity())) { return; }
		Player p = ((Player)e.getEntity().getShooter());
		
		if (e.getEntity().getType() == EntityType.ARROW)
		{
			if (IsInGame(p)) 
			{
				Players.get(p.getUniqueId()).TicksUntilArrow = -1;
			}
			e.getEntity().addPassenger((Entity) e.getEntity().getShooter());
		}
		else if (e.getEntity().getType() == EntityType.SNOWBALL)
		{
			Players.get(p.getUniqueId()).TicksUntilSnowball = -1;
		}
	}
	
	@EventHandler
	public void EntityDamaged(EntityDamageByEntityEvent  e)
	{
		if (!InGameArea(e.getEntity())) { return; }
		e.setCancelled(true);
	}
	
	public void StartGamePressed()
	{
		if (GameRunning || CountingDown) { return; }
		
		CountingDown = true;
		Amity.Broadcast(tagPrefix + "A game of tag will start in 30 seconds! To join, go to the tag lobby!");
		Bukkit.getScheduler().scheduleSyncDelayedTask(Amity.instance, new Runnable() {
		    @Override
		    public void run() {
		    	StartGameTimer();
		    }
		}, 30 * 20);
	}
	
	public void StartGameTimer()
	{
		CountingDown = false;
		GameRunning = true;
		
		Players.clear();
		
		ArrayList<String> playerNames = new ArrayList<String>();
		for (Player p : Bukkit.getOnlinePlayers())
		{
			if (Helper.PlayerInArea(p, lobby1, lobby2))
			{
				Players.put(p.getUniqueId(), new TagPlayer(p.getUniqueId()));
				playerNames.add(p.getDisplayName());
			}
		}
		
		for (Object idObject : Players.keySet().toArray())
		{
			Players.get((UUID)idObject).TicksItLeft = Players.size() * 60 * 20;
		}
		
		if (Players.size() < 2)
		{
			Players.clear();
			GameRunning = false;
			Amity.Broadcast(tagPrefix + ChatColor.RED + "Game cancelled, minimum 2 players.");
			return;
		}
		
		Amity.Broadcast(tagPrefix + "Tag has started! Players: " + ChatColor.AQUA + String.join(ChatColor.GOLD + ", " + ChatColor.AQUA, playerNames));

		RandomIt();
		Player itPlayer = Bukkit.getPlayer(It);
		Amity.Broadcast(tagPrefix + ChatColor.BLUE + itPlayer.getDisplayName() + ChatColor.GOLD + " was selected to be it first. You have 10 seconds until they're released!");

		itWaiting = true;
		for (UUID u : Players.keySet())
		{
			Player p = Bukkit.getPlayer(u);
			ClearAll(p);
			GiveGameTools(p);
			GiveEffects(p);
			p.setGameMode(GameMode.ADVENTURE);
			
			Bukkit.getScheduler().scheduleSyncDelayedTask(Amity.instance, new Runnable() {
			    @Override
			    public void run() {
			    	Amity.instance.TeleportPlayer(p, tagStart);
			    	if (u.equals(It))
			    	{
				    	itWaiting = false;
			    	}
			    }
			}, u.equals(It) ? 10 * 20 : 1);
		}
	}
	
	public void ClearAll(Player p)
	{
		p.getInventory().clear();
		for (PotionEffect effect : p.getActivePotionEffects())
		{
			p.removePotionEffect(effect.getType());
		}
	}
	
	public void GiveGameTools(Player p)
	{
		if (It.equals(p.getUniqueId()))
		{
			p.getInventory().setItem(0, Snowball());
		}
		
		if (Helper.GetLever(lever_arrow))
		{
			p.getInventory().setItem(1, Bow());
			p.getInventory().setItem(5, new ItemStack(Material.ARROW, 1));
		}
		p.getInventory().setItem(8, Totem(Players.get(p.getUniqueId()).Lives));
	}
	
	public void GiveEffects(Player p)
	{
		p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 1000000, 255, false, false, false));
		p.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 1000000, 255, false, false, false));
		p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 1000000, 255, false, false, false));
		p.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 1000000, 255, false, false, false));
	}
	
	public boolean IsInGame(Object e)
	{
		if (!GameRunning) { return false; }
		
		if (e instanceof Player)
		{
			Player p = (Player)e;
			return Players.containsKey(p.getUniqueId());
		}
		else 
		{
			return false;
		}
	}
	
	public void TagLobby(Player p)
	{
		Amity.instance.TeleportPlayer(p, lobby);
	}
	
	public void Explore(Player p)
	{
		if (!GameRunning)
		{
			Amity.instance.TeleportPlayer(p, tagStart);
			p.sendMessage(tagPrefix + "Use " + ChatColor.AQUA + "/lobby" + ChatColor.GOLD + " to get back.");
		}
		else
		{
			p.sendMessage(tagPrefix + ChatColor.RED + "You can't explore the map while a game is running.");
		}
	}
	
	public ItemStack Snowball()
	{
		ItemStack i = new ItemStack(Material.SNOWBALL, 1);
		ItemMeta m = i.getItemMeta();
		m.setDisplayName("Tagger");
		i.setItemMeta(m);
		return i;
	}
	
	public ItemStack Bow()
	{
		ItemStack i = new ItemStack(Material.BOW, 1);
		ItemMeta m = i.getItemMeta();
		m.setDisplayName("Flying Arrow Launcher");
		m.addEnchant(Enchantment.DURABILITY, 10, true);
		i.setItemMeta(m);
		return i;
	}
	
	public ItemStack Totem(Integer lives)
	{
		ItemStack i = new ItemStack(Material.TOTEM_OF_UNDYING, lives);
		ItemMeta m = i.getItemMeta();
		m.setDisplayName("Lives");
		i.setItemMeta(m);
		return i;
	}
	
	public ItemStack Clock(Integer timeLeft, String name)
	{
		ItemStack i = new ItemStack(Material.CLOCK, timeLeft);
		ItemMeta m = i.getItemMeta();
		m.setDisplayName(name);
		i.setItemMeta(m);
		return i;
	}
	
	public void HowToPlay(Player p)
	{
		String[] Messages = new String[]
		{
			"This is very similar to normal tag. One person is it.",
			"The runners run away from who's it.",
			"If you're it, use your snowball to tag others.",
			" - Arrow Of Flight -",
			"Everyone can use their bow to fly away.",
			"Arrows have a 10 second cooldown.",
			" - Blindness - ",
			"With blindess, you can still see the glows of other players.",
			"It will stop you from sprinting.",
		};
		
		for (String s : Messages)
		{
			p.sendMessage(tagPrefix + s);
		}
	}
	
	public void Spectate(Player p)
	{
		if (GameRunning && !IsInGame(p))
		{
			p.setGameMode(GameMode.SPECTATOR);
			Amity.instance.TeleportPlayer(p, tagStart);
			p.sendMessage("You are now spectating. You must use " + ChatColor.AQUA + "/lobby" + ChatColor.GOLD + " to get back.");
		}
		else
		{
			p.sendMessage(tagPrefix + "No game is being played to spectate right now.");
		}
	}
	
	public void Elimination(Player eliminated, Player couldWin)
	{
		Amity.instance.TeleportPlayer(eliminated, lobby);
		Players.remove(eliminated.getUniqueId());
		
		if (Players.size() > 1)
		{
			Amity.Broadcast(tagPrefix + ChatColor.AQUA + eliminated.getDisplayName() + ChatColor.GOLD + " has been eliminated!");
			eliminated.getWorld().playSound(eliminated.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1000000, 1);
			RandomIt();
			Amity.Broadcast(tagPrefix + ChatColor.AQUA + Bukkit.getPlayer(It).getDisplayName() + ChatColor.GOLD + " is now it! (no lives lost)");
		}
		else
		{
			eliminated.getWorld().playSound(eliminated.getLocation(), Sound.ENTITY_WITHER_SPAWN, 10000, 1);
			Amity.Broadcast(tagPrefix + ChatColor.AQUA + couldWin.getDisplayName() + ChatColor.AQUA + " has won!");
			GameRunning = false;
			
			for (Player p2 : Bukkit.getOnlinePlayers())
			{
				p2.setGameMode(GameMode.ADVENTURE);
				Amity.instance.TeleportPlayer(p2, lobby);
			}
		}
	}
	
	public void RandomIt()
	{
		int it = random.nextInt(Players.size());
		It = (UUID)Players.keySet().toArray()[it];
	}
	
	public boolean InGameArea(Entity p)
	{
		if (p.equals(null)) { return false; }
		return Helper.PlayerInArea(p, gameBox1, gameBox2) || Helper.PlayerInArea(p, lobby1, lobby2);
	}
	
	@EventHandler
	public void playerLeave(PlayerQuitEvent e)
	{
		if (IsInGame(e.getPlayer()))
		{
			Players.remove(e.getPlayer().getUniqueId());
			Amity.Broadcast(ChatColor.AQUA + e.getPlayer().getDisplayName() + ChatColor.GOLD + " has left the current Tag game.");
			if (It == e.getPlayer().getUniqueId())
			{
				Elimination(e.getPlayer(), Bukkit.getPlayer((UUID)Players.keySet().toArray()[0]));
			}
		}
	}
}