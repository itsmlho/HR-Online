package com.example.splash

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.splash.ui.theme.SplashTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SplashTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Aries Adityanto",
                        nim = "3124104096",
                        kelas = "TI.24.B1",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, nim : String, kelas : String, modifier: Modifier = Modifier) {
    // Use a Column to arrange Texts vertically and center them
    Column(
        modifier = modifier.fillMaxSize(), // Use the modifier from Scaffold and fill the size
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Nama : $name"
        )
        Spacer(modifier = Modifier.height(8.dp)) // Add vertical space
        Text(
            text = "NIM : $nim"
        )
        Spacer(modifier = Modifier.height(8.dp)) // Add vertical space
        Text(
            text = "Kelas : $kelas"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SplashTheme {
        Greeting("Aries Adityanto", "3124104096", "TI.24.B1")
    }
}
