package dev.saqeeb.typeahead

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform