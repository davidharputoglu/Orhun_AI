package com.harputoglu.orhun.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harputoglu.orhun.ui.theme.OrhunTheme

class BroadcastActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val type = intent.getStringExtra("TYPE") ?: "NOTE"
        val content = intent.getStringExtra("CONTENT") ?: ""

        setContent {
            OrhunTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black.copy(alpha = 0.8f)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        when (type) {
                            "NOTE" -> NoteDisplay(content)
                            "ANNOUNCEMENT" -> AnnouncementDisplay(content)
                            "VOICE_ANNOUNCEMENT" -> VoiceAnnouncementDisplay(intent.getStringExtra("AUDIO_URL") ?: "", onFinished = { 
                                // Signaler la fin pour restaurer le son
                                val controlIntent = android.content.Intent("com.harputoglu.orhun.MEDIA_CONTROL").apply {
                                    putExtra("ACTION", "DUCK_END")
                                }
                                sendBroadcast(controlIntent)
                                finish() 
                            })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NoteDisplay(text: String) {
    Column(
        modifier = Modifier
            .padding(32.dp)
            .background(MaterialTheme.colorScheme.surface, shape = MaterialTheme.shapes.large)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Note reçue",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 24.sp),
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun AnnouncementDisplay(text: String) {
    Text(
        text = text,
        color = Color.Yellow,
        style = MaterialTheme.typography.displayMedium,
        fontWeight = FontWeight.ExtraBold,
        modifier = Modifier.padding(16.dp)
    )
}

@Composable
fun VoiceAnnouncementDisplay(audioUrl: String, onFinished: () -> Unit) {
    androidx.compose.runtime.LaunchedEffect(audioUrl) {
        // Simulation de lecture audio
        kotlinx.coroutines.delay(5000)
        onFinished()
    }

    Column(
        modifier = Modifier
            .padding(32.dp)
            .background(MaterialTheme.colorScheme.primaryContainer, shape = MaterialTheme.shapes.large)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Lecture de l'annonce vocale...", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(16.dp))
        androidx.compose.material3.LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
    }
}
