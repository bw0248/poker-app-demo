package io.github.bw0248.simple.poker.tech_demo.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.bw0248.spe.card.Card
import io.github.bw0248.spe.card.CardSuit
import io.github.bw0248.spe.card.CardValue
import io.github.bw0248.spe.player.PlayerSeat
import io.github.bw0248.spe.player.PlayerStatus
import io.github.bw0248.spe.player.PlayerView
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun PokerGameView(viewModel: PokerGameViewModel = viewModel()) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.2f)
                //.background(color = Color.Blue)
        ) {}
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.6f),
            //horizontalArrangement = Arrangement.Center
                //.background(color = Color.Green)
        ) {
            PlayerView(
                name = "Player 1",
                playerView = viewModel.uiState.playerViews[PlayerSeat.ONE],
                modifier = Modifier.weight(0.25f)
            )
            TableView(
                viewModel,
                modifier = Modifier.weight(0.5f)
            )
            PlayerView(
                name = "Player 2",
                playerView = viewModel.uiState.playerViews[PlayerSeat.TWO],
                modifier = Modifier.weight(0.25f)
            )
        }
        ActionBar(viewModel = viewModel, modifier = Modifier.weight(0.2f))
    }
}

@Composable
@Preview
fun PlayerView(name: String, playerView: PlayerView?, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            //.background(color = Color.Red)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        playerView?.let {
            Column(
                modifier = Modifier
                    //.background(color = Color.White)
                    .fillMaxWidth(0.75f)
                    .fillMaxHeight(0.75f),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(
                    modifier = Modifier
                        .weight(0.6f)
                        //.background(color = Color.Green)
                        .fillMaxWidth(0.8f),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.Center
                ) {
                    it.holeCards.map { card ->
                        HoleCardView(card, modifier = Modifier.weight(1f))
                    }
                }
                val borderWidth = if (playerView.playerStatus == PlayerStatus.NEXT_TO_ACT) 4.dp else 0.dp
                Column(
                    modifier = Modifier
                        .weight(0.4f)
                        .fillMaxWidth()
                        .background(color = Color.Black)
                        .border(width = borderWidth, color = Color.Red, shape = RoundedCornerShape(16.dp)),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = name,
                        maxLines = 1,
                        softWrap = false,
                        autoSize = TextAutoSize.StepBased(minFontSize = 6.sp, 32.sp),
                        //style = MaterialTheme.typography.displaySmall.copy(fontSize = TextUnit.Unspecified),
                        //fontSize = 16.nonScaledSp,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        modifier = Modifier.weight(1f),
                        //text = "$222.444.99",
                        text = playerView.currentStack.format(),
                        maxLines = 1,
                        softWrap = false,
                        autoSize = TextAutoSize.StepBased(minFontSize = 6.sp, 32.sp),
                        //style = MaterialTheme.typography.displaySmall,
                        //fontSize = 16.nonScaledSp,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
@Preview
fun TableView(viewModel: PokerGameViewModel, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(color = Color(0xFF1B5E20), shape = RoundedCornerShape(64.dp))
            .border(16.dp, color = Color(0xFF2A2A2A), shape = RoundedCornerShape(64.dp))
            //.border(2.dp, color = Color(0xFF1B5E20), RoundedCornerShape(32.dp))
        ,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier
                .padding(4.dp)
                .background(color = Color.Black.copy(alpha = 0.5f), shape = RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp),
            //text = "Pot: $123,456.99",
            text = "Pot: ${viewModel.uiState.potView.amountIncludingPlayerBets.format()}",
            color = Color.White,
            style = MaterialTheme.typography.displaySmall,
            fontSize = 20.sp.nonScaledSp
        )
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .padding(32.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            (0..4).map {
                BoardCardView(viewModel.uiState.communityCards.getOrNull(it), modifier.weight(1f))
            }
        }
    }
}
