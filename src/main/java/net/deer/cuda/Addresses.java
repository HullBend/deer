package net.deer.cuda;

import java.util.concurrent.atomic.AtomicBoolean;

final class Addresses {

	/* package */static Address of(Object referent, long address, Cleaner deallocatorFunction) {
		return AddressImpl.create(address, referent, deallocatorFunction);
	}

	private static final class AddressImpl implements Address {
		private final long address;
		private final Object referent;
		private final Cleaner cleaner;
		private final AtomicBoolean isClosed = new AtomicBoolean();

		private AddressImpl(long address, Object referent, Cleaner deallocatorFunction) {
			this.address = address;
			this.referent = referent;
			this.cleaner = deallocatorFunction;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void close() throws Exception {
			if (isClosed.compareAndSet(false, true)) {
				long rc = tryClose();
				if (rc != 0L) {
					throw new LeakedResourceException("rc = " + rc + " " + this.toString());
				}
			}
		}

		private long tryClose() {
			if (cleaner != null) {
				return cleaner.applyAsLong(this);
			}
			return 0L;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isClosed() {
			return isClosed.get();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			int h = 0x7FFFF + (int) (address ^ (address >>> 32));
			return (h << 19) - h + System.identityHashCode(referent);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(Object otherAddress) {
			if (this == otherAddress) {
				return true;
			}
			if (otherAddress instanceof AddressImpl) {
				AddressImpl o = (AddressImpl) otherAddress;
				return address == o.address && referent == o.referent;
			}
			return false;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			StringBuilder buf = new StringBuilder(128);
			return buf.append("[Address: ").append(address).append(" @").append(System.identityHashCode(referent))
					.append(" ").append((referent == null) ? "null" : referent.toString()).append(" , closed: ")
					.append(isClosed.get()).append(" ]").toString();
		}

		static AddressImpl create(long address, Object referent, Cleaner deallocatorFunction) {
			return new AddressImpl(address, referent, deallocatorFunction);
		}
	}

	@SuppressWarnings("serial")
	private static final class LeakedResourceException extends RuntimeException {
		LeakedResourceException(String message) {
			super(message);
		}
	}

	private Addresses() {
		throw new AssertionError();
	}
}
