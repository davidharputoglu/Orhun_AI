package com.harputoglu.orhun.service

import com.google.mlkit.vision.digitalink.DigitalInkRecognition
import com.google.mlkit.vision.digitalink.DigitalInkRecognizerOptions
import com.google.mlkit.vision.digitalink.Ink
import com.google.mlkit.vision.digitalink.Ink.Stroke

class HandwritingService {
    
    fun recognizeHandwriting(ink: Ink, onResult: (String) -> Unit) {
        val modelIdentifier = "en-US" // Should be dynamic based on language
        val options = DigitalInkRecognizerOptions.builder(
            com.google.mlkit.vision.digitalink.DigitalInkRecognitionModelIdentifier.fromLanguageTag(modelIdentifier)!!
        ).build()

        val recognizer = DigitalInkRecognition.getClient(options)
        
        recognizer.recognize(ink)
            .addOnSuccessListener { result ->
                if (result.candidates.isNotEmpty()) {
                    onResult(result.candidates[0].text)
                }
            }
            .addOnFailureListener {
                // Handle error
            }
    }
}
