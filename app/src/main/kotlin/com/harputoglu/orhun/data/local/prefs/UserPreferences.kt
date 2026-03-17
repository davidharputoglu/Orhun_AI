package com.harputoglu.orhun.data.local.prefs

import android.content.Context
import android.content.SharedPreferences

class UserPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("orhun_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_DOWNLOAD_PATH = "download_path"
        private const val KEY_NOTIF_SOUND = "notif_sound"
        private const val KEY_NOTIF_VIBRO = "notif_vibro"
        private const val KEY_CURRENT_VOICE_ID = "current_voice_id"
        private const val KEY_VOICE_EMOTION = "voice_emotion"
        private const val KEY_AUTO_EMOTION = "auto_emotion"
        private const val KEY_GAMER_MODE = "gamer_mode"
    }

    var downloadPath: String?
        get() = prefs.getString(KEY_DOWNLOAD_PATH, null)
        set(value) = prefs.edit().putString(KEY_DOWNLOAD_PATH, value).apply()

    var currentVoiceId: String
        get() = prefs.getString(KEY_CURRENT_VOICE_ID, "coqui/XTTS-v2-french") ?: "coqui/XTTS-v2-french"
        set(value) = prefs.edit().putString(KEY_CURRENT_VOICE_ID, value).apply()

    var currentVoiceEmotion: String
        get() = prefs.getString(KEY_VOICE_EMOTION, "Naturel") ?: "Naturel"
        set(value) = prefs.edit().putString(KEY_VOICE_EMOTION, value).apply()

    var autoEmotionEnabled: Boolean
        get() = prefs.getBoolean(KEY_AUTO_EMOTION, false)
        set(value) = prefs.edit().putBoolean(KEY_AUTO_EMOTION, value).apply()

    var gamerModeEnabled: Boolean
        get() = prefs.getBoolean(KEY_GAMER_MODE, false)
        set(value) = prefs.edit().putBoolean(KEY_GAMER_MODE, value).apply()

    var notificationSoundEnabled: Boolean
        get() = prefs.getBoolean(KEY_NOTIF_SOUND, true)
        set(value) = prefs.edit().putBoolean(KEY_NOTIF_SOUND, value).apply()

    var notificationVibrationEnabled: Boolean
        get() = prefs.getBoolean(KEY_NOTIF_VIBRO, true)
        set(value) = prefs.edit().putBoolean(KEY_NOTIF_VIBRO, value).apply()
}
