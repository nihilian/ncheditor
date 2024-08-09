/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.content.pm;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Transfer a large list of Parcelable objects across an IPC. Splits into
 * multiple transactions if needed.
 *
 * Caveat: for efficiency and security, all elements must be the same concrete
 * type.
 * In order to avoid writing the class name of each object, we must ensure that
 * each object is the same type, or else unparceling then reparceling the data
 * may yield
 * a different result if the class name encoded in the Parcelable is a Base
 * type.
 * See b/17671747.
 *
 * @hide
 */
abstract class BaseParceledListSlice<T> implements Parcelable {
    private static final String TAG = "ParceledListSlice";
    private static final boolean DEBUG = false;

    private static final int MAX_IPC_SIZE = IBinder.getSuggestedMaxIpcSizeBytes();

    /**
     * As of 2024 and for some time, max size has been 64KB. If a single
     * element is too large, this class will write too big of Parcels,
     * so log. 64KB/4 is 16KB is still pretty big for a single element
     * (which could result in a ~64KB + 16KB = 80KB transaction). We may
     * want to reduce the warning size just in case. Though, 64KB is
     * already quite large for binder transactions, another strategy may
     * be needed.
     */
    private static final int WARN_ELM_SIZE = MAX_IPC_SIZE / 4;

    private List<T> mList;

    private int mInlineCountLimit = Integer.MAX_VALUE;

    private boolean mHasBeenParceled = false;

    public BaseParceledListSlice() {
    }

    public BaseParceledListSlice(List<T> list) {
        throw new RuntimeException("Stub");
    }

    BaseParceledListSlice(Parcel p, ClassLoader loader) {
        throw new RuntimeException("Stub");
    }

    private Class<?> readVerifyAndAddElement(Parcelable.Creator<?> creator, Parcel p, ClassLoader loader, Class<?> listElementClass) {
        throw new RuntimeException("Stub");
    }

    private T readCreator(Parcelable.Creator<?> creator, Parcel p, ClassLoader loader) {
        throw new RuntimeException("Stub");
    }

    private static void verifySameType(final Class<?> expected, final Class<?> actual) {
        throw new RuntimeException("Stub");
    }

    public List<T> getList() {
        throw new RuntimeException("Stub");
    }

    /**
     * Set a limit on the maximum number of entries in the array that will be
     * included
     * inline in the initial parcelling of this object.
     */
    public void setInlineCountLimit(int maxCount) {
        throw new RuntimeException("Stub");
    }

    /**
     * Write this to another Parcel. Note that this discards the internal Parcel
     * and should not be used anymore. This is so we can pass this to a Binder
     * where we won't have a chance to call recycle on this.
     *
     * This method can only be called once per BaseParceledListSlice to ensure that
     * the referenced list can be cleaned up before the recipient cleans up the
     * Binder reference.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        throw new RuntimeException("Stub");
    }

    protected abstract void writeElement(T parcelable, Parcel reply, int callFlags);

    protected abstract void writeParcelableCreator(T parcelable, Parcel dest);

    protected abstract Parcelable.Creator<?> readParcelableCreator(Parcel from, ClassLoader loader);
}
