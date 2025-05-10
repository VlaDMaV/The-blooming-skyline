package com.example.thebloomingskyline.catalogue

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.thebloomingskyline.R
import com.example.thebloomingskyline.catalogue.entity.Flower
import com.example.thebloomingskyline.databinding.ItemFlowerBinding

class FlowerAdapter(
    private val onItemClick: (Flower) -> Unit
) : ListAdapter<Flower, FlowerAdapter.FlowerViewHolder>(DiffCallback()) {

    inner class FlowerViewHolder(private val binding: ItemFlowerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                // Анимация при нажатии
                it.animate()
                    .translationY(10f) // Смещение вниз на 10 пикселей
                    .setDuration(100)
                    .withEndAction {
                        it.animate()
                            .translationY(0f)
                            .setDuration(100)
                            .start()
                    }
                    .start()

                onItemClick(getItem(adapterPosition))
            }
        }

        fun bind(flower: Flower) {
            binding.apply {
                flowerNameTextView.text = flower.name
                flowerPriceTextView.text = "%.2f ₽".format(flower.price)
                flowerCountTextView.text = "Осталось: ${flower.count}"
                root.setOnClickListener { onItemClick(flower) }
            }
        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlowerViewHolder {
        val binding = ItemFlowerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FlowerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FlowerViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<Flower>() {
        override fun areItemsTheSame(oldItem: Flower, newItem: Flower) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Flower, newItem: Flower) = oldItem == newItem
    }
}