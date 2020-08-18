package com.GuyInGrey.Amity;

import java.util.Arrays;

import org.bukkit.Location;
import org.bukkit.Material;
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
}