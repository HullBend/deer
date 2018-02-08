package net.deer.cuda;

import java.util.concurrent.atomic.AtomicBoolean;

final class Addresses {

    /* package */static Address of(Object referent, long address, Cleaner deallocatorFunction, int deviceId) {
        return AddressImpl.create(address, referent, deallocatorFunction, deviceId);
    }

    // A sliced address has no cleaner since it is always the base address that
    // must be closed natively (i.e., the root *not* the immediate parent)
    /* package */static Address slice(Object shadowReferent, Address parent, long offset) {
        AddressImpl base = (AddressImpl) parent;
        // We have to keep the base reference reachable until the very end of
        // this method, therefore construct the sliced address **before** the
        // final check
        Address sliced = AddressImpl.create(base.address + offset, shadowReferent, null, base.deviceId);
        if (base.cleaner != null && base.isClosed.get()) {
            // This is an attempt to create a slice from a base buffer that has
            // already been closed. It doesn't matter when the base is a sliced
            // buffer (cleaner == null) that has already been closed (which has
            // no effect). The problem in the latter case is that we don't know
            // whether the *real* base buffer is already closed (because we
            // don't know its identity)
            // TODO: we probably need an additonal *parent* reference to the
            // root of the slice hierarchy!
            throw new IllegalArgumentException("Address is already closed: " + base.toString());
        }
        return sliced;
    }

    // TODO:
    // 1) add reference to the root
    private static final class AddressImpl implements Address {
        private final int deviceId;
        private final long address;
        private final Object referent;
        private final Cleaner cleaner;
        private final AtomicBoolean isClosed = new AtomicBoolean();

        private AddressImpl(long address, Object referent, Cleaner deallocatorFunction, int deviceId) {
            this.deviceId = deviceId;
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

        static AddressImpl create(long address, Object referent, Cleaner deallocatorFunction, int deviceId) {
            return new AddressImpl(address, referent, deallocatorFunction, deviceId);
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
