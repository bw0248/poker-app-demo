package io.github.bw0248.simple.poker.tech_demo.view

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.bw0248.simple.poker.tech_demo.Logger
import io.github.bw0248.simple.poker.tech_demo.calculateChipDistribution
import io.github.bw0248.spe.BigBlind
import io.github.bw0248.spe.PotView
import io.github.bw0248.spe.bigBlind
import io.github.bw0248.spe.card.Card
import io.github.bw0248.spe.config.GameConfig
import io.github.bw0248.spe.game.CommandResult
import io.github.bw0248.spe.game.Game
import io.github.bw0248.spe.game.GameEvent
import io.github.bw0248.spe.game.GameStatus
import io.github.bw0248.spe.game.GameView
import io.github.bw0248.spe.game.JoinCommand
import io.github.bw0248.spe.game.PlayerBet
import io.github.bw0248.spe.game.PlayerBetCommand
import io.github.bw0248.spe.game.PlayerCalled
import io.github.bw0248.spe.game.PlayerCalledCommand
import io.github.bw0248.spe.game.PlayerChecked
import io.github.bw0248.spe.game.PlayerCheckedCommand
import io.github.bw0248.spe.game.PlayerCommand
import io.github.bw0248.spe.game.PlayerEvent
import io.github.bw0248.spe.game.PlayerFolded
import io.github.bw0248.spe.game.PlayerFoldedCommand
import io.github.bw0248.spe.game.PlayerPostedBigBlind
import io.github.bw0248.spe.game.PlayerPostedSmallBlind
import io.github.bw0248.spe.game.PlayerRaiseCommand
import io.github.bw0248.spe.game.PlayerRaised
import io.github.bw0248.spe.player.PlayerSeat
import io.github.bw0248.spe.player.PlayerStatus
import io.github.bw0248.spe.player.PlayerView
import java.math.BigDecimal
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.collections.lastIndex

class PokerGameViewModel() : ViewModel() {
    private var game: Game
    // @TODO: to ensure consistency all methods inside this class should operate on the current game view instead of the current game
    //        currentGameView will be in-sync with scheduled state updates whereas game might already be advanced further
    private var currentGameView: GameView
    val gameConfig: GameConfig = GameConfig.defaultNoLimitHoldem(
        smallBlindAmountInDollar = BigDecimal.valueOf(25),
        bigBlindAmountInDollar = BigDecimal.valueOf(50)
    )
    private val _uiState = MutablePokerGameState()
    val uiState: PokerGameState = _uiState

    private val scheduler = Executors.newSingleThreadScheduledExecutor()
    private val transitionDelayMillis: Long = 500

