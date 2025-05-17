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
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ItemDetailActivity : AppCompatActivity() {

    private var currentQuantity = 1
    private lateinit var currentItem: Item

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_detail)

        // Получаем текущего пользователя


        val item = intent.getSerializableExtra("item") as? Item ?: run {
            finish()
            return
        }
        currentItem = item

        // Инициализация views
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

        // Заполнение данных о товаре
        nameView.text = item.charack.name
        descView.text = item.charack.desc
        textView.text = item.charack.text
        priceView.text = "${item.price} ₽"
        quantityView.text = currentQuantity.toString()

        Glide.with(this)
            .load(item.image)
            .into(imageView)

        // Обработчики кнопок
        addToCartBtn.setOnClickListener {
            addToCart(currentQuantity)
        }

        removeFromCartBtn.setOnClickListener {
            removeFromCart()
        }

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

        //Проверка на наличие в корзине
        val currentItems = getUserCartItems().toMutableList()
        val existingItemIndex = currentItems.indexOfFirst { it.id == currentItem.id }
        if (existingItemIndex >= 0) {
            val removeFromCartBtn: AppCompatButton = findViewById(R.id.removeFromCartButton)
            removeFromCartBtn.visibility = View.VISIBLE
        }
    }

    private fun addToCart(quantity: Int) {
        val currentUser = getCurrentUser(true);
        if(currentUser == "Unknown") return;
        val prefs = getSharedPreferences("user_cart_$currentUser", MODE_PRIVATE)
        val currentItems = getUserCartItems().toMutableList()

        // Проверяем, есть ли уже такой товар в корзине
        val existingItemIndex = currentItems.indexOfFirst { it.id == currentItem.id }

        if (existingItemIndex != -1) {
            // Обновляем количество, если товар уже есть
            currentItems[existingItemIndex].count += quantity
        } else {
            // Добавляем новый товар
            currentItem.count = quantity
            currentItems.add(currentItem)
        }

        saveCartItems(currentItems)
        Toast.makeText(this, "Товар добавлен в корзину", Toast.LENGTH_SHORT).show()
        val removeFromCartBtn: AppCompatButton = findViewById(R.id.removeFromCartButton)
        removeFromCartBtn.visibility = View.VISIBLE
    }

    private fun getCurrentUser(action: Boolean): String {
        val sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE)
        var currentUserEmail = sharedPreferences.getString("user_email", null) ?: run {
            if (action) {
                Toast.makeText(this, "Пользователь не авторизован", Toast.LENGTH_SHORT).show()
            }
            return "Unknown"
        }
        return currentUserEmail;
    }

    private fun removeFromCart() {
        val currentUser = getCurrentUser(true);
        if (currentUser == "Unknown") return;
        val prefs = getSharedPreferences("user_cart_$currentUser", MODE_PRIVATE)
        val currentItems = getUserCartItems().toMutableList()

        currentItems.removeAll { it.id == currentItem.id }

        saveCartItems(currentItems)
        Toast.makeText(this, "Товар удален из корзины", Toast.LENGTH_SHORT).show()
        val removeFromCartBtn: AppCompatButton = findViewById(R.id.removeFromCartButton)
        removeFromCartBtn.visibility = View.INVISIBLE
    }

    private fun getUserCartItems(): List<Item> {
        val prefs = getSharedPreferences("user_cart_${getCurrentUser(false)}", MODE_PRIVATE)
        val json = prefs.getString("cart_items", null) ?: return emptyList()

        val type = object : TypeToken<List<Item>>() {}.type
        return Gson().fromJson(json, type) ?: emptyList()
    }

    private fun saveCartItems(items: List<Item>) {
        val currentUser = getCurrentUser(true);
        if (currentUser == "Unknown") return;
        val prefs = getSharedPreferences("user_cart_$currentUser", MODE_PRIVATE)
        val json = Gson().toJson(items)
        prefs.edit().putString("cart_items", json).apply()
    }
}
