package stormyNeutron.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A quadtree implementation working with point data. i.e. it is not able to store shapes such as rectangles. 
 * Instead it stores and queries against points.<br />
 * The quadtree partitions space as required, and can potentially grow to have a quite large overhead. 
 * For this reason it is a good idea to set the initial size of the tree quite small, just large enough to encompass the needs of the application.<br />
 * The tree selfprunes itself when needed. Meaning that when objects are removed, parts of the tree might be removed aswell
 * @author munhunger
 *
 * @param <E> the type of objects to store
 */
public class QuadTree<E>
{
	/**
	 * An object wrapper that keeps track of the coordinates of the object
	 * @author munhunger
	 *
	 * @param <V> the type of object to store
	 */
	private class Element<V>
	{
		/**
		 * The coordinates of this object
		 */
		private int x, y;
		/**
		 * The object that is being wrapped
		 */
		private V obj;
		/**
		 * Creates a new wrapper around the object with the specified coordinates
		 * @param x coordinate
		 * @param y coordinate
		 * @param obj the object to wrap
		 */
		public Element(int x, int y, V obj)
		{
			this.x = x;
			this.y = y;
			this.obj = obj;
		}
	}
	/**
	 * Defines the coordinate and size of the quadtree
	 */
	private int x, y, width, height;
	
	/**
	 * The objects stored in this level of the tree
	 */
	List<Element<E>> elements;

	/**
	 * Child quadtrees each representing a unique quadrant
	 */
	private QuadTree<E> upperLeftTree, upperRightTree, bottomLeftTree, bottomRightTree;
	
	/**
	 * The parent tree.<br />
	 * Should only be null if current tree is root
	 */
	private QuadTree<E> parent;

