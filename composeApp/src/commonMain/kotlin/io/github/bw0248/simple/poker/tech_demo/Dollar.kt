package io.github.bw0248.simple.poker.tech_demo

import java.math.BigDecimal
import java.math.RoundingMode

data class Dollar private constructor(val amount: BigDecimal) {
    fun subtract(amount: Dollar): Dollar = Dollar(this.amount.subtract(amount.amount))

    companion object {
        val ZERO = Dollar(BigDecimal(0).setScale(2, RoundingMode.HALF_UP))
        fun of(amount: Double): Dollar {
            return Dollar(BigDecimal(amount).setScale(2, RoundingMode.HALF_UP))
        }
        fun of(amount: BigDecimal) = Dollar(amount.setScale(2, RoundingMode.HALF_UP))
    }
}