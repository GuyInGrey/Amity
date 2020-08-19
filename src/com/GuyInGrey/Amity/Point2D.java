package com.GuyInGrey.Amity;

public class Point2D
{
	public int X;
	public int Y;
	
	public Point2D()
	{
		X = 0;
		Y = 0;
	}
	
	public Point2D(int x, int y)
	{
		X = x;
		Y = y;
	}
	
	public Point2D add(Point2D a)
	{
		return new Point2D(a.X + X, a.Y + Y);
	}
	
	public Point2D add(int x, int y)
	{
		return new Point2D(X + x, Y + y);
	}
	
    @Override
	public String toString() { 
	   return "(" + X + "," + Y + ")";
	} 
}
