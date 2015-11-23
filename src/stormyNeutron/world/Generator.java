package stormyNeutron.world;

import stormyNeutron.graphics.Graphics3D;
import stormyNeutron.graphics.RenderQueue;
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

import org.lwjgl.util.vector.Vector3f;

public class Generator 
{
	public static void main(String[] args)
	{
		Grass.init();
		Road.init();

		final Graphics3D g3d = new Graphics3D();
		new Thread(g3d).start();
		while(!g3d.ready())
		{
			try { Thread.sleep(10); } 
			catch (InterruptedException e){ e.printStackTrace(); }
		}
		g3d.getCamera().setPosition(new Vector3f(16f*16f, 16f*16f, 32f));
		
		JFrame frame = new JFrame("Map generator");
		@SuppressWarnings("serial")
		JPanel panel = new JPanel()
		{
			private boolean firstRun = true;
			public void paintComponent(Graphics g)
			{
				if(!firstRun)
				{
					try
					{
						Thread.sleep(10000);
					}
					catch(Exception e){}
				}
				firstRun = false;
				super.paintComponent(g);
				super.setBackground(Color.GRAY);
				Dungeon d = generateDungeon(64);
				g3d.setRenderQueue(new RenderQueue(d));
				List<Tile> tiles = d.getTiles();
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
