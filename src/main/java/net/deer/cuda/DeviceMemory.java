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
 * {@code copyTo} or {@code copyFrom} methods. A buffer may be filled with a
 * specific pattern through use of one of the {@code fillXxx} methods.
 * <p>
 * When no longer required, a DeviceMemory instance must be {@code close}d.
 */
public final class DeviceMemory implements AutoCloseable {

    private final int deviceId;

    private final long length;

    private final DeviceMemory parent;

    private final Address memoryAddress;

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
        this.memoryAddress = null; // TODO new
                                    // AtomicLong(allocate(this.deviceId,
                                    // byteCount));
        this.length = byteCount;
        this.parent = null;
    }

    Address getAddress() {
        // TODO
        throw new UnsupportedOperationException();
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
        // TODO
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the length in bytes of this buffer.
     *
     * @return the length in bytes of this buffer
     */
    public long getLength() {
        return length;
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
