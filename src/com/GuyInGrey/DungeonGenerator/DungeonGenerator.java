package com.GuyInGrey.DungeonGenerator;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import com.GuyInGrey.Amity.Amity;
import com.GuyInGrey.Amity.Point2D;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;

public class DungeonGenerator
{
  public static DungeonGenerator instance;
  public static Random rand;
  public static ArrayList<Structure> Structures;

  public DungeonGenerator()
  {
    instance = this;
  }

  public static Dungeon Run(int width, int height)
  {
    if (rand == null)
    {
      rand = new Random(50);
    }
    Dungeon toReturn = new Dungeon();
    boolean[][] visited = new boolean[width][height];

    for (Structure s : Structures)
    {
      for (int i = 0; i < s.Minimum;)
      {
        int x = rand.nextInt(width);
        int y = rand.nextInt(height);

        for (Point2D p : s.Cells)
        {
          Point2D p2 = new Point2D(p.X + x, p.Y + y);
          if (visited[p2.X][p2.Y])
          {
            continue;
          }
        }

        for (Point2D p : s.Cells)
        {
          Point2D p2 = new Point2D(p.X + x, p.Y + y);
          visited[p2.X][p2.Y] = true;
        }

        toReturn.Rooms.put(new Point2D(x, y), s);

        i++;
      }
    }

    // Create rooms. Make 1000 attempts, ignoring overlaps.
    for (int i = 0; i < 1000; i++)
    {
      Structure s = Structures.get(rand.nextInt(Structures.size()));
      int count = 0;
      for (Structure s2 : toReturn.Rooms.values())
      {
        if (s2.equals(s))
        {
          count++;
        }
      }

      if (count >= s.Maximum)
      {
        continue;
      }
      int x = rand.nextInt(width);
      int y = rand.nextInt(height);

      for (Point2D p : s.Cells)
      {
        Point2D p2 = new Point2D(p.X + x, p.Y + y);
        if (visited[p2.X][p2.Y])
        {
          continue;
        }
      }

      for (Point2D p : s.Cells)
      {
        Point2D p2 = new Point2D(p.X + x, p.Y + y);
        visited[p2.X][p2.Y] = true;
      }

      toReturn.Rooms.put(new Point2D(x, y), s);
    }

    return toReturn;
  }

  public static void LoadStructures()
  {
    Structures = new ArrayList<Structure>();

    for (File file : new File(Amity.instance.getDataFolder().getAbsolutePath() + File.separator + "structures")
        .listFiles())
    {
      Structure s = new Structure();

      YamlConfiguration struct = YamlConfiguration.loadConfiguration(file);
      String schematicName = struct.getString("schematicName");
      File schemFile = new File(Amity.instance.getDataFolder().getParentFile().getAbsolutePath() + File.separator
          + "WorldEdit" + File.separator + "schematics" + File.separator + schematicName + ".schem");

      Bukkit.broadcastMessage(schemFile.exists() + "");

      Clipboard clipboard = null;
      ClipboardFormat format = ClipboardFormats.findByFile(schemFile);
      try (ClipboardReader reader = format.getReader(new FileInputStream(schemFile)))
      {
        clipboard = reader.read();
      } catch (Exception e)
      {
        Bukkit.broadcastMessage("Failed to load schematic: " + schemFile.getName());
      }
      if (clipboard == null)
      {
        continue;
      }

      s.Schematic = clipboard;

      s.Cells = new ArrayList<Point2D>();
      String[] points = struct.getString("cells").split("-");
      for (String point : points)
      {
        String[] pointParts = point.split(",");
        Point2D p = new Point2D(Integer.parseInt(pointParts[0]), Integer.parseInt(pointParts[1]));
        s.Cells.add(p);
      }

      s.Minimum = struct.getInt("minimum");
      s.Maximum = struct.getInt("maximum");

      Structures.add(s);
    }

    Bukkit.broadcastMessage("Loaded " + Structures.size() + " structures.");
  }
}