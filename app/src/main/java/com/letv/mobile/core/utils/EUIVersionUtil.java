package com.letv.mobile.core.utils;

import android.util.Log;

import com.letv.leauto.ecolink.utils.Trace;

/**
 * @author liyikun
 *         获取 eui 系统版本的工具类
 *         5.0.119D
 *         5.0 表示 eui 版本为 1.0
 *         119 表示 eui 是当前的第 119 个版本
 *         D 开发版 development
 *         E 体验版 experience
 *         S 稳定版 stable
 *         wiki http://wiki.letv.cn/pages/viewpage.action?pageId=45285771
 */
public class EUIVersionUtil {
    private static final String TAG = "EUIVersion";
    private static final boolean DEBUG = true;

    private static final String KEY_EUI_VERSION = "ro.letv.release.version";
    private static final int VERSION_START = 5;

    private static final String VERSION_TYPE_DEVELOPMENT = "D";
    private static final String VERSION_TYPE_EXPERIENCE = "E";
    private static final String VERSION_TYPE_STABLE = "S";

    private static final String SPLIT = "\\.";

    private static String sFullCode = null;
    private static String sVersionType = null;
    private static String sMainVersion = null;
    private static String sMainBigVersion = null;
    private static String sMainSmallVersion = null;
    private static String sVersionCount = null;

    private static final int MIN_VERSION_LENGTH = 8;

    /**
     * @return eui 本机全部版本号
     *         e.g 5.0.119D
     */
    public static String getFullCode() {
        if (sFullCode == null) {
            sFullCode = SystemUtil.getSystemProperty(KEY_EUI_VERSION);
        }
        if (DEBUG) {
            Trace.Error(TAG, "EUI Version : " + sFullCode);
        }
        return sFullCode;
    }

    /**
     * @return 本机版本类型
     * @see #VERSION_DEVELOPMENT
     * @see #VERSION_EXPERIENCE
     * @see #VERSION_STABLE
     */
    static String getVersionType() {
        if (sVersionType == null) {
            String fullCode = getFullCode();
            sVersionType = getVersionType(fullCode);
        }
        return sVersionType;
    }

    /**
     * @param fullCode
     *            要判断版本类型的eui码
     * @return 版本类型
     * @see #VERSION_DEVELOPMENT
     * @see #VERSION_EXPERIENCE
     * @see #VERSION_STABLE
     */
    static String getVersionType(String fullCode) {
        char type = fullCode.charAt(fullCode.length() - 1);
        if (DEBUG) {
            Trace.Error(TAG, "EUI Version type : " + type);
        }
        return String.valueOf(type);
    }

    /**
     * @param fullCode
     *            要判断版本类型的eui码
     * @return if is development version or not
     *         e.g true or false
     */
    static boolean isDevelopmentVersion(String fullCode) {
        if (getVersionType(fullCode).equals(VERSION_TYPE_DEVELOPMENT)) {
            return true;
        }
        return false;
    }

    /**
     * @return if is development version or not
     *         e.g true or false
     */
    static boolean isDevelopmentVersion() {
        return isDevelopmentVersion(getFullCode());
    }

    /**
     * @param fullCode
     *            要判断版本类型的eui码
     * @return if is experience version or not
     *         e.g true or false
     */
    static boolean isExperienceVersion(String fullCode) {
        if (getVersionType(fullCode).equals(VERSION_TYPE_EXPERIENCE)) {
            return true;
        }
        return false;
    }

    /**
     * @return if is experience version or not
     *         e.g true or false
     */
    static boolean isExperienceVersion() {
        return isExperienceVersion(getFullCode());
    }

    /**
     * @param fullCode
     *            要判断版本类型的eui码
     * @return if is stable version or not
     *         e.g true or false
     */
    static boolean isStableVersion(String fullCode) {
        if (getVersionType(fullCode).equals(VERSION_TYPE_STABLE)) {
            return true;
        }
        return false;
    }

    /**
     * @return if is stable version or not
     *         e.g true or false
     */
    static boolean isStableVersion() {
        return isStableVersion(getFullCode());
    }

