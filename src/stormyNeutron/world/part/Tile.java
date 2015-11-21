package stormyNeutron.world.part;

import java.util.ArrayList;
import java.util.List;

import stormyNeutron.util.Invoke;
import stormyNeutron.util.Tuple;
import stormyNeutron.world.Dungeon;

public abstract class Tile 
{
	/**
	 * Each Tile subclass must add an entry to this list.
	 * The entry should provide an Invoke function returning true iff the tile believes that it can occupy a specific space in a dungeon.
	 */
	private static List<Tuple<Invoke<Boolean, Tuple<Dungeon,Tuple<Integer,Integer>>>, Tile>> tileAcceptance;
	
	/**
	 * Returns a list of Tile objects that are accepted by the space defined in the dungeon as passed by the input arguments
	 * Each tile must provide an Invoke function explaining whether or not it can be placed in a specific tile.
	 * @param d The dungeon to check against
	 * @param x the x-coordinate to check the insert of
	 * @param y the y-coordinate to check the insert of
	 * @return A list of tiles that are accepted by the space defined by the input arguments
	 * @see {@link Tile#tileAcceptance}
	 */
	public static List<Tile> acceptedTiles(Dungeon d, int x, int y)
	{
		List<Tile> accepted = new ArrayList<>();
		for(Tuple<Invoke<Boolean, Tuple<Dungeon,Tuple<Integer,Integer>>>, Tile> tuple : tileAcceptance)
			if(tuple.getFirst().invoke(new Tuple<Dungeon, Tuple<Integer,Integer>>(d, new Tuple<Integer,Integer>(x,y))))
				accepted.add(tuple.getSecond());
		return accepted;
	}
	
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

}
