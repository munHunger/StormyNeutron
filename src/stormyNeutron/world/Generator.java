package stormyNeutron.world;

import stormyNeutron.util.Invoke;
import stormyNeutron.util.Tuple;
import stormyNeutron.world.part.Entrance;
import stormyNeutron.world.part.Grass;
import stormyNeutron.world.part.Road;
import stormyNeutron.world.part.Tile;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Generator 
{
	public static void main(String[] args)
	{
		Grass.init();
		Road.init();
		JFrame frame = new JFrame("Map generator");
		@SuppressWarnings("serial")
		JPanel panel = new JPanel()
		{
			public void paintComponent(Graphics g)
			{
				super.paintComponent(g);
				super.setBackground(Color.GRAY);
				List<Tile> tiles = generateDungeon(64).getTiles();
				g.setColor(Color.CYAN);
				int zoom = 10;
				for(Tile t : tiles)
				{
					if(t instanceof Grass)
						g.setColor(Color.GREEN);
					else if(t instanceof Entrance)
						g.setColor(Color.CYAN);
					else if(t instanceof Road)
						g.setColor(Color.BLACK);
					g.fillRect(t.getX()*zoom, t.getY()*zoom, zoom, zoom);
				}
				try
				{
					Thread.sleep(1000);
				}
				catch(Exception e){}
				repaint();
			}
		};
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(panel);
		frame.setSize(300, 300);
		frame.setVisible(true);
		frame.setAlwaysOnTop(true);
	}
	
	public static Dungeon generateDungeon(int tiles)
	{
		Dungeon d = new Dungeon(32,32);
		d.addTile(new Entrance(16,16), 16, 16);
		for(int i = 0; i < tiles-1; i++)
		{
			List<Tuple<Integer, Integer>> availablePositions = d.getEmptyAdjacentPositions();
			if(availablePositions.isEmpty())
				return d;
			Tuple<Integer, Integer> position = availablePositions.get((int)(Math.random()*availablePositions.size()));
			List<Tuple<Float, Tile>> tileProbability = new ArrayList<>();
			for(Tuple<Invoke<Float, Tuple<Dungeon,Tuple<Integer,Integer>>>, Tile> p : Tile.tileAcceptance)
				tileProbability.add(new Tuple<Float, Tile>(p.getFirst().invoke(new Tuple<Dungeon,Tuple<Integer,Integer>>(d, position)), p.getSecond()));
			Collections.sort(tileProbability, new Comparator<Tuple<Float, Tile>>()
			{
				@Override
				public int compare(Tuple<Float, Tile> arg0, Tuple<Float, Tile> arg1)
				{
					return arg1.getFirst().compareTo(arg0.getFirst());
				}
				
			});
			for(Tuple<Float, Tile> candidate : tileProbability)
				if(Math.random() < candidate.getFirst())
				{
					d.addTile(candidate.getSecond().cloneTo(position.getFirst(), position.getSecond()), position.getFirst(), position.getSecond());
					break;
				}
		}
		return d;
	}
}
