/*
 * Copyright (C) 2016 The Android Open Source Project
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

import android.app.NotificationManager.Importance;

import android.content.ContentResolver;
import android.content.Context;

import android.media.AudioAttributes;

import android.net.Uri;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.VibrationEffect;

import android.provider.Settings;

import android.util.proto.ProtoOutputStream;

import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;

import org.json.JSONException;
import org.json.JSONObject;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * A representation of settings that apply to a collection of similarly themed notifications.
 */
public final class NotificationChannel implements Parcelable {
    private static final String TAG = "NotificationChannel";

    /**
     * The id of the default channel for an app. This id is reserved by the system. All
     * notifications posted from apps targeting {@link android.os.Build.VERSION_CODES#N_MR1} or
     * earlier without a notification channel specified are posted to this channel.
     */
    public static final String DEFAULT_CHANNEL_ID = "miscellaneous";

    /**
     * The formatter used by the system to create an id for notification
     * channels when it automatically creates conversation channels on behalf of an app. The format
     * string takes two arguments, in this order: the
     * {@link #getId()} of the original notification channel, and the
     * {@link ShortcutInfo#getId() id} of the conversation.
     * @hide
     */
    public static final String CONVERSATION_CHANNEL_ID_FORMAT = "%1$s : %2$s";

    /**
     * TODO: STOPSHIP  remove
     * Conversation id to use for apps that aren't providing them yet.
     * @hide
     */
    public static final String PLACEHOLDER_CONVERSATION_ID = ":placeholder_id";

    /**
     * Extra value for {@link Settings#EXTRA_CHANNEL_FILTER_LIST}. Include to show fields
     * that have to do with editing sound, like a tone picker
     * ({@link #setSound(Uri, AudioAttributes)}).
     */
    public static final String EDIT_SOUND = "sound";
    /**
     * Extra value for {@link Settings#EXTRA_CHANNEL_FILTER_LIST}. Include to show fields
     * that have to do with editing vibration ({@link #enableVibration(boolean)},
     * {@link #setVibrationPattern(long[])}).
     */
    public static final String EDIT_VIBRATION = "vibration";
    /**
     * Extra value for {@link Settings#EXTRA_CHANNEL_FILTER_LIST}. Include to show fields
     * that have to do with editing importance ({@link #setImportance(int)}) and/or conversation
     * priority.
     */
    public static final String EDIT_IMPORTANCE = "importance";
    /**
     * Extra value for {@link Settings#EXTRA_CHANNEL_FILTER_LIST}. Include to show fields
     * that have to do with editing behavior on devices that are locked or have a turned off
     * display ({@link #setLockscreenVisibility(int)}, {@link #enableLights(boolean)},
     * {@link #setLightColor(int)}).
     */
    public static final String EDIT_LOCKED_DEVICE = "locked";
    /**
     * Extra value for {@link Settings#EXTRA_CHANNEL_FILTER_LIST}. Include to show fields
     * that have to do with editing do not disturb bypass {(@link #setBypassDnd(boolean)}) .
     */
    public static final String EDIT_ZEN = "zen";
    /**
     * Extra value for {@link Settings#EXTRA_CHANNEL_FILTER_LIST}. Include to show fields
     * that have to do with editing conversation settings (demoting or restoring a channel to
     * be a Conversation, changing bubble behavior, or setting the priority of a conversation).
     */
    public static final String EDIT_CONVERSATION = "conversation";
    /**
     * Extra value for {@link Settings#EXTRA_CHANNEL_FILTER_LIST}. Include to show fields
     * that have to do with editing launcher behavior (showing badges)}.
     */
    public static final String EDIT_LAUNCHER = "launcher";

    /**
     * The maximum length for text fields in a NotificationChannel. Fields will be truncated at this
     * limit.
     * @hide
     */
    public static final int MAX_TEXT_LENGTH = 1000;
    /**
     * @hide
     */
    public static final int MAX_VIBRATION_LENGTH = 1000;

