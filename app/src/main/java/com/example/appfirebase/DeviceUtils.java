package com.example.appfirebase;

import android.content.Context;
import android.provider.Settings;
import android.telephony.TelephonyManager;

public class DeviceUtils {
    public static String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }
}
