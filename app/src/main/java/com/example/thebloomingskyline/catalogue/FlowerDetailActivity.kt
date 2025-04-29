package com.example.thebloomingskyline.catalogue

import com.bumptech.glide.Glide
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.thebloomingskyline.R
import com.example.thebloomingskyline.catalogue.viewmodel.FlowerViewModel

import com.example.thebloomingskyline.databinding.ActivityFlowerDetailBinding

class FlowerDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFlowerDetailBinding
    private lateinit var viewModel: FlowerViewModel
    private var flowerId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFlowerDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        flowerId = intent.getIntExtra("FLOWER_ID", -1)
        if (flowerId == -1) finish()

        viewModel = ViewModelProvider(this).get(FlowerViewModel::class.java)

        setupViews()
        observeFlower()
    }

    private fun setupViews() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.deleteButton.setOnClickListener {
            deleteFlower()
        }

        binding.editButton.setOnClickListener {
            editFlower()
        }
    }

    private fun observeFlower() {
        viewModel.getFlowerById(flowerId).observe(this) { flower ->
            flower?.let {
                binding.flowerName.text = it.name
                binding.flowerDescription.text = it.description
                binding.flowerCount.text = "Количество: ${it.count}"
                binding.flowerPrice.text = "Цена: ${it.price} ₽"
                binding.flowerCategory.text = "Категория: ${it.category}"

                it.imageUrl?.let { imageName ->
                    val resourceId = resources.getIdentifier(
                        imageName,  // имя файла (без расширения)
                        "drawable",  // тип ресурса
                        packageName  // пакет приложения
                    )

                    if (resourceId != 0) {
                        Glide.with(this)
                            .load(resourceId)  // загружаем по ID ресурса
                            .into(binding.flowerImage)
                    } else {
                        // Если изображение не найдено, можно установить placeholder
                        binding.flowerImage.setImageResource(R.drawable.placeholder)
                    }
                }


            }
        }

    }

    private fun deleteFlower() {
        viewModel.getFlowerById(flowerId).value?.let {
            viewModel.deleteFlower(it)
            finish()
        }
    }

    private fun editFlower() {
        // Реализация редактирования
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}