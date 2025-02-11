package com.example.wordnetpython

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.chaquo.python.PyObject
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import java.io.File
import java.io.FileOutputStream


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

