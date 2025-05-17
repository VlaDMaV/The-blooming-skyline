package com.example.thebloomingskyline

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentSnapshot
import java.text.SimpleDateFormat
import java.util.*

class OrdersAdapter(
    private val orders: List<DocumentSnapshot>,
    private val onOrderClicked: (DocumentSnapshot) -> Unit
) : RecyclerView.Adapter<OrdersAdapter.OrderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.order_item, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        holder.bind(order)

        holder.itemView.setOnClickListener {
            onOrderClicked(order)
        }
    }

    override fun getItemCount(): Int = orders.size

    class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val orderId: TextView = itemView.findViewById(R.id.orderId)
        private val orderDate: TextView = itemView.findViewById(R.id.orderDate)
        private val orderStatus: TextView = itemView.findViewById(R.id.orderStatus)
        private val orderItems: TextView = itemView.findViewById(R.id.orderItems)
        private val orderTotal: TextView = itemView.findViewById(R.id.orderTotal)
        private val shippingAddress: TextView = itemView.findViewById(R.id.shippingAddress)

        private val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())

        fun bind(order: DocumentSnapshot) {
            val timestamp = order.getLong("timestamp") ?: 0
            val date = Date(timestamp)

            val orderIdText = order.id.substringAfter("order_")
            orderId.text = "Заказ #$orderIdText"
            orderDate.text = dateFormat.format(date)
            orderStatus.text = "Статус: ${order.getString("status") ?: "Новый"}"
            shippingAddress.text = "Адрес: ${order.getString("shipping") ?: "Не указан"}"

            val items = order.get("items") as? List<Map<String, Any>> ?: emptyList()
            val itemsText = items.joinToString("\n") { item ->
                "${item["name"]} x${item["quantity"]} - ${item["price"]}₽"
            }
            orderItems.text = itemsText

            val total = items.sumOf { item ->
                (item["price"] as? Number)?.toInt()?.times(
                    (item["quantity"] as? Number)?.toInt() ?: 1
                ) ?: 0
            }
            orderTotal.text = "Итого: $total ₽"
        }
    }
}