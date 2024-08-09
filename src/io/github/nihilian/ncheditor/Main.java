package io.github.nihilian.ncheditor;

import android.app.INotificationManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;

import android.content.pm.IPackageManager;
import android.content.pm.ParceledListSlice;

import android.net.Uri;

import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import org.json.JSONException;

public class Main {
    private static final String PROG = "ncheditor";
    private static final String PKG_SHELL = "com.android.shell";

    private static INotificationManager getNotificationManagerService() {
        IBinder notificationBinder = ServiceManager.getService("notification");
        return INotificationManager.Stub.asInterface(notificationBinder);
    }

    private static IPackageManager getPackageManagerService() {
        IBinder packageBinder = ServiceManager.getService("package");
        return IPackageManager.Stub.asInterface(packageBinder);
    }

    private static long[] parseFromString(String str) {
        if (str.startsWith("[") && str.endsWith("]")) {
            str = str.substring(1, str.length() - 1);
        }
        String[] values = str.split(",");
        long[] longValues = new long[values.length];
        for (int i = 0; i < values.length; i++) {
            try {
                longValues[i] = Long.parseLong(values[i]);
            } catch (NumberFormatException e) {
                System.err.println("ERROR: input could not be parsed to type 'long[]', input=\"str\"");
                e.printStackTrace();
                System.exit(1);
            }
        }
        return longValues;
    }

    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(PROG, options, true);
    }

    private static void printUpdateFieldsInfo() {
        System.out.println("update_fields format: key0=value0 key1=value1 ...");
        System.out.println("list of keys:");
        System.out.println("  \"name\"                          use to set the user-facing name of a Notification Channel");
        System.out.println("  \"description\"                   use to set the brief description text of a Notification Channel");
        System.out.println("  \"importance\"                    numerical value between 0-4 that affects how notifications get displayed");
        System.out.println("  \"bypassDnd\"                     boolean indicating whether these notifications should interrupt Do Not Disturb");
        System.out.println("  \"lockscreenVisibility\"          ");
        System.out.println("  \"sound\"                         ");
        System.out.println("  \"lights\"                        ");
        System.out.println("  \"lightColor\"                    ");
        System.out.println("  \"vibrationPattern\"              ");
        System.out.println("  \"userVisibleTaskShown\"          ");
        System.out.println("  \"vibrationEnabled\"              ");
        System.out.println("  \"showBadge\"                     boolean indicating whether to show a dot on the app's homescreen icon");
        System.out.println("  \"deleted\"                       boolean indicating whether this channel is deleted");
        System.out.println("  \"deletedTimeMs\"                 ");
        System.out.println("  \"group\"                         the Notification Channel Group ID for which this Notification Channel is assigned to");
        System.out.println("  \"blockableSystem\"               set this to true on a fixed-permission system app's channel to be able to freely change its settings");
        System.out.println("  \"allowBubbles\"                  ");
        System.out.println("  \"importanceLockedDefaultApp\"    ");
        System.out.println("  \"originalImportance\"            ");
        System.out.println("  \"parentId\"                      ");
        System.out.println("  \"conversationId\"                ");
        System.out.println("  \"demoted\"                       ");
        System.out.println("  \"importantConvo\"                ");
        System.out.println("  \"lastNotificationUpdateTimeMs\"  ");
    }

    private static void listNotificationChannelsForPackage(String pkg, boolean includeDeleted) {
        INotificationManager notificationManager = getNotificationManagerService();
        IPackageManager packageManager = getPackageManagerService();
        try {
            int uid = packageManager.getPackageUid(pkg, 0, 0);
            ParceledListSlice<NotificationChannel> channels = notificationManager.getNotificationChannelsForPackage(pkg, uid, includeDeleted);
            final List<NotificationChannel> list = channels.getList();
            for (int i = 0; i < list.size(); i++) {
                int list_entry = i + 1;
                System.out.println("# " + list_entry + "/" + list.size());
                // System.out.println(list.get(i).toJson());
                System.out.println(list.get(i).toString());
            }
        // } catch (RemoteException | JSONException e) {
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private static void listNotificationChannelGroupsForPackage(String pkg, boolean includeDeleted) {
        INotificationManager notificationManager = getNotificationManagerService();
        IPackageManager packageManager = getPackageManagerService();
        try {
            int uid = packageManager.getPackageUid(pkg, 0, 0);
            ParceledListSlice<NotificationChannelGroup> channelGroups = notificationManager.getNotificationChannelGroupsForPackage(pkg, uid, includeDeleted);
            final List<NotificationChannelGroup> list = channelGroups.getList();
            for (int i = 0; i < list.size(); i++) {
                int list_entry = i + 1;
                System.out.println("# " + list_entry + "/" + list.size());
                // System.out.println(list.get(i).toJson());
                System.out.println(list.get(i).toString());
            }
        // } catch (RemoteException | JSONException e) {
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private static void getNotificationChannelForPackage(String pkg, String channelId) {
        INotificationManager notificationManager = getNotificationManagerService();
        try {
            NotificationChannel channel = notificationManager.getNotificationChannel(PKG_SHELL, 0, pkg, channelId);
            // System.out.println(channel.toJson());
            System.out.println(channel.toString());
        // } catch (RemoteException | JSONException e) {
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private static void getNotificationChannelGroupForPackage(String pkg, String channelGroupId) {
        INotificationManager notificationManager = getNotificationManagerService();
        IPackageManager packageManager = getPackageManagerService();
        try {
            int uid = packageManager.getPackageUid(pkg, 0, 0);
            NotificationChannelGroup channelGroup = notificationManager.getNotificationChannelGroupForPackage(channelGroupId, pkg, uid);
            // System.out.println(channelGroup.toJson());
            System.out.println(channelGroup.toString());
        // } catch (RemoteException | JSONException e) {
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private static void updateNotificationChannelForPackage(String pkg, String channelId, Properties update_fields) {
        INotificationManager notificationManager = getNotificationManagerService();
        IPackageManager packageManager = getPackageManagerService();
        try {
            int uid = packageManager.getPackageUid(pkg, 0, 0);
            NotificationChannel channel = notificationManager.getNotificationChannel(PKG_SHELL, 0, pkg, channelId);

            if (update_fields.containsKey("name")) {
                channel.setName(update_fields.getProperty("name"));
            }
            if (update_fields.containsKey("description")) {
                channel.setDescription(update_fields.getProperty("description"));
            }
            if (update_fields.containsKey("importance")) {
                channel.setImportance(Integer.parseInt(update_fields.getProperty("importance")));
            }
            if (update_fields.containsKey("bypassDnd")) {
                channel.setBypassDnd(Boolean.parseBoolean(update_fields.getProperty("bypassDnd")));
            }
            if (update_fields.containsKey("lockscreenVisibility")) {
                channel.setLockscreenVisibility(Integer.parseInt(update_fields.getProperty("lockscreenVisibility")));
            }
            if (update_fields.containsKey("sound")) {
                if (update_fields.getProperty("sound").equals("")) {
                    channel.setSound(null, channel.getAudioAttributes());
                } else {
                    channel.setSound(Uri.parse(update_fields.getProperty("sound")), channel.getAudioAttributes());
                }
            }
            if (update_fields.containsKey("lights")) {
                channel.enableLights(Boolean.parseBoolean(update_fields.getProperty("lights")));
            }
            if (update_fields.containsKey("lightColor")) {
                channel.setLightColor(Integer.parseInt(update_fields.getProperty("lightColor")));
            }
            if (update_fields.containsKey("vibrationPattern")) {
                channel.setVibrationPattern(parseFromString(update_fields.getProperty("vibrationPattern")));
            }
            if (update_fields.containsKey("userVisibleTaskShown")) {
                channel.setUserVisibleTaskShown(Boolean.parseBoolean(update_fields.getProperty("userVisibleTaskShown")));
            }
            if (update_fields.containsKey("vibrationEnabled")) {
                channel.enableVibration(Boolean.parseBoolean(update_fields.getProperty("vibrationEnabled")));
            }
            if (update_fields.containsKey("showBadge")) {
                channel.setShowBadge(Boolean.parseBoolean(update_fields.getProperty("showBadge")));
            }
            if (update_fields.containsKey("deleted")) {
                channel.setDeleted(Boolean.parseBoolean(update_fields.getProperty("deleted")));
            }
            if (update_fields.containsKey("deletedTimeMs")) {
                channel.setDeletedTimeMs(Long.parseLong(update_fields.getProperty("deletedTimeMs")));
            }
            if (update_fields.containsKey("group")) {
                channel.setGroup(update_fields.getProperty("group"));
            }
            if (update_fields.containsKey("blockableSystem")) {
                channel.setBlockable(Boolean.parseBoolean(update_fields.getProperty("blockableSystem")));
            }
            if (update_fields.containsKey("allowBubbles")) {
                channel.setAllowBubbles(Integer.parseInt(update_fields.getProperty("allowBubbles")));
            }
            if (update_fields.containsKey("importanceLockedDefaultApp")) {
                channel.setImportanceLockedByCriticalDeviceFunction(Boolean.parseBoolean(update_fields.getProperty("importanceLockedDefaultApp")));
            }
            if (update_fields.containsKey("originalImportance")) {
                channel.setOriginalImportance(Integer.parseInt(update_fields.getProperty("originalImportance")));
            }
            if (update_fields.containsKey("conversationId") && update_fields.containsKey("parentId")) {
                channel.setConversationId(update_fields.getProperty("parentId"), update_fields.getProperty("conversationId"));
            }
            if (update_fields.containsKey("demoted")) {
                channel.setDemoted(Boolean.parseBoolean(update_fields.getProperty("demoted")));
            }
            if (update_fields.containsKey("importantConvo")) {
                channel.setImportantConversation(Boolean.parseBoolean(update_fields.getProperty("importantConvo")));
            }
            if (update_fields.containsKey("lastNotificationUpdateTimeMs")) {
                channel.setLastNotificationUpdateTimeMs(Long.parseLong(update_fields.getProperty("lastNotificationUpdateTimeMs")));
            }

            notificationManager.updateNotificationChannelForPackage(pkg, uid, channel);
            System.out.println("Updated Notification Channel:\n");
            // System.out.println(channel.toJson());
            System.out.println(channel.toString());

        // } catch (RemoteException | JSONException e) {
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private static void updateNotificationChannelGroupForPackage(String pkg, String channelGroupId, Properties update_fields) {
        INotificationManager notificationManager = getNotificationManagerService();
        IPackageManager packageManager = getPackageManagerService();
        try {
            int uid = packageManager.getPackageUid(pkg, 0, 0);
            NotificationChannelGroup channelGroup = notificationManager.getNotificationChannelGroupForPackage(channelGroupId, pkg, uid);

            if (update_fields.containsKey("description")) {
                channelGroup.setDescription(update_fields.getProperty("description"));
            }
            if (update_fields.containsKey("blocked")) {
                channelGroup.setBlocked(Boolean.parseBoolean(update_fields.getProperty("blocked")));
            }

            notificationManager.updateNotificationChannelGroupForPackage(pkg, uid, channelGroup);
            System.out.println("Updated Notification Channel Group:\n");
            // System.out.println(channelGroup.toJson());
            System.out.println(channelGroup.toString());

        // } catch (RemoteException | JSONException e) {
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private static void unlockAllNotificationChannelsForPackage(String pkg) {
        INotificationManager notificationManager = getNotificationManagerService();
        IPackageManager packageManager = getPackageManagerService();
        try {
            int uid = packageManager.getPackageUid(pkg, 0, 0);
            ParceledListSlice<NotificationChannel> channels = notificationManager.getNotificationChannelsForPackage(pkg, uid, false);
            final List<NotificationChannel> list = channels.getList();
            for (NotificationChannel channel : list) {
                channel.setBlockable(true);
                notificationManager.updateNotificationChannelForPackage(pkg, uid, channel);
                System.out.println("Unlocked: channelId=\"" + channel.getId() + "\"");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws ParseException {
        Option helpCmd = Option.builder("h")
                               .longOpt("help")
                               .desc("Show help info")
                               .build();

        Option getCmd = Option.builder("get")
                              .desc("Get a specific Notification Channel or Group by using: -pkg <package> -id <channel_id> [-G] -get\nAlternatively, omit -id to get all Notification Channels/Groups for the given package: -pkg <package> [-D] [-G] -get")
                              .build();

        Option useChannelGroupsFlag = Option.builder("G")
                                            .longOpt("idIsGroup")
                                            .hasArg(false)
                                            .desc("Set this flag to indicate that -id refers to a Channel Group ID instead of a Channel ID")
                                            .build();

        Option includeDeletedFlag = Option.builder("D")
                                          .longOpt("includeDeleted")
                                          .hasArg(false)
                                          .desc("Use when calling -get without an -id (-pkg only) to include a package's deleted Notification Channels")
                                          .build();

        Option setCmd = Option.builder("set")
                                 .argName("update_fields")
                                 .hasArgs()
                                 .valueSeparator()
                                 .desc("Modify a Notification Channel or Group: -pkg <package> -id <channel_id> -set <update_fields>\nFor info on <update_fields> use: -fields")
                                 .build();

        Option fieldsInfoCmd = Option.builder("fields")
                                     .desc("Print the list of modifiable fields for use with -set\n")
                                     .build();

        Option unlockCmd = Option.builder("unlock")
                                     .desc("Unlock the Settings UI for all Notification Channels of a given package: -pkg <package> -unlock")
                                     .build();

        OptionGroup optionCmds = new OptionGroup()
                                      .addOption(helpCmd)
                                      .addOption(getCmd)
                                      .addOption(setCmd)
                                      .addOption(fieldsInfoCmd)
                                      .addOption(unlockCmd);
        optionCmds.setRequired(true);

        Option pkgOpt = Option.builder("pkg")
                              .argName("package")
                              .hasArg(true)
                              .desc("Package name")
                              .type(String.class)
                              .build();

        Option channelIdOpt = Option.builder("id")
                                    .argName("channel_or_group_id")
                                    .hasArg(true)
                                    .desc("By default -id is assumed to be a Notification Channel ID. In order to specifiy a Channel Group ID instead, -G must also be set")
                                    .type(String.class)
                                    .build();

        Options options = new Options()
                                .addOptionGroup(optionCmds)
                                .addOption(pkgOpt)
                                .addOption(channelIdOpt)
                                .addOption(useChannelGroupsFlag)
                                .addOption(includeDeletedFlag);

        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine line = parser.parse(options, args);
            if (line.hasOption(getCmd.getOpt())) {
                if (!line.hasOption(pkgOpt.getOpt())) {
                    System.err.println("Missing arg required: " + pkgOpt.getOpt());
                    System.exit(1);
                } else if (!line.hasOption(channelIdOpt.getOpt())) {
                    String pkg = line.getOptionValue(pkgOpt.getOpt());
                    boolean includeDeleted = line.hasOption(includeDeletedFlag.getOpt()) ? true : false;
                    if (line.hasOption(useChannelGroupsFlag.getOpt())) {
                        listNotificationChannelGroupsForPackage(pkg, includeDeleted);
                    } else {
                        listNotificationChannelsForPackage(pkg, includeDeleted);
                    }
                } else {
                    String pkg = line.getOptionValue(pkgOpt.getOpt());
                    String channelId = line.getOptionValue(channelIdOpt.getOpt());
                    if (line.hasOption(useChannelGroupsFlag.getOpt())) {
                        getNotificationChannelGroupForPackage(pkg, channelId);
                    } else {
                        getNotificationChannelForPackage(pkg, channelId);
                    }
                }
            } else if (line.hasOption(setCmd.getOpt())) {
                if (!line.hasOption(pkgOpt.getOpt()) || !line.hasOption(channelIdOpt.getOpt())) {
                    System.err.println("Missing arg(s) required: " + pkgOpt.getOpt() + ", " + channelIdOpt.getOpt());
                    System.exit(1);
                }
                String pkg = line.getOptionValue(pkgOpt.getOpt());
                String channelId = line.getOptionValue(channelIdOpt.getOpt());
                Properties properties = line.getOptionProperties(setCmd.getOpt());
                if (line.hasOption(useChannelGroupsFlag.getOpt())) {
                    updateNotificationChannelGroupForPackage(pkg, channelId, properties);
                } else {
                    updateNotificationChannelForPackage(pkg, channelId, properties);
                }
            } else if (line.hasOption(unlockCmd.getOpt())) {
                if (!line.hasOption(pkgOpt.getOpt())) {
                    System.err.println("Missing arg required: " + pkgOpt.getOpt());
                    System.exit(1);
                }
                String pkg = line.getOptionValue(pkgOpt.getOpt());
                unlockAllNotificationChannelsForPackage(pkg);
            } else if (line.hasOption(fieldsInfoCmd.getOpt())) {
                printUpdateFieldsInfo();
            } else {
                printHelp(options);
            }
        } catch (ParseException e) {
            System.err.println("Parsing error, message:");
            System.err.println(e.getMessage());
        }
    }
}
