package io.github.bw0248.simple.poker.tech_demo

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform