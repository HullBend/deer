package net.deer.cuda;

import java.util.concurrent.atomic.AtomicBoolean;

final class Addresses {

    /* package */static Address of(Object referent, long address, Cleaner deallocatorFunction, int deviceId) {
        return AddressImpl.create(address, referent, deallocatorFunction, null, deviceId);
    }

    // A sliced address has no cleaner since it is always the primordial root
    // address that must be closed natively
    /* package */static Address slice(Object shadowReferent, Address parent, long offset) {
        AddressImpl base = (AddressImpl) parent;
        Address root = (base.root != null) ? base.root : parent;
        // We have to keep the base reference reachable until the very end of
        // this method, therefore construct the sliced address *before* the
        // final check and ReachabilityFence
        Address sliced = AddressImpl.create(base.address + offset, shadowReferent, null, root, base.deviceId);
        if (root.isClosed()) {
            // This is an attempt to create a slice from a root buffer that has
            // already been closed.
            throw new IllegalArgumentException("Root address is already closed: " + root.toString());
        }
        ReachabilityFence.protect(base);
        return sliced;
    }

    private static final class AddressImpl implements Address {
        private final int deviceId;
        private final long address;
        private final Object referent;
        private final Address root;
        private final Cleaner cleaner;
        private final AtomicBoolean isClosed = new AtomicBoolean();

        private AddressImpl(long address, Object referent, Cleaner deallocatorFunction, Address root, int deviceId) {
            this.deviceId = deviceId;
            this.address = address;
            this.referent = referent;
            this.root = root;
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
                return cleaner.release(this);
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
            h = (h << 19) - h + System.identityHashCode(referent);
            return (h << 19) - h + deviceId;
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
                return address == o.address && referent == o.referent && deviceId == o.deviceId;
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
                    .append(" device: ").append(deviceId).append(" , ")
                    .append((referent == null) ? "null" : referent.toString()).append(" , closed: ")
                    .append(isClosed.get()).append(" ]").toString();
        }

        static AddressImpl create(long address, Object referent, Cleaner deallocatorFunction, Address root,
                int deviceId) {
            return new AddressImpl(address, referent, deallocatorFunction, root, deviceId);
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
