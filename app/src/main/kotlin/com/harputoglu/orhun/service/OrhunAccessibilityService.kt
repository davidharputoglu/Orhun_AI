package com.harputoglu.orhun.service

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent

class OrhunAccessibilityService : AccessibilityService() {

    companion object {
        var instance: OrhunAccessibilityService? = null
            private set
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}

    override fun onInterrupt() {}

    fun sendKey(keyCode: Int) {
        // En Android, l'injection de touches nécessite souvent d'être en premier plan
        // ou d'utiliser performGlobalAction pour certaines fonctions.
        // Pour les touches spécifiques comme TELETEXT, c'est dépendant du constructeur TV.
    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null
    }
}
