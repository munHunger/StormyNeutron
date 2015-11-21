package stormyNeutron.util;
/**
 * Tuple class that holds 2 objects of type E and V.<br />
 * The references are immutable.
 * @author munhunger
 *
 * @param <E> Type of object 1
 * @param <V> Type of object 2
 */
public class Tuple <E, V>
{
	/**
	 * The first object
	 */
	private E obj1;
	/**
	 * The second object
	 */
	private V obj2;
	
	/**
	 * Constructor that sets the 2 objects
	 * @param obj1 the first object
	 * @param obj2 the second object
	 */
	public Tuple(E obj1, V obj2)
	{
		this.obj1 = obj1;
		this.obj2 = obj2;
	}
	
	/**
	 * returns the first object
	 * @return
	 */
	public E getFirst()
	{
		return obj1;
	}
	
	/**
	 * returns the second object
	 * @return
	 */
	public V getSecond()
	{
		return obj2;
	}
}
