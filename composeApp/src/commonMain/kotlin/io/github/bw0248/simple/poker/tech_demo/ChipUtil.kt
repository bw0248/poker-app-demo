package io.github.bw0248.simple.poker.tech_demo

import io.github.bw0248.simple.poker.tech_demo.view.PokerGameViewModel
import io.github.bw0248.simple.poker.tech_demo.view.toDollar
import io.github.bw0248.spe.BigBlind
import io.github.bw0248.spe.game.GameEvent
import io.github.bw0248.spe.game.PlayerBet
import io.github.bw0248.spe.game.PlayerCalled
import io.github.bw0248.spe.game.PlayerChecked
import io.github.bw0248.spe.game.PlayerEvent
import io.github.bw0248.spe.game.PlayerFolded
import io.github.bw0248.spe.game.PlayerPostedBigBlind
import io.github.bw0248.spe.game.PlayerPostedSmallBlind
import io.github.bw0248.spe.game.PlayerRaised
import io.github.bw0248.spe.player.PlayerSeat
import java.math.BigDecimal

val AVAILABLE_CHIPS: Map<Double, String> = mapOf(
    0.01 to "_1c",
    0.10 to "_10c",
    0.25 to "_25c",
    1.0 to "_1",
    5.0 to "_5",
    25.0 to "_25",
    100.0 to "_100",
    500.0 to "_500",
    1_000.0 to "_1000",
    10_000.0 to "_10000"
)

fun calculateChipDistribution(amountToDistribute: Dollar, numberOfChipSlots: Int): List<List<String>> {
    val sortedAvailableChipValues: List<Double> = AVAILABLE_CHIPS.keys.toList().sorted()
    var remainingAmount = amountToDistribute
    var currentChipIndex = AVAILABLE_CHIPS.size - 1
    val chipDistribution = mutableMapOf<Dollar, Int>()

    while (remainingAmount.amount > BigDecimal.ZERO) {
        val currentChipValue = Dollar.of(sortedAvailableChipValues[currentChipIndex])
        if (canUseChip(remainingAmount, currentChipValue, chipDistribution, numberOfChipSlots)) {
            chipDistribution.merge(currentChipValue, 1, Integer::sum)
            remainingAmount = remainingAmount.subtract(currentChipValue)
        } else {
            currentChipIndex--
        }
    }

    val numberOfUsedChips = chipDistribution.values.sum()
    val minChipsPerSlot = numberOfUsedChips / numberOfChipSlots

    // split chips distribution into 2 parts: First part are chips that can evenly be distributed among required chip slots
    // second part is the remainder which is distributed over result afterwards
    val (minDistributed, remainderToDistribute) = chipDistribution
        .flatMap { entry -> (0 until entry.value).map { entry.key } }
        .sortedBy { it.amount }
        .reversed()
        .map { AVAILABLE_CHIPS[it.amount.toDouble()] ?: throw IllegalArgumentException("Chip $it not found") }
        .chunked(minChipsPerSlot)
        .let { it.take(numberOfChipSlots) to it.drop(numberOfChipSlots) }

    if (remainderToDistribute.size >= numberOfChipSlots) {
        Logger.error(
            "ChipUtil", "Remaining chips to distribute is unexpectedly bigger than chip slots to use"
        )
    }

    val result = minDistributed.withIndex().associate { it.index to it.value }.toMutableMap()
    // distribute remainder over result
    val b  = remainderToDistribute.withIndex().associate { it.index to it.value }
    b.forEach { result.merge(it.key, it.value) { e1, e2 -> e1.plus(e2) } }

    return result.map { it.value }
}

private fun canUseChip(
    amountStillToDistribute: Dollar,
    currentChipValue: Dollar,
    currentChipDistribution: Map<Dollar, Int>,
    numberOfRequiredChipSlots: Int
): Boolean {
    if (currentChipValue.amount > amountStillToDistribute.amount) {
        return false
    }

    val numChipsAlreadyUsed = currentChipDistribution.values.sum()

    // subtract 1 to account for current chip
    val stillNeededChips = numberOfRequiredChipSlots - numChipsAlreadyUsed - 1
    if (stillNeededChips <= 0) {
        // no need to check remainder if required chips amount is already fulfilled
        return true
    }

    // need at least one cent to distribute for each remaining required slot
    val remainder = amountStillToDistribute.subtract(currentChipValue)
    val minimumRequiredRemainder = BigDecimal(stillNeededChips) * BigDecimal(AVAILABLE_CHIPS.keys.minOf { it })
    return remainder.amount >= minimumRequiredRemainder
}

fun calculateChipsToRenderForBet(
    playerSeat: PlayerSeat,
    currentBet: Dollar?,
    playerEventsInBettingRound: List<GameEvent>
): List<List<String>> {
    if (currentBet == null || currentBet == Dollar.ZERO) {
        return emptyList()
    }

    val chipSlots = calculateChipSlotsForPlayer(playerSeat, playerEventsInBettingRound)
    val calculatedChipSlots = calculateChipDistribution(
        amountToDistribute = currentBet,
        numberOfChipSlots = chipSlots
    )

    return calculatedChipSlots
}

fun calculateChipSlotsForPlayer(
    playerSeat: PlayerSeat,
    eventsToConsider: List<GameEvent>?
        //pokerGameViewModel.currentGameView
        //.recordedHands
        //.lastOrNull()
        //?.recordedBettingRounds
        //?.getOrNull(pokerGameViewModel.currentGameView.currentBettingRoundIndex)
        //?.recordedEvents
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