package com.GuyInGrey.Amity;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.spigotmc.event.entity.EntityDismountEvent;

public class ChairSit implements Listener
{
	@EventHandler
	public void RightClick(PlayerInteractEvent e)
	{
		Block b = e.getClickedBlock();
		if (e.getAction() != Action.RIGHT_CLICK_BLOCK) { return; }
		if (e.getPlayer().isSneaking()) { return; }
		
		boolean hasSign = false;
		
		Chunk c = b.getChunk();
		
		for (int x = 0; x < 16; x++)
		{
			for (int z = 0; z < 16; z++)
			{
				for (int y = 0; y < 255; y++)
				{
					Block b2 = c.getBlock(x, y, z);
					if (!b2.getType().toString().toLowerCase().contains("sign")) { continue; }
					Sign s = (Sign)b2.getState();
					if (!s.getLine(0).trim().toLowerCase().contentEquals("[chair]")) { continue; }
					hasSign = true;
				}
			}
		}
		
		boolean isValid = b.getType().toString().toLowerCase().contains("stairs") && hasSign;
		double yAdjust = 0;
		
		if (!isValid)
		{
			isValid = b.getType().toString().toLowerCase().contains("carpet") &&
					b.getLocation().add(0, -1, 0).getBlock().getType() == Material.PISTON_HEAD;
			yAdjust = -0.8;
		}
		
		if (!isValid)
		{
			isValid = b.getType() == Material.PISTON_HEAD &&
					b.getLocation().add(0, +1, 0).getBlock().getType().toString().toLowerCase().contains("carpet");
			yAdjust = 0.2;
		}
		
		if (!isValid) { return; }
		
		e.setCancelled(true);
		Location loc = b.getLocation();
		loc.setX(loc.getX() + 0.5);
		loc.setZ(loc.getZ() + 0.5);
		loc.setY((loc.getY() - 1) + yAdjust);
		
		ArmorStand stand = (ArmorStand)b.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
		stand.setVisible(false);
		stand.addPassenger(e.getPlayer());
		stand.setGravity(false);
		stand.setInvulnerable(true);
	}
	
	@EventHandler
	public void Dismounted(EntityDismountEvent e)
	{
		if (e.getDismounted().getType() == EntityType.ARMOR_STAND &&
				e.getEntity().getType() == EntityType.PLAYER)
		{
			e.getDismounted().remove();
		}
	}
}