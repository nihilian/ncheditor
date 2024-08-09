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

package android.os;

import android.annotation.AppIdInt;
import android.annotation.UserIdInt;

import android.os.Parcel;
import android.os.Parcelable;

import android.util.SparseArray;

import java.io.PrintWriter;

import java.util.List;

/**
 * Representation of a user on the device.
 */
public final class UserHandle implements Parcelable {
    /**
     * @hide Range of uids allocated for a user.
     */
    public static final int PER_USER_RANGE = 100000;

    /** @hide A user id to indicate all users on the device */
    public static final @UserIdInt int USER_ALL = -1;

    /** @hide A user handle to indicate all users on the device */
    public static final UserHandle ALL = new UserHandle(USER_ALL);

    /** @hide A user id to indicate the currently active user */
    public static final @UserIdInt int USER_CURRENT = -2;

    /** @hide A user handle to indicate the current user of the device */
    public static final UserHandle CURRENT = new UserHandle(USER_CURRENT);

    /**
     * @hide A user id to indicate that we would like to send to the current
     *       user, but if this is calling from a user process then we will send it
     *       to the caller's user instead of failing with a security exception
     */
    public static final @UserIdInt int USER_CURRENT_OR_SELF = -3;

    /**
     * @hide A user handle to indicate that we would like to send to the current
     *       user, but if this is calling from a user process then we will send it
     *       to the caller's user instead of failing with a security exception
     */
    public static final UserHandle CURRENT_OR_SELF = new UserHandle(USER_CURRENT_OR_SELF);

    /** @hide An undefined user id */
    public static final @UserIdInt int USER_NULL = -10000;

    private static final UserHandle NULL = new UserHandle(USER_NULL);

    /**
     * @hide A user id constant to indicate the "owner" user of the device
     * @deprecated Consider using either {@link UserHandle#USER_SYSTEM} constant or
     *             check the target user's flag
     *             {@link android.content.pm.UserInfo#isAdmin}.
     */
    public static final @UserIdInt int USER_OWNER = 0;

    /**
     * @hide A user handle to indicate the primary/owner user of the device
     * @deprecated Consider using either {@link UserHandle#SYSTEM} constant or
     *             check the target user's flag
     *             {@link android.content.pm.UserInfo#isAdmin}.
     */
    public static final UserHandle OWNER = new UserHandle(USER_OWNER);

    /** @hide A user id constant to indicate the "system" user of the device */
    public static final @UserIdInt int USER_SYSTEM = 0;

    /** @hide A user serial constant to indicate the "system" user of the device */
    public static final int USER_SERIAL_SYSTEM = 0;

    /** @hide A user handle to indicate the "system" user of the device */
    public static final UserHandle SYSTEM = new UserHandle(USER_SYSTEM);

    /**
     * @hide Enable multi-user related side effects. Set this to false if
     *       there are problems with single user use-cases.
     */
    public static final boolean MU_ENABLED = true;

    /** @hide */
    public static final int MIN_SECONDARY_USER_ID = 10;

    /** @hide */
    public static final int MAX_SECONDARY_USER_ID = Integer.MAX_VALUE / UserHandle.PER_USER_RANGE - 1;

    /**
     * (Arbitrary) user handle cache size.
     * {@link #CACHED_USER_HANDLES} caches user handles in the range of
     * [{@link #MIN_SECONDARY_USER_ID}, {@link #MIN_SECONDARY_USER_ID} +
     * {@link #NUM_CACHED_USERS}).
     *
     * For other users, we cache UserHandles in {link #sExtraUserHandleCache}.
     *
     * Normally, {@link #CACHED_USER_HANDLES} should cover all existing users, but
     * use
     * {link #sExtraUserHandleCache} to ensure {@link UserHandle#of} will not cause
     * too many
     * object allocations even if the device happens to have a secondary user with a
     * large number
     * (e.g. the user kept creating and removing the guest user?).
     */
    private static final int NUM_CACHED_USERS = MU_ENABLED ? 8 : 0;

    /** @see #NUM_CACHED_USERS} */
    private static final UserHandle[] CACHED_USER_HANDLES = new UserHandle[NUM_CACHED_USERS];

    /**
     * Extra cache for users beyond CACHED_USER_HANDLES.
     *
     * @see #NUM_CACHED_USERS
     * @hide
     */
    public static final SparseArray<UserHandle> sExtraUserHandleCache = new SparseArray<>(0);

    /**
     * Max size of {@link #sExtraUserHandleCache}. Once it reaches this size, we
     * select
     * an element to remove at random.
     *
     * @hide
     */
    public static final int MAX_EXTRA_USER_HANDLE_CACHE_SIZE = 32;

    static {
        // Not lazily initializing the cache, so that we can share them across
        // processes.
        // (We'll create them in zygote.)
        for (int i = 0; i < CACHED_USER_HANDLES.length; i++) {
            CACHED_USER_HANDLES[i] = new UserHandle(MIN_SECONDARY_USER_ID + i);
        }
    }

