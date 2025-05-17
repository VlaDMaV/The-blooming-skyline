package com.example.thebloomingskyline

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class OrderDetailActivity : AppCompatActivity() {

    private lateinit var detailOrderId: TextView
    private lateinit var detailOrderDate: TextView
    private lateinit var detailOrderStatus: TextView
    private lateinit var detailShippingAddress: TextView
    private lateinit var detailOrderTotal: TextView
    private lateinit var detailNotes: TextView
    private lateinit var detailPayment: TextView
    private lateinit var productsRecyclerView: RecyclerView

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_detail)

        // Инициализация всех View элементов
        initViews()

        // Получаем данные из Intent
        val orderId = intent.getStringExtra("order_id") ?: run {
            showErrorAndFinish("Не удалось получить ID заказа")
            return
        }

        val userEmail = intent.getStringExtra("user_email") ?: run {
            showErrorAndFinish("Не удалось получить email пользователя")
            return
        }

        // Загружаем данные о заказе
        loadOrderDetails(userEmail, orderId)
    }

    private fun initViews() {
        detailOrderId = findViewById(R.id.detailOrderId)
        detailOrderDate = findViewById(R.id.detailOrderDate)
        detailOrderStatus = findViewById(R.id.detailOrderStatus)
        detailShippingAddress = findViewById(R.id.detailShippingAddress)
        detailOrderTotal = findViewById(R.id.detailOrderTotal)
        detailNotes = findViewById(R.id.detailNotes)
        detailPayment = findViewById(R.id.detailPayment)

        productsRecyclerView = findViewById(R.id.productsRecyclerView)
        productsRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun loadOrderDetails(userEmail: String, orderId: String) {
        db.collection("user_orders")
            .document(userEmail)
            .collection("orders")
            .document(orderId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    displayOrderDetails(document)
                } else {
                    showErrorAndFinish("Заказ не найден")
                }
            }
            .addOnFailureListener { e ->
                showErrorAndFinish("Ошибка загрузки: ${e.localizedMessage}")
            }
    }

    private fun displayOrderDetails(order: DocumentSnapshot) {
        // Форматирование даты
        val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        val timestamp = order.getLong("timestamp") ?: 0
        val date = Date(timestamp)

        // ID заказа
        detailOrderId.text = "Заказ #${order.id.substringAfterLast("/")}"
        detailOrderDate.text = "Дата оформления: ${dateFormat.format(date)}"

        // Статус заказа
        detailOrderStatus.text = when(val status = order.getString("status") ?: "new") {
            "new" -> "Статус: Новый"
            "processing" -> "Статус: В обработке"
            "shipped" -> "Статус: Отправлен"
            "delivered" -> "Статус: Доставлен"
            "cancelled" -> "Статус: Отменен"
            else -> "Статус: $status"
        }

        // Адрес доставки
        detailShippingAddress.text = "Адрес доставки: ${order.getString("shipping") ?: "Не указан"}"

        // Список товаров
        val items = order.get("items") as? List<Map<String, Any>> ?: emptyList()
        productsRecyclerView.adapter = OrderProductsAdapter(items)

        // Общая сумма
        val total = items.sumOf { item ->
            (item["price"] as? Number)?.toInt()?.times(
                (item["quantity"] as? Number)?.toInt() ?: 1
            ) ?: 0
        }
        detailOrderTotal.text = "Общая сумма: $total ₽"

        // Комментарий к заказу
        order.getString("notes")?.takeIf { it.isNotEmpty() }?.let {
            detailNotes.text = "Комментарий: $it"
            detailNotes.visibility = View.VISIBLE
        } ?: run {
            detailNotes.visibility = View.GONE
        }

        // Способ оплаты
        order.getString("payment_method")?.takeIf { it.isNotEmpty() }?.let {
            detailPayment.text = "Способ оплаты: $it"
            detailPayment.visibility = View.VISIBLE
        } ?: run {
            detailPayment.visibility = View.GONE
        }
    }

    private fun showErrorAndFinish(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        finish()
    }
}