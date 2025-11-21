package io.github.bw0248.simple.poker.tech_demo.view

import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import io.github.bw0248.spe.player.PlayerSeat

data class GameDimensions(
    val tableDimensions: TableDimensions,
    val playerDimensions: Map<PlayerSeat, PlayerDimension>,
) {
    companion object {
        fun init(maxWidth: Dp, maxHeight: Dp): GameDimensions {
            val tableDimensions = TableDimensions.fromParentContainerDimensions(width = maxWidth, height = maxHeight)
            val playerDimensions = PlayerDimension.calculateFor6MaxGame(
                maxWidth = maxWidth,
                maxHeight = maxHeight,
                tableDimensions = tableDimensions
            )

            return GameDimensions(tableDimensions, playerDimensions)
        }
    }
}

data class TableDimensions(
    val relativeTableWidth: Float,
    val relativeTableHeight: Float,
    val absoluteTableWidth: Dp,
    val absoluteTableHeight: Dp,
    val cornerRadius: Dp,
    val topPadding: Dp,
    val boardCardsHorizontalPadding: Dp,
    val boardCardsTopPadding: Dp,
    val bettingLinePadding: Dp,
    val bettingLineThickness: Dp,
    val railThickness: Dp,
    val potMaxFontSize: Dp,
    val potVerticalPadding: Dp,
) {
    companion object {
        const val RELATIVE_TABLE_WIDTH: Float = 0.7f
        const val RELATIVE_TABLE_HEIGHT: Float = 0.9f

        fun fromParentContainerDimensions(width: Dp, height: Dp): TableDimensions {
            val relativeTableWidth = 0.7f
            val relativeTableHeight = 0.9f
            val absoluteTableWidth = width * relativeTableWidth
            val absoluteTableHeight = height * relativeTableHeight
            val boardCardsHorizontalPadding = width * relativeTableWidth * 0.2f
            val boardCardsTopPadding = height * relativeTableHeight * 0.05f

            val bettingLinePadding = absoluteTableWidth * 0.05f
            val bettingLineThickness = absoluteTableWidth * 0.004f
            val railThickness = absoluteTableWidth * 0.022f

            val tableRadius = height * 0.50f
            val tableTopPadding = height * 0.05f

            val potMaxFontSize = absoluteTableWidth * 0.03f
            val potVerticalPadding = absoluteTableHeight * 0.05f
            return TableDimensions(
                relativeTableWidth = RELATIVE_TABLE_WIDTH,
                relativeTableHeight = RELATIVE_TABLE_HEIGHT,
                absoluteTableWidth = absoluteTableWidth,
                absoluteTableHeight = absoluteTableHeight,
                cornerRadius = tableRadius,
                topPadding = tableTopPadding,
                boardCardsHorizontalPadding = boardCardsHorizontalPadding,
                boardCardsTopPadding = boardCardsTopPadding,
                bettingLinePadding = bettingLinePadding,
                bettingLineThickness = bettingLineThickness,
                railThickness = railThickness,
                potMaxFontSize = potMaxFontSize,
                potVerticalPadding = potVerticalPadding
            )
        }
    }
}

