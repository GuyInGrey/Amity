package com.GuyInGrey.Amity;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.type.Switch;
import org.bukkit.entity.Entity;

public class Helper
{
	public static boolean PlayerInArea(Entity p, Location corner1, Location corner2)
	{
		if (!corner2.getWorld().getName().contentEquals(corner1.getWorld().getName()) ||
			!p.getWorld().getName().contentEquals(corner1.getWorld().getName()))
		{ return false; }

	    double[] dim = new double[2];
	    
	    dim[0] = corner1.getX();
	    dim[1] = corner2.getX();
	    Arrays.sort(dim);
	    if(p.getLocation().getX() > dim[1] || p.getLocation().getX() < dim[0])
	        return false;
	    
	    dim[0] = corner1.getZ();
	    dim[1] = corner2.getZ();
	    Arrays.sort(dim);
	    if(p.getLocation().getZ() > dim[1] || p.getLocation().getZ() < dim[0])
	        return false;
	    
	    dim[0] = corner1.getY();
	    dim[1] = corner2.getY();
	    Arrays.sort(dim);
	    if(p.getLocation().getY() > dim[1] || p.getLocation().getY() < dim[0])
	        return false;
	    
	    return true;
	}
	
	public static boolean GetLever(Location l)
	{
		if (l.getBlock().getType() != Material.LEVER) { return false; }
		Switch lever = (Switch)(l.getBlock().getBlockData());
		return lever.isPowered();
	}
	
	public static void FillArea(Material mat, Location loc1, Location loc2)
	{
		World w = loc1.getWorld();
		
		Location l1 = new Location(w, 
			Math.min(loc1.getX(), loc2.getX()),
			Math.min(loc1.getY(), loc2.getY()),
			Math.min(loc1.getZ(), loc2.getZ())
		);
		
		Location l2 = new Location(w, 
			Math.max(loc1.getX(), loc2.getX()),
			Math.max(loc1.getY(), loc2.getY()),
			Math.max(loc1.getZ(), loc2.getZ())
		);
		
		//Bukkit.broadcastMessage(loc1 + " : " + loc2 + " === " + l1 + " : " + l2);
		
		for (var x = l1.getX(); x <= l2.getX(); x++)
		{
			for (var y = l1.getY(); y <= l2.getY(); y++)
			{
				for (var z = l1.getZ(); z <= l2.getZ(); z++)
				{
					Location toSet = new Location(loc1.getWorld(), x, y, z);
					toSet.getBlock().setType(mat);
				}	
			}
		}
	}
}