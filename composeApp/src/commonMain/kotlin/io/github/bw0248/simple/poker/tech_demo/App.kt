package io.github.bw0248.simple.poker.tech_demo

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import io.github.bw0248.simple.poker.tech_demo.view.PokerGameView
import io.github.bw0248.spe.config.GameConfig
import io.github.bw0248.spe.game.Game
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

import tech_demo.composeapp.generated.resources.Res
import tech_demo.composeapp.generated.resources.compose_multiplatform

@Composable
@Preview
fun App() {
    MaterialTheme {
        //var showContent by remember { mutableStateOf(false) }
        Column(
            modifier = Modifier
                .background(Color.Black)
                .safeContentPadding()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            PokerGameView()
            //Button(onClick = {}) {
            //    Text("Start game")
            //}
            //AnimatedVisibility(showContent) {
            //    val greeting = remember { Greeting().greet() }
            //    Column(
            //        modifier = Modifier.fillMaxWidth(),
            //        horizontalAlignment = Alignment.CenterHorizontally,
            //    ) {
            //        Image(painterResource(Res.drawable.compose_multiplatform), null)
            //        Text("Compose: $greeting")
            //    }
            //}
        }
    }
}