data class PlayerDimension(
    val seat: PlayerSeat,
    val playerBoxDimensions: PlayerBoxDimensions,
) {
    companion object {
        private const val NUM_BOTTOM_PLAYERS = 4
        private const val RELATIVE_HORIZONTAL_SPACING_BETWEEN_PLAYERS = 0.07f
        val sixMaxSeats = setOf(
            PlayerSeat.ONE, PlayerSeat.TWO, PlayerSeat.THREE, PlayerSeat.FOUR, PlayerSeat.FIVE, PlayerSeat.SIX
        )
        fun calculateFor6MaxGame(
            maxWidth: Dp,
            maxHeight: Dp,
            tableDimensions: TableDimensions
        ): Map<PlayerSeat, PlayerDimension> {
            val numBottomPlayers = NUM_BOTTOM_PLAYERS
            val absoluteHorizontalSpacingBetweenPlayers = maxWidth * RELATIVE_HORIZONTAL_SPACING_BETWEEN_PLAYERS

            val totalHorizontalSpacingBetweenBottomPlayers = absoluteHorizontalSpacingBetweenPlayers * numBottomPlayers
            val playerViewWidth = (maxWidth - totalHorizontalSpacingBetweenBottomPlayers) / numBottomPlayers
            // @TODO: calculate without magic number
            val playerViewHeight = maxHeight * 0.3f

            // @TODO: calculate without magic numbers
            val bettingBoxHorizontalExtension = maxWidth * 0.1f
            val bettingBoxVerticalExtension = maxHeight * 0.15f

            val rightBottomCenterPlayerOffset = ((playerViewWidth / 2) + (absoluteHorizontalSpacingBetweenPlayers / 2))
            val rightBottomPlayerOffset = (rightBottomCenterPlayerOffset + playerViewWidth + absoluteHorizontalSpacingBetweenPlayers) - maxWidth * 0.02f
            val rightTopPlayerOffset = rightBottomPlayerOffset + maxWidth * 0.005f

            val tableVerticalMiddle = (tableDimensions.absoluteTableHeight / 2) + tableDimensions.topPadding
            val bottomOutsidePlayerVerticalOffset = maxHeight - tableVerticalMiddle - playerViewHeight
            val topOutsidePlayerVerticalOffset = tableDimensions.topPadding

            val outsidePlayerSize =
                DpSize(width = playerViewWidth + bettingBoxHorizontalExtension, height = playerViewHeight)
            val centerPlayerSize =
                DpSize(width = playerViewWidth, height = playerViewHeight + bettingBoxVerticalExtension)

            val bottomRightCenterPlayerOffset = DpOffset(x = rightBottomCenterPlayerOffset, y = 0.dp)
            val bottomLeftCenterPlayerOffset =
                DpOffset(x = -bottomRightCenterPlayerOffset.x, y = bottomRightCenterPlayerOffset.y)

            val bottomRightOutsidePlayerOffset =
                DpOffset(x = rightBottomPlayerOffset, y = -bottomOutsidePlayerVerticalOffset)
            val bottomLeftOutsidePlayerOffset =
                DpOffset(x = -bottomRightOutsidePlayerOffset.x, y = -bottomOutsidePlayerVerticalOffset)

            val topRightOutsidePlayerOffset = DpOffset(x = rightTopPlayerOffset, y = topOutsidePlayerVerticalOffset)
            val topLeftOutsidePlayerOffset =
                DpOffset(x = -topRightOutsidePlayerOffset.x, y = topOutsidePlayerVerticalOffset)

            return sixMaxSeats.map {
                when (it) {
                    PlayerSeat.ONE -> PlayerDimension(
                        it,
                        PlayerBoxDimensions(
                            size = outsidePlayerSize,
                            alignment = Alignment.TopCenter,
                            offset = topLeftOutsidePlayerOffset,
                            contentAlignment = Alignment.CenterStart,
                            playerViewDimensions = PlayerViewDimensions(
                                DpSize(
                                    width = playerViewWidth,
                                    height = playerViewHeight
                                )
                            ),
                            bettingBoxDimensions = BettingBoxDimensions(
                                bettingBoxSize = DpSize(
                                    width = bettingBoxHorizontalExtension,
                                    height = playerViewHeight
                                ),
                                bettingBoxAlignment = Alignment.CenterEnd,
                                dealerButtonDimensions = DealerButtonDimensions.forSeat(it, maxWidth, maxHeight)
                            )
                        )
                    )
                    PlayerSeat.TWO -> PlayerDimension(
                        it,
                        PlayerBoxDimensions(
                            size = outsidePlayerSize,
                            alignment = Alignment.TopCenter,
                            offset = topRightOutsidePlayerOffset,
                            contentAlignment = Alignment.CenterEnd,
                            playerViewDimensions = PlayerViewDimensions(
                                DpSize(
                                    width = playerViewWidth,
                                    height = playerViewHeight
                                )
                            ),
                            bettingBoxDimensions = BettingBoxDimensions(
                                bettingBoxSize = DpSize(
                                    width = bettingBoxHorizontalExtension,
                                    height = playerViewHeight
                                ),
                                bettingBoxAlignment = Alignment.CenterStart,
                                dealerButtonDimensions = DealerButtonDimensions.forSeat(it, maxWidth, maxHeight)
                            )
                        )
                    )
                    PlayerSeat.THREE -> PlayerDimension(
                        it,
                        PlayerBoxDimensions(
                            size = outsidePlayerSize,
                            alignment = Alignment.BottomCenter,
                            offset = bottomRightOutsidePlayerOffset,
                            contentAlignment = Alignment.BottomEnd,
                            playerViewDimensions = PlayerViewDimensions(
                                DpSize(
                                    width = playerViewWidth,
                                    height = playerViewHeight
                                )
                            ),
                            bettingBoxDimensions = BettingBoxDimensions(
                                bettingBoxSize = DpSize(
                                    width = bettingBoxHorizontalExtension,
                                    height = playerViewHeight
                                ),
                                bettingBoxAlignment = Alignment.CenterStart,
                                dealerButtonDimensions = DealerButtonDimensions.forSeat(it, maxWidth, maxHeight)
                            )
                        )
                    )
                    PlayerSeat.FOUR -> PlayerDimension(
                        it,
                        PlayerBoxDimensions(
                            size = centerPlayerSize,
                            alignment = Alignment.BottomCenter,
                            offset = bottomRightCenterPlayerOffset,
                            contentAlignment = Alignment.BottomCenter,
                            playerViewDimensions = PlayerViewDimensions(
                                DpSize(
                                    width = playerViewWidth,
                                    height = playerViewHeight
                                )
                            ),
                            bettingBoxDimensions = BettingBoxDimensions(
                                bettingBoxSize = DpSize(width = playerViewWidth, height = bettingBoxVerticalExtension),
                                bettingBoxAlignment = Alignment.TopStart,
                                dealerButtonDimensions = DealerButtonDimensions.forSeat(it, maxWidth, maxHeight)
                            )
                        )
                    )
                    PlayerSeat.FIVE -> PlayerDimension(
                        it,
                        PlayerBoxDimensions(
                            size = centerPlayerSize,
                            alignment = Alignment.BottomCenter,
                            offset = bottomLeftCenterPlayerOffset,
                            contentAlignment = Alignment.BottomCenter,
                            playerViewDimensions = PlayerViewDimensions(
                                DpSize(
                                    width = playerViewWidth,
                                    height = playerViewHeight
                                )
                            ),
                            bettingBoxDimensions = BettingBoxDimensions(
                                bettingBoxSize = DpSize(width = playerViewWidth, height = bettingBoxVerticalExtension),
                                bettingBoxAlignment = Alignment.TopStart,
                                dealerButtonDimensions = DealerButtonDimensions.forSeat(it, maxWidth, maxHeight)
                            )
                        )
                    )
                    PlayerSeat.SIX -> PlayerDimension(
                        it,
                        PlayerBoxDimensions(
                            size = outsidePlayerSize,
                            alignment = Alignment.BottomCenter,
                            offset = bottomLeftOutsidePlayerOffset,
                            contentAlignment = Alignment.TopStart,
                            playerViewDimensions = PlayerViewDimensions(
                                DpSize(
                                    width = playerViewWidth,
                                    height = playerViewHeight
                                )
                            ),
                            bettingBoxDimensions = BettingBoxDimensions(
                                bettingBoxSize = DpSize(
                                    width = bettingBoxHorizontalExtension,
                                    height = playerViewHeight
                                ),
                                bettingBoxAlignment = Alignment.CenterEnd,
                                dealerButtonDimensions = DealerButtonDimensions.forSeat(it, maxWidth, maxHeight)
                            )
                        )
                    )
                    PlayerSeat.SEVEN -> TODO()
                    PlayerSeat.EIGHT -> TODO()
                    PlayerSeat.NINE -> TODO()
                    PlayerSeat.TEN -> TODO()
                }
            }.associateBy { it.seat }
        }
    }
}

