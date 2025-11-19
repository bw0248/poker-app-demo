package io.github.bw0248.simple.poker.tech_demo.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.bw0248.spe.player.PlayerStatus
import io.github.bw0248.spe.player.PlayerView
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun PlayerView(name: String, playerView: PlayerView?, width: Dp, modifier: Modifier = Modifier.Companion) {
    Column(
        modifier = modifier
            .fillMaxHeight(0.3f)
            //.fillMaxWidth(0.2f)
            .width(width)
        //.background(color = Color.Red)
        //.fillMaxSize(),
        ,
        horizontalAlignment = Alignment.Companion.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        playerView?.let {
            Row(
                modifier = Modifier.Companion
                    .weight(0.45f)
                    .background(color = Color.Companion.Green)
                    .fillMaxWidth(0.8f),
                verticalAlignment = Alignment.Companion.Bottom,
                horizontalArrangement = Arrangement.Center
            ) {
                it.holeCards.map { card ->
                    HoleCardView(card, modifier = Modifier.Companion.weight(1f))
                    //HoleCardView(Card(CardSuit.SPADES, CardValue.ACE, CardState.OPEN), modifier = Modifier.weight(1f))
                    //HoleCardView(Card(CardSuit.SPADES, CardValue.ACE, CardState.HIDDEN), modifier = Modifier.weight(1f))
                }
            }
            val (borderWidth, borderColor) = if (playerView.playerStatus == PlayerStatus.NEXT_TO_ACT) {
                4.dp to Color.Companion.Red
            } else {
                4.dp to Color.Companion.DarkGray
            }
            val cornerShape = RoundedCornerShape(16.dp)
            Column(
                modifier = Modifier.Companion
                    .weight(0.55f)
                    .background(color = Color.Companion.Black, shape = cornerShape)
                    .border(width = borderWidth, color = borderColor, shape = cornerShape),
                verticalArrangement = Arrangement.Center
            ) {
                val maxFontSize = 32.sp
                Text(
                    modifier = Modifier.Companion
                        .weight(1f)
                        .fillMaxSize()
                    //.background(color = Color.LightGray)
                    ,
                    text = name,
                    maxLines = 1,
                    softWrap = false,
                    autoSize = TextAutoSize.Companion.StepBased(minFontSize = 6.sp, maxFontSize),
                    //style = MaterialTheme.typography.displaySmall.copy(fontSize = TextUnit.Unspecified),
                    //fontSize = 16.nonScaledSp,
                    color = Color.Companion.White,
                    textAlign = TextAlign.Companion.Center
                )
                Text(
                    modifier = Modifier.Companion
                        .weight(1f)
                        .fillMaxSize()
                    //.padding(2.dp)
                    //.background(color = Color.Cyan)
                    ,
                    //text = "$222.444.99",
                    text = playerView.currentStack.format(),
                    maxLines = 1,
                    softWrap = false,
                    autoSize = TextAutoSize.Companion.StepBased(minFontSize = 6.sp, maxFontSize),
                    //style = MaterialTheme.typography.displaySmall,
                    //fontSize = 16.nonScaledSp,
                    color = Color.Companion.White,
                    textAlign = TextAlign.Companion.Center
                )
            }
        }
    }
}