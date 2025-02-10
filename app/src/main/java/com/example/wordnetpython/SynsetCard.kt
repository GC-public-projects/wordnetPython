package com.example.wordnetpython

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SynsetCard(
    num: Int,
    gloss: String,
    examples: List<String>,
    synonyms: List<String>,
    lang: String?,
    translations: List<String>,
    antonyms: List<String>
) {
    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(5.dp)
    ) {
        Row {
            Text(
                text = "$num",
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp,
                    color = Color.Magenta
                ),
                modifier = Modifier.padding(15.dp)
            )
            Column(
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    "Gloss",
                    style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.DarkGray
                    ),
                    modifier = Modifier.padding(5.dp)
                )
                Text(
                    text = gloss,
                    modifier = Modifier.padding(10.dp, 0.dp, 10.dp, 5.dp)
                )
                Text(
                    "Examples",
                    style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.DarkGray
                    ),
                    modifier = Modifier.padding(5.dp)
                )
                Text(
                    text = examples.joinToString().ifEmpty { "/" },
                    modifier = Modifier.padding(10.dp, 0.dp, 10.dp, 5.dp)
                )
                Text(
                    "Synonyms",
                    style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.DarkGray),
                    modifier = Modifier.padding(5.dp)
                )
                Text(
                    text = synonyms.joinToString().ifEmpty { "/" },
                    modifier = Modifier.padding(10.dp, 0.dp, 5.dp, 5.dp)
                )
                if(lang != "") {
                    Text(
                        "Translations in $lang",
                        style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.DarkGray),
                        modifier = Modifier.padding(5.dp)
                    )
                }
                Text(
                    text = translations.joinToString().ifEmpty { "/" },
                    modifier = Modifier.padding(10.dp, 0.dp, 5.dp, 5.dp)
                )
                Text(
                    "Antonyms",
                    style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.DarkGray),
                    modifier = Modifier.padding(5.dp)
                )
                Text(
                    text = antonyms.joinToString().ifEmpty { "/" },
                    modifier = Modifier.padding(10.dp, 0.dp, 5.dp, 5.dp)
                )
            }
        }
    }
}