    private static final String TAG_CHANNEL = "channel";
    private static final String ATT_NAME = "name";
    private static final String ATT_DESC = "desc";
    private static final String ATT_ID = "id";
    private static final String ATT_DELETED = "deleted";
    private static final String ATT_PRIORITY = "priority";
    private static final String ATT_VISIBILITY = "visibility";
    private static final String ATT_IMPORTANCE = "importance";
    private static final String ATT_LIGHTS = "lights";
    private static final String ATT_LIGHT_COLOR = "light_color";
    private static final String ATT_VIBRATION = "vibration";
    private static final String ATT_VIBRATION_EFFECT = "vibration_effect";
    private static final String ATT_VIBRATION_ENABLED = "vibration_enabled";
    private static final String ATT_SOUND = "sound";
    private static final String ATT_USAGE = "usage";
    private static final String ATT_FLAGS = "flags";
    private static final String ATT_CONTENT_TYPE = "content_type";
    private static final String ATT_SHOW_BADGE = "show_badge";
    private static final String ATT_USER_LOCKED = "locked";
    /**
     * This attribute represents both foreground services and user initiated jobs in U+.
     * It was not renamed in U on purpose, in order to avoid creating an unnecessary migration path.
     */
    private static final String ATT_FG_SERVICE_SHOWN = "fgservice";
    private static final String ATT_GROUP = "group";
    private static final String ATT_BLOCKABLE_SYSTEM = "blockable_system";
    private static final String ATT_ALLOW_BUBBLE = "allow_bubbles";
    private static final String ATT_ORIG_IMP = "orig_imp";
    private static final String ATT_PARENT_CHANNEL = "parent";
    private static final String ATT_CONVERSATION_ID = "conv_id";
    private static final String ATT_IMP_CONVERSATION = "imp_conv";
    private static final String ATT_DEMOTE = "dem";
    private static final String ATT_DELETED_TIME_MS = "del_time";
    private static final String DELIMITER = ",";

    /**
     * @hide
     */
    public static final int USER_LOCKED_PRIORITY = 0x00000001;
    /**
     * @hide
     */
    public static final int USER_LOCKED_VISIBILITY = 0x00000002;
    /**
     * @hide
     */
    public static final int USER_LOCKED_IMPORTANCE = 0x00000004;
    /**
     * @hide
     */
    public static final int USER_LOCKED_LIGHTS = 0x00000008;
    /**
     * @hide
     */
    public static final int USER_LOCKED_VIBRATION = 0x00000010;
    /**
     * @hide
     */
    public static final int USER_LOCKED_SOUND = 0x00000020;

    /**
     * @hide
     */
    public static final int USER_LOCKED_SHOW_BADGE = 0x00000080;

    /**
     * @hide
     */
    public static final int USER_LOCKED_ALLOW_BUBBLE = 0x00000100;

    /**
     * @hide
     */
    public static final int[] LOCKABLE_FIELDS = new int[] {
            USER_LOCKED_PRIORITY,
            USER_LOCKED_VISIBILITY,
            USER_LOCKED_IMPORTANCE,
            USER_LOCKED_LIGHTS,
            USER_LOCKED_VIBRATION,
            USER_LOCKED_SOUND,
            USER_LOCKED_SHOW_BADGE,
            USER_LOCKED_ALLOW_BUBBLE
    };

    /**
     * @hide
     */
    public static final int DEFAULT_ALLOW_BUBBLE = -1;
    /**
     * @hide
     */
    public static final int ALLOW_BUBBLE_ON = 1;
    /**
     * @hide
     */
    public static final int ALLOW_BUBBLE_OFF = 0;

    private static final int DEFAULT_LIGHT_COLOR = 0;
    private static final int DEFAULT_VISIBILITY = NotificationManager.VISIBILITY_NO_OVERRIDE;
    private static final int DEFAULT_IMPORTANCE = NotificationManager.IMPORTANCE_UNSPECIFIED;
    private static final boolean DEFAULT_DELETED = false;
    private static final boolean DEFAULT_SHOW_BADGE = true;
    private static final long DEFAULT_DELETION_TIME_MS = -1;

