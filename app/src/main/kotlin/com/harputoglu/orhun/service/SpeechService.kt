package com.harputoglu.orhun.service

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

class SpeechService(context: Context) : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech = TextToSpeech(context, this)
    private var isInitialized = false

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isInitialized = true
            tts.language = Locale.FRENCH // Default, can be changed
        }
    }

    fun speak(text: String) {
        if (isInitialized) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    fun setLanguage(locale: Locale) {
        tts.language = locale
    }

    fun shutdown() {
        tts.stop()
        tts.shutdown()
    }
}