    init {
       game = Game.initializeFromConfig(gameConfig)
           .processCommand(JoinCommand(100.bigBlind(), playerSeat = PlayerSeat.ONE))
           .updatedGame
           .processCommand(JoinCommand(100.bigBlind(), playerSeat = PlayerSeat.TWO))
           .updatedGame
           .processCommand(JoinCommand(100.bigBlind(), playerSeat = PlayerSeat.THREE))
           .updatedGame
           //.processCommand(JoinCommand(100.bigBlind(), playerSeat = PlayerSeat.FOUR))
           //.updatedGame
           //.processCommand(JoinCommand(100.bigBlind(), playerSeat = PlayerSeat.FIVE))
           //.updatedGame
           //.processCommand(JoinCommand(100.bigBlind(), playerSeat = PlayerSeat.SIX))
           //.updatedGame
        currentGameView = game.view()
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

    fun calculateChipsToRenderForPlayer(playerSeat: PlayerSeat): List<List<String>> {
        val playerView = currentGameView.playerViews[playerSeat]
        val currentBet = playerView?.currentBet
        if (currentBet == null || currentBet == BigBlind.ZERO) {
            return emptyList()
        }
        val chipSlots = calculateChipSlotsForPlayer(playerSeat)
        val calculatedChipSlots = calculateChipDistribution(
            amountToDistribute = currentBet.toDollar(gameConfig),
            numberOfChipSlots = chipSlots
        )

        return calculatedChipSlots
    }

    private fun calculateChipSlotsForPlayer(
        playerSeat: PlayerSeat,
        eventsToConsider: List<GameEvent>? = currentGameView
            .recordedHands
            .lastOrNull()
            ?.recordedBettingRounds
            ?.getOrNull(currentGameView.currentBettingRoundIndex)
            ?.recordedEvents
    ): Int {
        val eventsInCurrentBettingRound = eventsToConsider ?: return 1

        val relevantEvents = setOf(
            PlayerPostedSmallBlind::class,
            PlayerPostedBigBlind::class,
            PlayerBet::class,
            PlayerCalled::class,
            PlayerChecked::class,
            PlayerFolded::class,
            PlayerRaised::class,
        )
        val indexOfLastPlayerEvent = eventsInCurrentBettingRound
            .indexOfLast { it is PlayerEvent && it.playerSeat == playerSeat && it::class in relevantEvents }

        if (indexOfLastPlayerEvent == -1) {
            throw IllegalStateException("No event found for player in seat $playerSeat")
        }

        val relevantRecordedEvents = eventsInCurrentBettingRound.subList(0, indexOfLastPlayerEvent + 1)
        val lastPlayerAction = relevantRecordedEvents.last() as PlayerEvent
        if (lastPlayerAction.playerSeat != playerSeat) {
            throw IllegalStateException("Last event is not for player")
        }

        return when (lastPlayerAction) {
            is PlayerPostedSmallBlind -> 1
            is PlayerPostedBigBlind -> 1
            is PlayerBet -> 1
            // should match num slots of last bet/raise/blind event
            is PlayerCalled -> relevantRecordedEvents
                .indexOfLast { it is PlayerRaised || it is PlayerBet || it is PlayerPostedBigBlind || it is PlayerPostedSmallBlind }
                .let { relevantRecordedEvents.subList(0, it + 1) }
                .let { l -> calculateChipSlotsForPlayer((l.last() as PlayerEvent).playerSeat, l) }
            // should probably only happen when BB checks after SB complete
            // otherwise checking is only possible when there is no bet at all and thus this method should not be called
            is PlayerChecked -> 1
            // should match last slots of player event
            is PlayerFolded -> relevantRecordedEvents
                .subList(0, indexOfLastPlayerEvent)
                .let { calculateChipSlotsForPlayer(playerSeat, it) }

            // last bet/raise/blind event + 1 (increment happens by including current raise of player)
            is PlayerRaised -> minOf(
                4,
                relevantRecordedEvents.filter { it is PlayerRaised || it is PlayerBet || it is PlayerPostedBigBlind }.size
            )
            else -> throw IllegalStateException()
        }
    }

    private fun updateWithCommand(command: PlayerCommand) {
        Logger.info("PokerGameViewModel", "Sending command $command for player ${command.playerSeat}")
        val commandResult = game.processCommand(command)
        scheduleUiStateUpdates(commandResult)
        game = commandResult.updatedGame
        //_uiState.update(commandResult)
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
                { updateUiState(it.value) },
                totalDelay,
                TimeUnit.MILLISECONDS
            )
        }
        scheduler.schedule(
            { updateUiState(commandResult.updatedGame.view()) },
            totalDelay + transitionDelayMillis,
            TimeUnit.MILLISECONDS
        )
    }

    private fun updateUiState(gameView: GameView) {
        currentGameView = gameView
        _uiState.update(gameView)
    }
}

private class MutablePokerGameState : PokerGameState {
    override var playerViews: Map<PlayerSeat, PlayerView> by mutableStateOf(emptyMap())
    override var communityCards: List<Card> by mutableStateOf(emptyList())
    override var potView: PotView by mutableStateOf(PotView(amount = BigBlind.of(0), BigBlind.of(0), emptyMap()))
    override var currentBet: BigBlind? by mutableStateOf(BigBlind.of(0))

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
