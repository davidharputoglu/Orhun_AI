package com.harputoglu.orhun.util

import android.content.Context
import android.view.KeyEvent
import com.harputoglu.orhun.service.OrhunAccessibilityService

class CommandProcessor(private val context: Context) {

    fun processPayload(payload: String) {
        val parts = payload.split("|", limit = 2)
        val command = parts[0].uppercase().trim()
        val data = if (parts.size > 1) parts[1] else ""

        when (command) {
            "OPEN_TELEX" -> openTeletext()
            "VOLUME_UP" -> volumeChange(true)
            "VOLUME_DOWN" -> volumeChange(false)
            "SHOW_NOTE" -> broadcastContent("NOTE", data)
            "SHOW_ANNOUNCEMENT" -> broadcastContent("ANNOUNCEMENT", data)
            "LAUNCH_APP" -> launchApp(data)
            "PLAY_MEDIA" -> playMedia(data)
            "MEDIA_PLAY" -> sendMediaCommand("PLAY")
            "MEDIA_PAUSE" -> sendMediaCommand("PAUSE")
            "MEDIA_SEEK" -> sendMediaCommand("SEEK", data)
            "ANNOUNCE_VOICE" -> playVoiceAnnounce(data)
            "SEARCH_MEDIA" -> openGallery(data)
        }
    }

    private fun openGallery(query: String) {
        val intent = android.content.Intent(context, com.harputoglu.orhun.ui.GalleryActivity::class.java).apply {
            putExtra("QUERY", query)
            addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    private fun playVoiceAnnounce(audioUrl: String) {
        // 1. Baisser le son du média en cours
        sendMediaCommand("DUCK_START")
        
        // 2. Jouer l'annonce (via un MediaPlayer temporaire ou BroadcastActivity)
        val intent = android.content.Intent(context, com.harputoglu.orhun.ui.BroadcastActivity::class.java).apply {
            putExtra("TYPE", "VOICE_ANNOUNCEMENT")
            putExtra("AUDIO_URL", audioUrl)
            addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
        
        // Note: Le DUCK_END sera envoyé par la BroadcastActivity quand l'audio sera fini
    }

    private fun sendMediaCommand(action: String, data: String = "") {
        val intent = android.content.Intent("com.harputoglu.orhun.MEDIA_CONTROL").apply {
            putExtra("ACTION", action)
            putExtra("DATA", data)
        }
        context.sendBroadcast(intent)
    }

    private fun playMedia(data: String) {
        // Format: URL|TITLE|THUMBNAIL_URL
        val parts = data.split("|")
        val url = parts.getOrNull(0) ?: ""
        val title = parts.getOrNull(1) ?: "Média"
        val thumb = parts.getOrNull(2) ?: ""

        val intent = android.content.Intent(context, com.harputoglu.orhun.ui.MediaActivity::class.java).apply {
            putExtra("MEDIA_URL", url)
            putExtra("TITLE", title)
            putExtra("THUMBNAIL_URL", thumb)
            addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    private fun launchApp(packageName: String) {
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)
        intent?.let {
            it.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(it)
        }
    }

    private fun broadcastContent(type: String, content: String) {
        val intent = android.content.Intent(context, com.harputoglu.orhun.ui.BroadcastActivity::class.java).apply {
            putExtra("TYPE", type)
            putExtra("CONTENT", content)
            addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    private fun openTeletext() {
        // Use accessibility service to send the TELETEXT key event
        OrhunAccessibilityService.instance?.sendKey(KeyEvent.KEYCODE_TV_TELETEXT)
    }

    private fun volumeChange(increase: Boolean) {
        val action = if (increase) KeyEvent.KEYCODE_VOLUME_UP else KeyEvent.KEYCODE_VOLUME_DOWN
        OrhunAccessibilityService.instance?.sendKey(action)
    }
}
