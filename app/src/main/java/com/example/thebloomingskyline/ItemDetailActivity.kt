package com.example.thebloomingskyline

import Item
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore

class ItemDetailActivity : AppCompatActivity() {

    private var currentQuantity = 1
    private lateinit var currentItem: Item
    private lateinit var currentUserEmail: String
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_detail)

        // Получаем текущего пользователя
        val sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE)
        currentUserEmail = sharedPreferences.getString("user_email", null) ?: run {
            Toast.makeText(this, "Пользователь не авторизован", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Получаем переданный товар
        val item = intent.getSerializableExtra("item") as? Item ?: run {
            finish()
            return
        }
        currentItem = item

        // Инициализация views
        initViews()

        // Заполнение данных о товаре
        displayItemDetails()

        // Проверка на наличие в корзине
        checkItemInCart()
    }

    private fun initViews() {
        val nameView: TextView = findViewById(R.id.detailName)
        val descView: TextView = findViewById(R.id.detailDesc)
        val textView: TextView = findViewById(R.id.detailText)
        val priceView: TextView = findViewById(R.id.detailPrice)
        val imageView: ImageView = findViewById(R.id.detailImage)
        val quantityView: TextView = findViewById(R.id.quantityText)
        val addToCartBtn: AppCompatButton = findViewById(R.id.addToCartButton)
        val removeFromCartBtn: AppCompatButton = findViewById(R.id.removeFromCartButton)
        val increaseBtn: AppCompatButton = findViewById(R.id.increaseButton)
        val decreaseBtn: AppCompatButton = findViewById(R.id.decreaseButton)

        // Обработчики кнопок
        addToCartBtn.setOnClickListener { addToCart() }
        removeFromCartBtn.setOnClickListener { removeFromCart() }

        increaseBtn.setOnClickListener {
            currentQuantity++
            quantityView.text = currentQuantity.toString()
        }

        decreaseBtn.setOnClickListener {
            if (currentQuantity > 1) {
                currentQuantity--
                quantityView.text = currentQuantity.toString()
            }
        }
    }

    private fun displayItemDetails() {
        findViewById<TextView>(R.id.detailName).text = currentItem.charack.name
        findViewById<TextView>(R.id.detailDesc).text = currentItem.charack.desc
        findViewById<TextView>(R.id.detailText).text = currentItem.charack.text
        findViewById<TextView>(R.id.detailPrice).text = "${currentItem.price} ₽"
        findViewById<TextView>(R.id.quantityText).text = currentQuantity.toString()

        Glide.with(this)
            .load(currentItem.image)
            .into(findViewById(R.id.detailImage))
    }

    private fun checkItemInCart() {
        db.collection("basket")
            .document(currentUserEmail)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val items = document.get("items") as? List<Map<String, Any>> ?: emptyList()
                    val isInCart = items.any {
                        (it["itemId"] as? Number)?.toInt() == currentItem.id
                    }
                    updateCartButtonVisibility(isInCart)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Ошибка проверки корзины", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateCartButtonVisibility(isInCart: Boolean) {
        findViewById<AppCompatButton>(R.id.removeFromCartButton).visibility =
            if (isInCart) View.VISIBLE else View.INVISIBLE
    }

    private fun addToCart() {
        db.collection("basket")
            .document(currentUserEmail)
            .get()
            .addOnSuccessListener { document ->
                val currentItems = if (document.exists()) {
                    (document.get("items") as? List<Map<String, Any>> ?: emptyList()).toMutableList()
                } else {
                    mutableListOf()
                }

                updateOrAddItem(currentItems, currentQuantity)
                saveCartToFirestore(currentItems)
                Toast.makeText(this, "Товар добавлен в корзину", Toast.LENGTH_SHORT).show()
                updateCartButtonVisibility(true)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Ошибка добавления в корзину", Toast.LENGTH_SHORT).show()
            }
    }

    private fun removeFromCart() {
        db.collection("basket")
            .document(currentUserEmail)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val currentItems = (document.get("items") as? List<Map<String, Any>> ?: emptyList())
                        .filterNot { (it["itemId"] as? Number)?.toInt() == currentItem.id }
                        .toMutableList()

                    saveCartToFirestore(currentItems)
                    Toast.makeText(this, "Товар удален из корзины", Toast.LENGTH_SHORT).show()
                    updateCartButtonVisibility(false)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Ошибка удаления из корзины", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateOrAddItem(items: MutableList<Map<String, Any>>, quantity: Int) {
        val existingItemIndex = items.indexOfFirst {
            (it["itemId"] as? Number)?.toInt() == currentItem.id
        }

        if (existingItemIndex != -1) {
            val currentQty = (items[existingItemIndex]["quantity"] as? Number)?.toInt() ?: 0
            items[existingItemIndex] = items[existingItemIndex].toMutableMap().apply {
                put("quantity", currentQty + quantity)
            }
        } else {
            items.add(createItemMap(quantity))
        }
    }

    private fun createItemMap(quantity: Int): Map<String, Any> {
        return mapOf(
            "itemId" to currentItem.id,
            "name" to currentItem.charack.name,
            "description" to currentItem.charack.desc,
            "price" to currentItem.price,
            "quantity" to quantity,
            "imageUrl" to currentItem.image
        )
    }

    private fun saveCartToFirestore(items: List<Map<String, Any>>) {
        val cartData = hashMapOf(
            "userId" to currentUserEmail,
            "items" to items,
            "lastUpdated" to System.currentTimeMillis()
        )

        db.collection("basket")
            .document(currentUserEmail)
            .set(cartData)
            .addOnFailureListener { e ->
                Toast.makeText(this, "Ошибка сохранения корзины: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}