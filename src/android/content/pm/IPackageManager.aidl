package android.content.pm;

import android.content.pm.PackageInfo;
import android.content.pm.VersionedPackage;

interface IPackageManager {
    void checkPackageStartable(String packageName, int userId);
    boolean isPackageAvailable(String packageName, int userId);
    PackageInfo getPackageInfo(String packageName, long flags, int userId);
    PackageInfo getPackageInfoVersioned(in VersionedPackage versionedPackage, long flags, int userId);
    int getPackageUid(String packageName, long flags, int userId);
    int[] getPackageGids(String packageName, long flags, int userId);
    List<String> getAllPackages();
    String[] getPackagesForUid(int uid);
    String getNameForUid(int uid);
    String[] getNamesForUids(in int[] uids);
    int getUidForSharedUser(String sharedUserName);
    int getFlagsForUid(int uid);
    int getPrivateFlagsForUid(int uid);
    boolean isUidPrivileged(int uid);
}