    private String mId;
    private String mName;
    private String mDesc;
    private int mImportance = DEFAULT_IMPORTANCE;
    private int mOriginalImportance = DEFAULT_IMPORTANCE;
    private boolean mBypassDnd;
    private int mLockscreenVisibility = DEFAULT_VISIBILITY;
    private Uri mSound = Settings.System.DEFAULT_NOTIFICATION_URI;
    private boolean mSoundRestored = false;
    private boolean mLights;
    private int mLightColor = DEFAULT_LIGHT_COLOR;
    private long[] mVibrationPattern;
    private VibrationEffect mVibrationEffect;
    // Bitwise representation of fields that have been changed by the user, preventing the app from
    // making changes to these fields.
    private int mUserLockedFields;
    private boolean mUserVisibleTaskShown;
    private boolean mVibrationEnabled;
    private boolean mShowBadge = DEFAULT_SHOW_BADGE;
    private boolean mDeleted = DEFAULT_DELETED;
    private String mGroup;
    private AudioAttributes mAudioAttributes = Notification.AUDIO_ATTRIBUTES_DEFAULT;
    // If this is a blockable system notification channel.
    private boolean mBlockableSystem = false;
    private int mAllowBubbles = DEFAULT_ALLOW_BUBBLE;
    private boolean mImportanceLockedDefaultApp;
    private String mParentId = null;
    private String mConversationId = null;
    private boolean mDemoted = false;
    private boolean mImportantConvo = false;
    private long mDeletedTime = DEFAULT_DELETION_TIME_MS;
    /** Do not (de)serialize this value: it only affects logic in system_server and that logic
     * is reset on each boot {@link NotificationAttentionHelper#buzzBeepBlinkLocked}.
     */
    private long mLastNotificationUpdateTimeMs = 0;

    /**
     * Creates a notification channel.
     *
     * @param id The id of the channel. Must be unique per package. The value may be truncated if
     *           it is too long.
     * @param name The user visible name of the channel. You can rename this channel when the system
     *             locale changes by listening for the {@link Intent#ACTION_LOCALE_CHANGED}
     *             broadcast. The recommended maximum length is 40 characters; the value may be
     *             truncated if it is too long.
     * @param importance The importance of the channel. This controls how interruptive notifications
     *                   posted to this channel are.
     */
    public NotificationChannel(String id, CharSequence name, @Importance int importance) {
        throw new RuntimeException("Stub");
    }

