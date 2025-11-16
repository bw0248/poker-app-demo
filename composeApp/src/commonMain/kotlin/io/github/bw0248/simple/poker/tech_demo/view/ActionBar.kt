package io.github.bw0248.simple.poker.tech_demo.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import io.github.bw0248.spe.bigBlind
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
        val minBet = viewModel.calculateMinRaise()
        val maxBet = 100f
        //val facingBet = viewModel.uiState.currentBet?.let { it != BigBlind.of(0) } ?: false
        val allowedToCheck = viewModel.allowedToCheck(activePlayer.key)
        val allowedToCRaise = viewModel.allowedToRaise(activePlayer.key)
        val betSliderPosition = remember { mutableStateOf(minBet) }
        ActionButton(
            modifier = Modifier.weight(0.22f),
            onClick = { viewModel.fold(activePlayer.key) },
            prefixText = "FOLD",
        )
        val (callText, callAmount) = if (allowedToCheck) "CHECK" to null else "CALL" to viewModel.uiState.currentBet
        ActionButton(
            modifier = Modifier.weight(0.22f),
            onClick = {
                if (allowedToCheck) viewModel.check(activePlayer.key) else viewModel.call(activePlayer.key)
            },
            prefixText = callText,
            amount = callAmount
        )
        ActionButton(
            modifier = Modifier.weight(0.22f),
            onClick = {
                if (allowedToCRaise) {
                    viewModel.raise(activePlayer.key, betSliderPosition.value)
                } else {
                    viewModel.bet(activePlayer.key, betSliderPosition.value)
                }
            },
            prefixText = if (allowedToCRaise) "RAISE" else "BET",
            amount = betSliderPosition.value
        )
        Column(
            modifier = Modifier
                .weight(0.33f),
        ) {
            Row(
                modifier = Modifier
                    .weight(0.5f)
                    .padding(4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(onClick = { betSliderPosition.value = viewModel.calculateMinRaise() })
                        .background(color = BUTTON_COLOR),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AutoSizeText(text = "MIN", maxFontSize = 16.sp, minFontSize = 5.sp)
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(onClick = { betSliderPosition.value = viewModel.calculatePotRaise(0.333) })
                        .background(color = BUTTON_COLOR),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AutoSizeText(text = "33%", maxFontSize = 16.sp, minFontSize = 5.sp)
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(onClick = { betSliderPosition.value = viewModel.calculatePotRaise(0.5) })
                        .background(color = BUTTON_COLOR),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AutoSizeText(text = "50%", maxFontSize = 16.sp, minFontSize = 5.sp)
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(onClick = { betSliderPosition.value = viewModel.calculatePotRaise(0.75) })
                        .background(color = BUTTON_COLOR),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AutoSizeText(text = "75%", maxFontSize = 16.sp, minFontSize = 5.sp)
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(onClick = { betSliderPosition.value = viewModel.calculatePotRaise(1.0) })
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
                //onValueChange = { betSliderPosition.value = BigBlind.of(round(it * 100.0) / 100.0) },
                onValueChange = { betSliderPosition.value =  it.bigBlind().round() },
                valueRange = minBet.amount.toFloat()..maxBet,
                colors = SliderDefaults.colors(thumbColor = BUTTON_COLOR, activeTrackColor = Color.Green)
            )
        }
    }
}

private fun Float.bigBlind(): BigBlind {
    return this.toDouble().bigBlind()
}

private fun BigBlind.round(): BigBlind {
    return this.multiply(100).divide(100.bigBlind())
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
