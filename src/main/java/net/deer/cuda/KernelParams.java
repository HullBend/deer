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

public final class KernelParams {

    /*
     * A bit-mask of missing values (the bit (1 << i) is set if the parameter at
     * values[i] is missing).
     */
    private long mask;

    private final Address[] values;

    /**
     * Creates a copy of the given parameter block.
     *
     * @param other
     *            the parameter block to be copied
     */
    public KernelParams(KernelParams other) {
        this.mask = other.mask;
        this.values = other.values.clone();
    }

    /**
     * Creates a new bundle of parameter values.
     *
     * @param count
     *            the number of values to be passed when the kernel is launched
     * @throws IllegalArgumentException
     *             if the count is negative or greater than 64
     */
    KernelParams(int count) {
        if (0 <= count && count <= Long.SIZE) {
            this.mask = (count == Long.SIZE) ? -1L : (1L << count) - 1;
            this.values = new Address[count];
        } else {
            throw new IllegalArgumentException("count: " + count);
        }
    }

    Address[] getValues() {
        return values;
    }

    boolean isComplete() {
        return mask == 0L;
    }

    KernelParams check() {
        if (!isComplete()) {
            throw new IllegalArgumentException("Not all parameters have their values defined");
        }
        return this;
    }

    /**
     * Replaces the parameter at the specified index with the given address
     * value.
     *
     * @param index
     *            the index of the parameter to be set
     * @param value
     *            the value to be passed when the kernel is launched
     * @throws IndexOutOfBoundsException
     *             if {@code index} &lt; 0 or {@code index} &gt;= the size of
     *             this parameter list
     */
    void set(int index, Address value) {
        if (value == null) {
            throw new IllegalArgumentException("null value at index " + index);
        }
        if (0 <= index && index < values.length) {
            mask &= ~(1L << index);
            values[index] = value;
        } else {
            throw new IndexOutOfBoundsException("index: " + index);
        }
    }
}
