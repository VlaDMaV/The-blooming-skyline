package com.example.thebloomingskyline

import Item
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class BasketAdapter(
    private var cartItems: MutableList<Item>,
    private val onItemRemoved: (Item) -> Unit,
    private val saveCartItems: (List<Item>) -> Unit,
    private val updateTotalAmount: (List<Item>) -> Unit
) : RecyclerView.Adapter<BasketAdapter.CartViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.cart_item, parent, false)
        return CartViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = cartItems[position]
        holder.productName.text = item.charack.name
        holder.productDescription.text = item.charack.text
        holder.productPrice.text = "%d р.".format(item.price)
        holder.productQuantity.text = "Количество: ${item.count}"

        // Загрузка изображения
        loadImage(holder, item.image)

        // Увеличение количества
        holder.increaseButton.setOnClickListener {
            item.count++
            notifyItemChanged(position)
            saveCartItems(cartItems) // Сохраняем изменения в корзине
            updateTotalAmount(cartItems)
        }

        // Уменьшение количества
        holder.decreaseButton.setOnClickListener {
            if (item.count > 1) {
                item.count--
                notifyItemChanged(position)
                saveCartItems(cartItems) // Сохраняем изменения в корзине
                updateTotalAmount(cartItems)
            }
        }

        // Удаление товара
        holder.removeButton.setOnClickListener {
            onItemRemoved(item)
            cartItems.removeAt(position)
            notifyItemRemoved(position)
            saveCartItems(cartItems) // Сохраняем изменения в корзине
            updateTotalAmount(cartItems)
        }
    }


    private fun loadImage(holder: CartViewHolder, imageUrl: String?) {
        if (imageUrl != null) {
            if (imageUrl.startsWith("http")) {
                // Загрузка изображения по URL
                Glide.with(holder.itemView.context)
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .override(48, 48)
                    .into(holder.productImage)
            } else {
                try {
                    val resourceId = holder.itemView.context.resources.getIdentifier(
                        imageUrl.replace(".jpg", ""),
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
        } else {
            holder.productImage.setImageResource(R.drawable.error_image)
        }
    }

    override fun getItemCount(): Int = cartItems.size

    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productName: TextView = itemView.findViewById(R.id.productName)
        val productDescription: TextView = itemView.findViewById(R.id.desc)
        val productPrice: TextView = itemView.findViewById(R.id.productPrice)
        val productImage: ImageView = itemView.findViewById(R.id.productImage)
        val productQuantity: TextView = itemView.findViewById(R.id.productQuantity)
        val increaseButton: Button = itemView.findViewById(R.id.increaseButton)
        val decreaseButton: Button = itemView.findViewById(R.id.decreaseButton)
        val removeButton: Button = itemView.findViewById(R.id.removeButton)
    }
}
