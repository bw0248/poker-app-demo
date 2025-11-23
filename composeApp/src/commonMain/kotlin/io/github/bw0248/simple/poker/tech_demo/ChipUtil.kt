package io.github.bw0248.simple.poker.tech_demo

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
