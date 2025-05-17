package com.example.thebloomingskyline

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class OrderProductsAdapter(private val products: List<Map<String, Any>>) :
    RecyclerView.Adapter<OrderProductsAdapter.ProductViewHolder>() {

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.productImage)
        val productName: TextView = itemView.findViewById(R.id.productName)
        val productQuantity: TextView = itemView.findViewById(R.id.productQuantity)
        val productPrice: TextView = itemView.findViewById(R.id.productPrice)
        val productTotal: TextView = itemView.findViewById(R.id.productTotal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]

        holder.productName.text = product["name"].toString()
        holder.productQuantity.text = "Количество: ${product["quantity"]}"
        holder.productPrice.text = "Цена: ${product["price"]}₽"

        val total = (product["price"] as? Number)?.toInt()?.times(
            (product["quantity"] as? Number)?.toInt() ?: 1
        ) ?: 0
        holder.productTotal.text = "$total₽"

        // Загрузка изображения с помощью Glide
        val imageUrl = product["imageUrl"]?.toString() ?: ""
        if (imageUrl.isNotEmpty()) {
            Glide.with(holder.itemView.context)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.placeholder_image)
                .into(holder.productImage)
        } else {
            // Если URL пустой, устанавливаем placeholder
            holder.productImage.setImageResource(R.drawable.placeholder_image)
        }
    }

    override fun getItemCount(): Int = products.size
}