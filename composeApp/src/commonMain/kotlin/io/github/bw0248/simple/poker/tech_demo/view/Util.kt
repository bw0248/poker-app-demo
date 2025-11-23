package io.github.bw0248.simple.poker.tech_demo.view

import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import io.github.bw0248.simple.poker.tech_demo.Dollar
import io.github.bw0248.spe.BigBlind
import io.github.bw0248.spe.config.GameConfig
import java.util.Locale

@Composable
fun AutoSizeText(
    modifier: Modifier = Modifier,
    text: String,
    maxFontSize: TextUnit,
    minFontSize: TextUnit = 8.sp,
    color: Color = Color.White
) {
    Text(
        modifier = modifier,
        text = text,
        maxLines = 1,
        softWrap = false,
        autoSize = TextAutoSize.StepBased(minFontSize = minFontSize, maxFontSize),
        color = color
    )
}

@Composable
fun AmountText(
    modifier: Modifier = Modifier,
    prefixText: String,
    amount: Dollar?,
    fontSize: TextUnit = 18.sp,
    color: Color = Color.White,
) {
    val amountText = amount?.format() ?: ""
    Text(
        modifier = modifier,
        text = "$prefixText $amountText",
        maxLines = 1,
        softWrap = false,
        autoSize = TextAutoSize.StepBased(minFontSize = 8.sp, fontSize),
        style = MaterialTheme.typography.displaySmall,
        color = color
    )
}

val TextUnit.nonScaledSp
    @Composable
    get() = (this.value / LocalDensity.current.fontScale).sp

fun BigBlind?.format(): String {
    return String.format(Locale.US, "$%,.2f", this?.amount ?: BigBlind.of(0).amount)
}

fun Dollar?.format(): String {
    return String.format(Locale.US, "$%,.2f", this?.amount ?: Dollar.ZERO.amount)
}

fun BigBlind.toDollar(gameConfig: GameConfig): Dollar = Dollar
    .of(this.amount.multiply(gameConfig.deadMoneyConfig.bigBlindAmountInDollar))