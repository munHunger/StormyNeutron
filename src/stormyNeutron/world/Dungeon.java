package stormyNeutron.world;

import java.util.ArrayList;
import java.util.List;

import stormyNeutron.world.part.Tile;

public class Dungeon 
{
	private List<Tile> tiles;
	
	public void addTile(Tile t)
	{
		tiles.add(t);
	}
	
	public Dungeon()
	{
		tiles = new ArrayList<>();
	}
	
	public List<Tile> getTiles()
	{
		return tiles;
	}
}