    /** @hide */
    public static final int ERR_GID = -1;
    /** @hide */
    public static final int AID_ROOT = android.os.Process.ROOT_UID;
    /** @hide */
    public static final int AID_APP_START = android.os.Process.FIRST_APPLICATION_UID;
    /** @hide */
    public static final int AID_APP_END = android.os.Process.LAST_APPLICATION_UID;
    /** @hide */
    public static final int AID_SHARED_GID_START = android.os.Process.FIRST_SHARED_APPLICATION_GID;
    /** @hide */
    public static final int AID_CACHE_GID_START = android.os.Process.FIRST_APPLICATION_CACHE_GID;

    /** The userId represented by this UserHandle. */
    final @UserIdInt int mHandle;

    /**
     * Checks to see if the user id is the same for the two uids, i.e., they belong
     * to the same
     * user.
     * 
     * @hide
     */
    public static boolean isSameUser(int uid1, int uid2) {
        throw new RuntimeException("Stub");
    }

    /**
     * Checks to see if both uids are referring to the same app id, ignoring the
     * user id part of the
     * uids.
     * 
     * @param uid1 uid to compare
     * @param uid2 other uid to compare
     * @return whether the appId is the same for both uids
     * @hide
     */
    public static boolean isSameApp(int uid1, int uid2) {
        throw new RuntimeException("Stub");
    }

    /**
     * Whether a UID is an "isolated" UID.
     * 
     * @hide
     */
    public static boolean isIsolated(int uid) {
        throw new RuntimeException("Stub");
    }

    /**
     * Whether a UID belongs to a regular app. *Note* "Not a regular app" does not
     * mean
     * "it's system", because of isolated UIDs. Use {@link #isCore} for that.
     * 
     * @hide
     */
    public static boolean isApp(int uid) {
        throw new RuntimeException("Stub");
    }

    /**
     * Whether a UID belongs to a system core component or not.
     * 
     * @hide
     */
    public static boolean isCore(int uid) {
        throw new RuntimeException("Stub");
    }

    /**
     * Whether a UID belongs to a shared app gid.
     * 
     * @hide
     */
    public static boolean isSharedAppGid(int uid) {
        throw new RuntimeException("Stub");
    }

    /**
     * Returns the user for a given uid.
     * 
     * @param uid A uid for an application running in a particular user.
     * @return A {@link UserHandle} for that user.
     */
    public static UserHandle getUserHandleForUid(int uid) {
        throw new RuntimeException("Stub");
    }

    /**
     * Returns the user id for a given uid.
     * 
     * @hide
     */
    public static @UserIdInt int getUserId(int uid) {
        throw new RuntimeException("Stub");
    }

    /** @hide */
    public static @UserIdInt int getCallingUserId() {
        throw new RuntimeException("Stub");
    }

    /** @hide */
    public static @AppIdInt int getCallingAppId() {
        throw new RuntimeException("Stub");
    }

    /** @hide */
    public static int[] fromUserHandles(List<UserHandle> users) {
        throw new RuntimeException("Stub");
    }

    /** @hide */
    public static List<UserHandle> toUserHandles(int[] userIds) {
        throw new RuntimeException("Stub");
    }

    /** @hide */
    public static UserHandle of(@UserIdInt int userId) {
        throw new RuntimeException("Stub");
    }

    /** @hide */
    public static UserHandle getUserHandleFromExtraCache(@UserIdInt int userId) {
        throw new RuntimeException("Stub");
    }

    /**
     * Returns the uid that is composed from the userId and the appId.
     * 
     * @hide
     */
    public static int getUid(@UserIdInt int userId, @AppIdInt int appId) {
        throw new RuntimeException("Stub");
    }

    /**
     * Returns the uid representing the given appId for this UserHandle.
     *
     * @param appId the AppId to compose the uid
     * @return the uid representing the given appId for this UserHandle
     * @hide
     */
    public int getUid(@AppIdInt int appId) {
        throw new RuntimeException("Stub");
    }

    /**
     * Returns the app id (or base uid) for a given uid, stripping out the user id
     * from it.
     * 
     * @hide
     */
    public static @AppIdInt int getAppId(int uid) {
        throw new RuntimeException("Stub");
    }

    /**
     * Returns the gid shared between all apps with this userId.
     * 
     * @hide
     */
    public static int getUserGid(@UserIdInt int userId) {
        throw new RuntimeException("Stub");
    }

    /**
     * Returns the gid shared between all users with the app that this uid
     * represents, or -1 if the
     * uid is invalid.
     * 
     * @hide
     */
    public static int getSharedAppGid(int uid) {
        throw new RuntimeException("Stub");
    }

    /** @hide */
    public static int getSharedAppGid(@UserIdInt int userId, @AppIdInt int appId) {
        throw new RuntimeException("Stub");
    }

