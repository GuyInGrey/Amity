package com.GuyInGrey.Amity;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

public class MazeGenerator
{
	static Random rand;
	
	public static ArrayList<Connection> Run(int width, int height)
	{
		if (rand == null) { rand = new Random(); }
		Point2D start = new Point2D(0, 0);
		Stack<Point2D> stack = new Stack<Point2D>();
		stack.push(start);
		
		ArrayList<Connection> connections = new ArrayList<Connection>();
		boolean[][] visited = new boolean[width][height];
		visited[start.X][start.Y] = true;
		int remaining = (width * height) - 1;
		
		while (remaining > 0)
		{
			Point2D last = stack.peek();
			ArrayList<Point2D> available = new ArrayList<Point2D>();
			for (int x = -1; x <= 1; x++)
			{
				for (int y = -1; y <= 1; y++)
				{
					if (x != 0 && y != 0) { continue; }
					Point2D test = new Point2D(last.X + x, last.Y + y);
					if (test.X >= 0 && test.X < width && test.Y >= 0 && test.Y < height
							&& !visited[test.X][test.Y])
					{ available.add(test); }
				}
			}
			
			if (available.size() > 0)
			{
				Point2D next = available.get(rand.nextInt(available.size()));
				stack.push(next);
				remaining--;
				visited[next.X][next.Y] = true;
				connections.add(new Connection(last, next));
			}
			else { stack.pop();	}
		}
		
		stack.clear();
		return connections;
	}
}
