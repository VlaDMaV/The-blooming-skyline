package com.example.thebloomingskyline

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.thebloomingskyline.catalogue.CategoryAdapter
import com.example.thebloomingskyline.catalogue.FlowersActivity
import com.example.thebloomingskyline.catalogue.config.FlowerDatabase
import com.example.thebloomingskyline.catalogue.viewmodel.FlowerViewModel
import com.example.thebloomingskyline.databinding.ActivityCategoriesBinding

class Catalog : AppCompatActivity() {
   /* @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_catalog)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val text: TextView = findViewById(R.id.textViewCat)

        text.text = "Catalog"
    }*/

    private lateinit var binding: ActivityCategoriesBinding
    private val categories = listOf("Гвоздики", "Тюльпаны", "Лилии", "Орхидеи", "Пионы","Весенние новинки")
    private lateinit var database: FlowerDatabase
    private lateinit var viewModel: FlowerViewModel



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoriesBinding.inflate(layoutInflater)
             database = FlowerDatabase.getDatabase(applicationContext)
        viewModel = ViewModelProvider(this).get(FlowerViewModel::class.java)

        // Добавляем розы при первом запуске (можно добавить проверку)
        viewModel.addSampleRoses()

        setContentView(binding.root)

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
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