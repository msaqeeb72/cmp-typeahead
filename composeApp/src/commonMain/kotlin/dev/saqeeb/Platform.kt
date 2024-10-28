package dev.saqeeb

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform