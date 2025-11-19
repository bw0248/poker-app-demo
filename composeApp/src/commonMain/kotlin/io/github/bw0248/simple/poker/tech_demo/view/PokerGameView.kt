package io.github.bw0248.simple.poker.tech_demo.view

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
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

            val playerDimensions = PlayerDimensions.from(
                maxWidth = boxWithConstraintsScope.maxWidth,
                maxHeight = boxWithConstraintsScope.maxHeight
            )

            PlayerView(
                "Player 1",
                viewModel.uiState.playerViews[PlayerSeat.ONE],
                width = playerDimensions.spacePerPlayer,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(x = playerDimensions.leftTopPlayerOffset, y = 30.dp)
            )
            PlayerView(
                "Player 2",
                viewModel.uiState.playerViews[PlayerSeat.TWO],
                width = playerDimensions.spacePerPlayer,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(x = playerDimensions.rightTopPlayerOffset, y = 30.dp)
            )
            PlayerView(
                "Player 3",
                viewModel.uiState.playerViews[PlayerSeat.THREE],
                width = playerDimensions.spacePerPlayer,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(x = playerDimensions.rightBottomPlayerOffset, y = (-40).dp)
            )
            PlayerView(
                "Player 4",
                viewModel.uiState.playerViews[PlayerSeat.FOUR],
                width = playerDimensions.spacePerPlayer,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(x = playerDimensions.rightBottomCenterPlayerOffset)
            )
            PlayerView(
                "Player 5",
                viewModel.uiState.playerViews[PlayerSeat.FIVE],
                width = playerDimensions.spacePerPlayer,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(x = playerDimensions.leftBottomCenterPlayerOffset)
            )
            PlayerView(
                "Player 6",
                viewModel.uiState.playerViews[PlayerSeat.SIX],
                width = playerDimensions.spacePerPlayer,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(x = playerDimensions.leftBottomPlayerOffset, y = (-40).dp)
            )
        }
        ActionBar(viewModel, modifier = Modifier.weight(0.15f))
    }
}

data class PlayerDimensions(
    val relativeHorizontalSpacingBetweenPlayers: Float,
    val absoluteHorizontalSpacingBetweenPlayers: Dp,
    val spacePerPlayer: Dp,
    val rightBottomCenterPlayerOffset: Dp,
    val leftBottomCenterPlayerOffset: Dp,
    val rightBottomPlayerOffset: Dp,
    val leftBottomPlayerOffset: Dp,
    val leftTopPlayerOffset: Dp,
    val rightTopPlayerOffset: Dp,
) {
    companion object {
        private const val NUM_BOTTOM_PLAYERS = 4
        private const val RELATIVE_HORIZONTAL_SPACING_BETWEEN_PLAYERS = 0.05f

        fun from(maxWidth: Dp, maxHeight: Dp): PlayerDimensions {
            val numBottomPlayers = 4
            //val relativeHorizontalSpacingBetweenPlayers = 0.05f
            val absoluteHorizontalSpacingBetweenPlayers = maxWidth * RELATIVE_HORIZONTAL_SPACING_BETWEEN_PLAYERS

            val totalHorizontalSpacingBetweenBottomPlayers = absoluteHorizontalSpacingBetweenPlayers * numBottomPlayers
            val spacePerPlayer = (maxWidth - totalHorizontalSpacingBetweenBottomPlayers) / numBottomPlayers

            // offsets relative to BottomCenter for bottom center
            val rightBottomCenterPlayerOffset = ((spacePerPlayer / 2) + (absoluteHorizontalSpacingBetweenPlayers / 2))
            val leftBottomCenterPlayerOffset = -rightBottomCenterPlayerOffset
            val leftBottomPlayerOffset = leftBottomCenterPlayerOffset - spacePerPlayer - absoluteHorizontalSpacingBetweenPlayers
            val rightBottomPlayerOffset = rightBottomCenterPlayerOffset + spacePerPlayer + absoluteHorizontalSpacingBetweenPlayers

            // offsets are relative to TopCenter for top players but aligned with horizontal offsets for bottom players
            val leftTopPlayerOffset = leftBottomPlayerOffset
            val rightTopPlayerOffset = rightBottomPlayerOffset
            return PlayerDimensions(
                relativeHorizontalSpacingBetweenPlayers = RELATIVE_HORIZONTAL_SPACING_BETWEEN_PLAYERS,
                absoluteHorizontalSpacingBetweenPlayers = absoluteHorizontalSpacingBetweenPlayers,
                spacePerPlayer = spacePerPlayer,
                rightBottomCenterPlayerOffset = rightBottomCenterPlayerOffset,
                leftBottomCenterPlayerOffset = leftBottomCenterPlayerOffset,
                rightBottomPlayerOffset = rightBottomPlayerOffset,
                leftBottomPlayerOffset = leftBottomPlayerOffset,
                rightTopPlayerOffset = rightTopPlayerOffset,
                leftTopPlayerOffset = leftTopPlayerOffset
            )
        }
    }
}
