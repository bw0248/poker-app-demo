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
    val chipsPerSlot = numberOfUsedChips / numberOfChipSlots
    val abc = chipDistribution
        .flatMap { entry -> (0 until entry.value).map { entry.key } }
        .sortedBy { it.amount }
        .reversed()
        .map { AVAILABLE_CHIPS[it.amount.toDouble()] ?: throw IllegalArgumentException("Chip $it not found") }
        .chunked(chipsPerSlot)

    return abc
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
