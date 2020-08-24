package com.GuyInGrey.DungeonGenerator;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class DungeonCommand implements CommandExecutor
{

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
  {
    if (!sender.isOp())
    {
      return false;
    }
    if (args[0].contentEquals("reload"))
    {
      DungeonGenerator.LoadStructures();
      return true;
    } else if (args[0].contentEquals("generate"))
    {
      Dungeon d = DungeonGenerator.Run(50, 50);
      sender.sendMessage(d.Rooms + "");
      return true;
    }

    return false;
  }

}
