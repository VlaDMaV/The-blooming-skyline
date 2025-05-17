package com.example.thebloomingskyline

import Item
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class Basket : AppCompatActivity() {
    private lateinit var shippingField: EditText
    private lateinit var deliveryField: EditText
    private lateinit var paymentField: EditText
    private lateinit var promoField: EditText
    private lateinit var placeOrderButton: AppCompatButton
    private lateinit var cartRecyclerView: RecyclerView

    private lateinit var currentUserEmail: String
    private lateinit var totalAmountTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        // Получаем email текущего пользователя
        val sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE)
        currentUserEmail = sharedPreferences.getString("user_email", null) ?: run {
            Toast.makeText(this, "Пользователь не авторизован", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Инициализация полей
        shippingField = findViewById(R.id.shippingField)
        deliveryField = findViewById(R.id.deliveryField)
        paymentField = findViewById(R.id.paymentField)
        promoField = findViewById(R.id.promoField)
        placeOrderButton = findViewById(R.id.placeOrderButton)
        cartRecyclerView = findViewById(R.id.cartRecyclerView)
        totalAmountTextView = findViewById(R.id.totalAmountTextView)

        // Инициализация RecyclerView с товарами текущего пользователя
        cartRecyclerView.layoutManager = LinearLayoutManager(this)
        // Получаем корзину текущего пользователя и преобразуем в MutableList
        val cartItems = getUserCartItems().toMutableList()
        updateTotalAmount(cartItems)

// Инициализация адаптера с передачей функции для удаления товара
        val basketAdapter = BasketAdapter(cartItems, { item ->
            // Логика обработки удаления товара
            println("Товар удален: ${item.charack.name}")
            updateTotalAmount(cartItems)
        }, ::saveCartItems, ::updateTotalAmount)
        cartRecyclerView.adapter = basketAdapter
        placeOrderButton.setOnClickListener {
            val shipping = shippingField.text.toString()
            val delivery = deliveryField.text.toString()
            val payment = paymentField.text.toString()
            val promo = promoField.text.toString()

            if (shipping.isEmpty() || delivery.isEmpty() || payment.isEmpty()) {
                Toast.makeText(this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show()
            } else {
                placeOrder()
            }
        }
    }


    private fun saveCartItems(items: List<Item>) {
        val prefs = getSharedPreferences("user_cart_$currentUserEmail", MODE_PRIVATE)
        val editor = prefs.edit()
        val json = Gson().toJson(items)
        editor.putString("cart_items", json)
        editor.apply()
    }

    // Получение корзины текущего пользователя
    fun getUserCartItems(): List<Item> {
        val prefs = getSharedPreferences("user_cart_$currentUserEmail", MODE_PRIVATE)
        val json = prefs.getString("cart_items", null) ?: return emptyList()

        val type = object : TypeToken<List<Item>>() {}.type
        return Gson().fromJson(json, type) ?: emptyList()
    }

    private fun updateTotalAmount(cartItems: List<Item>) {
        val totalAmount =
            cartItems.sumOf { it.price * it.count } // Предполагается, что у Item есть свойство price
        totalAmountTextView.text = "Сумма: %d".format(totalAmount)
    }

    private fun saveOrder(order: Order) {
        val prefs = getSharedPreferences("user_orders_$currentUserEmail", MODE_PRIVATE)
        val editor = prefs.edit()

        // Получаем текущие заказы
        val existingOrdersJson = prefs.getString("orders", null) ?: "[]"
        val type = object : TypeToken<MutableList<Order>>() {}.type
        val existingOrders: MutableList<Order> = Gson().fromJson(existingOrdersJson, type)

        // Добавляем новый заказ
        existingOrders.add(order)

        // Сохраняем обновленный список заказов
        val json = Gson().toJson(existingOrders)
        editor.putString("orders", json)
        editor.apply()
    }

    // Оформление заказа
    private fun placeOrder() {
        val cartItems = getUserCartItems()
        if (cartItems.isEmpty()) {
            Toast.makeText(this, "Корзина пуста", Toast.LENGTH_SHORT).show()
            return
        }

        val shipping = shippingField.text.toString()
        val delivery = deliveryField.text.toString()
        val payment = paymentField.text.toString()
        val promo = promoField.text.toString()

        val order = Order(cartItems, shipping, delivery, payment, promo)
        saveOrder(order) // Сохраняем заказ

        clearUserCart()

        Toast.makeText(this, "Заказ выполнен успешно", Toast.LENGTH_SHORT).show()
        finish()
    }


    // Очистка корзины пользователя
    private fun clearUserCart() {
        getSharedPreferences("user_cart_$currentUserEmail", MODE_PRIVATE)
            .edit()
            .remove("cart_items")
            .apply()
        totalAmountTextView.text = "Сумма: %d".format(0)
    }

}