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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun TableView(viewModel: PokerGameViewModel, boxWithConstraintsScope: BoxWithConstraintsScope, modifier: Modifier = Modifier.Companion) {
    val tableDimensions = TableDimensions.fromParentContainerDimensions(
        width = boxWithConstraintsScope.maxWidth,
        height = boxWithConstraintsScope.maxHeight
    )

    Card(
        modifier = modifier
            .fillMaxWidth(tableDimensions.relativeTableWidth)
            .fillMaxHeight(tableDimensions.relativeTableHeight)
            //.align(Alignment.TopCenter)
            .padding(top = tableDimensions.topPadding)
        ,
        //shape = RoundedCornerShape(300.dp),
        shape = RoundedCornerShape(tableDimensions.cornerRadius),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B5E20)),
        elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
    ) {
        Box() {
            Row(
                modifier = Modifier.Companion
                    .fillMaxSize()
                    .padding(tableDimensions.bettingLinePadding)
                    //.fillMaxSize(0.9f)
                    //.align(Alignment.CenterHorizontally)
                    .border(
                        width = tableDimensions.bettingLineThickness,
                        color = Color.Companion.White,
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(tableDimensions.cornerRadius)
                    ),
            ) {}
            Column(
                modifier = Modifier.Companion
                    .fillMaxSize(),
                horizontalAlignment = Alignment.Companion.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier.Companion
                        .background(
                            color = Color.Companion.Black.copy(alpha = 0.5f),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                        )
                        //.border(2.dp, color = Color.Red)
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    text = "Pot: $123,456.99",
                    //text = "Pot: ${viewModel.uiState.potView.amountIncludingPlayerBets.format()}",
                    autoSize = TextAutoSize.Companion.StepBased(
                        minFontSize = 6.sp,
                        maxFontSize = tableDimensions.potMaxFontSize.value.toInt().sp
                    ),
                    color = Color.Companion.White,
                    style = MaterialTheme.typography.displaySmall,
                    fontSize = 20.sp.nonScaledSp
                )
                Row(
                    modifier = Modifier.Companion
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
                        //BoardCardView(Card(CardSuit.HEARTS, CardValue.QUEEN, CardState.OPEN), Modifier.weight(1f))
                        BoardCardView(viewModel.uiState.communityCards.getOrNull(it), Modifier.Companion.weight(1f))
                    }
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
    val potMaxFontSize: Dp,
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
            val boardCardsTopPadding = height * relativeTableHeight * 0.1f

            val bettingLinePadding = absoluteTableWidth * 0.05f
            val bettingLineThickness = absoluteTableWidth * 0.004f

            val tableRadius = height * 0.50f
            val tableTopPadding = height * 0.05f

            val potMaxFontSize = absoluteTableWidth * 0.03f
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
                potMaxFontSize = potMaxFontSize
            )
        }
    }
}