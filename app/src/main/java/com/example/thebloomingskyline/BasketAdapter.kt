package com.example.thebloomingskyline


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.thebloomingskyline.catalogue.entity.Flower


class BasketAdapter(private val cartItems: List<Flower>) : RecyclerView.Adapter<BasketAdapter.CartViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.cart_item, parent, false)
        return CartViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = cartItems[position]
        holder.productName.text = item.name
        holder.productDescription.text = item.description
        holder.productPrice.text = "%.2f р.".format(item.price)

        // Проверяем, является ли imageUrl локальным ресурсом (без http/https)
        if (item.imageUrl!!.startsWith("http")) {
            // Загрузка изображения по URL
            Glide.with(holder.itemView.context)
                .load(item.imageUrl)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .override(48, 48)
                .into(holder.productImage)
        } else {
            try {
                val resourceId = holder.itemView.context.resources.getIdentifier(
                    item.imageUrl?.replace(".jpg", ""),
                    "drawable",
                    holder.itemView.context.packageName
                )

                if (resourceId != 0) {
                    Glide.with(holder.itemView.context)
                        .load(resourceId)
                        .placeholder(R.drawable.placeholder_image)
                        .error(R.drawable.error_image)
                        .override(48, 48)
                        .into(holder.productImage)
                } else {
                    holder.productImage.setImageResource(R.drawable.error_image)
                }
            } catch (e: Exception) {
                holder.productImage.setImageResource(R.drawable.error_image)
            }
        }
    }

    override fun getItemCount(): Int = cartItems.size

    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productName: TextView = itemView.findViewById(R.id.productName)
        val productDescription: TextView = itemView.findViewById(R.id.desc)
        val productPrice: TextView = itemView.findViewById(R.id.productPrice)
        val productImage: ImageView = itemView.findViewById(R.id.productImage)
    }
}