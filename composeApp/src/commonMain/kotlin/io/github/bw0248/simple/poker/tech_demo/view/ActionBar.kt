package io.github.bw0248.simple.poker.tech_demo.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.bw0248.spe.BigBlind
import kotlin.math.round

val BUTTON_COLOR = Color(0xFF800000)

@Composable
fun ActionBar(viewModel: PokerGameViewModel, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        //.background(color = Color.Blue),
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val activePlayer = viewModel.getActivePlayer() ?: return
        val minBet = BigBlind.of(1)
        val maxBet = 100f
        val facingBet = viewModel.uiState.currentBet?.let { it != BigBlind.of(0) } ?: false
        val betSliderPosition = remember { mutableStateOf(minBet) }
        ActionButton(
            modifier = Modifier.weight(0.22f),
            onClick = { viewModel.fold(activePlayer.key) },
            prefixText = "FOLD",
        )
        val (callText, callAmount) = if (facingBet) "CALL" to viewModel.uiState.currentBet else "CHECK" to null
        ActionButton(
            modifier = Modifier.weight(0.22f),
            onClick = {
                if (facingBet) viewModel.call(activePlayer.key) else viewModel.check(activePlayer.key)
            },
            prefixText = callText,
            amount = callAmount
        )
        ActionButton(
            modifier = Modifier.weight(0.22f),
            onClick = {
                if (facingBet) {
                    viewModel.raise(activePlayer.key, betSliderPosition.value)
                } else {
                    viewModel.bet(activePlayer.key, betSliderPosition.value)
                }
            },
            prefixText = if (facingBet) "RAISE" else "BET",
            amount = betSliderPosition.value
        )
        Column(
            modifier = Modifier
                .weight(0.33f),
            // .border(2.dp, Color.Red)
        ) {
            Row(
                modifier = Modifier
                    .weight(0.5f)
                    .padding(4.dp)
                //.border(2.dp, Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(color = BUTTON_COLOR),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AutoSizeText(text = "MIN", maxFontSize = 16.sp, minFontSize = 5.sp)
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(color = BUTTON_COLOR),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AutoSizeText(text = "33%", maxFontSize = 16.sp, minFontSize = 5.sp)
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(color = BUTTON_COLOR),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AutoSizeText(text = "50%", maxFontSize = 16.sp, minFontSize = 5.sp)
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(color = BUTTON_COLOR),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AutoSizeText(text = "75%", maxFontSize = 16.sp, minFontSize = 5.sp)
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(color = BUTTON_COLOR),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AutoSizeText(text = "POT", maxFontSize = 16.sp, minFontSize = 5.sp)
                }
            }
            Slider(
                modifier = Modifier.weight(0.5f),
                value = betSliderPosition.value.amount.toFloat(),
                onValueChange = { betSliderPosition.value = BigBlind.of(round(it * 100.0) / 100.0) },
                valueRange = minBet.amount.toFloat()..maxBet,
                colors = SliderDefaults.colors(thumbColor = BUTTON_COLOR, activeTrackColor = Color.Green)
            )
        }
    }
}

@Composable
fun ActionButton(modifier: Modifier = Modifier, onClick: () -> Unit, prefixText: String, amount: BigBlind? = null) {
    Button(
        modifier = modifier,
        onClick = onClick,
        shape = RectangleShape,
        colors = ButtonDefaults.buttonColors(containerColor = BUTTON_COLOR)
    ) {
        AmountText(prefixText = prefixText, amount = amount)
    }
}
