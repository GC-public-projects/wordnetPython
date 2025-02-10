package com.example.wordnetpython

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.chaquo.python.PyObject
import com.example.wordnetpython.dataclasses.WordData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


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


