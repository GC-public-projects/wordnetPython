package com.example.wordnetpython

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chaquo.python.PyObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    wnModule: PyObject
) {
    val viewModel: MyViewModel = viewModel(
        factory = MyViewModel.provideFactory(wnModule)
    )

    var myWord by remember { mutableStateOf("") }
    val modifyMyWord = { word: String -> myWord = word }

    var myLang by remember { mutableStateOf("") }
    val modifyMyLang = { word: String -> myLang = word }

    val languages = listOf(
        "als", "arb", "bul", "cat", "cmn", "dan", "ell", "eng", "eus", "fas", "fin", "fra",
        "glg", "heb", "hin", "hrv", "ind", "ita", "jpn", "nld", "nno", "nob", "pol", "por",
        "ron", "slk", "slv", "spa", "swe", "tha", "zsm"
    )
    var expanded by remember { mutableStateOf(false) }


    val myWordData by viewModel.myWordData.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(0.dp, 50.dp, 0.dp,0.dp)
    ) {
        OutlinedTextField(
            value = myWord,
            onValueChange = { modifyMyWord(it) },
            label = { Text("word") },
            placeholder = { Text("Type a word") },
            singleLine = true,
            modifier = Modifier
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                modifier = Modifier.menuAnchor(),
                value = myLang,
                onValueChange = { expanded = true },
                label = { Text("translation language") },
                singleLine = true,
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                languages.forEach { lang ->
                    DropdownMenuItem(
                        text = { Text(lang) },
                        onClick = {
                            modifyMyLang(lang)
                            expanded = false
                        }
                    )
                }
            }
        }

        Button(onClick = { viewModel.setWordData(myWord, myLang) }) {
            Text("show word data")
        }
        if (
            myWordData != null &&
            myWordData!!.posDataList.isNotEmpty() &&
            !isLoading
        ) {
            Column {
                var synsetNumber = 1
                Text(
                    text = "Word: ${myWordData!!.word}",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 35.sp,
                        color = Color.Gray
                    ),
                )
                Spacer(Modifier.height(10.dp))
                myWordData?.posDataList?.forEach { posData ->
                    Text(
                        text = "Part of Speech: ${posData.pos}",
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 25.sp,
                            color = Color.Red
                        ),
                        modifier = Modifier.padding(2.dp)
                    )
                    Text(
                        text = "Lemma: ${posData.lemma}",
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color.Blue
                        ),
                        modifier = Modifier.padding(5.dp)
                    )
                    posData.synsets.forEach { synset ->
                        SynsetCard(
                            synsetNumber,
                            synset.gloss,
                            synset.examples,
                            synset.synonyms,
                            myWordData?.translationLang,
                            synset.translations,
                            synset.antonyms
                        )
                        synsetNumber += 1
                    }
                    synsetNumber = 1
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        } else if (
            myWordData != null &&
            myWordData!!.posDataList.isEmpty()
        ) {
            Text(text = "word not found !!")
        }
        else if(isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
        }
    }
}
