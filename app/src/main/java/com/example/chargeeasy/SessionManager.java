package com.example.chargeeasy;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Manages the active user session using SharedPreferences.
 * Stores only the ID of the currently logged-in user.
 */
public class SessionManager {

    private static final String PREF_NAME = "ChargeEasySession";
    private static final String KEY_USER_ID = "user_id";
    private final SharedPreferences prefs;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Call this when a user successfully signs up or logs in.
     */
    public void createLoginSession(long userId) {
        prefs.edit().putLong(KEY_USER_ID, userId).apply();
    }

    /**
     * Gets the ID of the currently logged-in user.
     * Returns -1 if no user is logged in.
     */
    public long getActiveUserId() {
        return prefs.getLong(KEY_USER_ID, -1);
    }

    /**
     * Call this when a user logs out.
     */
    public void logoutUser() {
        prefs.edit().clear().apply();
    }

    public boolean isLoggedIn() {
        return getActiveUserId() != -1;
    }
}