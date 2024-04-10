package com.example.savefile

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.CreateDocument
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.savefile.ui.theme.SaveFileTheme
import java.io.IOException
import kotlin.contracts.contract

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SaveFileTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SaveFile()
                }
            }
        }
    }
}

@Composable
fun SaveFile(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var dialogState by remember {
        mutableStateOf(false )
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            dialogState = true
        }) {
            Text("Сохранить файл")
        }
        if ( dialogState ) {
            ShowSaveFileDialog(
                onSave = { uri ->
                    saveFile(context, uri)
                    dialogState = false
                },
                onDismiss = {
                    dialogState = false
                }
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowSaveFileDialog (
    onSave: (uri: Uri) -> Unit,
    onDismiss: () -> Unit
) {
    val saveFileLauncher = rememberLauncherForActivityResult(
        contract = CreateDocument("text/gtr")) { uri ->
        uri?.let{ onSave(it) }
    }

    var fileName by remember {
        mutableStateOf("")
    }

    AlertDialog(
        onDismissRequest = { /*TODO*/ },
        confirmButton = {
            Button(
                onClick = {
                    saveFileLauncher.launch(fileName)
                    fileName = ""
                }
            ) {
                Text("Сохранить")
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    fileName = ""
                    onDismiss()
                }
            ) {
                Text(text = "Отменить")
            }
        },
        title = { Text(text = "Сохранить файл") },
        text = {
            Column {
                OutlinedTextField(
                    value = fileName,
                    onValueChange = { fileName = it },
                    label = { Text("Укажите имя файла") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    )
}

fun saveFile(context: Context, uri: Uri) {
    try {
        context.contentResolver.openOutputStream(uri)?.use { outputStream ->
            val fileContent = "Content to save"
            outputStream.write(fileContent.toByteArray())
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SaveFileTheme {
        SaveFile()
    }
}