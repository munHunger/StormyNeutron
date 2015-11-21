package stormyNeutron.util;

/**
 * Interface for invoking a function expecting a certain object return in response to a specific object input
 * @author munhunger
 *
 * @param <E> The function return type
 * @param <V> The function argument type
 */
public interface Invoke <E, V>
{
	/**
	 * Invoke function
	 * @param arg
	 * @return
	 */
	public E invoke(V arg);
}
