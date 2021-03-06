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
 * The {@code CudaStream} class represents an independent queue of work for a
 * specific {@link GPUDevice}.
 * <p>
 * When no longer required, a stream must be {@code close}d.
 */
public final class CudaStream implements AutoCloseable {

    /**
     * Default stream creation flag.
     */
    public static final int FLAG_DEFAULT = 0;

    /**
     * Stream creation flag requesting no implicit synchronization with the
     * default stream.
     */
    public static final int FLAG_NON_BLOCKING = 1;

    // TODO ...

    private final int deviceId;

    /**
     * Creates a new stream on the specified device, with the default flags
     * and the default priority.
     *
     * @param device
     *          the specified device
     * @throws CudaException
     *          if a CUDA exception occurs
     */
    public CudaStream(GPUDevice device) throws CudaException {
        this.deviceId = device.getDeviceId();
//      this.nativeHandle = new AtomicLong(create(this.deviceId)); // TODO
    }

    /**
     * Closes this stream. Any work queued on this stream will be allowed to
     * complete: this method does not wait for that work (if any) to complete.
     * 
     * @throws CudaException
     *             if a CUDA exception occurs
     */
    @Override
    public void close() throws CudaException {
        // TODO
    }
}
