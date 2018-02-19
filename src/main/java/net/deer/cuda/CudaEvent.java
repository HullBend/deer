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
 * The {@code CudaEvent} class represents an event that can be queued in a
 * stream on a CUDA-capable device.
 * <p>
 * When no longer required, an event must be {@code close}d.
 */
public final class CudaEvent implements AutoCloseable {

    /** Default event creation flag. */
    public static final int FLAG_DEFAULT = 0;

    /** Use blocking synchronization. */
    public static final int FLAG_BLOCKING_SYNC = 1;

    /** Do not record timing data. */
    public static final int FLAG_DISABLE_TIMING = 2;

    /**
     * Event is suitable for interprocess use. FLAG_DISABLE_TIMING must be set.
     */
    public static final int FLAG_INTERPROCESS = 4;

    private final int deviceId;

    /**
     * Creates a new event on the specified device with default flags.
     *
     * @param device
     *            the specified device
     * @throws CudaException
     *             if a CUDA exception occurs
     */
    public CudaEvent(GPUDevice device) throws CudaException {
        this(device, FLAG_DEFAULT);
    }

    /**
     * Creates a new event on the specified device with the specified
     * {@code flags}.
     *
     * @param device
     *            the specified device
     * @param flags
     *            the desired flags
     * @throws CudaException
     *             if a CUDA exception occurs
     */
    public CudaEvent(GPUDevice device, int flags) throws CudaException {
        this.deviceId = device.getDeviceId();
//      this.nativeHandle = new AtomicLong(create(deviceId, flags)); // TODO
    }

    /**
     * Releases resources associated with this event.
     *
     * @throws CudaException
     *             if a CUDA exception occurs
     */
    @Override
    public void close() throws CudaException {
        // TODO
    }
}