	/**
	 * Creates a new quadtree root in the given space
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public QuadTree(int x, int y, int width, int height)
	{
		this(x, y, width, height, null);
	}
	
	/**
	 * Creates a new quadtree in the given space
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param parent the parent node. Should only be null for the root
	 */
	private QuadTree(int x, int y, int width, int height, QuadTree<E> parent)
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.parent = parent;
	}
	
	/**
	 * Remove the object at the x/y coordinate and prune as needed.
	 * @param x
	 * @param y
	 */
	public void remove(int x, int y)
	{
		if(inCurrent(x, y))
		{
			Iterator<Element<E>> it = elements.iterator();
			while(it.hasNext())
			{
				Element<E> e = it.next();
				if(e.x == x && e.y == y)
				{
					it.remove();
					break;
				}
			}
			if(elements.isEmpty() && parent != null)
				parent.removeChild(x, y);
		}
	}
	
	/**
	 * Adds the object at the specified coordinate in the quadtree
	 * @param elem the object to insert
	 * @param x coordinate
	 * @param y coordinate
	 */
	public void add(E elem, int x, int y)
	{
		if(inCurrent(x, y))
			elements.add(new Element<E>(x, y, elem));
		else
			getChild(x, y, true).add(elem, x, y);
	}
	
	/**
	 * Searches for the element at the specified coordinate in the tree
	 * @param x coordinate
	 * @param y coordinate
	 * @return the object at the specified x/y coordinate if such an object exists, otherwise null
	 */
	public E get(int x, int y)
	{
		if(inCurrent(x, y))
			for(Element<E> e : elements)
				if(e.x == x && e.y == y)
					return e.obj;
		QuadTree<E> child = getChild(x, y, false);
		return (child != null) ? child.get(x, y) : null;
	}
	
	/**
	 * Searches for elements whose points are in the search space
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @return A list of objects with coordinates inside the search space
	 */
	public List<E> get(int x, int y, int width, int height)
	{
		List<E> toReturn = new ArrayList<E>();
		if(inCurrent(x, y, width, height))
			for(Element<E> e : elements)
				if(contains(e.x, e.y, x, y, width, height))
					toReturn.add(e.obj);
		for(QuadTree<E> q : getChildren(x, y, width, height))
			toReturn.addAll(q.get(x, y, width, height));
		return toReturn;
	}
	
	/**
	 * Removes the child responsible for the area containing the x/y coordinate.<br />
	 * If, after removing that child the current level becomes unused(no elements at this level or in any nodes below it) this node will be removed if possible(not possible for root).<br />
	 * @param x
	 * @param y
	 */
	private void removeChild(int x, int y)
	{
		if(x < (this.x + this.width/2))
		{
			if(y < (this.y + this.height/2))
				upperLeftTree = null;
			else
				bottomLeftTree = null;
		}
		else
		{
			if(y < (this.y + this.height/2))
				upperRightTree = null;
			else
				bottomRightTree = null;
		}
		if(parent != null && elements.isEmpty() && upperLeftTree == null && upperRightTree == null && bottomLeftTree == null && bottomRightTree == null)
			parent.removeChild(x, y);
	}
	
	/**
	 * Returns the quadrant responsible for the input coordinate. i.e. the quadrant whose space is containing the coordinate
	 * @param x coordinate
	 * @param y coordinate
	 * @param createChild if true, the function will never return null as it creates a new tree in the quadrant if required. 
	 * If false, it will return null iff the current tree is a leaf node for the responsible quadrant
	 * @return the quadrant responsible for the input coordinate
	 */
	private QuadTree<E> getChild(int x, int y, boolean createChild)
	{
		QuadTree<E> returnTree;
		if(x < (this.x + this.width/2))
		{
			if(y < (this.y + this.height/2))
				returnTree = (createChild && upperLeftTree == null) ? new QuadTree<E>(x, y, width/2, height/2) : upperLeftTree;
			else
				returnTree = (createChild && bottomLeftTree == null) ? new QuadTree<E>(x, y+height/2, width/2, height/2) : bottomLeftTree;
		}
		else
		{
			if(y < (this.y + this.height/2))
				returnTree = (createChild && upperRightTree == null) ? new QuadTree<E>(x+width/2, y, width/2, height/2) : upperRightTree;
			else
				returnTree = (createChild && bottomRightTree == null) ? new QuadTree<E>(x+width/2, y+height/2, width/2, height/2) : bottomRightTree;
		}
		return returnTree;
	}
	
	/**
	 * Returns a list of all quadrants that contains any point of the search space
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @return
	 */
	private List<QuadTree<E>> getChildren(int x, int y, int width, int height)
	{
		List<QuadTree<E>> returnTree = new ArrayList<>();
		if(upperLeftTree != null && contains(x, y, width, height, upperLeftTree.x, upperLeftTree.y, upperLeftTree.width, upperLeftTree.height))
			returnTree.add(upperLeftTree);
		if(upperRightTree != null && contains(x, y, width, height, upperRightTree.x, upperRightTree.y, upperRightTree.width, upperRightTree.height))
			returnTree.add(upperRightTree);
		if(bottomLeftTree != null && contains(x, y, width, height, bottomLeftTree.x, bottomLeftTree.y, bottomLeftTree.width, bottomLeftTree.height))
			returnTree.add(bottomLeftTree);
		if(bottomRightTree != null && contains(x, y, width, height, bottomRightTree.x, bottomRightTree.y, bottomRightTree.width, bottomRightTree.height))
			returnTree.add(bottomRightTree);
		return returnTree;
	}

	/**
	 * Returns true if any point in the search space is covered by the space
	 * @param xa
	 * @param ya
	 * @param widtha
	 * @param heighta
	 * @param xb
	 * @param yb
	 * @param widthb
	 * @param heightb
	 * @return
	 */
	private boolean contains(int xa, int ya, int widtha, int heighta, int xb, int yb, int widthb, int heightb)
	{
		return contains(xa, ya, xb, yb, widthb, heightb) || contains(xa+widtha, ya, xb, yb, widthb, heightb)
				|| contains(xa, ya+heighta, xb, yb, widthb, heightb) || contains(xa+widtha, ya+heighta, xb, yb, widthb, heightb);
	}
	
	/**
	 * Checks if a point is covered by a space
	 * @param xa search coordinate
	 * @param ya search coordinate
	 * @param xb space coordinate
	 * @param yb space coordinate
	 * @param widthb space width
	 * @param heightb space height
	 * @return true iff the search coordinate is inside the space(edge inclusive)
	 */
	private boolean contains(int xa, int ya, int xb, int yb, int widthb, int heightb)
	{
		return (xa >= xb && xa <= xb+widthb) && (ya >= yb && ya <=yb+heightb);
	}
	
	/**
	 * Checks if the x/y coordinate falls on any of the mid-lines of the current tree.
	 * @param x coordinate
	 * @param y coordinate
	 * @return true iff the x/y coordinate falls exactly on any mid-lines(horizontal/vertical) of the tree. 
	 */
	private boolean inCurrent(int x, int y)
	{
		return x == (this.x + this.width/2) || y == (this.y + this.height/2);
	}
	
	/**
	 * Checks if the space is covered by this node
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @return true iff the space is crossing any of the mid-lines. The space is inclusive on all 4 edges
	 */
	private boolean inCurrent(int x, int y, int width, int height)
	{
		int xLine = this.x + this.width/2;
		int yLine = this.x + this.height/2;
		return (x <= xLine && x+width >= xLine) || (y <= yLine && y+height >= yLine); 
	}
}
