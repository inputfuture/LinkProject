package com.letv.auto.keypad.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by luzhiyong on 15-1-13.
 */
public class LetvSettings {

    private static final String KEY_SPEAK_INCOMING_CALL = "setting_speak_incoming_call";
    private static final String KEY_SPEAK_SMS = "setting_speak_sms";
    private static final String KEY_QUICK_RESPONSE = "setting_quick_response";
    private static final String KEY_SWITCH_SCREEN = "setting_switch_screen";
    private static final String KEY_SWITCH_WECHAT = "setting_switch_wechat";
    private static final String KEY_SWITCH_QQ = "setting_switch_qq";
    private static final String KEY_SWITCH_IMMSG = "setting_switch_immsg";
    private static final String KEY_SERVICE_SCREEN = "setting_service_screen";
    public static final String KEY_VOICE_WAKEUP = "setting_voice_wakeup";
    private static final String KEY_SMS_LAST_CHECK_TIME = "key_sms_last_check_time";
    private static final String KEY_KEYPAD_STATE = "key_keypad_state";
    private static final String KEY_KEYPAD_CONN_STATE = "key_keypad_conn_state";
    private static final String KEY_KEYPAD_SOUND = "key_keypad_sound";
    private static final String KEY_CUSTOM_KEYPAD = "key_custom_keypad";
    private static final String KEY_KEYPAD_REMINDER_LIGHT = "key_keypad_reminder_light";
    private static final String KEY_APP_SHOW_BLUETOOTH_DIALOG = "key_app_show_bluetooth_dialog";
    private static final String KEY_BATTERY_CHANGE = "key_battery_change";
    private static final String KEY_LETV_KEYPAD_INSTRUCTION = "key_letv_keypad_instruction";
    public static final int CUSTOM_KEY_VOICE = 0;
    public static final int CUSTOM_KEY_SNAPSHOT = 1;
    public static final int CUSTOM_KEY_CAPTURE_VIDEO = 2;
    public static final int CUSTOM_KEY_SHUFFLE_PLAY = 3;
    public static final int CUSTOM_KEY_NAVIGATION_HOME = 4;
    public static final int CUSTOM_KEY_NAVIGATION_COMPANY = 5;

    private static final String OBD_ADDR = "obd_addr";

    public static final boolean getSpeakIncomingCall(final Context context) {
        return getPrefs(context).getBoolean(KEY_SPEAK_INCOMING_CALL, false);
    }

    public static final void setSpeakIncomingCall(final Context context, boolean value) {
        getPrefs(context).edit().putBoolean(KEY_SPEAK_INCOMING_CALL, value).commit();
    }

    public static final boolean getSpeakSms(final Context context) {
        return getPrefs(context).getBoolean(KEY_SPEAK_SMS, true);
    }

    public static final void setSpeakSms(final Context context, boolean value) {
        getPrefs(context).edit().putBoolean(KEY_SPEAK_SMS, value).commit();
    }

    public static final boolean getQuickResponse(final Context context) {
        return getPrefs(context).getBoolean(KEY_QUICK_RESPONSE, false);
    }

    public static final void setQuickResponse(final Context context, boolean value) {
        getPrefs(context).edit().putBoolean(KEY_QUICK_RESPONSE, value).commit();
    }

    public static final boolean getSwitchScreen(final Context context) {
        return getPrefs(context).getBoolean(KEY_SWITCH_SCREEN, false);
    }

    public static final void setSwitchScreen(final Context context, boolean value) {
        getPrefs(context).edit().putBoolean(KEY_SWITCH_SCREEN, value).commit();
    }

    public static final boolean getServiceScreen(final Context context) {
        return getPrefs(context).getBoolean(KEY_SERVICE_SCREEN, false);
    }

    public static final void setServiceScreen(final Context context, boolean value) {
        getPrefs(context).edit().putBoolean(KEY_SERVICE_SCREEN, value).commit();
    }

    public static final void setSwitchWechat(final Context context, boolean value) {
        getPrefs(context).edit().putBoolean(KEY_SWITCH_WECHAT, value).commit();
    }

    public static final boolean getSwitchWechat(final Context context) {
        return getPrefs(context).getBoolean(KEY_SWITCH_WECHAT, false);
    }

    public static final void setSwitchQQ(final Context context, boolean value) {
        getPrefs(context).edit().putBoolean(KEY_SWITCH_QQ, value).commit();
    }

