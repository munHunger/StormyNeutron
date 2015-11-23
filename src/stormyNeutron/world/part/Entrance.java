package stormyNeutron.world.part;

import java.util.ArrayList;
import java.util.List;

public class Entrance extends Tile
{
	public Entrance(int x, int y)
	{
		super(x,y);
	}
	
	@Override
	public List<String> getResources()
	{
		List<String> resources = new ArrayList<>();
		resources.add("ShrubberyExtra");
		return resources;
	}
}
