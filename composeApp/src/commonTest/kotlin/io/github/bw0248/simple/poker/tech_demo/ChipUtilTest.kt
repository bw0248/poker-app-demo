package io.github.bw0248.simple.poker.tech_demo

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.assertEquals

class ChipUtilTest {

    @ParameterizedTest
    @MethodSource("numberOfChipSlots")
    fun `should calculate chip distribution with required number of chip slots`(numberOfChipSlots: Int) {
        (1..1_000_000).forEach {
            val calculatedDistribution = calculateChipDistribution(Dollar.of(it.toDouble()), numberOfChipSlots)
            assertEquals(3, calculatedDistribution.size)
        }
    }

    companion object {
        @JvmStatic
        fun numberOfChipSlots() = listOf(
            Arguments.of(1),
            Arguments.of(2),
            Arguments.of(3),
            Arguments.of(4),
        )
    }
}