data class VoiceModel(
    val id: String,
    val author: String,
    val name: String,
    val language: String,
    val likes: Int
)

class HuggingFaceManager(private val context: android.content.Context) {

    // URL de base pour les modèles (exemple pour Piper ou XTTS)
    private val HF_BASE_URL = "https://huggingface.co/api/models"

    fun getDemoAudioUrl(modelId: String): String {
        return "https://huggingface.co/$modelId/resolve/main/sample.wav"
    }

    suspend fun fetchTrendingVoices(): List<VoiceModel> {
        // Simulation d'une large liste pour le Top 20
        val voices = mutableListOf<VoiceModel>()
        
        // Français
        for (i in 1..25) {
            voices.add(VoiceModel("fr/voice-$i", "Author $i", "Voix FR $i", "Français", 2500 - (i * 100)))
        }
        
        // Türkçe
        for (i in 1..25) {
            voices.add(VoiceModel("tr/voice-$i", "Yazar $i", "Türkçe Ses $i", "Türkçe", 2000 - (i * 80)))
        }
        
        // English
        for (i in 1..25) {
            voices.add(VoiceModel("en/voice-$i", "Author $i", "English Voice $i", "English", 5000 - (i * 200)))
        }

        return voices
    }

    suspend fun fetchTop20ByLanguage(language: String): List<VoiceModel> {
        return fetchTrendingVoices()
            .filter { it.language == language }
            .sortedByDescending { it.likes }
            .take(20)
    }

    suspend fun fetchTop3Voices(): List<VoiceModel> {
        return fetchTrendingVoices().sortedByDescending { it.likes }.take(3)
    }

    suspend fun searchVoiceModels(query: String): List<VoiceModel> {
        return fetchTrendingVoices().filter { it.name.contains(query, ignoreCase = true) }
    }

    fun downloadModel(modelId: String, onProgress: (Float) -> Unit) {
        // Logique de téléchargement vers le stockage interne ou Micro-SD
        // Similaire au DownloadManager déjà créé
    }
}
