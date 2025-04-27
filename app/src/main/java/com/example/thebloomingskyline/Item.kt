data class Item(
    val charack: Charack = Charack(),  // ← значение по умолчанию
    val count: Int = 0,
    val id: Int = 0,
    val image: String = "",
    val price: Int = 0
) {
    data class Charack(
        val desc: String = "",
        val name: String = "",
        val text: String = ""
    )
}
