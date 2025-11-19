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
import io.github.bw0248.spe.card.CardState.FOLDED
import io.github.bw0248.spe.card.CardState.HIDDEN
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import tech_demo.composeapp.generated.resources.Res
import tech_demo.composeapp.generated.resources.allDrawableResources


@Composable
fun HoleCardView(card: Card, modifier: Modifier = Modifier) {
    val (aspectRatio, resource) = if (card.cardState in setOf(HIDDEN, FOLDED)) {
        1.33f to resolveHalfCardBack()
    } else {
        0.66f to resolveFullCard(card)
    }
    androidx.compose.material3.Card(
        modifier = modifier
            .aspectRatio(aspectRatio),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RectangleShape,
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Image(
            painter = painterResource(resource),
            contentDescription = "Playing Card",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
    }
}

@Composable
fun BoardCardView(card: Card?, modifier: Modifier = Modifier) {
    card?.let {
        androidx.compose.material3.Card(
            modifier = modifier.aspectRatio(0.666f),
            shape = MaterialTheme.shapes.small,
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Image(
                painter = painterResource(resolveFullCard(card)),
                contentDescription = "Playing Card",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds
            )
        }
    } ?: Box(
        modifier = modifier
            .aspectRatio(0.666f)
        //.background(color = Color.Red)
    ) {}
}

private fun resolveHalfCardBack(): DrawableResource {
    return loadResource("cb_half")
}

private fun resolveFullCard(card: Card): DrawableResource {
    return loadResource(cardToPath(card))
}

private fun loadResource(path: String): DrawableResource {
    return Res.allDrawableResources[path]
        ?: throw IllegalStateException("Not able to local Card at path $path in resources")
}

private fun cardToPath(card: Card): String {
    return "${card.cardValue.name.lowercase()}_${card.cardSuit.name.lowercase()}"
}
