package io.github.bw0248.simple.poker.tech_demo.view

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.TopCenter
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.bw0248.simple.poker.tech_demo.Logger
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
            val gameDimensions = GameDimensions.init(
                maxWidth = boxWithConstraintsScope.maxWidth,
                maxHeight = boxWithConstraintsScope.maxHeight
            )

            TableView(viewModel, gameDimensions, Modifier.align(TopCenter))

            Logger.info("PokerGameView", gameDimensions.tableDimensions.toString())
            Logger.info("PokerGameView", gameDimensions.playerDimensions.toString())

            gameDimensions.playerDimensions.values.forEach {
                PlayerBox(
                    name = it.seat.name,
                    playerView = viewModel.uiState.playerViews[it.seat],
                    playerSeat = it.seat,
                    viewModel = viewModel,
                    dimensions = it.playerBoxDimensions,
                    modifier = Modifier
                        .align(it.playerBoxDimensions.alignment)
                        .offset(x = it.playerBoxDimensions.offset.x, y = it.playerBoxDimensions.offset.y)
                )
            }
        }
        ActionBar(viewModel, modifier = Modifier.weight(0.15f))
    }
}
