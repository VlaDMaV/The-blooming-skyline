package com.example.thebloomingskyline

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


class OrdersActivity : AppCompatActivity() {
    private lateinit var ordersRecyclerView: RecyclerView
    private lateinit var currentUserEmail: String
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders)

        val sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE)
        currentUserEmail = sharedPreferences.getString("user_email", null) ?: run {
            Toast.makeText(this, "Пользователь не авторизован", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        ordersRecyclerView = findViewById(R.id.ordersRecyclerView)
        ordersRecyclerView.layoutManager = LinearLayoutManager(this)

        loadUserOrders()

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

    private fun loadUserOrders() {
        db.collection("user_orders")
            .document(currentUserEmail)
            .collection("orders")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    Toast.makeText(this, "У вас пока нет заказов", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                val ordersAdapter = OrdersAdapter(querySnapshot.documents) { order ->
                    val intent = Intent(this, OrderDetailActivity::class.java).apply {
                        putExtra("order_id", order.id)
                        putExtra("user_email", currentUserEmail)
                    }
                    startActivity(intent)
                }
                ordersRecyclerView.adapter = ordersAdapter
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Ошибка загрузки заказов: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}