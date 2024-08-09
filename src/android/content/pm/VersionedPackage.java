/*
 * Copyright (C) 2017 The Android Open Source Project
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

import android.annotation.IntRange;

import android.os.Parcel;
import android.os.Parcelable;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Encapsulates a package and its version code.
 */
public final class VersionedPackage implements Parcelable {
    private final String mPackageName;
    private final long mVersionCode;

    /** @hide */
    @Retention(RetentionPolicy.SOURCE)
    @IntRange(from = PackageManager.VERSION_CODE_HIGHEST)
    public @interface VersionCode {
    }

    /**
     * Creates a new instance. Use {@link PackageManager#VERSION_CODE_HIGHEST}
     * to refer to the highest version code of this package.
     * 
     * @param packageName The package name.
     * @param versionCode The version code.
     */
    public VersionedPackage(String packageName,
            @VersionCode int versionCode) {
        throw new RuntimeException("Stub");
    }

    /**
     * Creates a new instance. Use {@link PackageManager#VERSION_CODE_HIGHEST}
     * to refer to the highest version code of this package.
     * 
     * @param packageName The package name.
     * @param versionCode The version code.
     */
    public VersionedPackage(String packageName,
            @VersionCode long versionCode) {
        throw new RuntimeException("Stub");
    }

    private VersionedPackage(Parcel parcel) {
        throw new RuntimeException("Stub");
    }

    /**
     * Gets the package name.
     *
     * @return The package name.
     */
    public String getPackageName() {
        throw new RuntimeException("Stub");
    }

    /**
     * @deprecated use {@link #getLongVersionCode()} instead.
     */
    @Deprecated
    public @VersionCode int getVersionCode() {
        throw new RuntimeException("Stub");
    }

    /**
     * Gets the version code.
     *
     * @return The version code.
     */
    public @VersionCode long getLongVersionCode() {
        throw new RuntimeException("Stub");
    }

    @Override
    public String toString() {
        throw new RuntimeException("Stub");
    }

    @Override
    public boolean equals(Object o) {
        throw new RuntimeException("Stub");
    }

    @Override
    public int hashCode() {
        throw new RuntimeException("Stub");
    }

    @Override
    public int describeContents() {
        throw new RuntimeException("Stub");
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        throw new RuntimeException("Stub");
    }

    public static final Creator<VersionedPackage> CREATOR = new Creator<VersionedPackage>() {
        @Override
        public VersionedPackage createFromParcel(Parcel source) {
            throw new RuntimeException("Stub");
        }

        @Override
        public VersionedPackage[] newArray(int size) {
            throw new RuntimeException("Stub");
        }
    };
}