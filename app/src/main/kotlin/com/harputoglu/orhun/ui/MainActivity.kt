import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.OutlinedFlag
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.Card
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.activity.compose.setContent
import androidx.activity.ComponentActivity
import androidx.compose.material3.Text
import androidx.compose.material3.Switch
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.TextButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import android.os.Build
import android.content.Intent
import com.harputoglu.orhun.OrhunApp
import com.harputoglu.orhun.service.OverlayService
import com.harputoglu.orhun.ui.theme.OrhunTheme
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        checkPermissions()

        val deviceType = when {
            com.harputoglu.orhun.util.DeviceUtil.isAndroidTV(this) -> "Android TV"
            com.harputoglu.orhun.util.DeviceUtil.isTablet(this) -> "Tablette"
            else -> "Smartphone"
        }

        setContent {
            OrhunTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Orhun AI sur $deviceType")
                }
            }
        }
    }

    private fun checkPermissions() {
        if (!android.provider.Settings.canDrawOverlays(this)) {
            val intent = Intent(
                android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                android.net.Uri.parse("package:$packageName")
            )
            startActivityForResult(intent, 123)
        } else if (!isAccessibilityServiceEnabled()) {
            val intent = Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS)
            startActivityForResult(intent, 456)
        } else {
            startOverlayService()
        }
    }

    private fun isAccessibilityServiceEnabled(): Boolean {
        val expectedComponentName = android.content.ComponentName(this, com.harputoglu.orhun.service.OrhunAccessibilityService::class.java)
        val enabledServices = android.provider.Settings.Secure.getString(contentResolver, android.provider.Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
        return enabledServices?.contains(expectedComponentName.flattenToString()) == true
    }

    private fun startOverlayService() {
        val intent = Intent(this, OverlayService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 123) {
            if (android.provider.Settings.canDrawOverlays(this)) {
                startOverlayService()
            }
        }
    }

    fun selectDownloadFolder() {
        // TODO: Implementation for storage picker
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Composable
fun RemoteControlUI() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val userPrefs = remember { com.harputoglu.orhun.data.local.prefs.UserPreferences(context) }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant, shape = MaterialTheme.shapes.medium)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Réglages Notifications", style = MaterialTheme.typography.titleMedium)
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Son")
            androidx.compose.material3.Switch(
                checked = userPrefs.notificationSoundEnabled,
                onCheckedChange = { userPrefs.notificationSoundEnabled = it }
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text("Vibration")
            androidx.compose.material3.Switch(
                checked = userPrefs.notificationVibrationEnabled,
                onCheckedChange = { userPrefs.notificationVibrationEnabled = it }
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Gamepad, contentDescription = null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Mode Gamer (Cast plein écran)", style = MaterialTheme.typography.labelMedium)
            Spacer(modifier = Modifier.width(8.dp))
            androidx.compose.material3.Switch(
                checked = userPrefs.gamerModeEnabled,
                onCheckedChange = { 
                    userPrefs.gamerModeEnabled = it
                    if (it) {
                        // Action de lancement du miroir système
                        val intent = Intent(android.provider.Settings.ACTION_CAST_SETTINGS)
                        context.startActivity(intent)
                    }
                },
                modifier = Modifier.scale(0.8f)
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        EmotionSelectorUI()

        Spacer(modifier = Modifier.height(16.dp))
        
        MediaHistoryUI()

        Spacer(modifier = Modifier.height(16.dp))
        
        TextButton(onClick = { (context as? MainActivity)?.selectDownloadFolder() }) {
            Text(if (userPrefs.downloadPath == null) "Configurer le dossier de sauvegarde" else "Dossier configuré (Micro-SD)")
        }
    }
}


@androidx.compose.material3.OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun EmotionSelectorUI() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val userPrefs = remember { com.harputoglu.orhun.data.local.prefs.UserPreferences(context) }
    val emotions = listOf("Naturel", "Calme", "Joyeux", "Sérieux", "Excité")
    val isAuto = userPrefs.autoEmotionEnabled
    val selectedEmotion = if (isAuto) getAutoEmotion() else userPrefs.currentVoiceEmotion

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Mode Synchro (Auto)", style = MaterialTheme.typography.labelMedium)
            Spacer(modifier = Modifier.width(8.dp))
            androidx.compose.material3.Switch(
                checked = isAuto,
                onCheckedChange = { userPrefs.autoEmotionEnabled = it },
                modifier = Modifier.scale(0.8f)
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text("Émotion : $selectedEmotion", style = MaterialTheme.typography.titleSmall)
        Spacer(modifier = Modifier.height(8.dp))
        
        androidx.compose.foundation.lazy.LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.alpha(if (isAuto) 0.5f else 1f)
        ) {
            items(emotions) { emotion ->
                androidx.compose.material3.FilterChip(
                    enabled = !isAuto,
                    selected = selectedEmotion == emotion,
                    onClick = { userPrefs.currentVoiceEmotion = emotion },
                    label = { Text(emotion) },
                    leadingIcon = if (selectedEmotion == emotion) {
                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    } else null
                )
            }
        }
    }
}

