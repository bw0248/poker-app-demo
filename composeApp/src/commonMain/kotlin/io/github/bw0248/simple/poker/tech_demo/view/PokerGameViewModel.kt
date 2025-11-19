package io.github.bw0248.simple.poker.tech_demo.view

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import io.github.bw0248.simple.poker.tech_demo.Logger
import io.github.bw0248.spe.BigBlind
import io.github.bw0248.spe.PotView
import io.github.bw0248.spe.bigBlind
import io.github.bw0248.spe.card.Card
import io.github.bw0248.spe.config.GameConfig
import io.github.bw0248.spe.game.CommandResult
import io.github.bw0248.spe.game.Game
import io.github.bw0248.spe.game.GameStatus
import io.github.bw0248.spe.game.GameView
import io.github.bw0248.spe.game.JoinCommand
import io.github.bw0248.spe.game.PlayerBet
import io.github.bw0248.spe.game.PlayerBetCommand
import io.github.bw0248.spe.game.PlayerCalledCommand
import io.github.bw0248.spe.game.PlayerCheckedCommand
import io.github.bw0248.spe.game.PlayerCommand
import io.github.bw0248.spe.game.PlayerFoldedCommand
import io.github.bw0248.spe.game.PlayerPostedBigBlind
import io.github.bw0248.spe.game.PlayerRaiseCommand
import io.github.bw0248.spe.game.PlayerRaised
import io.github.bw0248.spe.player.PlayerSeat
import io.github.bw0248.spe.player.PlayerStatus
import io.github.bw0248.spe.player.PlayerView
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class PokerGameViewModel() : ViewModel() {
    private var game: Game
    private val _uiState = MutablePokerGameState()
    val uiState: PokerGameState = _uiState
    init {
       game = Game.initializeFromConfig(GameConfig.defaultNoLimitHoldem())
           .processCommand(JoinCommand(100.bigBlind(), playerSeat = PlayerSeat.ONE))
           .updatedGame
           .processCommand(JoinCommand(100.bigBlind(), playerSeat = PlayerSeat.TWO))
           .updatedGame
           .processCommand(JoinCommand(100.bigBlind(), playerSeat = PlayerSeat.THREE))
           .updatedGame
           .processCommand(JoinCommand(100.bigBlind(), playerSeat = PlayerSeat.FOUR))
           .updatedGame
           .processCommand(JoinCommand(100.bigBlind(), playerSeat = PlayerSeat.FIVE))
           .updatedGame
           .processCommand(JoinCommand(100.bigBlind(), playerSeat = PlayerSeat.SIX))
           .updatedGame
        _uiState.update(game.view())
    }

    fun getActivePlayer(): Map.Entry<PlayerSeat, PlayerView>? {
        return _uiState.playerViews.entries.firstOrNull { it.value.playerStatus == PlayerStatus.NEXT_TO_ACT }
    }

    fun joinGame(playerSeat: PlayerSeat, buyIn: BigBlind) = updateWithCommand(JoinCommand(buyIn, playerSeat))
    fun fold(playerSeat: PlayerSeat) = updateWithCommand(PlayerFoldedCommand(playerSeat))
    fun call(playerSeat: PlayerSeat) = updateWithCommand(PlayerCalledCommand(playerSeat))
    fun check(playerSeat: PlayerSeat) = updateWithCommand(PlayerCheckedCommand(playerSeat))
    fun bet(playerSeat: PlayerSeat, betAmount: BigBlind) = updateWithCommand(PlayerBetCommand(betAmount, playerSeat))
    fun raise(playerSeat: PlayerSeat, amount: BigBlind) {
        updateWithCommand(PlayerRaiseCommand(amount, playerSeat))
    }

    fun calculateMinRaise(): BigBlind {
        return if (game.view().currentBet == null || game.view().currentBet == BigBlind.ZERO) {
            game.view().config.deadMoneyConfig.bigBlindAmount
        } else {
            game.view().recordedHands.last().recordedBettingRounds.last().recordedEvents
                .findLast { it is PlayerRaised || it is PlayerBet || it is PlayerPostedBigBlind }
                .let {
                    when (it) {
                        is PlayerRaised -> it.betIncrementRelativeToPreviousBet.plus(game.view().currentBet)
                        is PlayerBet -> it.amount.multiply(2)
                        is PlayerPostedBigBlind -> game.view().config.deadMoneyConfig.bigBlindAmount.multiply(2)
                        else -> throw IllegalStateException("current bet is not null but no bet or raise event recorded")
                    }
                }
        }
    }

    fun calculatePotRaise(factor: Double): BigBlind {
        val currentBet = game.view().currentBet ?: BigBlind.ZERO
        return BigBlind.of(factor)
            .multiply(currentBet.plus(game.view().pot.amountIncludingPlayerBets))
            .plus(currentBet)
    }

    fun allowedToCheck(playerSeat: PlayerSeat): Boolean {
        return game.view().currentBet == null ||
            game.view().currentBet == BigBlind.ZERO ||
            game.view().currentBet == game.view().playerViews[playerSeat]?.currentBet
    }

    fun allowedToRaise(playerSeat: PlayerSeat): Boolean {
        return game.view().currentBet != null && game.view().currentBet != BigBlind.ZERO
    }

    private fun updateWithCommand(command: PlayerCommand) {
        Logger.info("PokerGameViewModel", "Sending command $command for player ${command.playerSeat}")
        val commandResult = game.processCommand(command)
        game = commandResult.updatedGame
        _uiState.update(commandResult)
    }
}

private class MutablePokerGameState : PokerGameState {
    override var playerViews: Map<PlayerSeat, PlayerView> by mutableStateOf(emptyMap())
    override var communityCards: List<Card> by mutableStateOf(emptyList())
    override var potView: PotView by mutableStateOf(PotView(amount = BigBlind.of(0), BigBlind.of(0), emptyMap()))
    override var currentBet: BigBlind? by mutableStateOf(BigBlind.of(0))

    private val scheduler = Executors.newSingleThreadScheduledExecutor()
    private val transitionDelayMillis: Long = 500

    fun update(commandResult: CommandResult) {
        var totalDelay = 0L
        commandResult.recordedGameViewSnapshots.withIndex().forEach {

            val delay = if (it.value.gameStatus == GameStatus.AFTER_HAND) {
                1_000
            } else {
                transitionDelayMillis
            }
            totalDelay += delay
            Logger.info("PokerGameViewModel", "Scheduling ${it.value.gameStatus} game state with delay of ${totalDelay}")
                scheduler.schedule(
                { update(it.value) },
                totalDelay,
                TimeUnit.MILLISECONDS
            )
        }
        scheduler.schedule(
            { update(commandResult.updatedGame.view()) },
            totalDelay + transitionDelayMillis,
            TimeUnit.MILLISECONDS
        )
    }

    fun update(gameView: GameView) {
        Logger.info("PokerGameViewModel", "Updating GameState")
        Logger.info("PokerGameViewModel", "Players: ${gameView.playerViews}")
        Logger.info(
            "PokerGameViewModel",
            "Game: ${gameView.gameStatus}, ${gameView.communityCards}, ${gameView.pot}, ${gameView.currentBettingRoundIndex}, ${gameView.currentBet}"
        )
        playerViews = gameView.playerViews
        communityCards = gameView.communityCards
        potView = gameView.pot
        currentBet = gameView.currentBet
        //currentBet = BigBlind.of(694242.99)
    }
}

interface PokerGameState {
    val communityCards: List<Card>
    val playerViews: Map<PlayerSeat, PlayerView>
    val potView: PotView
    val currentBet: BigBlind?
}
