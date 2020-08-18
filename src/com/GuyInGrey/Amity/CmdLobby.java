package com.GuyInGrey.Amity;

import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

public class CmdLobby implements CommandExecutor
{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if (sender instanceof Player)
		{
			Player p = (Player)sender;
			
			boolean canTeleport = true;
			
			if (Tag.instance.IsInGame(p)) { canTeleport = false; }
			
			if (canTeleport)
			{
				if (p.getGameMode() == GameMode.SPECTATOR)
				{
					p.setGameMode(GameMode.ADVENTURE);
				}
				Amity.instance.TeleportPlayer(p, Amity.instance.lobby);
			}
			else
			{
				sender.sendMessage(ChatColor.RED + "You cannot teleport at this time.");
			}
			return true;
		}
		sender.sendMessage("This command cannot be used in the console.");
		return false;
	}
}
