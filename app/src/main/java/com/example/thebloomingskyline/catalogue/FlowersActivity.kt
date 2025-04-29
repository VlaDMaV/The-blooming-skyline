package com.example.thebloomingskyline.catalogue

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.thebloomingskyline.R
import com.example.thebloomingskyline.catalogue.entity.Flower

import com.example.thebloomingskyline.catalogue.viewmodel.FlowerViewModel
import com.example.thebloomingskyline.databinding.ActivityFlowersBinding

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
        observeFlowers()
        //setupAddButton()
    }

    private fun setupRecyclerView() {
        val adapter = FlowerAdapter { flower ->
            showFlowerDetails(flower)
        }
        binding.flowersRecyclerView.adapter = adapter
        binding.flowersRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun observeFlowers() {
        viewModel.getFlowersByCategory(category).observe(this) { flowers ->
            (binding.flowersRecyclerView.adapter as FlowerAdapter).submitList(flowers)
        }
    }

    /*private fun setupAddButton() {
        binding.addFlowerButton.setOnClickListener {
            showAddFlowerDialog()
        }
    }

    private fun showAddFlowerDialog() {
        val dialog = AlertDialog.Builder(this)
            .setTitle("Добавить новый цветок")
            .setView(R.layout.dialog_add_flower)
            .setPositiveButton("Добавить") { _, _ ->
                // Логика добавления
            }
            .setNegativeButton("Отмена", null)
            .create()

        dialog.show()
    }*/

    private fun showFlowerDetails(flower: Flower) {
        val intent = Intent(this, FlowerDetailActivity::class.java).apply {
            putExtra("FLOWER_ID", flower.id)
        }
        startActivity(intent)
    }
}