package com.harputoglu.orhun.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import com.harputoglu.orhun.util.DeviceUtil

class CallReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == TelephonyManager.ACTION_PHONE_STATE_CHANGED) {
            val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
            
            // On ne déclenche que sur Smartphone/Tablette
            if (DeviceUtil.getDeviceType(context) != "Android TV") {
                if (state == TelephonyManager.EXTRA_STATE_RINGING || state == TelephonyManager.EXTRA_STATE_OFFHOOK) {
                    // Envoyer l'ordre de pause à la TV
                    // Note: Dans une version finale, on appellerait le SyncService ici
                    // Pour l'instant, c'est le squelette de la détection
                    println("Orhun AI: Appel détecté, mise en pause demandée")
                }
            }
        }
    }
}