@Composable
fun MediaHistoryUI() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val history by remember { 
        com.harputoglu.orhun.OrhunApp.database.mediaDao().getAllMedia() 
    }.collectAsState(initial = emptyList())

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.History, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Dernières diffusions sur la TV", style = MaterialTheme.typography.titleMedium)
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        androidx.compose.foundation.lazy.LazyColumn(
            modifier = Modifier.height(150.dp)
        ) {
            items(history) { media ->
                MediaHistoryItem(media)
            }
        }
    }
}

@Composable
fun MediaHistoryItem(media: com.harputoglu.orhun.data.local.entity.MediaEntity) {
    val sdf = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            val icon = when(media.type) {
                "VIDEO" -> Icons.Default.PlayCircle
                "IMAGE" -> Icons.Default.Image
                "VOICE" -> Icons.Default.Mic
                else -> Icons.Default.Link
            }
            Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp), tint = MaterialTheme.colorScheme.secondary)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(media.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                Text(sdf.format(java.util.Date(media.timestamp)), style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }
            
            IconButton(onClick = { 
                kotlinx.coroutines.GlobalScope.launch(kotlinx.coroutines.Dispatchers.IO) {
                    com.harputoglu.orhun.OrhunApp.database.mediaDao().updateMedia(media.copy(isPinned = !media.isPinned))
                }
            }) {
                Icon(
                    if (media.isPinned) Icons.Default.PushPin else Icons.Default.OutlinedFlag, 
                    contentDescription = "Épingler",
                    tint = if (media.isPinned) MaterialTheme.colorScheme.primary else Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }

            IconButton(onClick = { /* Rediffuser */ }) {
                Icon(Icons.Default.Refresh, contentDescription = "Renvoyer")
            }
        }
    }
}

private fun getAutoEmotion(): String {
    val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
    return when {
        hour in 6..10 -> "Joyeux"
        hour in 11..17 -> "Naturel"
        hour in 18..22 -> "Calme"
        else -> "Sérieux"
    }
}

@Composable
fun VoiceGalleryUI() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val hfManager = remember { com.harputoglu.orhun.util.HuggingFaceManager(context) }
    var voiceModels by remember { androidx.compose.runtime.mutableStateOf(listOf<com.harputoglu.orhun.util.VoiceModel>()) }
    var playingModelId by remember { androidx.compose.runtime.mutableStateOf<String?>(null) }

    androidx.compose.runtime.LaunchedEffect(Unit) {
        voiceModels = hfManager.fetchTrendingVoices()
    }

    val groupedVoices = voiceModels.groupBy { it.language }

    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Settings, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Boutique de Voix (Hugging Face)", style = MaterialTheme.typography.titleMedium)
        }

        groupedVoices.forEach { (language, models) ->
            val top20 = models.sortedByDescending { it.likes }.take(20)
            
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Language, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Top 20 $language", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            
            androidx.compose.foundation.lazy.LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                itemsIndexed(top20) { index, model ->
                    VoiceModelCard(
                        model = model,
                        rank = index + 1,
                        isPlaying = playingModelId == model.id,
                        onPlay = { 
                            playingModelId = model.id
                            val intent = Intent(context, com.harputoglu.orhun.ui.BroadcastActivity::class.java).apply {
                                putExtra("TYPE", "VOICE_ANNOUNCEMENT")
                                putExtra("AUDIO_URL", hfManager.getDemoAudioUrl(model.id))
                            }
                            context.startActivity(intent)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun VoiceModelCard(
    model: com.harputoglu.orhun.util.VoiceModel,
    rank: Int,
    isPlaying: Boolean,
    onPlay: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val userPrefs = remember { com.harputoglu.orhun.data.local.prefs.UserPreferences(context) }
    val isSelected = userPrefs.currentVoiceId == model.id

    Card(
        modifier = Modifier.width(170.dp),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
        ),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (rank <= 3) {
                    Text(
                        text = when(rank) { 1 -> "🥇"; 2 -> "🥈"; else -> "🥉" },
                        modifier = Modifier.padding(end = 4.dp)
                    )
                } else {
                    Text(
                        text = "#$rank",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                }
                Text(model.name, fontWeight = FontWeight.Bold, maxLines = 1, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
            }
            Text(model.author, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Favorite, contentDescription = null, tint = Color.Red, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("${model.likes}", style = MaterialTheme.typography.labelSmall)
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                IconButton(
                    onClick = onPlay,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayCircle,
                        contentDescription = "Écouter",
                        tint = if (isPlaying) Color.Red else MaterialTheme.colorScheme.primary
                    )
                }
                
                androidx.compose.material3.Button(
                    onClick = { /* Start DL */ },
                    modifier = Modifier.height(36.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Text("Importer", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}
