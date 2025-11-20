package io.github.bw0248.simple.poker.tech_demo.view

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.bw0248.spe.card.Card
import io.github.bw0248.spe.card.CardState
import io.github.bw0248.spe.card.CardSuit
import io.github.bw0248.spe.card.CardValue
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun TableView(
    viewModel: PokerGameViewModel,
    boxWithConstraintsScope: BoxWithConstraintsScope,
    modifier: Modifier = Modifier
) {
    val tableDimensions = TableDimensions.fromParentContainerDimensions(
        width = boxWithConstraintsScope.maxWidth,
        height = boxWithConstraintsScope.maxHeight
    )

    Box(
        modifier = modifier
            .fillMaxWidth(tableDimensions.relativeTableWidth)
            .fillMaxHeight(tableDimensions.relativeTableHeight)
            .padding(top = tableDimensions.topPadding)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color(0xFF1B5E20), shape = RoundedCornerShape(tableDimensions.cornerRadius))
                .border(
                    width = tableDimensions.railThickness,
                    //color = Color(0xFF8D6E63),
                    color = Color(0xFF4E342E),
                    shape = RoundedCornerShape(tableDimensions.cornerRadius)
                ),
        ) {}
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
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .weight(0.2f)
                    .fillMaxSize()
                //.background(color = Color.Yellow),
                ,
                horizontalArrangement = Arrangement.Center,
                //verticalAlignment = Alignment.CenterVertically
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    modifier = Modifier
                        .background(color = Color.Black.copy(alpha = 0.5f), shape = RoundedCornerShape(8.dp))
                        .padding(8.dp),
                    text = "Pot: $123,456.99",
                    //text = "Pot: ${viewModel.uiState.potView.amountIncludingPlayerBets.format()}",
                    autoSize = TextAutoSize.StepBased(
                        minFontSize = 6.sp,
                        maxFontSize = tableDimensions.potMaxFontSize.value.toInt().sp
                    ),
                    color = Color.White,
                )
            }
            Row(
                modifier = Modifier
                    .weight(0.8f)
                    .padding(
                        start = tableDimensions.boardCardsHorizontalPadding,
                        end = tableDimensions.boardCardsHorizontalPadding,
                        top = tableDimensions.boardCardsTopPadding,
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

data class TableDimensions(
    val relativeTableWidth: Float,
    val relativeTableHeight: Float,
    val absoluteTableWidth: Dp,
    val absoluteTableHeight: Dp,
    val cornerRadius: Dp,
    val topPadding: Dp,
    val boardCardsHorizontalPadding: Dp,
    val boardCardsTopPadding: Dp,
    val bettingLinePadding: Dp,
    val bettingLineThickness: Dp,
    val railThickness: Dp,
    val potMaxFontSize: Dp,
    val potVerticalPadding: Dp,
) {
    companion object {
        const val RELATIVE_TABLE_WIDTH: Float = 0.7f
        const val RELATIVE_TABLE_HEIGHT: Float = 0.9f

        fun fromParentContainerDimensions(width: Dp, height: Dp): TableDimensions {
            val relativeTableWidth = 0.7f
            val relativeTableHeight = 0.9f
            val absoluteTableWidth = width * relativeTableWidth
            val absoluteTableHeight = height * relativeTableHeight
            val boardCardsHorizontalPadding = width * relativeTableWidth * 0.2f
            val boardCardsTopPadding = height * relativeTableHeight * 0.05f

            val bettingLinePadding = absoluteTableWidth * 0.05f
            val bettingLineThickness = absoluteTableWidth * 0.004f
            val railThickness = absoluteTableWidth * 0.022f

            val tableRadius = height * 0.50f
            val tableTopPadding = height * 0.05f

            val potMaxFontSize = absoluteTableWidth * 0.03f
            val potVerticalPadding = absoluteTableHeight * 0.05f
            return TableDimensions(
                relativeTableWidth = RELATIVE_TABLE_WIDTH,
                relativeTableHeight = RELATIVE_TABLE_HEIGHT,
                absoluteTableWidth = absoluteTableWidth,
                absoluteTableHeight = absoluteTableHeight,
                cornerRadius = tableRadius,
                topPadding = tableTopPadding,
                boardCardsHorizontalPadding = boardCardsHorizontalPadding,
                boardCardsTopPadding = boardCardsTopPadding,
                bettingLinePadding = bettingLinePadding,
                bettingLineThickness = bettingLineThickness,
                railThickness = railThickness,
                potMaxFontSize = potMaxFontSize,
                potVerticalPadding = potVerticalPadding
            )
        }
    }
}