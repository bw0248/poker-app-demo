package io.github.bw0248.simple.poker.tech_demo.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.bw0248.simple.poker.tech_demo.calculateChipDistribution
import io.github.bw0248.spe.BigBlind
import io.github.bw0248.spe.config.GameConfig
import io.github.bw0248.spe.player.PlayerRole
import io.github.bw0248.spe.player.PlayerSeat
import io.github.bw0248.spe.player.PlayerStatus
import io.github.bw0248.spe.player.PlayerView
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import tech_demo.composeapp.generated.resources.Res
import tech_demo.composeapp.generated.resources.allDrawableResources

@Composable
fun PlayerBox(
    name: String,
    playerView: PlayerView?,
    playerSeat: PlayerSeat,
    viewModel: PokerGameViewModel,
    dimensions: PlayerBoxDimensions,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(dimensions.size)
            .background(Color.Transparent)
            //.border(2.dp, Color.Yellow),
        ,
        contentAlignment = dimensions.contentAlignment
    ) {
        playerView?.let {
            PlayerView(
                name = name,
                playerView = it,
                gameConfig = viewModel.gameConfig,
                playerViewDimensions = dimensions.playerViewDimensions,
            )
            Box(
                modifier = Modifier.align(dimensions.bettingBoxDimensions.bettingBoxAlignment)
                    .size(dimensions.bettingBoxDimensions.bettingBoxSize)
                //.background(Color.Red)
            ) {
                if (it.roles.contains(PlayerRole.DEALER)) {
                    Image(
                        painter = painterResource(Res.allDrawableResources["dealer_flat"]!!),
                        contentDescription = "Dealer Button",
                        modifier = Modifier
                            .size(dimensions.bettingBoxDimensions.dealerButtonDimensions.size)
                            .align(dimensions.bettingBoxDimensions.dealerButtonDimensions.alignment)
                            .offset(
                                x = dimensions.bettingBoxDimensions.dealerButtonDimensions.offset.x,
                                y = dimensions.bettingBoxDimensions.dealerButtonDimensions.offset.y
                            ),
                        contentScale = ContentScale.FillBounds
                    )
                }
                it.currentBet?.let {
                    if (it > BigBlind.ZERO) {
                        val numChipSlots = viewModel.calculateChipSlotsForPlayer(playerSeat)
                        val chipSlotDimensions = dimensions.bettingBoxDimensions.chipSlotBoxes
                        val calculatedChipSlots = calculateChipDistribution(
                            amountToDistribute = it.toDollar(viewModel.gameConfig),
                            numberOfChipSlots = numChipSlots
                        )
                        chipSlotDimensions
                            .take(numChipSlots)
                            .sortedBy { it.drawingOrder }
                            .zip(calculatedChipSlots)
                            .forEach {
                                val slotDimensions = it.first
                                val chipsInSlot = it.second
                                Box(
                                    modifier = Modifier
                                        .align(slotDimensions.alignment)
                                        .offset(x = slotDimensions.chipSlotOffset.x, y = slotDimensions.chipSlotOffset.y)
                                    //.background(Color.Red),
                                    ,
                                    contentAlignment = slotDimensions.alignment//Alignment.BottomCenter
                                ) {
                                    chipsInSlot.forEachIndexed { index, chip ->
                                        Image(
                                            painter = painterResource(Res.allDrawableResources[chip]!!),
                                            contentDescription = "Chips",
                                            modifier = Modifier
                                                .size(slotDimensions.size)
                                                .offset(y = slotDimensions.chipSlotOffset.y - (slotDimensions.verticalOffsetIncrementPerChip * index)),
                                            contentScale = ContentScale.FillBounds
                                        )
                                    }
                                }
                            }
                    }
                }
            }
        }
    }
}

@Composable
@Preview
fun PlayerView(
    name: String,
    playerView: PlayerView?,
    gameConfig: GameConfig,
    playerViewDimensions: PlayerViewDimensions,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .height(playerViewDimensions.size.height)
            .width(playerViewDimensions.size.width)
        //.background(color = Color.Red)
        ,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        playerView?.let {
            Row(
                modifier = Modifier
                    .weight(0.45f)
                    //.background(color = Color.Green)
                    .fillMaxWidth(0.8f),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Center
            ) {
                it.holeCards.map { card ->
                    HoleCardView(card, modifier = Modifier.weight(1f))
                    //HoleCardView(Card(CardSuit.SPADES, CardValue.ACE, CardState.OPEN), modifier = Modifier.weight(1f))
                    //HoleCardView(Card(CardSuit.SPADES, CardValue.ACE, CardState.HIDDEN), modifier = Modifier.weight(1f))
                }
            }
            val (borderWidth, borderColor) = if (playerView.playerStatus == PlayerStatus.NEXT_TO_ACT) {
                4.dp to Color.Red
            } else {
                4.dp to Color.DarkGray
            }
            val cornerShape = RoundedCornerShape(16.dp)
            Column(
                modifier = Modifier
                    .weight(0.55f)
                    .background(color = Color.Black, shape = cornerShape)
                    .border(width = borderWidth, color = borderColor, shape = cornerShape),
                verticalArrangement = Arrangement.Center
            ) {
                val maxFontSize = 32.sp
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                    //.background(color = Color.LightGray)
                    ,
                    text = name,
                    maxLines = 1,
                    softWrap = false,
                    autoSize = TextAutoSize.StepBased(minFontSize = 6.sp, maxFontSize),
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                    //.padding(2.dp)
                    //.background(color = Color.Cyan)
                    ,
                    //text = "$222.444.99",
                    text = playerView.currentStack.toDollar(gameConfig).format(),
                    maxLines = 1,
                    softWrap = false,
                    autoSize = TextAutoSize.StepBased(minFontSize = 6.sp, maxFontSize),
                    //style = MaterialTheme.typography.displaySmall,
                    //fontSize = 16.nonScaledSp,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}