package io.github.bw0248.simple.poker.tech_demo.view

import androidx.lifecycle.ViewModel
import io.github.bw0248.simple.poker.tech_demo.Logger
import io.github.bw0248.simple.poker.tech_demo.SimpleBot
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
import io.github.bw0248.spe.game.RecordedHand
import io.github.bw0248.spe.player.PlayerSeat
import io.github.bw0248.spe.player.PlayerStatus
import io.github.bw0248.spe.player.PlayerView
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.math.BigDecimal
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class PokerGameViewModel() : ViewModel() {
    private var game: Game
    val gameConfig: GameConfig = GameConfig.defaultNoLimitHoldem(
        smallBlindAmountInDollar = BigDecimal.valueOf(25),
        bigBlindAmountInDollar = BigDecimal.valueOf(50)
    )
    val heroSeat = PlayerSeat.SIX
    private val _uiState = MutableStateFlow(PokerGameState(
        gameConfig = gameConfig,
        communityCards = emptyList(),
        playerViews = emptyMap(),
        potView = PotView(BigBlind.ZERO, BigBlind.ZERO, emptyMap()),
        currentBet = null,
        recordedHands = emptyList()
    ))
    val uiState = _uiState.asStateFlow()

    private val scheduler = Executors.newSingleThreadScheduledExecutor()
    private val transitionDelayMillis: Long = 500

    private val bot = SimpleBot(PlayerSeat.THREE, ::sendPlayerCommand)

    init {
       game = Game.initializeFromConfig(gameConfig)
           .processCommand(JoinCommand(100.bigBlind(), playerSeat = PlayerSeat.SIX))
           .updatedGame
           .processCommand(JoinCommand(100.bigBlind(), playerSeat = PlayerSeat.THREE))
           .updatedGame
           //.processCommand(JoinCommand(100.bigBlind(), playerSeat = PlayerSeat.ONE))
           //.updatedGame
           //.processCommand(JoinCommand(100.bigBlind(), playerSeat = PlayerSeat.TWO))
           //.updatedGame
           //.processCommand(JoinCommand(100.bigBlind(), playerSeat = PlayerSeat.FOUR))
           //.updatedGame
           //.processCommand(JoinCommand(100.bigBlind(), playerSeat = PlayerSeat.FIVE))
           //.updatedGame
        updateUiState(game.view(), true)
    }

    fun sendPlayerCommand(event: PlayerCommand) {
        updateWithCommand(event)
    }

    fun joinGame(playerSeat: PlayerSeat, buyIn: BigBlind) = updateWithCommand(JoinCommand(buyIn, playerSeat))
    fun fold(playerSeat: PlayerSeat) = updateWithCommand(PlayerFoldedCommand(playerSeat))
    fun call(playerSeat: PlayerSeat) {
        updateWithCommand(PlayerCalledCommand(playerSeat))
    }
    fun check(playerSeat: PlayerSeat) = updateWithCommand(PlayerCheckedCommand(playerSeat))
    fun bet(playerSeat: PlayerSeat, betAmount: BigBlind) = updateWithCommand(PlayerBetCommand(betAmount, playerSeat))
    fun raise(playerSeat: PlayerSeat, amount: BigBlind) {
        updateWithCommand(PlayerRaiseCommand(amount, playerSeat))
    }

    // @TODO: move to GameState class
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

    // @TODO: move to GameState class
    fun calculatePotRaise(factor: Double): BigBlind {
        val currentBet = game.view().currentBet ?: BigBlind.ZERO
        return BigBlind.of(factor)
            .multiply(currentBet.plus(game.view().pot.amountIncludingPlayerBets))
            .plus(currentBet)
    }

    // @TODO: move to GameState class
    fun allowedToCheck(playerSeat: PlayerSeat): Boolean {
        return game.view().currentBet == null ||
            game.view().currentBet == BigBlind.ZERO ||
            game.view().currentBet == game.view().playerViews[playerSeat]?.currentBet
    }

    // @TODO: move to GameState class
    fun allowedToRaise(playerSeat: PlayerSeat): Boolean {
        return game.view().currentBet != null && game.view().currentBet != BigBlind.ZERO
    }

    private fun updateWithCommand(command: PlayerCommand) {
        Logger.info("PokerGameViewModel", "Sending command $command for player ${command.playerSeat}")
        val commandResult = game.processCommand(command)
        scheduleUiStateUpdates(commandResult)
        game = commandResult.updatedGame
    }

    private fun scheduleUiStateUpdates(commandResult: CommandResult) {
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
                { updateUiState(it.value, false) },
                totalDelay,
                TimeUnit.MILLISECONDS
            )
        }
        scheduler.schedule(
            { updateUiState(commandResult.updatedGame.view(), true) },
            totalDelay + transitionDelayMillis,
            TimeUnit.MILLISECONDS
        )
    }

    private fun updateUiState(gameView: GameView, notifyConsumers: Boolean) {
        _uiState.update { PokerGameState.fromGameView(gameView) }
        if (notifyConsumers) {
            bot.receiveGameViewUpdates(gameView)
        }
    }
}

data class PokerGameState(
    val gameConfig: GameConfig,
    val communityCards: List<Card>,
    val playerViews: Map<PlayerSeat, PlayerView>,
    val potView: PotView,
    val currentBet: BigBlind?,
    val recordedHands: List<RecordedHand>
) {

    fun getActivePlayer(): Map.Entry<PlayerSeat, PlayerView>? {
        return playerViews.entries.firstOrNull { it.value.playerStatus == PlayerStatus.NEXT_TO_ACT }
    }

    companion object {
        fun fromGameView(gameView: GameView): PokerGameState {
            Logger.info("PokerGameViewModel", "Updating GameState")
            Logger.info("PokerGameViewModel", "Players: ${gameView.playerViews}")
            return PokerGameState(
                gameConfig = gameView.config,
                communityCards = gameView.communityCards,
                playerViews = gameView.playerViews,
                potView = gameView.pot,
                currentBet = gameView.currentBet,
                recordedHands = gameView.recordedHands
            )
        }
    }
}
