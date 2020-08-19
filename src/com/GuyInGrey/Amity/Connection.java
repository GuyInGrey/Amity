package com.GuyInGrey.Amity;

public class Connection
{
	public Point2D A;
	public Point2D B;
	
	public Connection(Point2D a, Point2D b)
	{
		A = a;
		B = b;
	}
	
    @Override
	public String toString() { 
	   return A + " -> " + B; 
	} 
}
