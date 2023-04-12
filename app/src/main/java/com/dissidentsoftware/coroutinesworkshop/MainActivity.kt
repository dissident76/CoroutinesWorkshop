package com.dissidentsoftware.coroutinesworkshop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dissidentsoftware.coroutinesworkshop.ui.theme.CoroutinesWorkshopTheme
import com.dissidentsoftware.coroutinesworkshop.ui.viewmodel.CoroutinesWorkshopViewModel

private const val WELCOME_MESSAGE = "Check Logcat for output"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CoroutinesWorkshopTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MessageScreen(WELCOME_MESSAGE)
                }
            }
        }
    }
}

@Composable
fun MessageScreen(
    message: String,
    modifier: Modifier = Modifier,
    coroutinesWorkshopViewModel: CoroutinesWorkshopViewModel = viewModel()
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = { coroutinesWorkshopViewModel.startPlayingWithCoroutines() }
        ) {
            Text(text = "Let's play coroutines!")
            
        }
        Text(
            text = message,
            modifier = modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MessageScreenPreview() {
    CoroutinesWorkshopTheme {
        MessageScreen(WELCOME_MESSAGE)
    }
}