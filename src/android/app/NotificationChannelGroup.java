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
package android.app;

import android.os.Parcel;
import android.os.Parcelable;

import android.util.proto.ProtoOutputStream;

import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

/**
 * A grouping of related notification channels. e.g., channels that all belong to a single account.
 */
public final class NotificationChannelGroup implements Parcelable {

    /**
     * The maximum length for text fields in a NotificationChannelGroup. Fields will be truncated at
     * this limit.
     * @hide
     */
    public static final int MAX_TEXT_LENGTH = 1000;

    private static final String TAG_GROUP = "channelGroup";
    private static final String ATT_NAME = "name";
    private static final String ATT_DESC = "desc";
    private static final String ATT_ID = "id";
    private static final String ATT_BLOCKED = "blocked";
    private static final String ATT_USER_LOCKED = "locked";

    /**
     * @hide
     */
    public static final int USER_LOCKED_BLOCKED_STATE = 0x00000001;

    /**
     * @see #getId()
     */
    private final String mId;
    private CharSequence mName;
    private String mDescription;
    private boolean mBlocked;
    private List<NotificationChannel> mChannels = new ArrayList();
    // Bitwise representation of fields that have been changed by the user
    private int mUserLockedFields;

    /**
     * Creates a notification channel group.
     *
     * @param id The id of the group. Must be unique per package.  the value may be truncated if
     *           it is too long.
     * @param name The user visible name of the group. You can rename this group when the system
     *             locale changes by listening for the {@link Intent#ACTION_LOCALE_CHANGED}
     *             broadcast. <p>The recommended maximum length is 40 characters; the value may be
     *             truncated if it is too long.
     */
    public NotificationChannelGroup(String id, CharSequence name) {
        throw new RuntimeException("Stub");
    }

    /**
     * @hide
     */
    protected NotificationChannelGroup(Parcel in) {
        throw new RuntimeException("Stub");
    }

    private String getTrimmedString(String input) {
        throw new RuntimeException("Stub");
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        throw new RuntimeException("Stub");
    }

    /**
     * Returns the id of this group.
     */
    public String getId() {
        throw new RuntimeException("Stub");
    }

    /**
     * Returns the user visible name of this group.
     */
    public CharSequence getName() {
        throw new RuntimeException("Stub");
    }

    /**
     * Returns the user visible description of this group.
     */
    public String getDescription() {
        throw new RuntimeException("Stub");
    }

    /**
     * Returns the list of channels that belong to this group
     */
    public List<NotificationChannel> getChannels() {
        throw new RuntimeException("Stub");
    }

    /**
     * Returns whether or not notifications posted to {@link NotificationChannel channels} belonging
     * to this group are blocked. This value is independent of
     * {@link NotificationManager#areNotificationsEnabled()} and
     * {@link NotificationChannel#getImportance()}.
     */
    public boolean isBlocked() {
        throw new RuntimeException("Stub");
    }

    /**
     * Sets the user visible description of this group.
     *
     * <p>The recommended maximum length is 300 characters; the value may be truncated if it is too
     * long.
     */
    public void setDescription(String description) {
        throw new RuntimeException("Stub");
    }

    /**
     * @hide
     */
    public void setBlocked(boolean blocked) {
        throw new RuntimeException("Stub");
    }

    /**
     * @hide
     */
    public void addChannel(NotificationChannel channel) {
        throw new RuntimeException("Stub");
    }

    /**
     * @hide
     */
    public void setChannels(List<NotificationChannel> channels) {
        throw new RuntimeException("Stub");
    }

    /**
     * @hide
     */
    public void lockFields(int field) {
        throw new RuntimeException("Stub");
    }

    /**
     * @hide
     */
    public void unlockFields(int field) {
        throw new RuntimeException("Stub");
    }

    /**
     * @hide
     */
    public int getUserLockedFields() {
        throw new RuntimeException("Stub");
    }

    /**
     * @hide
     */
    public void populateFromXml(TypedXmlPullParser parser) {
        throw new RuntimeException("Stub");
    }

    /**
     * @hide
     */
    public void writeXml(TypedXmlSerializer out) throws IOException {
        throw new RuntimeException("Stub");
    }

    /**
     * @hide
     */
    public JSONObject toJson() throws JSONException {
        throw new RuntimeException("Stub");
    }

    public static final Creator<NotificationChannelGroup> CREATOR = new Creator<NotificationChannelGroup>() {
        @Override
        public NotificationChannelGroup createFromParcel(Parcel in) {
            throw new RuntimeException("Stub");
        }

        @Override
        public NotificationChannelGroup[] newArray(int size) {
            throw new RuntimeException("Stub");
        }
    };

    @Override
    public int describeContents() {
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
    public NotificationChannelGroup clone() {
        throw new RuntimeException("Stub");
    }

    @Override
    public String toString() {
        throw new RuntimeException("Stub");
    }

    /** @hide */
    public void dumpDebug(ProtoOutputStream proto, long fieldId) {
        throw new RuntimeException("Stub");
    }
}
