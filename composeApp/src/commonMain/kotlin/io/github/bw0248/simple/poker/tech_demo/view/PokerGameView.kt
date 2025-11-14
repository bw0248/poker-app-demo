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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButtonDefaults.elevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    Row(modifier = Modifier.fillMaxHeight(0.25f)) {}
    TableView(viewModel)
    Row(modifier = Modifier.fillMaxHeight(0.25f)) {}
    //Text(text = "Poker Demo", color = Color.White, modifier = Modifier.align(Alignment.TopStart).padding(8.dp))
    //Box(
    //    modifier = Modifier
    //        .fillMaxSize()
    //        .background(color = Color.Black)
    //        .padding(16.dp),
    //    contentAlignment = Alignment.Center
    //) {
    //    TableView(viewModel, modifier = Modifier.align(Alignment.Center))

    //    Box(modifier = Modifier.align(Alignment.BottomCenter)) {
    //        PlayerView(
    //            name = "Player 1",
    //            playerView = viewModel.uiState.playerViews[PlayerSeat.ONE]
    //        )
    //    }

    //    Box(modifier = Modifier.align(Alignment.TopCenter)) {
    //        PlayerView(
    //            name = "Player 2",
    //            playerView = viewModel.uiState.playerViews[PlayerSeat.FIVE]
    //        )
    //    }
    //}
}

@Composable
@Preview
fun PlayerView(name: String, playerView: PlayerView?) {
    playerView?.let {
        Row {
            it.holeCards.map { HoleCardView(it) }
        }
        Card(
            modifier = Modifier
                .fillMaxWidth(0.33f)
                .height(80.dp)
                .padding(4.dp)
                .border(width = 2.dp, color = Color.Yellow)
                .background(Color.Red)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = SpaceAround,
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Red)
                    .padding(all = 8.dp)
                    //.border(width = 1.dp, color = Color.White)
            ) {
                Column {
                    Text(text = name, color = Color.White)
                    //Spacer(Modifier.weight())
                    Text(text = "$${it.currentStack.amount}", color = Color.White)
                }
            }
        }
    }
}

@Composable
@Preview
fun TableView(viewModel: PokerGameViewModel, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth(0.5f)
            .fillMaxHeight(0.5f)
            .border(width = 2.dp, color = Color.Red),
    ) {
        Row(
            modifier = Modifier
                .weight(0.7f)
                .fillMaxHeight()
                .background(color = Color.Yellow),
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
        Column(
            modifier = Modifier
                .weight(0.3f)
                .fillMaxHeight()
                .background(color = Color.Magenta),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Column(modifier = Modifier.border(2.dp, color = Color.Green)) {
                Text(
                    modifier = Modifier.border(width = 2.dp, color = Color.Black),
                    text = "Pot",
                    color = Color.White,
                    style = MaterialTheme.typography.displaySmall,
                    fontSize = 20.sp.nonScaledSp
                )
                Text(
                    modifier = Modifier.border(width = 2.dp, color = Color.Green),
                    //text = "$${viewModel.uiState.potView.amountIncludingPlayerBets.amount}",
                    //text = "$123,456.99",
                    text = "$123",
                    color = Color.White,
                    style = MaterialTheme.typography.displaySmall,
                    fontSize = 20.sp.nonScaledSp
                )
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
        modifier = modifier.aspectRatio(0.666f),//Modifier.size(120.dp, 200.dp),
        shape = MaterialTheme.shapes.small,
        //modifier = modifier,
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
        modifier = Modifier.size(60.dp, 90.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
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