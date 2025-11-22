package io.github.bw0248.simple.poker.tech_demo.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.bw0248.simple.poker.tech_demo.Dollar
import io.github.bw0248.simple.poker.tech_demo.calculateChipDistribution
import io.github.bw0248.spe.card.Card
import io.github.bw0248.spe.card.CardState
import io.github.bw0248.spe.card.CardSuit
import io.github.bw0248.spe.card.CardValue
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import tech_demo.composeapp.generated.resources.Res
import tech_demo.composeapp.generated.resources.allDrawableResources

@Composable
@Preview
fun TableView(
    viewModel: PokerGameViewModel,
    gameDimensions: GameDimensions,
    modifier: Modifier = Modifier
) {
    val tableDimensions = gameDimensions.tableDimensions
    Box(
        modifier = modifier
            .fillMaxWidth(tableDimensions.relativeTableWidth)
            .fillMaxHeight(tableDimensions.relativeTableHeight)
            .padding(top = tableDimensions.topPadding)
    ) {
        TableRail(tableDimensions)
        BettingLine(tableDimensions)
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .weight(0.3f)
                    .fillMaxWidth(0.6f)
                    .fillMaxHeight()
                    //.fillMaxSize()
                    //.background(color = Color.Yellow)
                ,
            ) {
                Box(
                    modifier = Modifier
                        .weight(0.5f)
                        .fillMaxSize()
                        //.background(Color.Red)
                    ,
                    contentAlignment = Alignment.BottomCenter
                ) {
                    val numSlots = 4
                    val initialOffset = -((gameDimensions.chipSize * numSlots) / 2)
                    val potsize = Dollar.of(123_456.99)
                    val chipSlots = calculateChipDistribution(potsize, numSlots)
                    chipSlots.forEachIndexed { slotIndex, slot ->
                       slot.forEachIndexed { chipIndex, chip ->
                           Image(
                               painter = painterResource(Res.allDrawableResources[chip]!!),
                               contentDescription = "Chips in pot",
                               modifier = Modifier
                                   .size(gameDimensions.chipSize)
                                   .offset(x = initialOffset + (gameDimensions.chipSize * slotIndex), y = -((gameDimensions.chipSize * 0.2f) * chipIndex)),
                               contentScale = ContentScale.FillBounds

                           )
                       }
                    }
                }
                Row(
                    modifier = Modifier
                        .weight(0.5f)
                        .fillMaxSize()
                        //.background(Color.Green)
                    ,
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        modifier = Modifier
                            .background(color = Color.Black.copy(alpha = 0.5f), shape = RoundedCornerShape(8.dp))
                            .padding(4.dp)
                                ,
                        text = "Pot: $123,456.99",
                        //text = "Pot: ${viewModel.uiState.potView.amountIncludingPlayerBets.format()}",
                        autoSize = TextAutoSize.StepBased(
                            minFontSize = 6.sp,
                            maxFontSize = tableDimensions.potMaxFontSize.value.toInt().sp
                        ),
                        color = Color.White,
                    )
                }
            }
            Row(
                modifier = Modifier
                    .weight(0.7f)
                    .padding(
                        start = tableDimensions.boardCardsHorizontalPadding,
                        end = tableDimensions.boardCardsHorizontalPadding,
                        //top = tableDimensions.boardCardsTopPadding,
                        //bottom = boardCardsBottomPadding,
                    ),
                //.fillMaxSize()
                //.background(Color.Magenta)
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                (0..4).map {
                    BoardCardView(Card(CardSuit.HEARTS, CardValue.QUEEN, CardState.OPEN), Modifier.weight(1f))
                    //BoardCardView(viewModel.uiState.communityCards.getOrNull(it), Modifier.Companion.weight(1f))
                }
            }
        }
    }
}

@Composable
fun TableRail(tableDimensions: TableDimensions, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = Color(0xFF1B5E20), shape = RoundedCornerShape(tableDimensions.cornerRadius))
            .border(
                width = tableDimensions.railThickness,
                //color = Color(0xFF8D6E63),
                color = Color(0xFF4E342E),
                shape = RoundedCornerShape(tableDimensions.cornerRadius)
            ),
    ) {}
}

@Composable
fun BettingLine(tableDimensions: TableDimensions, modifier: Modifier = Modifier) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(tableDimensions.bettingLinePadding)
            //.fillMaxSize(0.9f)
            //.align(Alignment.CenterHorizontally)
            .border(
                width = tableDimensions.bettingLineThickness,
                color = Color.White,
                shape = RoundedCornerShape(tableDimensions.cornerRadius)
            ),
    ) {}
}