// main outer box around player containing holecards, player info and betting box
data class PlayerBoxDimensions(
    val size: DpSize,
    val alignment: Alignment,
    val offset: DpOffset,
    // specifies how content, e.g. playerViewBox and bettingBox is laid out in player box
    val contentAlignment: Alignment,
    val playerViewDimensions: PlayerViewDimensions,
    //val dealerButtonDimensions: DealerButtonDimensions,
    val bettingBoxDimensions: BettingBoxDimensions,
)

// box around holecards and player infos
data class PlayerViewDimensions(val size: DpSize)

data class BettingBoxDimensions(
    val bettingBoxSize: DpSize,
    val bettingBoxAlignment: Alignment,
    val dealerButtonDimensions: DealerButtonDimensions,
)

data class DealerButtonDimensions(
    val size: Dp,
    val alignment: Alignment,
    val offset: DpOffset
) {
    companion object {
        fun forSeat(playerSeat: PlayerSeat, screenWidth: Dp, screenHeight: Dp): DealerButtonDimensions {
            // @TODO: size calculation should probably be unified with chip size calculation
            val size = screenWidth * 0.03f
            val topOutsidePlayerDealerButtonOffset = DpOffset(x = screenWidth * 0.01f, y = 0.dp)
            val bottomOutsidePlayerOffset = DpOffset(x = screenWidth * 0.01f, y = -(screenHeight * 0.1f))
            val centerPlayerDealerButtonOffset = DpOffset(x = screenWidth * 0.025f, y = screenHeight * 0.15f)
            return when (playerSeat) {
                PlayerSeat.ONE -> DealerButtonDimensions(
                    size = size,
                    alignment = Alignment.BottomStart,
                    offset = topOutsidePlayerDealerButtonOffset
                )
                PlayerSeat.TWO -> DealerButtonDimensions(
                    size = size,
                    alignment = Alignment.BottomEnd,
                    offset = DpOffset(x = -topOutsidePlayerDealerButtonOffset.x, y = topOutsidePlayerDealerButtonOffset.y)
                )
                PlayerSeat.THREE -> DealerButtonDimensions(
                    size = size,
                    alignment = Alignment.BottomEnd,
                    offset = DpOffset(x = -bottomOutsidePlayerOffset.x, y = bottomOutsidePlayerOffset.y)
                )
                PlayerSeat.FOUR -> DealerButtonDimensions(
                    size = size,
                    alignment = Alignment.BottomStart,
                    offset = DpOffset(x = -centerPlayerDealerButtonOffset.x, y = centerPlayerDealerButtonOffset.y)
                )
                PlayerSeat.FIVE -> DealerButtonDimensions(
                    size = size,
                    alignment = Alignment.BottomEnd,
                    offset = DpOffset(x = centerPlayerDealerButtonOffset.x, y = centerPlayerDealerButtonOffset.y)
                )
                PlayerSeat.SIX -> DealerButtonDimensions(
                    size = size,
                    alignment = Alignment.BottomStart,
                    offset = DpOffset(x = bottomOutsidePlayerOffset.x, y = bottomOutsidePlayerOffset.y)
                )
                PlayerSeat.SEVEN -> TODO()
                PlayerSeat.EIGHT -> TODO()
                PlayerSeat.NINE -> TODO()
                PlayerSeat.TEN -> TODO()
            }
        }
    }
}
