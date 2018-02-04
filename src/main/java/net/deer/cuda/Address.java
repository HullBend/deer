package net.deer.cuda;

/**
 * A Java wrapper for native resources that can be represented as pointers or
 * handles.
 */
interface Address extends AutoCloseable {

	/**
	 * Was there already an attempt to release the underlying native resource?
	 * 
	 * @return {@code true} if a release of the underlying resource has already
	 *         been attempted, otherwise {@code false}.
	 */
	boolean isClosed();

	/**
	 * {@inheritDoc}
	 */
	@Override
	boolean equals(Object otherAddress);

	/**
	 * {@inheritDoc}
	 */
	@Override
	int hashCode();

	/**
	 * {@inheritDoc}
	 */
	@Override
	String toString();
}