    /**
     * @param fullCode
     *            要判断版本类型的eui码
     * @return 当前版本下的第几个版本
     * @exception 返回0
     */
    static int getVersionCountForInt(String fullCode) {
        String versionCount = getVersionCountForString(fullCode);
        int count = 0;
        try {
            count = Integer.parseInt(versionCount);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        if (DEBUG) {
            Trace.Error(TAG, "EUI Version count : " + count);
        }
        return count;
    }

    /**
     * @return 当前版本下的第几个版本
     * @exception 返回0
     */
    static int getVersionCountForInt() {
        return getVersionCountForInt(getFullCode());
    }

    /**
     * @param fullCode
     *            要判断版本类型的eui码
     * @return 当前版本下的第几个版本
     */
    static String getVersionCountForString(String fullCode) {
        String[] splits = fullCode.split(SPLIT);
        String temp = splits[splits.length - 1];
        String versionCount = temp.substring(0, temp.length() - 1);
        if (DEBUG) {
            Trace.Error(TAG, "EUI Version count String : " + versionCount);
        }
        return versionCount;
    }

    /**
     * @return 当前版本下的第几个版本
     */
    static String getVersionCountForString() {
        if (sVersionCount == null) {
            sVersionCount = getVersionCountForString(getFullCode());
        }
        return sVersionCount;
    }

    /**
     * @param fullCode
     *            要判断版本类型的eui码
     * @return 主版本
     *         e.g 5.0
     */
    static String getMainVersion(String fullCode) {
        return getMainBigVersionForString(fullCode) + SPLIT
                + getMainSmallVersionForString(fullCode);
    }

    /**
     * @return 主版本
     *         e.g 5.0
     */
    static String getMainVersion() {
        if (sMainVersion == null) {
            sMainVersion = getMainVersion(getFullCode());
        }
        return sMainVersion;
    }

    /**
     * @param fullCode
     *            要判断版本类型的eui码
     * @return 主版本，小数点之前
     *         e.g 5
     */
    static String getMainBigVersionForString(String fullCode) {
        String[] splits = fullCode.split(SPLIT);
        if (DEBUG) {
            Trace.Error(TAG, "splits : " + splits[0]);
        }
        return splits[0];
    }

    /**
     * @return 主版本，小数点之前
     *         e.g 5
     */
    static String getMainBigVersionForString() {
        if (sMainBigVersion == null) {
            sMainBigVersion = getMainBigVersionForString(getFullCode());
        }
        return sMainBigVersion;
    }

    /**
     * @param fullCode
     *            要判断版本类型的eui码
     * @return 主版本，小数点之前
     *         e.g 5
     * @exception 返回负1
     */
    static int getMainBigVersionForInt(String fullCode) {
        int version = -1;
        try {
            version = Integer.parseInt(getMainBigVersionForString(fullCode));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        if (DEBUG) {
            Trace.Error(TAG, "EUI Main Big Version : " + version);
        }
        return version;
    }

    /**
     * @return 主版本，小数点之前
     *         e.g 5
     * @exception 返回负1
     */
    static int getMainBigVersionForInt() {
        return getMainBigVersionForInt(getFullCode());
    }

    /**
     * @param fullCode
     *            要判断版本类型的eui码
     * @return 主版本，小数点之后
     *         e.g 0
     */
    static String getMainSmallVersionForString(String fullCode) {
        String[] splits = fullCode.split(SPLIT);
        if (DEBUG) {
            Trace.Error("get main small version for string", splits[1]);
        }
        return splits[1];
    }

    /**
     * @return 本机主版本，小数点之后
     *         e.g 0
     */
    static String getMainSmallVersionForString() {
        if (sMainSmallVersion == null) {
            sMainSmallVersion = getMainSmallVersionForString(getFullCode());
        }
        return sMainSmallVersion;
    }

    /**
     * @param fullCode
     *            要判断版本类型的eui码
     * @return 主版本，小数点之后
     *         e.g 5
     * @exception 返回负1
     */
    static int getMainSmallVersionForInt(String fullCode) {
        int version = -1;
        try {
            version = Integer.parseInt(getMainBigVersionForString(fullCode));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        if (DEBUG) {
            Trace.Error(TAG, "EUI Main Small Version : " + version);
        }
        return version;
    }

    /**
     * @return 主版本，小数点之后
     *         e.g 5
     * @exception 返回负1
     */
    static int getMainSmallVersionForInt() {
        return getMainSmallVersionForInt(getFullCode());
    }

    /**
     * 如果仅仅是判断是否支持系统版本，使用以下方法，因为这个方法不仅在算法上做了优化，而且更安全，传入时请传入整个version
     * @param development_version
     *            开发版version
     * @param experience_version
     *            体验版version
     * @param stable_version
     *            正式版version
     * @return true or false
     */
    public static boolean isSupportTheVersion(String development_version, String experience_version,
            String stable_version) {
        if (getFullCode() == null
                || getFullCode().length() < MIN_VERSION_LENGTH) {
            return false;
        }

        if (isDevelopmentVersion() && development_version != null) {
            if (compareTo2(development_version) >= EUI_VERSION_COMPARE_EQUAL) {
                return true;
            }
        } else if (isExperienceVersion() && experience_version != null) {
            if (compareTo2(experience_version) >= EUI_VERSION_COMPARE_EQUAL) {
                return true;
            }
        } else if (isStableVersion() && stable_version != null) {
            if (compareTo2(stable_version) >= EUI_VERSION_COMPARE_EQUAL) {
                return true;
            }
        }
        return false;
    }

    // 5.0.119D
    // 大版本号（比如 5）所在数组的位置
    private static int EUI_VERSION_LENGTH_BIG = 0;
    // 小版本号（比如 0）所在数组的位置
    private static int EUI_VERSION_LENGTH_SMALL = 1;
    // 当前版本下的第几个版本（比如119）所在数组的位置
    private static int EUI_VERSION_LENGTH_COUNT = 2;
    // 数组正确的长度
    private static int EUI_VERSION_RIGHT_LENGTH_ARRAY = 3;
    // 大版本号最小长度
    private static int EUI_VERSION_RIGHT_LENGTH_BIG = 1;
    // 小版本号正确的长度
    private static int EUI_VERSION_RIGHT_LENGTH_SMALL = 1;
    // 当前版本下的第几个版本正确的长度
    private static int EUI_VERSION_RIGHT_LENGTH_COUNT = 4;

    // 将本机version与给定version做比较
    private static int compareTo2(String version) {
        int result = EUI_VERSION_COMPARE_CANT_COMPARE;
        String[] array = version.split(SPLIT);
        if (array == null || array.length != EUI_VERSION_RIGHT_LENGTH_ARRAY
                || array[EUI_VERSION_LENGTH_BIG] == null
                || array[EUI_VERSION_LENGTH_BIG]
                        .length() < EUI_VERSION_RIGHT_LENGTH_BIG
                || array[EUI_VERSION_LENGTH_SMALL] == null
                || array[EUI_VERSION_LENGTH_SMALL]
                        .length() != EUI_VERSION_RIGHT_LENGTH_SMALL
                || array[EUI_VERSION_LENGTH_COUNT] == null
                || array[EUI_VERSION_LENGTH_COUNT]
                        .length() != EUI_VERSION_RIGHT_LENGTH_COUNT) {
            return EUI_VERSION_COMPARE_CANT_COMPARE;
        }
        String type = array[EUI_VERSION_LENGTH_COUNT]
                .substring(array[EUI_VERSION_LENGTH_COUNT].length() - 1);
        if (!getVersionType().equals(type)) {
            return EUI_VERSION_COMPARE_CANT_COMPARE;
        }
        String mainBigVersion1 = getMainBigVersionForString();
        String mainBigVersion2 = array[EUI_VERSION_LENGTH_BIG];
        result = mainBigVersion1.compareTo(mainBigVersion2);
        if (DEBUG) {
            Trace.Error(TAG, "main big version 1 " + mainBigVersion1
                    + " compare to 2 " + mainBigVersion2 + " : " + result);
        }
        if (result == EUI_VERSION_COMPARE_EQUAL) {
            String mainSmallVersion1 = getMainSmallVersionForString();
            String mainSmallVersion2 = array[EUI_VERSION_LENGTH_SMALL];
            result = mainSmallVersion1.compareTo(mainSmallVersion2);
            if (DEBUG) {
                Trace.Error(TAG,
                        "main small version 1 " + mainSmallVersion1
                                + " compare to 2 " + mainSmallVersion2 + " : "
                                + result);
            }
            if (result == EUI_VERSION_COMPARE_EQUAL) {
                String versionCount1 = getVersionCountForString();
                String versionCount2 = array[EUI_VERSION_LENGTH_COUNT]
                        .substring(0,
                                array[EUI_VERSION_LENGTH_COUNT].length() - 1);
                result = versionCount1.compareTo(versionCount2);
                if (DEBUG) {
                    Trace.Error(TAG,
                            "version count 1 " + versionCount1
                                    + " compare to 2 " + versionCount2 + " : "
                                    + result);
                }
            }
        }
        if (result > EUI_VERSION_COMPARE_EQUAL) {
            return EUI_VERSION_COMPARE_BEFORE;
        } else if (result < EUI_VERSION_COMPARE_EQUAL) {
            return EUI_VERSION_COMPARE_AFTER;
        } else {
            return EUI_VERSION_COMPARE_EQUAL;
        }
    }

    public static final int EUI_VERSION_COMPARE_CANT_COMPARE = -2;
    public static final int EUI_VERSION_COMPARE_AFTER = -1;
    public static final int EUI_VERSION_COMPARE_EQUAL = 0;
    public static final int EUI_VERSION_COMPARE_BEFORE = 1;

    /**
     * @param version1
     *            要比较的版本号
     * @param version2
     *            被比较的版本号
     * @return 如果version1在version2之后，返回1
     *         如果version1在version2之前，返回-1
     *         如果version1和version2相同，返回0
     * @exception 无法比较
     *                返回-2
     */
    static int compare(String version1, String version2) {
        if (version1 == null || version2 == null) {
            return EUI_VERSION_COMPARE_CANT_COMPARE;
        }

        if ((EUIVersionUtil.isDevelopmentVersion(version1)
                && EUIVersionUtil.isDevelopmentVersion(version2))
                || (EUIVersionUtil.isExperienceVersion(version1)
                        && EUIVersionUtil.isExperienceVersion(version2))
                || (EUIVersionUtil.isStableVersion(version1)
                        && EUIVersionUtil.isStableVersion(version2))) {
            int mainBigVersion1 = EUIVersionUtil
                    .getMainBigVersionForInt(version1);
            int mainBigVersion2 = EUIVersionUtil
                    .getMainBigVersionForInt(version2);
            if (mainBigVersion1 == -1 || mainBigVersion2 == -1) {
                return EUI_VERSION_COMPARE_CANT_COMPARE;
            }
            if (mainBigVersion1 > mainBigVersion2) {
                return EUI_VERSION_COMPARE_BEFORE;
            } else if (mainBigVersion1 < mainBigVersion2) {
                return EUI_VERSION_COMPARE_AFTER;
            } else {
                int mainSmallVersion1 = EUIVersionUtil
                        .getMainSmallVersionForInt(version1);
                int mainSmallVersion2 = EUIVersionUtil
                        .getMainSmallVersionForInt(version2);
                if (mainSmallVersion1 == -1 || mainSmallVersion2 == -1) {
                    return EUI_VERSION_COMPARE_CANT_COMPARE;
                }
                if (mainSmallVersion1 > mainSmallVersion2) {
                    return EUI_VERSION_COMPARE_BEFORE;
                } else if (mainSmallVersion1 < mainSmallVersion2) {
                    return EUI_VERSION_COMPARE_AFTER;
                } else {
                    int versionCount1 = EUIVersionUtil
                            .getVersionCountForInt(version1);
                    int versionCount2 = EUIVersionUtil
                            .getVersionCountForInt(version2);
                    if (versionCount1 == 0 || versionCount2 == 0) {
                        return EUI_VERSION_COMPARE_CANT_COMPARE;
                    }
                    if (versionCount1 > versionCount2) {
                        return EUI_VERSION_COMPARE_BEFORE;
                    } else if (versionCount1 < versionCount2) {
                        return EUI_VERSION_COMPARE_AFTER;
                    } else {
                        return EUI_VERSION_COMPARE_EQUAL;
                    }
                }
            }
        }

        return EUI_VERSION_COMPARE_CANT_COMPARE;
    }

    /**
     * @param version
     *            要比较的版本号
     * @return 如果eui在version之后，返回1
     *         如果eui在version之前，返回-1
     *         如果eui和version相同，返回0
     * @exception 无法比较
     *                返回-2
     */
    static int compareTo(String version) {
        return compare(getFullCode(), version);
    }

    // for test
    static void getString() {
        if (DEBUG) {
            Trace.Error(TAG, "full code : " + getFullCode() + "\n"
                    + "main big version : " + getMainBigVersionForString()
                    + "\n" + "main small version : "
                    + getMainSmallVersionForString() + "\n" + "version count : "
                    + getVersionCountForString() + "\n" + "version type : "
                    + getVersionType());
        }

    }
}