    /**
     * Returns the app id for a given shared app gid. Returns -1 if the ID is
     * invalid.
     * 
     * @hide
     */
    public static @AppIdInt int getAppIdFromSharedAppGid(int gid) {
        throw new RuntimeException("Stub");
    }

    /** @hide */
    public static int getCacheAppGid(int uid) {
        throw new RuntimeException("Stub");
    }

    /** @hide */
    public static int getCacheAppGid(@UserIdInt int userId, @AppIdInt int appId) {
        throw new RuntimeException("Stub");
    }

    /**
     * Generate a text representation of the uid, breaking out its individual
     * components -- user, app, isolated, etc.
     * 
     * @hide
     */
    public static void formatUid(StringBuilder sb, int uid) {
        throw new RuntimeException("Stub");
    }

    /**
     * Generate a text representation of the uid, breaking out its individual
     * components -- user, app, isolated, etc.
     *
     * @param uid The uid to format
     * @return A string representing the UID with its individual components broken
     *         out
     * @hide
     */
    public static String formatUid(int uid) {
        throw new RuntimeException("Stub");
    }

    /**
     * Generate a text representation of the uid, breaking out its individual
     * components -- user, app, isolated, etc.
     * 
     * @hide
     */
    public static void formatUid(PrintWriter pw, int uid) {
        throw new RuntimeException("Stub");
    }

    /** @hide */
    public static @UserIdInt int parseUserArg(String arg) {
        throw new RuntimeException("Stub");
    }

    /**
     * Returns the user id of the current process
     * 
     * @return user id of the current process
     * @hide
     */
    public static @UserIdInt int myUserId() {
        throw new RuntimeException("Stub");
    }

    /**
     * Returns true if this UserHandle refers to the owner user; false otherwise.
     * 
     * @return true if this UserHandle refers to the owner user; false otherwise.
     * @hide
     * @deprecated please use {@link #isSystem()} or check for
     *             {@link android.content.pm.UserInfo#isPrimary()}
     *             {@link android.content.pm.UserInfo#isAdmin()} based on your
     *             particular use case.
     */
    public boolean isOwner() {
        throw new RuntimeException("Stub");
    }

    /**
     * @return true if this UserHandle refers to the system user; false otherwise.
     * @hide
     */
    public boolean isSystem() {
        throw new RuntimeException("Stub");
    }

    /** @hide */
    public UserHandle(@UserIdInt int userId) {
        throw new RuntimeException("Stub");
    }

    /**
     * Returns the userId stored in this UserHandle.
     * 
     * @hide
     */
    public @UserIdInt int getIdentifier() {
        throw new RuntimeException("Stub");
    }

    @Override
    public String toString() {
        throw new RuntimeException("Stub");
    }

    @Override
    public boolean equals(Object obj) {
        throw new RuntimeException("Stub");
    }

    @Override
    public int hashCode() {
        throw new RuntimeException("Stub");
    }

    public int describeContents() {
        throw new RuntimeException("Stub");
    }

    public void writeToParcel(Parcel out, int flags) {
        throw new RuntimeException("Stub");
    }

    /**
     * Write a UserHandle to a Parcel, handling null pointers. Must be
     * read with {@link #readFromParcel(Parcel)}.
     *
     * @param h   The UserHandle to be written.
     * @param out The Parcel in which the UserHandle will be placed.
     *
     * @see #readFromParcel(Parcel)
     */
    public static void writeToParcel(UserHandle h, Parcel out) {
        throw new RuntimeException("Stub");
    }

    /**
     * Read a UserHandle from a Parcel that was previously written
     * with {@link #writeToParcel(UserHandle, Parcel)}, returning either
     * a null or new object as appropriate.
     *
     * @param in The Parcel from which to read the UserHandle
     * @return Returns a new UserHandle matching the previously written
     *         object, or null if a null had been written.
     *
     * @see #writeToParcel(UserHandle, Parcel)
     */
    public static UserHandle readFromParcel(Parcel in) {
        throw new RuntimeException("Stub");
    }

    public static final Parcelable.Creator<UserHandle> CREATOR = new Parcelable.Creator<UserHandle>() {
        public UserHandle createFromParcel(Parcel in) {
            throw new RuntimeException("Stub");
        }

        public UserHandle[] newArray(int size) {
            throw new RuntimeException("Stub");
        }
    };

    /**
     * Instantiate a new UserHandle from the data in a Parcel that was
     * previously written with {@link #writeToParcel(Parcel, int)}. Note that you
     * must not use this with data written by
     * {@link #writeToParcel(UserHandle, Parcel)} since it is not possible
     * to handle a null UserHandle here.
     *
     * @param in The Parcel containing the previously written UserHandle,
     *           positioned at the location in the buffer where it was written.
     */
    public UserHandle(Parcel in) {
        throw new RuntimeException("Stub");
    }
}