# wordnetPython
use of Python version of Wordnet & Multilingual Wordnet (OMW) in a Jetpack compose app thanks to Chaquopy.

`Chaquopy` is a convenient way to integer Python code in Java or Kotlin Android projects

### Project status : Workable, doc completed

## target audience
This project is for Jetpack Compose initiated user

## Presentation
WordNet is a large lexical database of English, organizing words into synsets (sets of synonyms) based on their meanings. Each synset represents a single concept, containing lemmas (base forms of words) and associated POS (Parts of Speech: noun, verb, adjective, adverb). Synsets include glosses (definitions) and examples. Words are linked via semantic relations like hypernyms (parents), hyponyms (children), antonyms, and more. The hierarchy starts with POS, which contains index words pointing to synsets, each storing lemmas and their relationships. This structure helps navigate word meanings and relationships efficiently.

This version of Wordnet (Python) fully integrates OMW (Multilingual Wordnet) and has more featues compared to the Java version that only integrates the Spanish.

## Overview
- 1 : main page word and translation language not setup
- 2 : selection of the translation language
- 3 : progression circle while the data of the word is loading
- 4 : part of speech `noun` for the word "cool"
- 5 : part of speech `verb` for the word "cool"
- 6 : part of speech `adjective` for the word "cool"
- 7 : part of speech : `adjective satellite` for the word "cool"

<img src="/screenshots/screen1.png" alt="main page word and translation language not setup" height="400">&emsp;
<img src="/screenshots/screen2.png" alt="selection of the translation language" height="400">&emsp;
<img src="/screenshots/screen3.png" alt="progression circle while the data of the word is loading" height="400">&emsp;
<img src="/screenshots/screen4.png" alt="part of speech noun for the word cool" height="400">&emsp;


<img src="/screenshots/screen5.png" alt="part of speech verb for the word cool" height="400">&emsp;
<img src="/screenshots/screen6.png" alt="part of speech adjective for the word cool" height="400">&emsp;
<img src="/screenshots/screen7.png" alt="part of speech adjective satellite for the word cool" height="400">&emsp;



# Init

## Dependencies


### Chacopy dependencies

In build.gradle.kts (project) add : 
``` kotlin
plugins {
    ...
    id("com.chaquo.python") version "16.0.0" apply false
}
```

In build.gradle.kts (app) add : 
``` kotlin
plugins { // to sync before
    ...
    id("com.chaquo.python")
}

chaquopy { // to sync after
    defaultConfig {
       version = "3.10"
        pip { }
    }
    productFlavors { }
    sourceSets { }
}

android { // to sync before
    ...
    defaultConfig {
        ...
        ndk {
            // On Apple silicon, you can omit x86_64.
            abiFilters += listOf("arm64-v8a", "x86_64")
        }
    }
}
```

### Wordnet dependencies
The python version of Wordnet belongs to `nltk` library. So modify build.gradle.kts (app) in order to implement it.

``` kotlin
chaquopy {
    defaultConfig {
       version = "3.10"
        pip {
            install("nltk")
        }
    }
    productFlavors { }
    sourceSets { }
}
```
It is not enough to just implement `nltk`, we also need to download the Wordnet and the OMW libraries thanks to `nltk`. As the download requires additional permissions to setup it is easyer to make the download apart in Windows/Linux and, once done, copy the downloaded libraries in our project asset folder in order to copy them in the app folder of the Android device during the compilation.

Code to execute on Windows/Linux to download the wordnet libraries : 
``` python
#!/usr/bin/python3

import nltk

try:
	nltk.data.find('corpora/wordnet.zip')
	print('Wordnet already installed')
except LookupError:
	print('Wordnet installation...')
	nltk.download('wordnet')


try:
	nltk.data.find('corpora/omw-1.4.zip')
	print('Wordnet multilanguage already installed')
except LookupError:
	print('Wordnet multilanguage installation...')
	nltk.download('omw-1.4')
```
Once this script was run, the downloaded libraries should be on linux here `/home/YourUsername/nltk_data/corpora` & on Windows here : `C:\Users\YourUsername\AppData\Roaming\nltk_data\corpora` The files names should be `wordnet.zip` & `omw-1.4.zip`.

