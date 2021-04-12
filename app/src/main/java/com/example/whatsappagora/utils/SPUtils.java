package com.example.whatsappagora.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.whatsappagora.Constants;

public class SPUtils {
    public static void saveUserId(Context context, String id) {
        SharedPreferences pf = context.getSharedPreferences(Constants.SP_NAME, Context.MODE_PRIVATE);
        pf.edit().putString(Constants.PREF_USER_ID, id).apply();
    }

    public static String getUserId(Context context) {
        SharedPreferences pf = context.getSharedPreferences(Constants.SP_NAME, Context.MODE_PRIVATE);
        return pf.getString(Constants.PREF_USER_ID, null);
    }
}