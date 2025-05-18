package com.example.thebloomingskyline

import Item
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.thebloomingskyline.catalogue.CategoryAdapter
import com.example.thebloomingskyline.catalogue.FlowersActivity
import com.example.thebloomingskyline.catalogue.config.FlowerDatabase
import com.example.thebloomingskyline.catalogue.entity.Flower
import com.example.thebloomingskyline.catalogue.viewmodel.FlowerViewModel
import com.example.thebloomingskyline.databinding.ActivityCategoriesBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Catalog : AppCompatActivity() {

    private lateinit var binding: ActivityCategoriesBinding
    private lateinit var database: FlowerDatabase
    private lateinit var viewModel: FlowerViewModel



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoriesBinding.inflate(layoutInflater)
             database = FlowerDatabase.getDatabase(applicationContext)
        viewModel = ViewModelProvider(this).get(FlowerViewModel::class.java)

        setContentView(binding.root)
        setupRecyclerView(loadCategoriesFromCache())

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

    private fun loadCategoriesFromCache(): List<String> {
        val prefs = getSharedPreferences("my_prefs", MODE_PRIVATE)
        val json = prefs.getString("items_map", null)

        val categories = HashSet<String>();

        if (json != null) {
            val type = object : TypeToken<List<Item>>() {}.type
            val itemsList: List<Item> = Gson().fromJson(json, type)

            for (item in itemsList) {
                categories.add(item.charack.desc)
            }

            Log.d("HomePage", "Кэш успешно загружен.")
        } else {
            Log.d("HomePage", "Кэш не найден, загрузка невозможна.")
        }
        return categories.toList()
    }

    private fun setupRecyclerView(categories : List<String>) {
        val adapter = CategoryAdapter(categories) { category ->
            openFlowersActivity(category)
        }
        binding.categoriesRecyclerView.adapter = adapter
        binding.categoriesRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun openFlowersActivity(category: String) {
        val intent = Intent(this, FlowersActivity::class.java).apply {
            putExtra("CATEGORY_NAME", category)
        }
        startActivity(intent)
    }
}