    public static final boolean getSwitchQQ(final Context context) {
        return getPrefs(context).getBoolean(KEY_SWITCH_QQ, false);
    }

    public static final void setSwitchIMMSG(final Context context, boolean value) {
        getPrefs(context).edit().putBoolean(KEY_SWITCH_IMMSG, value).commit();
    }

    public static final boolean getSwitchIMMSG(final Context context) {
        return getPrefs(context).getBoolean(KEY_SWITCH_IMMSG, false);
    }

    public static final boolean getVoiceWakeup(final Context context) {
        return getPrefs(context).getBoolean(KEY_VOICE_WAKEUP, true);
    }

    public static final void setVoiceWakeup(final Context context, boolean value) {
        getPrefs(context).edit().putBoolean(KEY_VOICE_WAKEUP, value).commit();
    }


    public static final long getLastSmsTime(final Context context) {
        return getPrefs(context).getLong(KEY_SMS_LAST_CHECK_TIME, 0);
    }

    public static final void setLastSmsTime(final Context context, long value) {
        getPrefs(context).edit().putLong(KEY_SMS_LAST_CHECK_TIME, value).commit();
    }

    public static final boolean getKeypadState(final Context context) {
        return getPrefs(context).getBoolean(KEY_KEYPAD_STATE, false);
    }

    public static final void setKeypadConnectionState(final Context context, boolean value) {
        getPrefs(context).edit().putBoolean(KEY_KEYPAD_CONN_STATE, value).commit();
    }

    public static final boolean getKeypadConnectionState(final Context context) {
        return getPrefs(context).getBoolean(KEY_KEYPAD_CONN_STATE, false);
    }

    public static final void setKeypadState(final Context context, boolean value) {
        getPrefs(context).edit().putBoolean(KEY_KEYPAD_STATE, value).commit();
    }

    public static final boolean getKeypadSound(final Context context) {
        return getPrefs(context).getBoolean(KEY_KEYPAD_SOUND, true);
    }

    public static final void setKeypadSound(final Context context, boolean value) {
        getPrefs(context).edit().putBoolean(KEY_KEYPAD_SOUND, value).commit();
    }

    public static final int getCustomKeyPad(final Context context) {
        return getPrefs(context).getInt(KEY_CUSTOM_KEYPAD, 0);
    }

    public static final void setCustomKeyPad(final Context context, int value) {
        getPrefs(context).edit().putInt(KEY_CUSTOM_KEYPAD, value).commit();
    }

    public static final boolean getKeypadReminderLight(final Context context) {
        return getPrefs(context).getBoolean(KEY_KEYPAD_REMINDER_LIGHT, true);
    }

    public static final void setKeypadReminderLight(final Context context, boolean value) {
        getPrefs(context).edit().putBoolean(KEY_KEYPAD_REMINDER_LIGHT, value).commit();
    }

    public static final String getObdAddr(final Context context) {
        return getPrefs(context).getString(OBD_ADDR, "");
    }

    public static final void setObdAddr(final Context context, String addr) {
        getPrefs(context).edit().putString(OBD_ADDR, addr).commit();
    }

    public static final boolean getShowBlueToothState(final Context context) {
        return getPrefs(context).getBoolean(KEY_APP_SHOW_BLUETOOTH_DIALOG, true);
    }

    public static final void setShowBlueToothState(final Context context, boolean value) {
        getPrefs(context).edit().putBoolean(KEY_APP_SHOW_BLUETOOTH_DIALOG, value).commit();
    }

    public static final int getBatteryChange(final Context context) {
        return getPrefs(context).getInt(KEY_BATTERY_CHANGE, 50);
    }

    public static final void setBatteryChange(final Context context, int newValue) {
        getPrefs(context).edit().putInt(KEY_BATTERY_CHANGE, newValue).commit();
    }

    public static final String getLetvKeypadInstruction(final Context context) {
        return getPrefs(context).getString(KEY_LETV_KEYPAD_INSTRUCTION, "00000000");
    }

    public static final void setLetvKeypadInstruction(final Context context, String newValue) {
        getPrefs(context).edit().putString(KEY_LETV_KEYPAD_INSTRUCTION, newValue).commit();
    }

    private static final SharedPreferences getPrefs(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    private LetvSettings() {
    }
}
