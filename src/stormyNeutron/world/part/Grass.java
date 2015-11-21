package stormyNeutron.world.part;

import stormyNeutron.util.Invoke;
import stormyNeutron.util.Tuple;
import stormyNeutron.world.Dungeon;

public class Grass extends Tile 
{
	public Grass(int x, int y)
	{
		super(x,y);
	}
	
	public static void init()
	{
		Tile.tileAcceptance.add(new Tuple<Invoke<Float, Tuple<Dungeon,Tuple<Integer,Integer>>>, Tile>(new Invoke<Float, Tuple<Dungeon,Tuple<Integer,Integer>>>()
		{
			@Override
			public Float invoke(Tuple<Dungeon, Tuple<Integer, Integer>> arg)
			{
				return 1f;
			}
			
		}, new Grass(0,0)));
	}
	
	@Override
	public Tile cloneTo(int x, int y)
	{
		return new Grass(x, y);
	}
}
