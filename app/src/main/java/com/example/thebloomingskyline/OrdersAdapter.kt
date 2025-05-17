package com.example.thebloomingskyline

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class OrdersAdapter(private val orders: List<Order>) : RecyclerView.Adapter<OrdersAdapter.OrderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.order_item, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        holder.shippingTextView.text = "Доставка: ${order.delivery}"
        holder.paymentTextView.text = "Оплата: ${order.payment}"
        holder.promoTextView.text = "Промо: ${order.promo}"

        // Отображение товаров в заказе
        holder.itemsTextView.text = order.items.joinToString { it.charack.name }
    }

    override fun getItemCount(): Int = orders.size

    class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val shippingTextView: TextView = itemView.findViewById(R.id.shippingTextView)
        val paymentTextView: TextView = itemView.findViewById(R.id.paymentTextView)
        val promoTextView: TextView = itemView.findViewById(R.id.promoTextView)
        val itemsTextView: TextView = itemView.findViewById(R.id.itemsTextView)
    }
}
