package stormyNeutron.world;

import stormyNeutron.world.part.Entrance;
import stormyNeutron.world.part.Grass;
import stormyNeutron.world.part.Tile;

import java.awt.Color;
import java.awt.Graphics;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Generator 
{
	public static void main(String[] args)
	{
		JFrame frame = new JFrame("Map generator");
		JPanel panel = new JPanel()
		{
			public void paintComponent(Graphics g)
			{
				super.paintComponent(g);
				super.setBackground(Color.GRAY);
				List<Tile> tiles = generateDungeon(10).getTiles();
				g.setColor(Color.CYAN);
				int zoom = 10;
				for(Tile t : tiles)
				{
					if(t instanceof Grass)
						g.setColor(Color.GREEN);
					else if(t instanceof Entrance)
						g.setColor(Color.CYAN);
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
		Dungeon d = new Dungeon();
		d.addTile(new Entrance((int)(Math.random()*10),(int)(Math.random()*10)));
		for(int i = 0; i < tiles-1; i++)
			d.addTile(new Grass((int)(Math.random()*10),(int)(Math.random()*10)));
		return d;
	}
}
