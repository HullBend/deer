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
 * The {@code GPUDevice} class represents a CUDA-capable device.
 */
public final class GPUDevice {

    private final int deviceId;

    /**
     * Creates a device handle corresponding to {@code deviceId}.
     * <p>
     * No checking is done on {@code deviceId}, but it must be non-negative and
     * less than the value returned {@link #getCount()} to be useful.
     *
     * @param deviceId
     *            an integer identifying the device of interest
     */
    public GPUDevice(int deviceId) {
        this.deviceId = deviceId;
    }

    /**
     * Returns an integer identifying this device (the value provided when this
     * object was constructed).
     *
     * @return an integer identifying this device
     */
    public int getDeviceId() {
        return deviceId;
    }
}
