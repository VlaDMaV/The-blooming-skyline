package com.example.thebloomingskyline

import Item
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class Basket : AppCompatActivity() {
    private lateinit var shippingField: EditText
    private lateinit var deliveryField: EditText
    private lateinit var paymentField: EditText
    private lateinit var promoField: EditText
    private lateinit var placeOrderButton: AppCompatButton
    private lateinit var cartRecyclerView: RecyclerView
    private lateinit var totalAmountTextView: TextView

    private lateinit var currentUserEmail: String
    private val db = FirebaseFirestore.getInstance()
    private lateinit var basketAdapter: BasketAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        val sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE)
        currentUserEmail = sharedPreferences.getString("user_email", null) ?: run {
            Toast.makeText(this, "Пользователь не авторизован", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        initViews()
        setupRecyclerView()
        loadCartFromFirestore()
        setupPlaceOrderButton()
        loadUserPaymentData()

        // === Меню ===
        findViewById<ImageButton>(R.id.imageButton1).setOnClickListener {
            startActivity(Intent(this, HomePage::class.java))
        }
        findViewById<ImageButton>(R.id.imageButton2).setOnClickListener {
            startActivity(Intent(this, Catalog::class.java))
        }
        findViewById<ImageButton>(R.id.imageButton4).setOnClickListener {
            startActivity(Intent(this, Basket::class.java))
        }
        findViewById<ImageButton>(R.id.imageButton5).setOnClickListener {
            startActivity(Intent(this, Profile::class.java))
        }
    }

    // В вашей активности (например, CheckoutActivity.kt)
    private fun loadUserPaymentData() {
        // 1. Получаем ссылку на Firestore и текущего пользователя
        val db = Firebase.firestore
        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email
            ?: return // Если пользователь не авторизован, выходим


        db.collection("infAboutUsers")
            .document(currentUserEmail)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // 3. Извлекаем номер карты из документа
                    val cardNumber = document.getString("card_number") ?: ""

                    // 5. Устанавливаем значение в поле paymentField
                    findViewById<AppCompatEditText>(R.id.paymentField).apply {
                        setText(cardNumber)
                        // Делаем поле нередактируемым (опционально)
                        isEnabled = false
                        setTextColor(ContextCompat.getColor(context, R.color.black))
                    }
                } else {
                    Log.d("Firestore", "Документ не существует")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Ошибка получения данных", exception)
                Toast.makeText(this, "Ошибка загрузки данных карты", Toast.LENGTH_SHORT).show()
            }
    }

    private fun initViews() {
        shippingField = findViewById(R.id.shippingField)
        deliveryField = findViewById(R.id.deliveryField)
        paymentField = findViewById(R.id.paymentField)
        promoField = findViewById(R.id.promoField)
        placeOrderButton = findViewById(R.id.placeOrderButton)
        cartRecyclerView = findViewById(R.id.cartRecyclerView)
        totalAmountTextView = findViewById(R.id.totalAmountTextView)
    }

    private fun setupRecyclerView() {
        cartRecyclerView.layoutManager = LinearLayoutManager(this)
        basketAdapter = BasketAdapter(mutableListOf(), currentUserEmail, ::updateTotalAmount)
        cartRecyclerView.adapter = basketAdapter
    }

    private fun loadCartFromFirestore() {
        db.collection("basket")
            .document(currentUserEmail)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Toast.makeText(this, "Ошибка загрузки корзины", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val itemsData = snapshot.get("items") as? List<Map<String, Any>> ?: emptyList()
                    val items = itemsData.map { itemMap ->
                        Item(
                            charack = Item.Charack(
                                desc = itemMap["description"].toString(),
                                name = itemMap["name"].toString(),
                                text = ""
                            ),
                            count = (itemMap["quantity"] as? Number)?.toInt() ?: 1,
                            id = (itemMap["itemId"] as? Number)?.toInt() ?: 0,
                            image = itemMap["imageUrl"].toString(),
                            price = (itemMap["price"] as? Number)?.toInt() ?: 0
                        )
                    }
                    basketAdapter.updateItems(items)
                }
            }
    }

    private fun updateTotalAmount(cartItems: List<Item>) {
        val totalAmount = cartItems.sumOf { it.price * it.count }
        totalAmountTextView.text = "Сумма: %d ₽".format(totalAmount)
    }

    private fun setupPlaceOrderButton() {
        placeOrderButton.setOnClickListener {
            if (basketAdapter.itemCount == 0) {
                Toast.makeText(this, "Корзина пуста", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val shipping = shippingField.text.toString()
            val delivery = deliveryField.text.toString()
            val payment = paymentField.text.toString()

            if (shipping.isEmpty() || delivery.isEmpty() || payment.isEmpty()) {
                Toast.makeText(this, "Заполните все обязательные поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            placeOrder()
        }
    }

    private fun placeOrder() {
        if (basketAdapter.itemCount == 0) {
            Toast.makeText(this, "Корзина пуста", Toast.LENGTH_SHORT).show()
            return
        }

        val shipping = shippingField.text.toString()
        val delivery = deliveryField.text.toString()
        val payment = paymentField.text.toString()
        val promo = promoField.text.toString()

        val items = basketAdapter.getItems()
        val timestamp = System.currentTimeMillis()
        val orderId = "order_$timestamp" // Уникальный ID заказа

        val orderData = hashMapOf(
            "items" to items.map { item ->
                hashMapOf(
                    "id" to item.id,
                    "name" to item.charack.name,
                    "description" to item.charack.desc,
                    "price" to item.price,
                    "quantity" to item.count,
                    "imageUrl" to item.image
                )
            },
            "shipping" to shipping,
            "delivery" to delivery,
            "payment" to payment,
            "promo" to promo,
            "timestamp" to timestamp,
            "status" to "Новый"
        )

        // Сохраняем заказ в подколлекцию пользователя
        db.collection("user_orders")
            .document(currentUserEmail) // Документ с email пользователя
            .collection("orders")       // Подколлекция заказов
            .document(orderId)          // Документ заказа
            .set(orderData)
            .addOnSuccessListener {
                clearCart()
                Toast.makeText(this, "Заказ оформлен успешно", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Ошибка оформления заказа: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun clearCart() {
        db.collection("basket")
            .document(currentUserEmail)
            .delete()
            .addOnSuccessListener {
                basketAdapter.updateItems(emptyList())
            }
            .addOnFailureListener {
                Toast.makeText(this, "Ошибка при очистке корзины", Toast.LENGTH_SHORT).show()
            }
    }
}