package io.github.bw0248.simple.poker.tech_demo

import io.github.bw0248.spe.game.GameView
import io.github.bw0248.spe.game.PlayerCommand
import io.github.bw0248.spe.game.PlayerFoldedCommand
import io.github.bw0248.spe.player.PlayerSeat
import io.github.bw0248.spe.player.PlayerStatus

interface Bot {
    fun gameViewUpdateCallback(): (view: GameView) -> Unit
}

class SimpleBot(
    val playerSeat: PlayerSeat,
    val sendActionCommandCallback: (command: PlayerCommand) -> Unit
) : Bot {
    override fun gameViewUpdateCallback(): (view: GameView) -> Unit {
        return ::receiveGameViewUpdates
    }

    fun receiveGameViewUpdates(view: GameView) {
        Logger.info("SimpleBot", "Received GameView")
        if (view.playerViews[playerSeat]!!.playerStatus == PlayerStatus.NEXT_TO_ACT) {
            Logger.info("SimpleBot", "Bot is next to act")
            sendActionCommand()
        }
    }

    fun sendActionCommand() {
        // @TODO: should be inside coroutine to prevent UI locking
        Logger.info("SimpleBot", "Sending action command")
        Thread.sleep(1_000)
        sendActionCommandCallback.invoke(PlayerFoldedCommand(playerSeat))
    }
}