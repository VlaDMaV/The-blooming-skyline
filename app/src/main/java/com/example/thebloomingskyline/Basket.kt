package com.example.thebloomingskyline

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.thebloomingskyline.catalogue.entity.Flower

class Basket : AppCompatActivity() {

    private lateinit var shippingField: EditText
    private lateinit var deliveryField: EditText
    private lateinit var paymentField: EditText
    private lateinit var promoField: EditText
    private lateinit var placeOrderButton: AppCompatButton
    private lateinit var cartRecyclerView: RecyclerView
    private lateinit var cartAdapter: BasketAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        // Инициализация полей
        shippingField = findViewById(R.id.shippingField)
        deliveryField = findViewById(R.id.deliveryField)
        paymentField = findViewById(R.id.paymentField)
        promoField = findViewById(R.id.promoField)
        placeOrderButton = findViewById(R.id.placeOrderButton)
        cartRecyclerView = findViewById(R.id.cartRecyclerView)

        // Инициализация RecyclerView для отображения товаров в корзине
        cartRecyclerView.layoutManager = LinearLayoutManager(this)
        cartAdapter = BasketAdapter(getFlowers())
        cartRecyclerView.adapter = cartAdapter

        // Настройка кнопки "Place Order"
        placeOrderButton.setOnClickListener {
            // Логика для размещения заказа
            val shipping = shippingField.text.toString()
            val delivery = deliveryField.text.toString()
            val payment = paymentField.text.toString()
            val promo = promoField.text.toString()

            if (shipping.isEmpty() || delivery.isEmpty() || payment.isEmpty()) {
                Toast.makeText(this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show()
            } else {
                // Показать сообщение об успешном заказе
                Toast.makeText(this, "Заказ выполнен успешно", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Пример данных для товаров в корзине
    fun getFlowers(): List<Flower> {
        return listOf(
            Flower(
                id = 1,
                name = "Rose",
                description = "A beautiful red rose",
                count = 10,
                price = 299.99,
                category = "Flowers",
                imageUrl = "redrose.jpg"
            ),
            Flower(
                id = 2,
                name = "Tulip",
                description = "A bright yellow tulip",
                count = 15,
                price = 199.99,
                category = "Flowers",
                imageUrl = "https://example.com/images/tulip.jpg"
            ),
            Flower(
                id = 3,
                name = "Orchid",
                description = "A delicate purple orchid",
                count = 5,
                price = 499.99,
                category = "Exotic Flowers",
                imageUrl = "https://example.com/images/orchid.jpg"
            ),
            Flower(
                id = 4,
                name = "Lily",
                description = "A white lily with a sweet fragrance",
                count = 7,
                price = 149.99,
                category = "Flowers",
                imageUrl = "https://example.com/images/lily.jpg"
            ),
            Flower(
                id = 5,
                name = "Sunflower",
                description = "A bright yellow sunflower",
                count = 20,
                price = 99.99,
                category = "Flowers",
                imageUrl = "https://example.com/images/sunflower.jpg"
            )
        )
    }
}
