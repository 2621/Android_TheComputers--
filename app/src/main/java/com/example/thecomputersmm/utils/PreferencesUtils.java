package com.example.thecomputersmm.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferencesUtils {
    public PreferencesUtils(){}

    public static boolean saveUsername (String username, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = preferences.edit();
        prefsEditor.putString(Constants.USERNAME, username);
        prefsEditor.apply();
        return true;
    }

    public static String getUsername (Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(Constants.USERNAME,"");
    }
}