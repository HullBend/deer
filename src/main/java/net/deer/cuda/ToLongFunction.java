package net.deer.cuda;

/**
 * Represents a function that produces a long-valued result.
 * 
 * @param <T>
 *            the type of the input to the function
 */
interface ToLongFunction<T> {
	/**
	 * Applies this function to the given argument.
	 *
	 * @param value
	 *            the function argument
	 * @return the function result
	 */
	long applyAsLong(T value);
}
