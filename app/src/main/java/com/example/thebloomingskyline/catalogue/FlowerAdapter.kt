package com.example.thebloomingskyline.catalogue

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.thebloomingskyline.R
import com.example.thebloomingskyline.catalogue.entity.Flower
import com.example.thebloomingskyline.databinding.ItemFlowerBinding

class FlowerAdapter(
    private val onItemClick: (Flower) -> Unit
) : ListAdapter<Flower, FlowerAdapter.FlowerViewHolder>(DiffCallback()) {

    inner class FlowerViewHolder(private val binding: ItemFlowerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(flower: Flower) {
            binding.apply {
                // Текстовые данные
                flowerNameTextView.text = flower.name
                flowerPriceTextView.text = "%.2f ₽".format(flower.price)
                flowerCountTextView.text = "Осталось: ${flower.count}"

                // Загрузка изображения
                flower.imageUrl?.let { url ->
                    Glide.with(root.context)
                        .load(url) // Загружаем по URL
                        .placeholder(R.drawable.baseline_upload_24) // Показываем заглушку, пока грузится
                        .error(R.drawable.baseline_upload_24) // Показываем заглушку при ошибке
                        .into(flowerImageView) // Загружаем в ImageView
                } ?: run {
                    flowerImageView.setImageResource(R.drawable.baseline_upload_24) // Если URL null
                }

                // Анимация при клике
                root.setOnClickListener {
                    it.animate()
                        .translationY(10f)
                        .setDuration(100)
                        .withEndAction {
                            it.animate()
                                .translationY(0f)
                                .setDuration(100)
                                .start()
                            onItemClick(flower)
                        }
                        .start()
                }
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