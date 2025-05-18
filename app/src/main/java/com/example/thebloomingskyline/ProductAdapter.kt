package com.example.thebloomingskyline

import Item
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

private val VIEW_TYPE_HEADER = 0
private val VIEW_TYPE_ITEM = 1

class ProductAdapter(
    private val headerData: HeaderData,
    private val items: List<Item>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.headerTextView)
        val imageView: ImageView = itemView.findViewById(R.id.headerImageView)
    }

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.productImage)
        val name: TextView = itemView.findViewById(R.id.productName)
        val price: TextView = itemView.findViewById(R.id.productPrice)
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) VIEW_TYPE_HEADER else VIEW_TYPE_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_HEADER) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_header, parent, false)
            HeaderViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_product, parent, false)
            ProductViewHolder(view)
        }
    }

    override fun getItemCount(): Int = items.size + 1 // +1 для хедера

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == VIEW_TYPE_HEADER) {
            val headerHolder = holder as HeaderViewHolder
            headerHolder.textView.text = headerData.title
            Glide.with(headerHolder.itemView.context)
                .load(headerData.imageUrl)
                .into(headerHolder.imageView)
        } else {
            val itemHolder = holder as ProductViewHolder
            val item = items[position - 1] // смещаем индекс, т.к. 0 — хедер
            itemHolder.name.text = item.charack.name
            itemHolder.price.text = "${item.price} ₽"
            Glide.with(itemHolder.itemView.context)
                .load(item.image)
                .into(itemHolder.image)

            itemHolder.itemView.setOnClickListener {
                val context = it.context
                val intent = Intent(context, ItemDetailActivity::class.java)
                intent.putExtra("item", item)
                context.startActivity(intent)
            }
        }
    }
}

data class HeaderData(val title: String, val imageUrl: Int)
