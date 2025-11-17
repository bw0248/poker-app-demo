package io.github.bw0248.simple.poker.tech_demo.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import io.github.bw0248.spe.card.Card
import io.github.bw0248.spe.card.CardState
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import tech_demo.composeapp.generated.resources.Res
import tech_demo.composeapp.generated.resources.allDrawableResources


@Composable
fun HoleCardView(card: Card, modifier: Modifier = Modifier) {
    androidx.compose.material3.Card(
        modifier = modifier
            .aspectRatio(1.33f),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RectangleShape,
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Image(
            painter = painterResource(resolveHoleCardResource(card, card.cardState == CardState.HIDDEN)),
            contentDescription = "Playing Card",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
    }
}

@Composable
fun BoardCardView(card: Card?, modifier: Modifier = Modifier) {
    androidx.compose.material3.Card(
        modifier = modifier.aspectRatio(0.666f),
        shape = MaterialTheme.shapes.small,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        card?.let {
            Image(
                painter = painterResource(resolveBoardCardResource(card)),
                contentDescription = "Playing Card",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds
            )
        } ?: Box(
            modifier = Modifier
                .weight(1f)
                .aspectRatio(0.666f)
                .background(color = Color.Red)
            //.border(2.dp, color = Color.Green)
        ) {}
    }
}

private fun resolveHoleCardResource(card: Card, isHidden: Boolean): DrawableResource {
    val path = if (isHidden) "cb_half" else "${cardToPath(card)}_half"
    return loadResource(path)
}

private fun resolveBoardCardResource(card: Card): DrawableResource {
    return loadResource(cardToPath(card))
}

private fun loadResource(path: String): DrawableResource {
    return Res.allDrawableResources[path]
        ?: throw IllegalStateException("Not able to local Card at path $path in resources")
}

private fun cardToPath(card: Card): String {
    return "${card.cardValue.name.lowercase()}_${card.cardSuit.name.lowercase()}"
}
