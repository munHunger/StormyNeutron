package stormyNeutron.graphics;

import java.util.ArrayList;
import java.util.List;

import stormyNeutron.world.Dungeon;
import stormyNeutron.world.part.Tile;

public class RenderQueue
{
	private Dungeon dungeon;
	private List<GraphicObject> dungeonQueue = new ArrayList<>();
	private List<GraphicObject> extras = new ArrayList<>();
	
	private List<GraphicObject> queue = new ArrayList<>();
	public RenderQueue()
	{
		
	}
	
	public RenderQueue(Dungeon d)
	{
		this.dungeon = d;
		List<Tile> tiles = dungeon.getTiles();
		dungeonQueue = new ArrayList<>();
		for(Tile t : tiles)
		{
			List<String> resources = t.getResources();
			dungeonQueue.add(new GraphicObject((float)t.getX()*32f, (float)t.getY()*32f, 0f, resources.get((int)(Math.random()*resources.size()))));
		}
		queue = new ArrayList<>();
		queue.addAll(dungeonQueue);
		queue.addAll(extras);
	}
	
	public List<GraphicObject> getModels()
	{
		return queue;
	}
}
