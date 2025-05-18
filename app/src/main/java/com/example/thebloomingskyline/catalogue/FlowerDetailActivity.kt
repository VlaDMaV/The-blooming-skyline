package com.example.thebloomingskyline.catalogue

import Item
import android.content.Intent
import com.bumptech.glide.Glide
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.thebloomingskyline.Basket
import com.example.thebloomingskyline.Catalog
import com.example.thebloomingskyline.HomePage
import com.example.thebloomingskyline.Profile
import com.example.thebloomingskyline.R
import com.example.thebloomingskyline.catalogue.entity.Flower
import com.example.thebloomingskyline.catalogue.viewmodel.FlowerViewModel

import com.example.thebloomingskyline.databinding.ActivityFlowerDetailBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

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
        loadFlowerFromCache()?.let { observeFlower(it) }

        // === Меню ===
        findViewById<ImageButton>(R.id.imageButton1).setOnClickListener {
            startActivity(Intent(this, HomePage::class.java))
        }
        findViewById<ImageButton>(R.id.imageButton2).setOnClickListener {
            startActivity(Intent(this, Catalog::class.java))
        }
        findViewById<ImageButton>(R.id.imageButton4).setOnClickListener {
            startActivity(Intent(this, Basket::class.java))
        }
        findViewById<ImageButton>(R.id.imageButton5).setOnClickListener {
            startActivity(Intent(this, Profile::class.java))
        }
    }

    private fun loadFlowerFromCache(): Flower? {
        val prefs = getSharedPreferences("my_prefs", MODE_PRIVATE)
        val json = prefs.getString("items_map", null)

        if (json != null) {
            val type = object : TypeToken<List<Item>>() {}.type
            val itemsList: List<Item> = Gson().fromJson(json, type)

            for (item in itemsList) {
                if (item.id == flowerId) {
                    val flower = Flower(
                        item.id, item.charack.name, item.charack.text, item.count,
                        item.price.toDouble(), item.charack.desc
                    );
                    //flower.imageUrl = item.image;
                    return flower
                }
            }

            Log.d("HomePage", "Кэш успешно загружен.")
        } else {
            Log.d("HomePage", "Кэш не найден, загрузка невозможна.")
        }
        return null;
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

    private fun observeFlower(flower: Flower) {
        binding.flowerName.text = flower.name
        binding.flowerDescription.text = flower.description
        binding.flowerCount.text = "Количество: ${flower.count}"
        binding.flowerPrice.text = "Цена: ${flower.price} ₽"
        binding.flowerCategory.text = "Категория: ${flower.category}"

        flower.imageUrl?.let { imageName ->
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