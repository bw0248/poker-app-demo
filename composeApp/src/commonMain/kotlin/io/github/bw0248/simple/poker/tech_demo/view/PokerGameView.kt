package io.github.bw0248.simple.poker.tech_demo.view

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.bw0248.simple.poker.tech_demo.Logger
import io.github.bw0248.spe.player.PlayerSeat
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun PokerGameView(viewModel: PokerGameViewModel = viewModel()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .weight(0.85f)
                //.background(color = Color.Yellow)
                .fillMaxSize()
        ) {
            val boxWithConstraintsScope = this

            TableView(viewModel, boxWithConstraintsScope, Modifier.align(Alignment.TopCenter))

            val tableDimensions = TableDimensions.fromParentContainerDimensions(
                width = boxWithConstraintsScope.maxWidth,
                height = boxWithConstraintsScope.maxHeight
            )
            val playerDimensions = PlayerDimensions.from(
                maxWidth = boxWithConstraintsScope.maxWidth,
                maxHeight = boxWithConstraintsScope.maxHeight,
                tableDimensions = tableDimensions
            )

            Logger.info("PokerGameView", tableDimensions.toString())
            Logger.info("PokerGameView", playerDimensions.toString())

            PlayerView(
                "Player 1",
                viewModel.uiState.playerViews[PlayerSeat.ONE],
                playerDimensions,
                //width = playerDimensions.playerWidth,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(x = playerDimensions.leftTopPlayerHorizontalOffset, y = playerDimensions.topPlayerVerticalOffsetFromTop)
            )
            PlayerView(
                "Player 2",
                viewModel.uiState.playerViews[PlayerSeat.TWO],
                playerDimensions,
                //width = playerDimensions.playerWidth,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(x = playerDimensions.rightTopPlayerHorizontalOffset, y = playerDimensions.topPlayerVerticalOffsetFromTop)
            )
            PlayerView(
                "Player 3",
                viewModel.uiState.playerViews[PlayerSeat.THREE],
                playerDimensions,
                //width = playerDimensions.playerWidth,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    //.align(Alignment.CenterStart)
                    .offset(x = playerDimensions.rightBottomPlayerHorizontalOffset, y = -playerDimensions.bottomOutsidePlayerVerticalOffset)
            )
            PlayerView(
                "Player 4",
                viewModel.uiState.playerViews[PlayerSeat.FOUR],
                playerDimensions,
                //width = playerDimensions.playerWidth,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(x = playerDimensions.rightBottomCenterPlayerHorizontalOffset)
            )
            PlayerView(
                "Player 5",
                viewModel.uiState.playerViews[PlayerSeat.FIVE],
                playerDimensions,
                //width = playerDimensions.playerWidth,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(x = playerDimensions.leftBottomCenterPlayerHorizontalOffset)
            )
            PlayerView(
                "Player 6",
                viewModel.uiState.playerViews[PlayerSeat.SIX],
                playerDimensions,
                //width = playerDimensions.playerWidth,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    //.offset(x = playerDimensions.leftBottomPlayerHorizontalOffset, y = (-40).dp)
                    .offset(x = playerDimensions.leftBottomPlayerHorizontalOffset, y = -playerDimensions.bottomOutsidePlayerVerticalOffset)
            )
        }
        ActionBar(viewModel, modifier = Modifier.weight(0.15f))
    }
}

data class PlayerDimensions(
    val relativeHorizontalSpacingBetweenPlayers: Float,
    val absoluteHorizontalSpacingBetweenPlayers: Dp,
    val playerWidth: Dp,
    val playerHeight: Dp,
    val rightBottomCenterPlayerHorizontalOffset: Dp,
    val leftBottomCenterPlayerHorizontalOffset: Dp,
    val rightBottomPlayerHorizontalOffset: Dp,
    val leftBottomPlayerHorizontalOffset: Dp,
    val leftTopPlayerHorizontalOffset: Dp,
    val rightTopPlayerHorizontalOffset: Dp,
    val bottomOutsidePlayerVerticalOffset: Dp,
    val topPlayerVerticalOffsetFromTop: Dp,
) {
    companion object {
        private const val NUM_BOTTOM_PLAYERS = 4
        private const val RELATIVE_HORIZONTAL_SPACING_BETWEEN_PLAYERS = 0.07f

        fun from(maxWidth: Dp, maxHeight: Dp, tableDimensions: TableDimensions): PlayerDimensions {
            val numBottomPlayers = NUM_BOTTOM_PLAYERS
            val absoluteHorizontalSpacingBetweenPlayers = maxWidth * RELATIVE_HORIZONTAL_SPACING_BETWEEN_PLAYERS

            val totalHorizontalSpacingBetweenBottomPlayers = absoluteHorizontalSpacingBetweenPlayers * numBottomPlayers
            val spacePerPlayer = (maxWidth - totalHorizontalSpacingBetweenBottomPlayers) / numBottomPlayers

            // offsets relative to BottomCenter for bottom center
            val rightBottomCenterPlayerOffset = ((spacePerPlayer / 2) + (absoluteHorizontalSpacingBetweenPlayers / 2))
            val leftBottomCenterPlayerOffset = -rightBottomCenterPlayerOffset
            // adding some magic offsets to make players not touch betting line
            val leftBottomPlayerOffset = (leftBottomCenterPlayerOffset - spacePerPlayer - absoluteHorizontalSpacingBetweenPlayers) - maxWidth * 0.015f
            val rightBottomPlayerOffset = (rightBottomCenterPlayerOffset + spacePerPlayer + absoluteHorizontalSpacingBetweenPlayers) + maxWidth * 0.015f

            // adding some magic offsets to make players not touch betting line
            val leftTopPlayerOffset = leftBottomPlayerOffset - maxWidth * 0.005f
            val rightTopPlayerOffset = rightBottomPlayerOffset + maxWidth * 0.005f

            val playerHeight = maxHeight * 0.3f
            val tableVerticalMiddle = (tableDimensions.absoluteTableHeight / 2) + tableDimensions.topPadding
            Logger.info("PokerGameView", "TableVerticalMiddle: $tableVerticalMiddle")
            Logger.info("PokerGameView", "maxHeight: $maxHeight")
            val bottomLeftPlayerVerticalOffset = maxHeight - tableVerticalMiddle - playerHeight
            val topLeftPlayerVerticalOffset = tableDimensions.topPadding

            return PlayerDimensions(
                relativeHorizontalSpacingBetweenPlayers = RELATIVE_HORIZONTAL_SPACING_BETWEEN_PLAYERS,
                absoluteHorizontalSpacingBetweenPlayers = absoluteHorizontalSpacingBetweenPlayers,
                playerWidth = spacePerPlayer,
                playerHeight = playerHeight,
                rightBottomCenterPlayerHorizontalOffset = rightBottomCenterPlayerOffset,
                leftBottomCenterPlayerHorizontalOffset = leftBottomCenterPlayerOffset,
                rightBottomPlayerHorizontalOffset = rightBottomPlayerOffset,
                leftBottomPlayerHorizontalOffset = leftBottomPlayerOffset,
                rightTopPlayerHorizontalOffset = rightTopPlayerOffset,
                leftTopPlayerHorizontalOffset = leftTopPlayerOffset,
                bottomOutsidePlayerVerticalOffset = bottomLeftPlayerVerticalOffset,
                topPlayerVerticalOffsetFromTop = topLeftPlayerVerticalOffset
            )
        }
    }
}
