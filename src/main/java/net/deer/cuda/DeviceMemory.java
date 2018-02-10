/*******************************************************************************
 * Copyright (c) 2013, 2017 IBM Corp. and others
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License 2.0 which accompanies this
 * distribution and is available at https://www.eclipse.org/legal/epl-2.0/
 * or the Apache License, Version 2.0 which accompanies this distribution and
 * is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * This Source Code may also be made available under the following
 * Secondary Licenses when the conditions for such availability set
 * forth in the Eclipse Public License, v. 2.0 are satisfied: GNU
 * General Public License, version 2 with the GNU Classpath
 * Exception [1] and GNU General Public License, version 2 with the
 * OpenJDK Assembly Exception [2].
 *
 * [1] https://www.gnu.org/software/classpath/license.html
 * [2] http://openjdk.java.net/legal/assembly-exception.html
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0 OR GPL-2.0 WITH Classpath-exception-2.0 OR LicenseRef-GPL-2.0 WITH Assembly-exception
 *******************************************************************************/
package net.deer.cuda;

/**
 * The {@code DeviceMemory} class represents a region of memory on a specific
 * device.
 * <p>
 * Data may be transferred between the device and the Java host via the various
 * {@code transferToDevice} or {@code fetchToHost} methods. A buffer may be
 * filled with a specific pattern through use of one of the {@code fillXxx}
 * methods.
 * <p>
 * When no longer required, a DeviceMemory instance must be {@code close}d.
 */
public final class DeviceMemory implements AutoCloseable {

    private final int deviceId;

    private final long length;

    private final DeviceMemory root;

    private final Address memoryAddress;

    private static native long cudaMallocN(int deviceId, long byteCount) throws CudaException;

    // never throw an exception
    private static native long cudaFreeN(Address memoryAddress);

    private static native void cudaMemcpyHostToDeviceN(Address address, float[] array, int fromIndex, int toIndex)
            throws CudaException;

    private static native void cudaMemcpyDeviceToHostN(Address address, float[] array, int fromIndex, int toIndex)
            throws CudaException;

    private static final Cleaner cudaFreeCleaner = new Cleaner() {
        @Override
        public long release(Address address) throws CudaException {
            return cudaFreeN(address);
        }
    };

    /**
     * Allocates a new region on the specified {@code device} of size
     * {@code byteCount} bytes.
     *
     * @param device
     *            the device on which the region is to be allocated
     * @param byteCount
     *            the allocation size in bytes
     * @throws CudaException
     *             if a CUDA exception occurs
     */
    public DeviceMemory(GPUDevice device, long byteCount) throws CudaException {
        this.deviceId = device.getDeviceId();
        this.memoryAddress = Addresses.of(this, cudaMallocN(deviceId, byteCount), cudaFreeCleaner, deviceId);
        this.length = byteCount;
        this.root = null;
    }

    private DeviceMemory(DeviceMemory parent, int deviceId, Address parentAddress, long fromOffset, long length) {
        this.deviceId = deviceId;
        this.memoryAddress = Addresses.slice(this, parentAddress, fromOffset);
        this.length = length;
        this.root = parent;
    }