    /**
     * @hide
     */
    protected NotificationChannel(Parcel in) {
        throw new RuntimeException("Stub");
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
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
    public void setUserVisibleTaskShown(boolean shown) {
        throw new RuntimeException("Stub");
    }

    /**
     * @hide
     */
    public void setDeleted(boolean deleted) {
        throw new RuntimeException("Stub");
    }

    /**
     * @hide
     */
    public void setDeletedTimeMs(long time) {
        throw new RuntimeException("Stub");
    }

    /**
     * @hide
     */
    public void setImportantConversation(boolean importantConvo) {
        throw new RuntimeException("Stub");
    }

    /**
     * Allows users to block notifications sent through this channel, if this channel belongs to
     * a package that otherwise would have notifications "fixed" as enabled.
     *
     * If the channel does not belong to a package that has a fixed notification permission, this
     * method does nothing, since such channels are blockable by default and cannot be set to be
     * unblockable.
     * @param blockable if {@code true}, allows users to block notifications on this channel.
     */
    public void setBlockable(boolean blockable) {
        throw new RuntimeException("Stub");
    }
    // Modifiable by apps post channel creation

    /**
     * Sets the user visible name of this channel.
     *
     * <p>The recommended maximum length is 40 characters; the value may be truncated if it is too
     * long.
     */
    public void setName(CharSequence name) {
        throw new RuntimeException("Stub");
    }

    /**
     * Sets the user visible description of this channel.
     *
     * <p>The recommended maximum length is 300 characters; the value may be truncated if it is too
     * long.
     */
    public void setDescription(String description) {
        mDesc = getTrimmedString(description);
    }

    private String getTrimmedString(String input) {
        throw new RuntimeException("Stub");
    }

    /**
     * @hide
     */
    public void setId(String id) {
        throw new RuntimeException("Stub");
    }

    // Modifiable by apps on channel creation.

    /**
     * Sets what group this channel belongs to.
     *
     * Group information is only used for presentation, not for behavior.
     *
     * Only modifiable before the channel is submitted to
     * {@link NotificationManager#createNotificationChannel(NotificationChannel)}, unless the
     * channel is not currently part of a group.
     *
     * @param groupId the id of a group created by
     * {@link NotificationManager#createNotificationChannelGroup(NotificationChannelGroup)}.
     */
    public void setGroup(String groupId) {
        throw new RuntimeException("Stub");
    }

    /**
     * Sets whether notifications posted to this channel can appear as application icon badges
     * in a Launcher.
     *
     * Only modifiable before the channel is submitted to
     * {@link NotificationManager#createNotificationChannel(NotificationChannel)}.
     *
     * @param showBadge true if badges should be allowed to be shown.
     */
    public void setShowBadge(boolean showBadge) {
        throw new RuntimeException("Stub");
    }

    /**
     * Sets the sound that should be played for notifications posted to this channel and its
     * audio attributes. Notification channels with an {@link #getImportance() importance} of at
     * least {@link NotificationManager#IMPORTANCE_DEFAULT} should have a sound.
     *
     * Only modifiable before the channel is submitted to
     * {@link NotificationManager#createNotificationChannel(NotificationChannel)}.
     */
    public void setSound(Uri sound, AudioAttributes audioAttributes) {
        throw new RuntimeException("Stub");
    }

    /**
     * Sets whether notifications posted to this channel should display notification lights,
     * on devices that support that feature.
     *
     * Only modifiable before the channel is submitted to
     * {@link NotificationManager#createNotificationChannel(NotificationChannel)}.
     */
    public void enableLights(boolean lights) {
        throw new RuntimeException("Stub");
    }

    /**
     * Sets the notification light color for notifications posted to this channel, if lights are
     * {@link #enableLights(boolean) enabled} on this channel and the device supports that feature.
     *
     * Only modifiable before the channel is submitted to
     * {@link NotificationManager#createNotificationChannel(NotificationChannel)}.
     */
    public void setLightColor(int argb) {
        throw new RuntimeException("Stub");
    }

    /**
     * Sets whether notification posted to this channel should vibrate. The vibration pattern can
     * be set with {@link #setVibrationPattern(long[])}.
     *
     * Only modifiable before the channel is submitted to
     * {@link NotificationManager#createNotificationChannel(NotificationChannel)}.
     */
    public void enableVibration(boolean vibration) {
        throw new RuntimeException("Stub");
    }

    /**
     * Sets the vibration pattern for notifications posted to this channel. If the provided
     * pattern is valid (non-null, non-empty with at least 1 non-zero value), will enable vibration
     * on this channel (equivalent to calling {@link #enableVibration(boolean)} with {@code true}).
     * Otherwise, vibration will be disabled unless {@link #enableVibration(boolean)} is
     * used with {@code true}, in which case the default vibration will be used.
     *
     * Only modifiable before the channel is submitted to
     * {@link NotificationManager#createNotificationChannel(NotificationChannel)}.
     */
    public void setVibrationPattern(long[] vibrationPattern) {
        throw new RuntimeException("Stub");
    }

    /**
     * Sets a {@link VibrationEffect} for notifications posted to this channel. If the
     * provided effect is non-null, will enable vibration on this channel (equivalent
     * to calling {@link #enableVibration(boolean)} with {@code true}). Otherwise
     * vibration will be disabled unless {@link #enableVibration(boolean)} is used with
     * {@code true}, in which case the default vibration will be used.
     *
     * <p>The effect passed here will be returned from {@link #getVibrationEffect()}.
     * If the provided {@link VibrationEffect} is an equivalent to a wave-form
     * vibration pattern, the equivalent wave-form pattern will be returned from
     * {@link #getVibrationPattern()}.
     *
     * <p>Note that some {@link VibrationEffect}s may not be playable on some devices.
     * In cases where such an effect is passed here, vibration will still be enabled
     * for the channel, but the default vibration will be used. Nonetheless, the
     * provided effect will be stored and be returned from {@link #getVibrationEffect}
     * calls, and could be used by the same channel on a different device, for example,
     * in cases the user backs up and restores to a device that does have the ability
     * to play the effect, where that effect will be used instead of the default. To
     * avoid such issues that could make the vibration behavior of your notification
     * channel differ among different devices, it's recommended that you avoid
     * vibration effect primitives, as the support for them differs widely among
     * devices (read {@link VibrationEffect.Composition} for more on vibration
     * primitives).
     *
     * <p>Only modifiable before the channel is submitted to
     * {@link NotificationManager#createNotificationChannel(NotificationChannel)}.
     *
     * @see #getVibrationEffect()
     * @see Vibrator#areEffectsSupported(int...)
     * @see Vibrator#arePrimitivesSupported(int...)
     */
    public void setVibrationEffect(VibrationEffect effect) {
        throw new RuntimeException("Stub");
    }

    /**
     * Sets the level of interruption of this notification channel.
     *
     * Only modifiable before the channel is submitted to
     * {@link NotificationManager#createNotificationChannel(NotificationChannel)}.
     *
     * @param importance the amount the user should be interrupted by
     *            notifications from this channel.
     */
    public void setImportance(@Importance int importance) {
        throw new RuntimeException("Stub");
    }

    // Modifiable by a notification ranker.

    /**
     * Sets whether or not notifications posted to this channel can interrupt the user in
     * {@link android.app.NotificationManager.Policy#INTERRUPTION_FILTER_PRIORITY} mode.
     *
     * Only modifiable by the system and notification ranker.
     */
    public void setBypassDnd(boolean bypassDnd) {
        throw new RuntimeException("Stub");
    }

    /**
     * Sets whether notifications posted to this channel appear on the lockscreen or not, and if so,
     * whether they appear in a redacted form. See e.g. {@link Notification#VISIBILITY_SECRET}.
     *
     * Only modifiable by the system and notification ranker.
     */
    public void setLockscreenVisibility(int lockscreenVisibility) {
        throw new RuntimeException("Stub");
    }

    /**
     * As of Android 11 this value is no longer respected.
     * @see #canBubble()
     * @see Notification#getBubbleMetadata()
     */
    public void setAllowBubbles(boolean allowBubbles) {
        throw new RuntimeException("Stub");
    }

    /**
     * @hide
     */
    public void setAllowBubbles(int allowed) {
        throw new RuntimeException("Stub");
    }

    /**
     * Sets this channel as being converastion-centric. Different settings and functionality may be
     * exposed for conversation-centric channels.
     *
     * @param parentChannelId The {@link #getId()} id} of the generic channel that notifications of
     *                        this type would be posted to in absence of a specific conversation id.
     *                        For example, if this channel represents 'Messages from Person A', the
     *                        parent channel would be 'Messages.'
     * @param conversationId The {@link ShortcutInfo#getId()} of the shortcut representing this
     *                       channel's conversation.
     */
    public void setConversationId(String parentChannelId, String conversationId) {
        throw new RuntimeException("Stub");
    }

    /**
     * Returns the id of this channel.
     */
    public String getId() {
        throw new RuntimeException("Stub");
    }

    /**
     * Returns the user visible name of this channel.
     */
    public CharSequence getName() {
        throw new RuntimeException("Stub");
    }

    /**
     * Returns the user visible description of this channel.
     */
    public String getDescription() {
        throw new RuntimeException("Stub");
    }

    /**
     * Returns the user specified importance e.g. {@link NotificationManager#IMPORTANCE_LOW} for
     * notifications posted to this channel. Note: This value might be >
     * {@link NotificationManager#IMPORTANCE_NONE}, but notifications posted to this channel will
     * not be shown to the user if the parent {@link NotificationChannelGroup} or app is blocked.
     * See {@link NotificationChannelGroup#isBlocked()} and
     * {@link NotificationManager#areNotificationsEnabled()}.
     */
    public int getImportance() {
        throw new RuntimeException("Stub");
    }

    /**
     * Whether or not notifications posted to this channel can bypass the Do Not Disturb
     * {@link NotificationManager#INTERRUPTION_FILTER_PRIORITY} mode.
     */
    public boolean canBypassDnd() {
        throw new RuntimeException("Stub");
    }

    /**
     * Whether or not this channel represents a conversation.
     */
    public boolean isConversation() {
        throw new RuntimeException("Stub");
    }


    /**
     * Whether or not notifications in this conversation are considered important.
     *
     * <p>Important conversations may get special visual treatment, and might be able to bypass DND.
     *
     * <p>This is only valid for channels that represent conversations, that is,
     * where {@link #isConversation()} is true.
     */
    public boolean isImportantConversation() {
        throw new RuntimeException("Stub");
    }

    /**
     * Returns the notification sound for this channel.
     */
    public Uri getSound() {
        throw new RuntimeException("Stub");
    }

    /**
     * Returns the audio attributes for sound played by notifications posted to this channel.
     */
    public AudioAttributes getAudioAttributes() {
        throw new RuntimeException("Stub");
    }

    /**
     * Returns whether notifications posted to this channel trigger notification lights.
     */
    public boolean shouldShowLights() {
        throw new RuntimeException("Stub");
    }

    /**
     * Returns the notification light color for notifications posted to this channel. Irrelevant
     * unless {@link #shouldShowLights()}.
     */
    public int getLightColor() {
        throw new RuntimeException("Stub");
    }

    /**
     * Returns whether notifications posted to this channel always vibrate.
     */
    public boolean shouldVibrate() {
        throw new RuntimeException("Stub");
    }

    /**
     * Returns the vibration pattern for notifications posted to this channel. Will be ignored if
     * vibration is not enabled ({@link #shouldVibrate()}).
     */
    public long[] getVibrationPattern() {
        throw new RuntimeException("Stub");
    }

    /**
     * Returns the {@link VibrationEffect} for notifications posted to this channel.
     * The returned effect is derived from either the effect provided in the
     * {@link #setVibrationEffect(VibrationEffect)} method, or the equivalent vibration effect
     * of the pattern set via the {@link #setVibrationPattern(long[])} method, based on setter
     * method that was called last.
     *
     * The returned effect will be ignored in one of the following cases:
     * <ul>
     *   <li> vibration is not enabled for the channel (i.e. {@link #shouldVibrate()}
     *        returns {@code false}).
     *   <li> the effect is not supported/playable by the device. In this case, if
     *        vibration is enabled for the channel, the default channel vibration will
     *        be used instead.
     * </ul>
     *
     * @return the {@link VibrationEffect} set via {@link
     *         #setVibrationEffect(VibrationEffect)}, or the equivalent of the
     *         vibration set via {@link #setVibrationPattern(long[])}.
     *
     *  @see VibrationEffect#createWaveform(long[], int)
     */
    public VibrationEffect getVibrationEffect() {
        throw new RuntimeException("Stub");
    }

    /**
     * Returns whether or not notifications posted to this channel are shown on the lockscreen in
     * full or redacted form.
     */
    public int getLockscreenVisibility() {
        throw new RuntimeException("Stub");
    }

    /**
     * Returns whether notifications posted to this channel can appear as badges in a Launcher
     * application.
     *
     * Note that badging may be disabled for other reasons.
     */
    public boolean canShowBadge() {
        throw new RuntimeException("Stub");
    }

    /**
     * Returns what group this channel belongs to.
     *
     * This is used only for visually grouping channels in the UI.
     */
    public String getGroup() {
        throw new RuntimeException("Stub");
    }

    /**
     * Returns whether notifications posted to this channel are allowed to display outside of the
     * notification shade, in a floating window on top of other apps.
     *
     * @see Notification#getBubbleMetadata()
     */
    public boolean canBubble() {
        throw new RuntimeException("Stub");
    }

    /**
     * @hide
     */
    public int getAllowBubbles() {
        throw new RuntimeException("Stub");
    }

    /**
     * Returns the {@link #getId() id} of the parent notification channel to this channel, if it's
     * a conversation related channel. See {@link #setConversationId(String, String)}.
     */
    public String getParentChannelId() {
        throw new RuntimeException("Stub");
    }

    /**
     * Returns the {@link ShortcutInfo#getId() id} of the conversation backing this channel, if it's
     * associated with a conversation. See {@link #setConversationId(String, String)}.
     */
    public String getConversationId() {
        throw new RuntimeException("Stub");
    }

    /**
     * @hide
     */
    public boolean isDeleted() {
        throw new RuntimeException("Stub");
    }

    /**
     * @hide
     */
    public long getDeletedTimeMs() {
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
    public boolean isUserVisibleTaskShown() {
        throw new RuntimeException("Stub");
    }

    /**
     * Returns whether this channel is always blockable, even if the app is 'fixed' as
     * non-blockable.
     */
    public boolean isBlockable() {
        throw new RuntimeException("Stub");
    }

    /**
     * @hide
     */
    public void setImportanceLockedByCriticalDeviceFunction(boolean locked) {
        throw new RuntimeException("Stub");
    }

    /**
     * @hide
     */
    public boolean isImportanceLockedByCriticalDeviceFunction() {
        throw new RuntimeException("Stub");
    }

    /**
     * @hide
     */
    public int getOriginalImportance() {
        throw new RuntimeException("Stub");
    }

    /**
     * @hide
     */
    public void setOriginalImportance(int importance) {
        throw new RuntimeException("Stub");
    }

    /**
     * @hide
     */
    public void setDemoted(boolean demoted) {
        throw new RuntimeException("Stub");
    }

    /**
     * Returns whether the user has decided that this channel does not represent a conversation. The
     * value will always be false for channels that never claimed to be conversations - that is,
     * for channels where {@link #getConversationId()} and {@link #getParentChannelId()} are empty.
     */
    public boolean isDemoted() {
        throw new RuntimeException("Stub");
    }

    /**
     * Returns whether the user has chosen the importance of this channel, either to affirm the
     * initial selection from the app, or changed it to be higher or lower.
     * @see #getImportance()
     */
    public boolean hasUserSetImportance() {
        throw new RuntimeException("Stub");
    }

    /**
     * Returns whether the user has chosen the sound of this channel.
     * @see #getSound()
     */
    public boolean hasUserSetSound() {
        throw new RuntimeException("Stub");
    }

    /**
     * Returns the time of the notification post or last update for this channel.
     * @return time of post / last update
     * @hide
     */
    public long getLastNotificationUpdateTimeMs() {
        throw new RuntimeException("Stub");
    }

    /**
     * Sets the time of the notification post or last update for this channel.
     * @hide
     */
    public void setLastNotificationUpdateTimeMs(long updateTimeMs) {
        throw new RuntimeException("Stub");
    }

    /**
     * @hide
     */
    public void populateFromXmlForRestore(XmlPullParser parser, boolean pkgInstalled, Context context) {
        throw new RuntimeException("Stub");
    }

    /**
     * @hide
     */
    public void populateFromXml(XmlPullParser parser) {
        throw new RuntimeException("Stub");
    }

    /**
     * If {@param forRestore} is true, {@param Context} MUST be non-null.
     */
    private void populateFromXml(TypedXmlPullParser parser, boolean forRestore, boolean pkgInstalled, Context context) {
        throw new RuntimeException("Stub");
    }

    /**
     * Returns whether the sound for this channel was successfully restored
     *  from backup.
     * @return false if the sound was not restored successfully. true otherwise (default value)
     * @hide
     */
    public boolean isSoundRestored() {
        throw new RuntimeException("Stub");
    }

    private Uri getCanonicalizedSoundUri(ContentResolver contentResolver, Uri uri) {
        throw new RuntimeException("Stub");
    }

    private Uri getUncanonicalizedSoundUri(ContentResolver contentResolver, Uri uri, int usage) {
        throw new RuntimeException("Stub");
    }

    /**
     * Restore/validate sound Uri from backup
     * @param context The Context
     * @param uri The sound Uri to restore
     * @param pkgInstalled If the parent package is installed
     * @return restored and validated Uri
     * @hide
     */
    public Uri restoreSoundUri(Context context, Uri uri, boolean pkgInstalled, int usage) {
        throw new RuntimeException("Stub");
    }

    /**
     * @hide
     */
    public void writeXml(XmlSerializer out) throws IOException {
        throw new RuntimeException("Stub");
    }

    /**
     * @hide
     */
    public void writeXmlForBackup(XmlSerializer out, Context context) throws IOException {
        throw new RuntimeException("Stub");
    }

    private Uri getSoundForBackup(Context context) {
        throw new RuntimeException("Stub");
    }

    /**
     * If {@param forBackup} is true, {@param Context} MUST be non-null.
     */
    private void writeXml(TypedXmlSerializer out, boolean forBackup, Context context) throws IOException {
        throw new RuntimeException("Stub");
    }

    /**
     * @hide
     */
    public JSONObject toJson() throws JSONException {
        throw new RuntimeException("Stub");
    }

    private static AudioAttributes safeAudioAttributes(TypedXmlPullParser parser) {
        throw new RuntimeException("Stub");
    }

    private static Uri safeUri(TypedXmlPullParser parser, String att) {
        throw new RuntimeException("Stub");
    }

    private static String vibrationToString(VibrationEffect effect) {
        throw new RuntimeException("Stub");
    }

    private static VibrationEffect safeVibrationEffect(TypedXmlPullParser parser, String att) {
        throw new RuntimeException("Stub");
    }

    private static int safeInt(TypedXmlPullParser parser, String att, int defValue) {
        throw new RuntimeException("Stub");
    }

    private static boolean safeBool(TypedXmlPullParser parser, String att, boolean defValue) {
        throw new RuntimeException("Stub");
    }

    private static long[] safeLongArray(TypedXmlPullParser parser, String att, long[] defValue) {
        throw new RuntimeException("Stub");
    }

    private static String longArrayToString(long[] values) {
        throw new RuntimeException("Stub");
    }

    public static final Creator<NotificationChannel> CREATOR = new Creator<NotificationChannel>() {
        @Override
        public NotificationChannel createFromParcel(Parcel in) {
            throw new RuntimeException("Stub");
        }

        @Override
        public NotificationChannel[] newArray(int size) {
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

    /** @hide */
    public void dump(PrintWriter pw, String prefix, boolean redacted) {
        throw new RuntimeException("Stub");
    }

    @Override
    public String toString() {
        throw new RuntimeException("Stub");
    }

    private String getFieldsString() {
        throw new RuntimeException("Stub");
    }

    /** @hide */
    public void dumpDebug(ProtoOutputStream proto, long fieldId) {
        throw new RuntimeException("Stub");
    }
}
