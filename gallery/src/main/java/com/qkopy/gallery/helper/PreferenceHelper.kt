package com.qkopy.gallery.helper

import android.content.Context

object PreferenceHelper {
    private const val PREFS_FILE_NAME = "Gallery"
    fun firstTimeAskingPermission(
        context: Context,
        permission: String?,
        isFirstTime: Boolean
    ) {
        val preferences = context.getSharedPreferences(
            PREFS_FILE_NAME,
            Context.MODE_PRIVATE
        )
        preferences.edit().putBoolean(permission, isFirstTime).apply()
    }

    fun isFirstTimeAskingPermission(
        context: Context,
        permission: String?
    ): Boolean {
        return context.getSharedPreferences(
            PREFS_FILE_NAME,
            Context.MODE_PRIVATE
        ).getBoolean(permission, true)
    }
}
