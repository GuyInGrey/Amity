package com.GuyInGrey.Amity;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class MazeCommand implements CommandExecutor
{
	public static MazeCommand instance;
	Location origin;
	Material block;
	int width = 50;
	int height = 50;
	
	ArrayList<Area> ToFill = new ArrayList<Area>();
	
	public MazeCommand()
	{
		origin = new Location(Bukkit.getWorlds().get(0), 61.5, 245, -33.5, -90, 0);
		block = Material.GLASS;
		instance = this;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		
		ArrayList<Connection> maze = MazeGenerator.Run(width, height);
		
		ClearArea();
		
		for (Connection c : maze)
		{
			Location a = ConLoc(c.A);
			Location b = ConLoc(c.B);
			b.add(0, 1, 0);
			
			ToFill.add(new Area(a, b));
		}
		
		return true;
	}
	
	public void ClearArea()
	{
		for (int x = 0; x < (width * 2) + 1; x++)
		{
			for (int z = 0; z < (height * 2) + 1; z++)
			{
				for (int y = 0; y < 3; y++)
				{
					origin.clone().add(x - 1, y - 1, z - 1).getBlock().setType(block);
				}
			}
		}
	}
	
	public Location ConLoc(Point2D c)
	{
		return new Location(Bukkit.getWorlds().get(0), origin.getX() + (c.X * 2), origin.getY(), origin.getZ() + (c.Y * 2));
	}
	
	int ticks = 0;
	
	public void Tick()
	{
		int perTick = 4;
		
		for (int i = 0; i < perTick; i++)
		{
			if (ToFill.size() > 0)
			{
				Helper.FillArea(Material.AIR, ToFill.get(0).A, ToFill.get(0).B);
				ToFill.remove(0);
			}
		}
		
		if (ticks % 20 == 0 && ToFill.size() != 0)
		{
			Bukkit.broadcastMessage(ToFill.size() + " remaining.");
		}
		
		ticks++;
	}
	
	public class Area
	{
		public Location A;
		public Location B;
		
		public Area(Location a, Location b)
		{
			A = a;
			B = b;
		}
	}
}