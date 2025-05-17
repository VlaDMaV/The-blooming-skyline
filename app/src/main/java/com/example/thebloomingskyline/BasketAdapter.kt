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
import com.google.firebase.firestore.FirebaseFirestore

class BasketAdapter(
    private var cartItems: MutableList<Item>,
    private val currentUserEmail: String,
    private val updateTotalAmount: (List<Item>) -> Unit
) : RecyclerView.Adapter<BasketAdapter.CartViewHolder>() {

    private val db = FirebaseFirestore.getInstance()

    fun getItems(): List<Item> {
        return cartItems.toList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.cart_item, parent, false)
        return CartViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = cartItems[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = cartItems.size

    fun updateItems(newItems: List<Item>) {
        cartItems.clear()
        cartItems.addAll(newItems)
        notifyDataSetChanged()
        updateTotalAmount(cartItems)
    }

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val productName: TextView = itemView.findViewById(R.id.productName)
        private val productDescription: TextView = itemView.findViewById(R.id.desc)
        private val productPrice: TextView = itemView.findViewById(R.id.productPrice)
        private val productImage: ImageView = itemView.findViewById(R.id.productImage)
        private val productQuantity: TextView = itemView.findViewById(R.id.productQuantity)
        private val increaseButton: Button = itemView.findViewById(R.id.increaseButton)
        private val decreaseButton: Button = itemView.findViewById(R.id.decreaseButton)
        private val removeButton: Button = itemView.findViewById(R.id.removeButton)

        fun bind(item: Item) {
            productName.text = item.charack.name
            productDescription.text = item.charack.text
            productPrice.text = "%d р.".format(item.price)
            productQuantity.text = "Количество: ${item.count}"

            loadImage(item.image)

            increaseButton.setOnClickListener {
                updateItemInFirestore(item.id, item.count + 1)
            }

            decreaseButton.setOnClickListener {
                if (item.count > 1) {
                    updateItemInFirestore(item.id, item.count - 1)
                }
            }

            removeButton.setOnClickListener {
                removeItemFromFirestore(item.id)
            }
        }

        private fun loadImage(imageUrl: String?) {
            if (imageUrl != null) {
                if (imageUrl.startsWith("http")) {
                    Glide.with(itemView.context)
                        .load(imageUrl)
                        .placeholder(R.drawable.placeholder_image)
                        .error(R.drawable.error_image)
                        .override(48, 48)
                        .into(productImage)
                } else {
                    try {
                        val resourceId = itemView.context.resources.getIdentifier(
                            imageUrl.replace(".jpg", ""),
                            "drawable",
                            itemView.context.packageName
                        )

                        if (resourceId != 0) {
                            Glide.with(itemView.context)
                                .load(resourceId)
                                .placeholder(R.drawable.placeholder_image)
                                .error(R.drawable.error_image)
                                .override(48, 48)
                                .into(productImage)
                        } else {
                            productImage.setImageResource(R.drawable.error_image)
                        }
                    } catch (e: Exception) {
                        productImage.setImageResource(R.drawable.error_image)
                    }
                }
            } else {
                productImage.setImageResource(R.drawable.error_image)
            }
        }

        private fun updateItemInFirestore(itemId: Int, newQuantity: Int) {
            db.collection("basket")
                .document(currentUserEmail)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val items = document.get("items") as? List<Map<String, Any>> ?: emptyList()
                        val updatedItems = items.map { itemMap ->
                            if ((itemMap["itemId"] as? Number)?.toInt() == itemId) {
                                mutableMapOf<String, Any>().apply {
                                    putAll(itemMap)
                                    put("quantity", newQuantity)
                                }
                            } else {
                                itemMap
                            }
                        }

                        db.collection("basket")
                            .document(currentUserEmail)
                            .update("items", updatedItems)
                    }
                }
        }

        private fun removeItemFromFirestore(itemId: Int) {
            db.collection("basket")
                .document(currentUserEmail)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val items = document.get("items") as? List<Map<String, Any>> ?: emptyList()
                        val updatedItems = items.filter {
                            (it["itemId"] as? Number)?.toInt() != itemId
                        }

                        db.collection("basket")
                            .document(currentUserEmail)
                            .update("items", updatedItems)
                    }
                }
        }
    }
}