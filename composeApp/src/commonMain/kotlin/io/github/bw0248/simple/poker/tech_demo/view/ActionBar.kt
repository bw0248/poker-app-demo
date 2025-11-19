package io.github.bw0248.simple.poker.tech_demo.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.bw0248.spe.BigBlind
import io.github.bw0248.spe.bigBlind

val BUTTON_COLOR = Color(0xFF800000)

@Composable
fun ActionBar(viewModel: PokerGameViewModel, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxSize()
            //.fillMaxWidth()
            //.background(color = Color.Blue)
            //.border(2.dp, color = Color.Red)
            //.padding(8.dp),
                ,
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val activePlayer = viewModel.getActivePlayer() ?: return
        val minBet = viewModel.calculateMinRaise()
            .minOf(activePlayer.value.currentStack.plus(activePlayer.value.currentBet))
        val maxBet = activePlayer.value.currentStack.plus(activePlayer.value.currentBet)
            .minOf(activePlayer.value.currentStack.plus(activePlayer.value.currentBet))
        val allowedToCheck = viewModel.allowedToCheck(activePlayer.key)
        val allowedToCRaise = viewModel.allowedToRaise(activePlayer.key)
        val betSliderPosition = remember { mutableStateOf(minBet) }
        ActionButton(
            modifier = Modifier.weight(0.22f),
            onClick = { viewModel.fold(activePlayer.key) },
            prefixText = "FOLD",
        )
        val (callText, callAmount) = if (allowedToCheck) "CHECK" to null else "CALL" to viewModel.uiState.currentBet?.minOf(activePlayer.value.currentStack)
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
                .weight(0.33f)
            //.border(2.dp, color = Color.Yellow),
        ) {
            Row(
                modifier = Modifier
                    .weight(1f),
                    //.border(2.dp, color = Color.Magenta)
                    //.padding(4.dp),
                verticalAlignment = Alignment.Bottom,
                //verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(onClick = { betSliderPosition.value = viewModel.calculateMinRaise()
                            .minOf(activePlayer.value.currentStack.plus(activePlayer.value.currentBet)) })
                        .background(color = BUTTON_COLOR)
                        .border(1.dp, color = Color.Black),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AutoSizeText(text = "MIN", maxFontSize = 16.sp, minFontSize = 5.sp)
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(onClick = { betSliderPosition.value = viewModel.calculatePotRaise(0.333)
                            .minOf(activePlayer.value.currentStack.plus(activePlayer.value.currentBet)) })
                        .background(color = BUTTON_COLOR)
                        .border(1.dp, color = Color.Black),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AutoSizeText(text = "33%", maxFontSize = 16.sp, minFontSize = 5.sp)
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(onClick = { betSliderPosition.value = viewModel.calculatePotRaise(0.5)
                            .minOf(activePlayer.value.currentStack.plus(activePlayer.value.currentBet)) })
                        .background(color = BUTTON_COLOR)
                        .border(1.dp, color = Color.Black),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AutoSizeText(text = "50%", maxFontSize = 16.sp, minFontSize = 5.sp)
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(onClick = { betSliderPosition.value = viewModel.calculatePotRaise(0.75)
                            .minOf(activePlayer.value.currentStack.plus(activePlayer.value.currentBet)) })
                        .background(color = BUTTON_COLOR)
                        .border(1.dp, color = Color.Black),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AutoSizeText(text = "75%", maxFontSize = 16.sp, minFontSize = 5.sp)
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(onClick = { betSliderPosition.value = viewModel.calculatePotRaise(1.0)
                            .minOf(activePlayer.value.currentStack.plus(activePlayer.value.currentBet)) })
                        .background(color = BUTTON_COLOR)
                        .border(1.dp, color = Color.Black),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AutoSizeText(text = "POT", maxFontSize = 16.sp, minFontSize = 5.sp)
                }
            }
            Slider(
                modifier = Modifier.weight(1.2f),
                //.border(2.dp, color = Color.Green),
                value = betSliderPosition.value.amount.toFloat(),
                onValueChange = { betSliderPosition.value =  it.bigBlind().round() },
                valueRange = minBet.amount.toFloat()..maxBet.amount.toFloat(),
                colors = SliderDefaults.colors(thumbColor = BUTTON_COLOR, activeTrackColor = BUTTON_COLOR)
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
    Column(
        modifier = modifier//Modifier.weight(1f)
            .clickable(onClick = onClick)
            //.border(1.dp, color = Color.Black)
            .background(color = BUTTON_COLOR),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AmountText(prefixText = prefixText, amount = amount)
    }
}
