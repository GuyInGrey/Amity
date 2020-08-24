package com.GuyInGrey.DungeonGenerator;

import java.util.ArrayList;
import java.util.HashMap;

import com.GuyInGrey.Amity.Connection;
import com.GuyInGrey.Amity.Point2D;

public class Dungeon
{
  public ArrayList<Connection> Paths;
  public HashMap<Point2D, Structure> Rooms;

  public Dungeon()
  {
    Paths = new ArrayList<Connection>();
    Rooms = new HashMap<Point2D, Structure>();
  }
}