Then, copy the `nltk_data` in app/src/main/assets (create assets if doesn't exist). 
We will replicate the folder structure and content into our app folder by using some functions later in our code. We will also need to add the folder created path in the nltk data path variable thanks to a python function. 


### Viewmodel dependencies

In build.gradle.kts (app) add : 

``` kotlin
dependencies {
	...
	implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")	
}
```

# Code

# Python code
Two .py files created : 
- init_nltk_path.py 
- wn.py

## 1. Init of the nltk path
In order to know where nltk will fetch the wordnet data the path of nltk needs to be modified to integer the new folder created in the app that content the wordnet libraries > `nltk_data`.

### Content
- In app/src/main/ create a folder named `python` if not present.
- In `python` folder create .py file named : `init_nltk_path.py`
``` python
import os
import nltk

def initPath(files_dir):
    # Add the path to NLTK's data search paths
    if files_dir not in nltk.data.path:
        nltk.data.path.append(files_dir)
        print(f"Added {files_dir} to NLTK data paths.")
    else:
        print(f"{files_dir} is already in NLTK data paths.")


    print(f"NLTK data directory: {nltk.data.path}")

```
### Components explanations
The path is sent to the python function from the Kotlin code when `initPath` is called.


## 1. call of Wordnet library
Python function that returns a Java/Kotlin `WordData` object that contents all the data needed for a specific word. the hierarchy returned by the python code if the same than the one of our data classes of course.

### Content
- In `python` folder create .py file named : `wn.py`

``` python
from com.example.wordnetpython.dataclasses import WordData
from com.example.wordnetpython.dataclasses import POSData
from com.example.wordnetpython.dataclasses import SynsetData

from java.util import ArrayList

from nltk.corpus import wordnet as wn


def getWordData(word: str, lang: str) -> WordData:
    wordData = WordData(word, lang)
    synsets = wn.synsets(word)

    if synsets is not None:
        for synset in synsets:
            pos = synset.pos()
            gloss = synset.definition()
            examples = ArrayList() 
            synonyms = ArrayList()
            antonymsSet = set() # use of set forces only one iteration by word
            antonyms = ArrayList()
            translationsSet = set()
            translations = ArrayList()
            createPosDataFlag = False # if new pos detected > POSData object needs to be created

            for example in synset.examples():
                examples.add(example)

            for lemma in synset.lemmas():
                if word != lemma.name():  # Excludes the initial word from synonyms list
                    synonyms.add(lemma.name())
                for antonym in lemma.antonyms():
                    antonymsSet.add(antonym.name())
            for antonym in antonymsSet:
                antonyms.add(antonym)

            # translation
            if lang != "": # only if a language is setup
                for lemma in synset.lemmas(lang = lang):
                    translationsSet.add(lemma.name())
                for translation in translationsSet:
                    translations.add(translation)

            synsetData = SynsetData(pos, gloss, examples, synonyms, translations, antonyms)

            if wordData.getPosDataList().size() == 0:
                createPosDataFlag = True
            else:
                for i in range(wordData.getPosDataList().size()):
                    if synsetData.getPos() != wordData.getPosDataList().get(i).getPos():
                        createPosDataFlag = True
                    else:
                        wordData.getPosDataList().get(i).getSynsets().add(synsetData)
                        createPosDataFlag = False
                        break

            if createPosDataFlag:
                myPosData = POSData(synsetData.getPos())
                myPosData.getSynsets().add(synsetData)
                wordData.getPosDataList().add(myPosData)
                createPosDataFlag = False

        for i in range(wordData.getPosDataList().size()):
            posData = wordData.getPosDataList().get(i)
            posData.setLemma(wn.morphy(word, posData.getPos()))

    return wordData


```
### Components explanations

- importation in the python code of our `data classes` in order to return a wordData object
- importation of `Arraylist` from java in order to replace the empty lists in our dataclasses when needed. `Arraylist` is the real component behind the interface `MutableList` in our case.


- in Kotlin `setters` & `getters` are implicits, all atributtes have `getters` but if an attribute is of type `var` it gets also a `setter`. In python when we import classes from our Java classes, all atributes are imported but the implicit mechanism cannot be. So Chacopy automatically creates for us the settters and the getters methods. Their names are the ones of the attributes with `set` or `get` before.

To check all the methods of an imported class we can add this line in the python code :
``` python
print(f"Synset data class content : {dir(SynsetData)}")
```
In logCat the result will be displayed like that 
```
Synset data class content : ['$stable', '<init>', '__call__', '__class__', '__delattr__', '__dict__', '__dir__', '__doc__', '__eq__', '__format__', '__ge__', '__getattribute__', '__gt__', '__hash__', '__init__', '__init_subclass__', '__le__', '__lt__', '__module__', '__ne__', '__new__', '__reduce__', '__reduce_ex__', '__repr__', '__setattr__', '__sizeof__', '__str__', '__subclasshook__', '__weakref__', '_chaquopy_j_klass', '_chaquopy_reflector', 'clone', 'component1', 'component2', 'component3', 'component4', 'component5', 'component6', 'copy', 'equals', 'finalize', 'getAntonyms', 'getClass', 'getExamples', 'getGloss', 'getPos', 'getSynonyms', 'getTranslations', 'hashCode', 'notify', 'notifyAll', 'setAntonyms', 'setExamples', 'setSynonyms', 'setTranslations', 'toString', 'wait']
```
- Concerning the functions of Wordnet the documentation is findable on the net.

# Kotlin code

## Main activity (class)

### Purpose
- creation of the nltk libs folder in android app
- copy of the nltk folder with its content from assets to nltk android app folder
- init of the python modules thanks to Chacopy lib
- call of the UI (MainScreen composable)

### Content
Modify MainActivity.kt
``` kotlin
class MainActivity : ComponentActivity() {
    private lateinit var wnModule: PyObject

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // creation of the nltk_data folder
        val nltkDataPath = File(filesDir, "nltk_data").absolutePath
        // copy of the wordnet files
        copyAssetFolder(this, "nltk_data", nltkDataPath)

        if (! Python.isStarted()) {
            Python.start(AndroidPlatform(this))
        }
        val python = Python.getInstance()

        val nltkModule = python.getModule("init_nltk_path")
        nltkModule.callAttr("initPath", nltkDataPath)

        wnModule = python.getModule("wn")


        enableEdgeToEdge()
        setContent {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                MainScreen(wnModule)
            }
        }
    }

    private fun copyAssetFolder(context: Context, assetFolder: String, destFolder: String) {
        val assetManager = context.assets
        val files = assetManager.list(assetFolder) ?: return

        val destDir = File(destFolder)
        if (!destDir.exists()) {
            destDir.mkdirs()
        }

        for (fileName in files) {
            val assetPath = "$assetFolder/$fileName"
            val destPath = "$destFolder/$fileName"

            if (assetManager.list(assetPath)?.isEmpty() == false) {
                // It's a folder; copy recursively
                copyAssetFolder(context, assetPath, destPath)
            } else {
                // It's a file; copy it
                copyAssetFile(context, assetPath, destPath)
            }
        }
    }
    private fun copyAssetFile(context: Context, assetPath: String, destPath: String) {
        val destFile = File(destPath)
        if (destFile.exists()) return  // Skip if already copied

        context.assets.open(assetPath).use { inputStream ->
            FileOutputStream(destFile).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
    }
}
```

## data classes 
Purpose : Retrieve the data of a word from wordnet lib in an orderly way

### WordData(data class)
The main class of the word we need to extract the data.

#### Content
in Main package create package `dataclasses`
inside `dataclasses` create kotlin Data class named `WordData`
``` kotlin
data class WordData(
    val word: String, // The searched word
    val translationLang: String
) {
    var posDataList: MutableList<POSData> = mutableListOf()
}
```

### POSData (data class)
For each differnet pos detected in all the synset a POSData object is created and added to `posDataList` from WordData object.

#### Content
inside package `dataclasses` create kotlin Data class named `POSData`
``` kotlin
data class POSData(
    val pos: String, // Part of Speech
) {
    var lemma: String = "" // original form of the word
    var synsets: MutableList<SynsetData> = mutableListOf()
}
```

### SynsetData (data class)
Represents the information about a single Synset, following the pos attribute it is added to the synset list of a POSData object

#### Content
inside package `dataclasses` create kotlin Data class named `SynsetData`
``` kotlin
data class SynsetData(
    val pos: String, // Part of Speech (e.g., noun, verb)
    val gloss: String, // Definition or gloss
    var examples: MutableList<String>, // examples between quotes
    var synonyms: MutableList<String>, // Words in the synset
    var translations: MutableList<String>,
    var antonyms: MutableList<String>
)
```

## MyViewModel (class)
viewModel linked to the `MainScreen` composable

### Purpose
call the function `getWordData` in the module related to wn.ph and store the result in `_myWordData`. 

### Content
in Main pacage create Kotlin class named `MyViewModel`
``` kotlin
class MyViewModel(
    private val _wnModule: PyObject
): ViewModel() {
    companion object {
        fun provideFactory(
            _wnModule: PyObject
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
            ): T {
                if (modelClass.isAssignableFrom(MyViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return MyViewModel(_wnModule) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }

    private val _myWordData = MutableStateFlow<WordData?>(null)
    val myWordData: StateFlow<WordData?> = _myWordData

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading


    fun setWordData(word: String, lang: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true

            val pyObject = _wnModule.callAttr("getWordData", word, lang)
            _myWordData.value = pyObject.toJava(WordData::class.java)

            _isLoading.value = false
        }
    }
}
```

### Components explanations

- The static `provideFactory` function return a ViewModelProvider.Factory singleton in order to pass the module related to wn.py as param during the instanciation of the viewModel.

- `_myWordData` is of type MutableStateFlow in order to recomose the UI once it is updated.

- `_isLoading` is update when `_myWordData` is assigned the new object in order to stop the display of the progression circle and show the word date in the UI.


## SynsetCard (Composable)
Composable that shows the content of a synset in a structured manner. It is call for each synset in each POSData.

### Content
In main package create Kotlin File named `SynsetCard `
``` kotlin
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
```

## MainScreen (Composable)
The Main UI. 2 textfields to capture the word and the language translation and one button to load the data fetching. SynsetCard is called for each Synset in each POSData in order to show the data of the word once it is completely loaded.

### Content
in main package create Kotlin file named `MainScreen`
``` kotlin
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
```



