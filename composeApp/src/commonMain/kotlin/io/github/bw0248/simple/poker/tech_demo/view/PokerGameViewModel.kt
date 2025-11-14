package io.github.bw0248.simple.poker.tech_demo.view

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import io.github.bw0248.spe.BigBlind
import io.github.bw0248.spe.PotView
import io.github.bw0248.spe.bigBlind
import io.github.bw0248.spe.card.Card
import io.github.bw0248.spe.config.GameConfig
import io.github.bw0248.spe.game.Game
import io.github.bw0248.spe.game.GameView
import io.github.bw0248.spe.game.JoinCommand
import io.github.bw0248.spe.player.PlayerSeat
import io.github.bw0248.spe.player.PlayerView
import kotlinx.coroutines.flow.StateFlow

class PokerGameViewModel() : ViewModel() {
    private var game: Game// = Game.initializeFromConfig(GameConfig.defaultNoLimitHoldem())
    //private val _uiState = mutableStateOf(PokerGameState.init())
    //var pokerGameState by mutableStateOf(PokerGameState(emptyList()))
    private val _uiState = MutablePokerGameState()
    val uiState: PokerGameState = _uiState
    init {
       game = Game.initializeFromConfig(GameConfig.defaultNoLimitHoldem())
           .processCommand(JoinCommand(100.bigBlind(), playerSeat = PlayerSeat.TWO))
           .updatedGame
           .processCommand(JoinCommand(100.bigBlind(), PlayerSeat.ONE))
           .updatedGame
        _uiState.update(game.view())
    }

    fun joinGame() {
        val res = game.processCommand(JoinCommand(100.bigBlind(), PlayerSeat.ONE))
        game = res.updatedGame
        _uiState.update(game.view())
    }
}

private class MutablePokerGameState : PokerGameState {
    override var playerViews: Map<PlayerSeat, PlayerView> by mutableStateOf(emptyMap())
    override var communityCards: List<Card> by mutableStateOf(emptyList())
    override var potView: PotView by mutableStateOf(PotView(amount = BigBlind.of(0), BigBlind.of(0), emptyMap()))

    fun update(gameView: GameView) {
        playerViews = gameView.playerViews
        communityCards = gameView.communityCards
        potView = gameView.pot
    }
}

interface PokerGameState {
    val communityCards: List<Card>
    val playerViews: Map<PlayerSeat, PlayerView>
    val potView: PotView
}
