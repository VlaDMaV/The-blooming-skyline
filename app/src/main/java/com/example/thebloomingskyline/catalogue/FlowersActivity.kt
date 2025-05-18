package com.example.thebloomingskyline.catalogue

import Item
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.thebloomingskyline.Basket
import com.example.thebloomingskyline.Catalog
import com.example.thebloomingskyline.HomePage
import com.example.thebloomingskyline.ItemDetailActivity
import com.example.thebloomingskyline.Profile
import com.example.thebloomingskyline.R
import com.example.thebloomingskyline.catalogue.entity.Flower

import com.example.thebloomingskyline.catalogue.viewmodel.FlowerViewModel
import com.example.thebloomingskyline.databinding.ActivityFlowersBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class FlowersActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFlowersBinding
    private lateinit var viewModel: FlowerViewModel
    private lateinit var category: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFlowersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        category = intent.getStringExtra("CATEGORY_NAME") ?: "Все цветы"
        supportActionBar?.title = category

        viewModel = ViewModelProvider(this).get(FlowerViewModel::class.java)

        setupRecyclerView()

        observeFlowers(loadFlowersFromCache());
        //setupAddButton()

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

    private fun loadFlowersFromCache(): List<Flower> {
        val prefs = getSharedPreferences("my_prefs", MODE_PRIVATE)
        val json = prefs.getString("items_map", null)

        val flowers = HashSet<Flower>();
        if (json != null) {
            val type = object : TypeToken<List<Item>>() {}.type
            val itemsList: List<Item> = Gson().fromJson(json, type)

            for (item in itemsList) {
                if(category == item.charack.desc) {
                    val flower = Flower(
                        item.id, item.charack.name, item.charack.text, item.count,
                        item.price.toDouble(), item.charack.desc
                    );
                    flower.imageUrl = item.image;
                    flowers.add(flower)
                }
            }

            Log.d("HomePage", "Кэш успешно загружен.")
        } else {
            Log.d("HomePage", "Кэш не найден, загрузка невозможна.")
        }
        return flowers.toList();
    }

    private fun setupRecyclerView() {
        val adapter = FlowerAdapter { flower ->
            showFlowerDetails(flower)
        }
        binding.flowersRecyclerView.adapter = adapter
        binding.flowersRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun observeFlowers(flowers: List<Flower>) {
        (binding.flowersRecyclerView.adapter as FlowerAdapter).submitList(flowers)
    }

    private fun showFlowerDetails(flower: Flower) {
        val item = flower.imageUrl?.let {
            Item(Item.Charack(flower.category,flower.name,flower.description),
                flower.count,flower.id, it,flower.price.toInt())
        };
        val intent = Intent(this, ItemDetailActivity::class.java)
        intent.putExtra("item", item)
        startActivity(intent)
    }
}