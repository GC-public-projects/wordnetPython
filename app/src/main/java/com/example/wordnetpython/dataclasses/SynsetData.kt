package com.example.wordnetpython.dataclasses

// Represents infoArrayListrmation about a single Synset
data class SynsetData(
    val pos: String, // Part of Speech (e.g., noun, verb)
    val gloss: String, // Definition or gloss
    var examples: MutableList<String>, // examples between quotes
    var synonyms: MutableList<String>, // Words in the synset
    var translations: MutableList<String>,
    var antonyms: MutableList<String>
)