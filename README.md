# wordnetPython
use of Python version of Wordnet & Multilingual Wordnet (OMW) in a Jetpack compose app thanks to Chaquopy

### Project status : Workable

## target audience
This project is for Jetpack Compose initiated user

## Presentation
WordNet is a large lexical database of English, organizing words into synsets (sets of synonyms) based on their meanings. Each synset represents a single concept, containing lemmas (base forms of words) and associated POS (Parts of Speech: noun, verb, adjective, adverb). Synsets include glosses (definitions) and examples. Words are linked via semantic relations like hypernyms (parents), hyponyms (children), antonyms, and more. The hierarchy starts with POS, which contains index words pointing to synsets, each storing lemmas and their relationships. This structure helps navigate word meanings and relationships efficiently.

This version of Wordnet (Python) fully integrates OMW (Multilingual Wordnet) and has more featues compared to the Java version that only integrates the Spanish.

## Overview
- 1 : main page word and translation language not setup


<img src="/screenshots/screen1.png" alt="" height="400">&emsp;
<img src="/screenshots/screen2.png" alt="" height="400">&emsp;
<img src="/screenshots/screen3.png" alt="" height="400">&emsp;
<img src="/screenshots/screen4.png" alt="" height="400">&emsp;


<img src="/screenshots/screen5.png" alt="" height="400">&emsp;
<img src="/screenshots/screen6.png" alt="" height="400">&emsp;
<img src="/screenshots/screen7.png" alt="" height="400">&emsp;



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
plugins {
    ...
    id("com.chaquo.python")
}

chaquopy {
    defaultConfig {
       version = "3.10"
        pip { }
    }
    productFlavors { }
    sourceSets { }
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
Once this script was run the downloaded libraries should be on linux here `/home/username/nltk_data/corpora`. The files names should be `wordnet.zip` & `omw-1.4.zip`


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