    Address getAddress() {
        if ((root == null || !root.memoryAddress.isClosed()) && !memoryAddress.isClosed()) {
            return memoryAddress;
        }
        try {
            memoryAddress.close();
        } catch (Exception ignore) {
        }
        throw new IllegalStateException("Root address or this address is already closed (or both)");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void finalize() throws Throwable {
        try {
            close();
        } finally {
            super.finalize();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws Exception {
        memoryAddress.close();
    }

    /**
     * Returns a sub-region of this buffer. The new buffer begins at the
     * specified fromOffset and extends to the specified toOffset (exclusive).
     *
     * @param fromOffset
     *            the byte offset of the start of the sub-region within this
     *            buffer
     * @param toOffset
     *            the byte offset of the end of the sub-region within this
     *            buffer
     * @return the specified sub-region
     * @throws IllegalArgumentException
     *             if {@code fromOffset > toOffset}
     * @throws IllegalStateException
     *             if this buffer has been closed (see {@link #close()})
     * @throws IndexOutOfBoundsException
     *             if {@code fromOffset} is negative, {@code toOffset > length},
     *             or the number of source bytes is larger than the length of
     *             this buffer
     */
    public DeviceMemory slice(long fromOffset, long toOffset) {
        if (fromOffset == 0L && toOffset == length) {
            return this;
        } else {
            rangeCheck(length, fromOffset, toOffset);
            return new DeviceMemory( // <br>
                    (root != null) ? root : this, // <br>
                    deviceId, // <br>
                    getAddress(), // <br>
                    fromOffset, // <br>
                    (toOffset - fromOffset)); // <br>
        }
    }

    /**
     * Returns the length in bytes of this buffer.
     *
     * @return the length in bytes of this buffer
     */
    public long getLength() {
        return length;
    }

    /**
     * Copies all data from the specified {@code array} (on the Java host) to
     * this buffer (on the device). Equivalent to
     * 
     * <pre>
     * transferToDevice(array, 0, array.length);
     * </pre>
     *
     * @param array
     *            the source array
     * @throws CudaException
     *             if a CUDA exception occurs
     * @throws IllegalStateException
     *             if this buffer has been closed (see {@link #close()})
     * @throws IndexOutOfBoundsException
     *             if the number of source bytes is larger than the length of
     *             this buffer
     */
    public void transferToDevice(float[] array) throws CudaException {
        transferToDevice(array, 0, array.length);
    }

    /**
     * Copies data from the specified {@code array} (on the Java host) to this
     * buffer (on the device). Elements are read from {@code array} beginning at
     * {@code fromIndex} continuing up to, but excluding, {@code toIndex}
     * storing them in the same order in this buffer.
     * <p>
     * A sub-buffer may be created (see {@link #atOffset(long)}) when the data
     * are to be copied somewhere other than the beginning of this buffer.
     *
     * @param array
     *            the source array
     * @param fromIndex
     *            the source starting offset (inclusive)
     * @param toIndex
     *            the source ending offset (exclusive)
     * @throws CudaException
     *             if a CUDA exception occurs
     * @throws IllegalArgumentException
     *             if {@code fromIndex > toIndex}
     * @throws IllegalStateException
     *             if this buffer has been closed (see {@link #close()})
     * @throws IndexOutOfBoundsException
     *             if {@code fromIndex} is negative,
     *             {@code toIndex > array.length}, or the number of source bytes
     *             is larger than the length of this buffer
     */
    public void transferToDevice(float[] array, int fromIndex, int toIndex) throws CudaException {
        rangeCheck(array.length, fromIndex, toIndex);
        lengthCheck(toIndex - fromIndex, 2);
        cudaMemcpyHostToDeviceN(getAddress(), array, fromIndex, toIndex);
    }

    /**
     * Copies data from this buffer (on the device) to the specified
     * {@code array} (on the Java host). Equivalent to
     * 
     * <pre>
     * fetchToHost(array, 0, array.length);
     * </pre>
     *
     * @param array
     *            the destination array
     * @throws CudaException
     *             if a CUDA exception occurs
     * @throws IllegalStateException
     *             if this buffer has been closed (see {@link #close()})
     * @throws IndexOutOfBoundsException
     *             if the number of required source bytes is larger than the
     *             length of this buffer
     */
    public void fetchToHost(float[] array) throws CudaException {
        fetchToHost(array, 0, array.length);
    }

    /**
     * Copies data from this buffer (on the device) to the specified
     * {@code array} (on the Java host). Elements are read starting at the
     * beginning of this buffer and stored in {@code array} beginning at
     * {@code fromIndex} continuing up to, but excluding, {@code toIndex}.
     * <p>
     * A sub-buffer may be created (see {@link #atOffset(long)}) when the source
     * data are not located at the beginning of this buffer.
     *
     * @param array
     *            the destination array
     * @param fromIndex
     *            the destination starting offset (inclusive)
     * @param toIndex
     *            the destination ending offset (exclusive)
     * @throws CudaException
     *             if a CUDA exception occurs
     * @throws IllegalArgumentException
     *             if {@code fromIndex > toIndex}
     * @throws IllegalStateException
     *             if this buffer has been closed (see {@link #close()})
     * @throws IndexOutOfBoundsException
     *             if {@code fromIndex} is negative,
     *             {@code toIndex > array.length}, or the number of required
     *             source bytes is larger than the length of this buffer
     */
    public void fetchToHost(float[] array, int fromIndex, int toIndex) throws CudaException {
        rangeCheck(array.length, fromIndex, toIndex);
        lengthCheck(toIndex - fromIndex, 2);
        cudaMemcpyDeviceToHostN(getAddress(), array, fromIndex, toIndex);
    }

    private void lengthCheck(long elementCount, int logBase2UnitSize) {
        if (!(0 <= elementCount && elementCount <= (length >> logBase2UnitSize))) {
            throw new IndexOutOfBoundsException("elementCount: " + elementCount);
        }
    }

    private static void rangeCheck(long length, long fromIndex, long toIndex) {
        if (fromIndex > toIndex) {
            throw new IllegalArgumentException("fromIndex(" + fromIndex + ") > toIndex(" + toIndex + ')');
        }
        if (fromIndex < 0) {
            throw new IndexOutOfBoundsException("fromIndex: " + fromIndex);
        }
        if (toIndex > length) {
            throw new IndexOutOfBoundsException("toIndex: " + toIndex);
        }
    }
}
