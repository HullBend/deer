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

public final class ParamsBuilder {

    private final KernelParams params;

    public ParamsBuilder(int parameterCount) {
        params = new KernelParams(parameterCount);
    }

    public KernelParams build() {
        return params.check();
    }

    /**
     * Replaces the parameter at the specified index with the given long value.
     *
     * @param index
     *            the index of the parameter to be set
     * @param value
     *            the value to be passed when the kernel is launched
     * @return this builder
     * @throws IndexOutOfBoundsException
     *             if {@code index} &lt; 0 or {@code index} &gt;= the size of
     *             the parameter list
     */
    public ParamsBuilder set(int index, long value) {
        params.set(index, value);
        return this;
    }

    /**
     * Replaces the parameter at the specified index with the given byte value.
     *
     * @param index
     *            the index of the parameter to be set
     * @param value
     *            the value to be passed when the kernel is launched
     * @return this builder
     * @throws IndexOutOfBoundsException
     *             if {@code index} &lt; 0 or {@code index} &gt;= the size of
     *             the parameter list
     */
    public ParamsBuilder set(int index, byte value) {
        return set(index, (long) value);
    }

    /**
     * Replaces the parameter at the specified index with the given character
     * value.
     *
     * @param index
     *            the index of the parameter to be set
     * @param value
     *            the value to be passed when the kernel is launched
     * @return this builder
     * @throws IndexOutOfBoundsException
     *             if {@code index} &lt; 0 or {@code index} &gt;= the size of
     *             the parameter list
     */
    public ParamsBuilder set(int index, char value) {
        return set(index, (long) value);
    }

    /**
     * Replaces the parameter at the specified index with the given buffer
     * address.
     *
     * @param index
     *            the index of the parameter to be set
     * @param value
     *            the value to be passed when the kernel is launched, or null to
     *            pass a null pointer
     * @return this builder
     * @throws IndexOutOfBoundsException
     *             if {@code index} &lt; 0 or {@code index} &gt;= the size of
     *             the parameter list
     */
    // public ParamsBuilder set(int index, CudaBuffer value) { // XXX
    // return set(index, value == null ? 0L : value.getAddress());
    // } // TODO CudaBuffer

    /**
     * Replaces the parameter at the specified index with the given double
     * value.
     *
     * @param index
     *            the index of the parameter to be set
     * @param value
     *            the value to be passed when the kernel is launched
     * @return this builder
     * @throws IndexOutOfBoundsException
     *             if {@code index} &lt; 0 or {@code index} &gt;= the size of
     *             the parameter list
     */
    public ParamsBuilder set(int index, double value) {
        return set(index, Double.doubleToRawLongBits(value));
    }

    /**
     * Replaces the parameter at the specified index with the given float value.
     *
     * @param index
     *            the index of the parameter to be set
     * @param value
     *            the value to be passed when the kernel is launched
     * @return this builder
     * @throws IndexOutOfBoundsException
     *             if {@code index} &lt; 0 or {@code index} &gt;= the size of
     *             the parameter list
     */
    public ParamsBuilder set(int index, float value) {
        return set(index, (long) Float.floatToRawIntBits(value));
    }

    /**
     * Replaces the parameter at the specified index with the given int value.
     *
     * @param index
     *            the index of the parameter to be set
     * @param value
     *            the value to be passed when the kernel is launched
     * @return this builder
     * @throws IndexOutOfBoundsException
     *             if {@code index} &lt; 0 or {@code index} &gt;= the size of
     *             the parameter list
     */
    public ParamsBuilder set(int index, int value) {
        return set(index, (long) value);
    }

    /**
     * Replaces the parameter at the specified index with a short value.
     *
     * @param index
     *            the index of the parameter to be set
     * @param value
     *            the value to be passed when the kernel is launched
     * @return this builder
     * @throws IndexOutOfBoundsException
     *             if {@code index} &lt; 0 or {@code index} &gt;= the size of
     *             the parameter list
     */
    public ParamsBuilder set(int index, short value) {
        return set(index, (long) value);
    }
}
