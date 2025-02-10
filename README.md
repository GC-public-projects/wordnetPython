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

## Python code
Two .py files created : 
- 1. init_nltk_path.py 
- 2. wn.py

### 1. Init of the nltk path

#### Purpose
In order to know where nltk will fetch the wordnet data the path of nltk needs to be modified to integer the new folder created in the app that content the wordnet libraries > `nltk_data`.

#### Content
- In app/src/main/ create a folder named `python` if not present.
- In `pyton` folder create .py file named : `init_nltk_path.py`
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


### 1. call of Wordnet library
Python function that return a Java/Kotlin `WordData` object the contents all the data needed for a specific word. the hierarchy returnd py the python code if the same than teh one of our data classes of course.




