package com.harputoglu.orhun.util

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

enum class DownloadStep {
    IDLE, ANALYZING, CHECKING_SPACE, DOWNLOADING_PARTS, MERGING, COMPLETED, ERROR
}

data class DownloadStatus(
    val step: DownloadStep = DownloadStep.IDLE,
    val progress: Float = 0f,
    val message: String = "",
    val detail: String = ""
)

class DownloadManager(private val context: android.content.Context) {
    private val _status = MutableStateFlow(DownloadStatus())
    val status: StateFlow<DownloadStatus> = _status

    suspend fun startDownload(url: String, destinationPath: String) {
        try {
            // 1. Analyse du lien
            _status.value = DownloadStatus(DownloadStep.ANALYZING, 0.1f, "Analyse du lien...", "Vérification de la source média")
            kotlinx.coroutines.delay(1000)

            // 2. Vérification de l'espace disque
            _status.value = DownloadStatus(DownloadStep.CHECKING_SPACE, 0.2f, "Vérification de l'espace...", "Calcul de l'espace requis sur la Micro-SD")
            kotlinx.coroutines.delay(1000)

            // 3. Téléchargement par parties (simulation)
            for (i in 1..4) {
                _status.value = DownloadStatus(DownloadStep.DOWNLOADING_PARTS, 0.2f + (i * 0.15f), "Téléchargement...", "Segment $i sur 4 en cours")
                kotlinx.coroutines.delay(1500)
            }

            // 4. Fusion des fichiers
            _status.value = DownloadStatus(DownloadStep.MERGING, 0.9f, "Fusion des segments...", "Assemblage final du fichier vidéo")
            kotlinx.coroutines.delay(2000)

            // 5. Terminé
            _status.value = DownloadStatus(DownloadStep.COMPLETED, 1.0f, "Téléchargement terminé !", "Fichier enregistré dans Orhun_Captures")
            triggerNotification(context)
        } catch (e: Exception) {
            _status.value = DownloadStatus(DownloadStep.ERROR, 0f, "Erreur", e.message ?: "Inconnu")
        }
    }

    private fun triggerNotification(context: android.content.Context) {
        val prefs = com.harputoglu.orhun.data.local.prefs.UserPreferences(context)
        
        if (prefs.notificationVibrationEnabled) {
            val vibrator = context.getSystemService(android.content.Context.VIBRATOR_SERVICE) as android.os.Vibrator
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                vibrator.vibrate(android.os.VibrationEffect.createOneShot(500, android.os.VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                vibrator.vibrate(500)
            }
        }
        
        if (prefs.notificationSoundEnabled) {
            val notification = android.media.RingtoneManager.getDefaultUri(android.media.RingtoneManager.TYPE_NOTIFICATION)
            val r = android.media.RingtoneManager.getRingtone(context, notification)
            r.play()
        }
    }
}
