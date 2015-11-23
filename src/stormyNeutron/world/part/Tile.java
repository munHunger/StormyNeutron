package stormyNeutron.world.part;

import java.util.ArrayList;
import java.util.List;

import stormyNeutron.util.Invoke;
import stormyNeutron.util.Tuple;
import stormyNeutron.world.Dungeon;

public abstract class Tile implements Renderable
{
	/**
	 * Each Tile subclass must add an entry to this list.
	 * The entry should provide an Invoke function returning the probability of the tile being placed at the given input
	 */
	public static List<Tuple<Invoke<Float, Tuple<Dungeon,Tuple<Integer,Integer>>>, Tile>> tileAcceptance = new ArrayList<>();
	
	private int x, y;
	
	public Tile(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	public int getX()
	{
		return x;
	}

	public int getY()
	{
		return y;
	}
	
	public Tile cloneTo(int first, int second)
	{
		return null;
	}

}
