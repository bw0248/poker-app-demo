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
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.bw0248.simple.poker.tech_demo.Dollar
import io.github.bw0248.simple.poker.tech_demo.Logger
import io.github.bw0248.spe.BigBlind
import io.github.bw0248.spe.card.Card
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
    //playerView: PlayerView?,
    holeCards: List<Card>,
    playerSeat: PlayerSeat,
    chipSlotsToRender: List<List<String>>,
    currentBet: Dollar?,
    currentStack: Dollar?,
    playerStatus: PlayerStatus,
    isHero: Boolean,
    roles: Set<PlayerRole>,
    //viewModel: PokerGameViewModel,
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
        //playerView?.let {
            PlayerInfoView(
                name = name,
                holeCards = holeCards,
                currentStack = currentStack,
                playerStatus = playerStatus,
                isHero = isHero,
                //viewModel = viewModel,
                //playerView = it,
                //playerSeat = playerSeat,
                //gameConfig = viewModel.gameConfig,
                playerViewDimensions = dimensions.playerViewDimensions,
            )
                Box(
                    modifier = Modifier.align(dimensions.bettingBoxDimensions.bettingBoxAlignment)
                        .size(dimensions.bettingBoxDimensions.bettingBoxSize)
                    //.background(Color.Red)
                ) {
                    if (roles.contains(PlayerRole.DEALER)) {
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
                    val chipSlotDimensions = dimensions.bettingBoxDimensions.chipSlotBoxes
                    //val calculatedChipSlots = viewModel.calculateChipsToRenderForPlayer(playerSeat)
                    val calculatedChipSlots = chipSlotsToRender
                    val chipSlotsToBeUsed = chipSlotDimensions
                        .take(calculatedChipSlots.size)
                        .sortedBy { it.drawingOrder }
                    chipSlotsToBeUsed
                        .zip(calculatedChipSlots)
                        .forEach {
                            val slotDimensions = it.first
                            val chipsInSlot = it.second
                            Box(
                                modifier = Modifier
                                    .align(slotDimensions.alignment)
                                    .offset(x = slotDimensions.chipSlotOffset.x, y = slotDimensions.chipSlotOffset.y)
                                //.border(2.dp, Color.Yellow)
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
                    if (chipSlotsToBeUsed.isNotEmpty()) {


                    //playerView.currentBet?.let {
                    //    if (playerView.currentBet != BigBlind.ZERO) {
                    val slotAlignment = chipSlotsToBeUsed.first().alignment
                    val bottomChipBettingTextRef: DpOffset =
                        chipSlotsToBeUsed
                            //chipSlotDimensions.sortedBy { it.drawingOrder }
                            .zip(calculatedChipSlots)
                            .last()
                            .let { (slotDim, chips) ->
                                val lastChipVerticalOffset =
                                    (slotDim.verticalOffsetIncrementPerChip * (chips.size - 1)) //+ (slotDim.size * 1.5f)
                                DpOffset(
                                    x = slotDim.chipSlotOffset.x,
                                    //y = slotDim.chipSlotOffset.y - lastChipVerticalOffset + (slotDim.size * 2.0f)
                                    y = slotDim.chipSlotOffset.y
                                )
                            }
                    val topChipBettingTextRef =
                        chipSlotsToBeUsed
                            .zip(calculatedChipSlots)
                            .first()
                            .let { (slotDim, chips) ->
                                val lastChipVerticalOffset =
                                    (slotDim.verticalOffsetIncrementPerChip * chips.size) + (slotDim.size * 0.5f)
                                DpOffset(
                                    x = slotDim.chipSlotOffset.x,
                                    y = slotDim.chipSlotOffset.y - lastChipVerticalOffset
                                )
                                //DpOffset(x = slotDim.chipSlotOffset.x, y = slotDim.chipSlotOffset.y)
                            }
                    val chipSlotReferenceForBettingText = when (playerSeat) {
                        PlayerSeat.ONE, PlayerSeat.TWO -> slotAlignment to topChipBettingTextRef
                        PlayerSeat.FOUR, PlayerSeat.FIVE -> Alignment.BottomCenter to DpOffset(0.dp, 0.dp)
                        PlayerSeat.THREE, PlayerSeat.SIX -> slotAlignment to bottomChipBettingTextRef
                        else -> Alignment.BottomCenter to DpOffset(0.dp, 0.dp)
                    }
                    AutoSizeText(
                        Modifier
                            .align(chipSlotReferenceForBettingText.first)
                            //.offset(x = bettingTextRef.x, y = bettingTextRef.y)
                            .offset(
                                x = chipSlotReferenceForBettingText.second.x,
                                y = chipSlotReferenceForBettingText.second.y
                            )
                            .background(
                                color = Color.Black.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(8.dp)
                            )
                        //.border(2.dp, Color.Yellow)
                        ,
                        maxFontSize = 20.sp,
                        //text = "$123_456.99",
                        text = currentBet.format()
                        //text = toDollar(viewModel.gameConfig).format()
                    )
                    }
                }
        //}
    }
}

@Composable
@Preview
fun PlayerInfoView(
    name: String,
    //viewModel: PokerGameViewModel,
    holeCards: List<Card>,
    currentStack: Dollar?,
    playerStatus: PlayerStatus,
    isHero: Boolean,
    //playerSeat: PlayerSeat,
   // playerView: PlayerView?,
    //gameConfig: GameConfig,
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
        //playerView?.let {
        Row(
            modifier = Modifier
                .weight(0.45f)
                //.background(color = Color.Green)
                .fillMaxWidth(0.8f),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.Center
        ) {
            holeCards.map { card ->
                HoleCardView(card, isHero = isHero, modifier = Modifier.weight(1f))
                //HoleCardView(Card(CardSuit.SPADES, CardValue.ACE, CardState.OPEN), modifier = Modifier.weight(1f))
                //HoleCardView(Card(CardSuit.SPADES, CardValue.ACE, CardState.HIDDEN), modifier = Modifier.weight(1f))
            }
        }
        val (borderWidth, borderColor) = if (playerStatus == PlayerStatus.NEXT_TO_ACT) {
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
                //text = playerView.currentStack.toDollar(gameConfig).format(),
                text = currentStack.format(),
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
//    }
}