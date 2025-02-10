package com.example.wordnetpython.dataclasses

// Represents all information for a specific word
data class WordData(
    val word: String, // The searched word
    val translationLang: String
) {
    var posDataList: MutableList<POSData> = mutableListOf()
}