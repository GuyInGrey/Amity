package com.GuyInGrey.Amity;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class CellBorderCommand implements CommandExecutor
{
  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
  {
    if (!(sender instanceof Player))
    {
      return false;
    }

    Player p = (Player) sender;
    Location o = p.getLocation();

    int distance = 2;
    int xOrigin = (int) (o.getX() - (o.getX() % 8));
    int yOrigin = (int) (o.getY() - (o.getY() % 8));
    int zOrigin = (int) (o.getZ() - (o.getZ() % 8));

    for (int x = -distance; x <= distance; x++)
    {
      for (int y = -distance; y <= distance; y++)
      {
        for (int z = -distance; z <= distance; z++)
        {
          int x2 = xOrigin + (x * 8);
          int y2 = yOrigin + (y * 8);
          int z2 = zOrigin + (z * 8);

          SendChange(p, o, x2, y2, z2);
          SendChange(p, o, x2 - 1, y2, z2);
          SendChange(p, o, x2, y2 - 1, z2);
          SendChange(p, o, x2, y2, z2 - 1);
          SendChange(p, o, x2, y2 - 1, z2 - 1);
          SendChange(p, o, x2 - 1, y2, z2 - 1);
          SendChange(p, o, x2 - 1, y2 - 1, z2);
          SendChange(p, o, x2 - 1, y2 - 1, z2 - 1);
        }
      }
    }

    return true;
  }

  public void SendChange(Player p, Location origin, int x, int y, int z)
  {
    Location l = new Location(origin.getWorld(), x, y, z);
    p.sendBlockChange(l, Material.GLASS.createBlockData());
    new BukkitRunnable()
    {
      @Override
      public void run()
      {
        p.sendBlockChange(l, l.getBlock().getBlockData());
      }
    }.runTaskLater(Amity.instance, 5 * 20);
  }
}
