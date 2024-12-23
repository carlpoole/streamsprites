package codes.carl.streamsprites.model

data class SpriteData(
    val dataType: String,
    val dataContent: String,
    val twitchUserId: String,
    val timestamp: String? = null
)