package android.app;

import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;

import android.content.pm.ParceledListSlice;

import android.os.UserHandle;

interface INotificationManager
{
    // void cancelAllNotifications(String pkg, int userId);
    // void clearData(String pkg, int uid, boolean fromApp);
    // void enqueueTextToast(String pkg, IBinder token, CharSequence text, int duration, boolean isUiContext, int displayId, @nullable ITransientNotificationCallback callback);
    // void enqueueToast(String pkg, IBinder token, ITransientNotification callback, int duration, boolean isUiContext, int displayId);
    // void cancelToast(String pkg, IBinder token);
    // void finishToken(String pkg, IBinder token);
    // void enqueueNotificationWithTag(String pkg, String opPkg, String tag, int id, in Notification notification, int userId);
    // void cancelNotificationWithTag(String pkg, String opPkg, String tag, int id, int userId);
    boolean isInCall(String pkg, int uid);
    void setShowBadge(String pkg, int uid, boolean showBadge);
    boolean canShowBadge(String pkg, int uid);
    boolean hasSentValidMsg(String pkg, int uid);
    boolean isInInvalidMsgState(String pkg, int uid);
    boolean hasUserDemotedInvalidMsgApp(String pkg, int uid);
    void setInvalidMsgAppDemoted(String pkg, int uid, boolean isDemoted);
    boolean hasSentValidBubble(String pkg, int uid);
    void setNotificationsEnabledForPackage(String pkg, int uid, boolean enabled);
    void setNotificationsEnabledWithImportanceLockForPackage(String pkg, int uid, boolean enabled);
    boolean areNotificationsEnabledForPackage(String pkg, int uid);
    boolean areNotificationsEnabled(String pkg);
    int getPackageImportance(String pkg);
    boolean isImportanceLocked(String pkg, int uid);
    List<String> getAllowedAssistantAdjustments(String pkg);
    boolean shouldHideSilentStatusIcons(String callingPkg);
    void setHideSilentStatusIcons(boolean hide);
    void setBubblesAllowed(String pkg, int uid, int bubblePreference);
    boolean areBubblesAllowed(String pkg);
    boolean areBubblesEnabled(in UserHandle user);
    int getBubblePreferenceForPackage(String pkg, int uid);
    void createNotificationChannelGroups(String pkg, in ParceledListSlice channelGroupList);
    void createNotificationChannels(String pkg, in ParceledListSlice channelsList);
    void createNotificationChannelsForPackage(String pkg, int uid, in ParceledListSlice channelsList);
    ParceledListSlice getConversations(boolean onlyImportant);
    ParceledListSlice getConversationsForPackage(String pkg, int uid);
    ParceledListSlice getNotificationChannelGroupsForPackage(String pkg, int uid, boolean includeDeleted);
    NotificationChannelGroup getNotificationChannelGroupForPackage(String groupId, String pkg, int uid);
    NotificationChannelGroup getPopulatedNotificationChannelGroupForPackage(String pkg, int uid, String groupId, boolean includeDeleted);
    ParceledListSlice getRecentBlockedNotificationChannelGroupsForPackage(String pkg, int uid);
    void updateNotificationChannelGroupForPackage(String pkg, int uid, in NotificationChannelGroup group);
    void updateNotificationChannelForPackage(String pkg, int uid, in NotificationChannel channel);
    void unlockNotificationChannel(String pkg, int uid, String channelId);
    void unlockAllNotificationChannels();
    NotificationChannel getNotificationChannel(String callingPkg, int userId, String pkg, String channelId);
    NotificationChannel getConversationNotificationChannel(String callingPkg, int userId, String pkg, String channelId, boolean returnParentIfNoConversationChannel, String conversationId);
    void createConversationNotificationChannelForPackage(String pkg, int uid, in NotificationChannel parentChannel, String conversationId);
    NotificationChannel getNotificationChannelForPackage(String pkg, int uid, String channelId, String conversationId, boolean includeDeleted);
    void deleteNotificationChannel(String pkg, String channelId);
    ParceledListSlice getNotificationChannels(String callingPkg, String targetPkg, int userId);
    ParceledListSlice getNotificationChannelsForPackage(String pkg, int uid, boolean includeDeleted);
    int getNumNotificationChannelsForPackage(String pkg, int uid, boolean includeDeleted);
    int getDeletedChannelCount(String pkg, int uid);
    int getBlockedChannelCount(String pkg, int uid);
    void deleteNotificationChannelGroup(String pkg, String channelGroupId);
    NotificationChannelGroup getNotificationChannelGroup(String pkg, String channelGroupId);
    ParceledListSlice getNotificationChannelGroups(String pkg);
    boolean isPermissionFixed(String pkg, int userId);
    void silenceNotificationSound();
}