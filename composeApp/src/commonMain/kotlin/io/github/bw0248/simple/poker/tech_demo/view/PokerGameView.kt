package io.github.bw0248.simple.poker.tech_demo.view

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.TopCenter
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.bw0248.simple.poker.tech_demo.Logger
import io.github.bw0248.simple.poker.tech_demo.calculateChipsToRenderForBet
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun PokerGameView(viewModel: PokerGameViewModel = viewModel()) {
    val gameState by viewModel.uiState.collectAsState()
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

            TableView(
                potSize = gameState.potView.amount.toDollar(gameState.gameConfig),
                potSizeIncludingPlayerBets = gameState.potView.amount.toDollar(gameState.gameConfig),
                communityCards = gameState.communityCards,
                gameDimensions = gameDimensions,
                modifier = Modifier.align(TopCenter),
            )

            //Logger.info("PokerGameView", gameDimensions.tableDimensions.toString())
            //Logger.info("PokerGameView", gameDimensions.playerDimensions.toString())

            gameDimensions.playerDimensions.values.forEach { (seat, dimensions) ->
                val currentPlayerView = gameState.playerViews[seat]
                //val currentPlayerView = viewModel.uiState.playerViews[seat]?.copy()
                currentPlayerView?.let {
                    val currentBet = currentPlayerView.currentBet?.toDollar(viewModel.gameConfig)
                    //val eventsInCurrentBettingRound = viewModel.eventsInCurrentBettingRound()
                    val eventsInCurrentBettingRound = gameState.recordedHands
                        .lastOrNull()
                        ?.recordedBettingRounds
                        ?.lastOrNull()
                        ?.recordedEvents
                        ?: emptyList()
                    PlayerBox(
                        name = seat.name,
                        holeCards = currentPlayerView.holeCards,
                        playerSeat = seat,
                        chipSlotsToRender = calculateChipsToRenderForBet(
                            playerSeat = seat,
                            currentBet = currentBet,
                            playerEventsInBettingRound = eventsInCurrentBettingRound
                        ),
                        currentBet = currentBet,
                        currentStack = currentPlayerView.currentStack.toDollar(viewModel.gameConfig),
                        playerStatus = currentPlayerView.playerStatus,
                        isHero = seat == viewModel.heroSeat,
                        roles = currentPlayerView.roles,
                        dimensions = dimensions,
                        modifier = Modifier
                            .align(dimensions.alignment)
                            .offset(x = dimensions.offset.x, y = dimensions.offset.y)
                    )
                }
            }
        }
        ActionBar(
            gameState = gameState,
            activePlayer = gameState.getActivePlayer(),
            currentBet = gameState.currentBet,
            viewModel = viewModel,
            modifier = Modifier.weight(0.15f)
        )
    }
}
