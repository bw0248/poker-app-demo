package io.github.bw0248.simple.poker.tech_demo.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.SpaceAround
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButtonDefaults.elevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.bw0248.spe.card.Card
import io.github.bw0248.spe.card.CardState
import io.github.bw0248.spe.card.CardSuit
import io.github.bw0248.spe.card.CardValue
import io.github.bw0248.spe.player.PlayerSeat
import io.github.bw0248.spe.player.PlayerView
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import tech_demo.composeapp.generated.resources.Res
import tech_demo.composeapp.generated.resources.ac_full
import tech_demo.composeapp.generated.resources.cb_half


@Composable
@Preview
fun PokerGameView(viewModel: PokerGameViewModel = viewModel()) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.25f)
                //.background(color = Color.Blue)
        ) {}
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.5f),
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
        Row(modifier = Modifier
            .fillMaxWidth()
            .weight(0.25f)
            //.background(color = Color.Blue)
        ) {}
    }
}

@Composable
@Preview
fun PlayerView(name: String, playerView: PlayerView?, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = Color.Red),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        playerView?.let {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .fillMaxHeight(0.75f)
                    .background(color = Color.White),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(
                    modifier = Modifier
                        .weight(0.6f)
                        .fillMaxWidth(0.8f)
                        .background(color = Color.Green),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.Center
                ) {
                    it.holeCards.map { card ->
                        HoleCardView(card, modifier = Modifier.weight(1f))
                    }
                }
                Column(
                    modifier = Modifier
                        .weight(0.4f)
                        .fillMaxWidth()
                        .background(color = Color.Black)
                        .border(width = 2.dp, color = Color.Red),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(name, color = Color.White)
                    Text("$1234.99", color = Color.White)
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
            .background(Color(0xFF1B5E20)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier
                .padding(4.dp)
                .background(color = Color.Black.copy(alpha = 0.5f), shape = RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp),
            text = "Pot: $123,456.99",
            color = Color.White,
            style = MaterialTheme.typography.displaySmall,
            fontSize = 20.sp.nonScaledSp
        )
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .padding(32.dp)
            ,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            (0..4).map {
                BoardCardView(
                    Card(CardSuit.DIAMONDS, CardValue.ACE),
                    modifier = Modifier.weight(1f)
                )
                //Box(
                //    modifier = Modifier
                //        .weight(1f)
                //        .aspectRatio(0.666f)
                //        .border(2.dp, color = Color.Green)
                //) {}
            }
        }
    }
}

@Composable
fun BoardCardView(card: Card, modifier: Modifier = Modifier) {
    val value = card.cardValue.name.lowercase().first()
    val suit = card.cardSuit.name.lowercase().first()
    val resourceName = "${suit}_${value}_full.png"

    Card(
        modifier = modifier.aspectRatio(0.666f),
        shape = MaterialTheme.shapes.small,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Image(
            painter = painterResource(Res.drawable.ac_full),
            contentDescription = "Playing Card",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
    }
}

@Composable
fun HoleCardView(card: Card, modifier: Modifier = Modifier) {
    val value = card.cardValue.name.lowercase().first()
    val suit = card.cardSuit.name.lowercase().first()
    val state = card.cardState
    val resourceName = if (state == CardState.HIDDEN) {
        "cb_half.png"
    } else {
        "${suit}_${value}_half.png"
    }

    Card(
        modifier = modifier
            .aspectRatio(1.33f),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RectangleShape,
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Image(
            painter = painterResource(Res.drawable.cb_half),
            contentDescription = "Playing Card",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
    }

}

val TextUnit.nonScaledSp
    @Composable
    get() = (this.value / LocalDensity.current.fontScale).sp