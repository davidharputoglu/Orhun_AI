package com.harputoglu.orhun.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.harputoglu.orhun.ui.theme.OrhunTheme

data class MovieResult(
    val id: String,
    val title: String,
    val imageUrl: String,
    val year: String
)

class GalleryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val query = intent.getStringExtra("QUERY") ?: ""
        
        // Simulation de résultats pour la démo
        val results = listOf(
            MovieResult("1", "Inception", "https://example.com/inception.jpg", "2010"),
            MovieResult("2", "Interstellar", "https://example.com/interstellar.jpg", "2014"),
            MovieResult("3", "The Dark Knight", "https://example.com/darkknight.jpg", "2008"),
            MovieResult("4", "Avatar", "https://example.com/avatar.jpg", "2009")
        )

        setContent {
            OrhunTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF121212)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Résultats pour : \"$query\"",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.White,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(minSize = 200.dp),
                            contentPadding = PaddingValues(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(results) { movie ->
                                MovieCard(movie)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MovieCard(movie: MovieResult) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.7f)
            .clickable { /* Launch PLAY_MEDIA command */ },
        shape = MaterialTheme.shapes.medium
    ) {
        Box {
            AsyncImage(
                model = movie.imageUrl,
                contentDescription = movie.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .align(androidx.compose.ui.Alignment.BottomStart)
                    .background(Color.Black.copy(alpha = 0.7f))
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = movie.title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = movie.year,
                    color = Color.LightGray,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
