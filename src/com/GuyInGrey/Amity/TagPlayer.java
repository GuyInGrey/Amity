package com.GuyInGrey.Amity;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TagPlayer
{
  public TagPlayer(UUID id)
  {
    ID = id;
  }

  public UUID ID;

  public Player Player()
  {
    return Bukkit.getPlayer(ID);
  }

  public int TicksUntilArrow = -1;
  public int TicksUntilSnowball = -1;
  public int Lives = 3;
  public int TicksItLeft = 2 * 60 * 20;
}