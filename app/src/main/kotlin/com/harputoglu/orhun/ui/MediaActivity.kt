package com.harputoglu.orhun.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.harputoglu.orhun.ui.theme.OrhunTheme

import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

class MediaActivity : ComponentActivity() {
    private var player: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mediaUrl = intent.getStringExtra("MEDIA_URL") ?: ""
        val thumbnailUrl = intent.getStringExtra("THUMBNAIL_URL") ?: ""
        val title = intent.getStringExtra("TITLE") ?: "Chargement..."

        player = ExoPlayer.Builder(this).build().apply {
            setMediaItem(MediaItem.fromUri(mediaUrl))
            prepare()
            playWhenReady = true
        }

        val receiver = object : android.content.BroadcastReceiver() {
            override fun onReceive(context: android.content.Context?, intent: android.content.Intent?) {
                val action = intent?.getStringExtra("ACTION")
                val data = intent?.getStringExtra("DATA")
                when (action) {
                    "PLAY" -> player?.play()
                    "PAUSE" -> player?.pause()
                    "DUCK_START" -> player?.volume = 0.2f
                    "DUCK_END" -> player?.volume = 1.0f
                    "SEEK" -> {
                        val pos = data?.toLongOrNull() ?: 0L
                        player?.seekTo(player!!.currentPosition + (pos * 1000L))
                    }
                }
            }
        }
        registerReceiver(receiver, android.content.IntentFilter("com.harputoglu.orhun.MEDIA_CONTROL"))

        setContent {
            OrhunTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black
                ) {
                    var isMediaReady by remember { mutableStateOf(false) }
                    
                    // Listener pour masquer la miniature quand la vidéo commence
                    LaunchedEffect(player) {
                        player?.addListener(object : androidx.media3.common.Player.Listener {
                            override fun onIsPlayingChanged(isPlaying: Boolean) {
                                if (isPlaying) isMediaReady = true
                            }
                        })
                    }

                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        AndroidView(
                            factory = { context ->
                                PlayerView(context).apply {
                                    this.player = this@MediaActivity.player
                                    useController = true
                                }
                            },
                            modifier = Modifier.fillMaxSize()
                        )

                        if (!isMediaReady) {
                            // La miniature reste au-dessus tant que ça charge
                            if (thumbnailUrl.isNotEmpty()) {
                                AsyncImage(
                                    model = thumbnailUrl,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop,
                                    alpha = 0.8f
                                )
                            }
                            
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
        player = null
    }
}
