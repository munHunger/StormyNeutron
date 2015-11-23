package stormyNeutron.world.part;

import java.util.ArrayList;
import java.util.List;

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
				List<Tile> adjacent = arg.getFirst().getAdjacentTiles(arg.getSecond().getFirst(), arg.getSecond().getSecond());
				for(Tile t : adjacent)
					if(t instanceof Entrance)
					{
						List<Tile> entranceAdjecency = arg.getFirst().getAdjacentTiles(arg.getSecond().getFirst(), arg.getSecond().getSecond());
						return 1f-((entranceAdjecency.size())/2f);
					}
					else if(t instanceof Grass)
						return 0.85f;
				return 0.75f;
			}
			
		}, new Grass(0,0)));
	}
	
	@Override
	public Tile cloneTo(int x, int y)
	{
		return new Grass(x, y);
	}
	
	@Override
	public List<String> getResources()
	{
		List<String> resources = new ArrayList<>();
		resources.add("GrassTile001");
		resources.add("GrassTile002");
		resources.add("GrassTile003");
		return resources;
	}
}
