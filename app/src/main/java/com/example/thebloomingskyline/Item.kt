import java.io.Serializable

data class Item(
    val charack: Charack,
    val count: Int,
    val id: Int,
    val image: String,
    val price: Int
) : Serializable {
    data class Charack(
        val desc: String,
        val name: String,
        val text: String
    ) : Serializable
}
