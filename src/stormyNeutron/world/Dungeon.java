package stormyNeutron.world;

import java.util.ArrayList;
import java.util.List;

import stormyNeutron.util.QuadTree;
import stormyNeutron.util.Tuple;
import stormyNeutron.world.part.Tile;

public class Dungeon 
{
	private QuadTree<Tile> tiles;
	
	private int width, height;
	
	public void addTile(Tile t, int x, int y)
	{
		tiles.add(t, x, y);
	}
	
	public Dungeon(int width, int height)
	{
		tiles = new QuadTree<>(0, 0, width, height);
		this.width = width;
		this.height = height;
	}
	
	public List<Tile> getTiles()
	{
		return tiles.get(0, 0, width, height);
	}
	
	public List<Tile> getAdjacentTiles(int x, int y)
	{
		return tiles.get(x-1, y-1, 2, 2);
	}
	
	public List<Tuple<Integer,Integer>> getEmptyAdjacentPositions()
	{
		List<Tuple<Integer,Integer>> positions = new ArrayList<>();
		List<Tile> tiles = getTiles();
		for(Tile t : tiles)
		{
			if(this.tiles.get(t.getX()-1, t.getY()) == null)
				positions.add(new Tuple<Integer, Integer>(t.getX()-1, t.getY()));
			if(this.tiles.get(t.getX()+1, t.getY()) == null)
				positions.add(new Tuple<Integer, Integer>(t.getX()+1, t.getY()));
			if(this.tiles.get(t.getX(), t.getY()-1) == null)
				positions.add(new Tuple<Integer, Integer>(t.getX(), t.getY()-1));
			if(this.tiles.get(t.getX(), t.getY()+1) == null)
				positions.add(new Tuple<Integer, Integer>(t.getX(), t.getY()+1));
		}
		return positions;
	}
}
