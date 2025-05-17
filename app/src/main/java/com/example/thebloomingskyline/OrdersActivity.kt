package com.example.thebloomingskyline

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class OrdersActivity : AppCompatActivity() {
    private lateinit var ordersRecyclerView: RecyclerView
    private lateinit var ordersAdapter: OrdersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders)

        ordersRecyclerView = findViewById(R.id.ordersRecyclerView)
        ordersRecyclerView.layoutManager = LinearLayoutManager(this)

        val orders = getUserOrders()
        ordersAdapter = OrdersAdapter(orders)
        ordersRecyclerView.adapter = ordersAdapter
    }

    private fun getUserOrders(): List<Order> {
        val sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE)
        var currentUserEmail = sharedPreferences.getString("user_email", null) ?: run {
            Toast.makeText(this, "Пользователь не авторизован", Toast.LENGTH_SHORT).show()
        }
        val prefs = getSharedPreferences("user_orders_$currentUserEmail", MODE_PRIVATE)
        val json = prefs.getString("orders", null) ?: return emptyList()

        val type = object : TypeToken<List<Order>>() {}.type
        return Gson().fromJson(json, type) ?: emptyList()
    }
}
