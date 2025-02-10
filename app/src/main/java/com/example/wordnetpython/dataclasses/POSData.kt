package com.example.wordnetpython.dataclasses

// Represents information grouped by a specific Part of Speech
data class POSData(
    val pos: String, // Part of Speech
) {
    var lemma: String = "" // original form of the word
    var synsets: MutableList<SynsetData> = mutableListOf()
}