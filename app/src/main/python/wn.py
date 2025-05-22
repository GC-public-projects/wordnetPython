# dataclasses names, paths and project name used for the import should strictly follow the Android project implementations 

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

