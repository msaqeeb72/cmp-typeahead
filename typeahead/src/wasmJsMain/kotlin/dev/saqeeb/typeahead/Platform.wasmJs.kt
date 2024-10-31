package dev.saqeeb.typeahead

class WebPlatform : Platform {
    override val name: String = "Web"
}
actual fun getPlatform():Platform = WebPlatform()