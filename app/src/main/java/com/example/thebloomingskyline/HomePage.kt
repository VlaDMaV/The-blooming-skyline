package com.example.thebloomingskyline

import Item
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import android.content.Intent
import androidx.core.view.WindowInsetsCompat
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.ListenerRegistration
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class HomePage : AppCompatActivity() {
    private var listenerRegistration: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home_page)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        loadItemsFromCache()      // 1. показываем кэш
        checkFirebaseForUpdates() // 2. слушаем Firebase для обновлений

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


    private fun loadItemsFromCache() {
        val prefs = getSharedPreferences("my_prefs", MODE_PRIVATE)
        val json = prefs.getString("items_map", null)

        if (json != null) {
            val type = object : TypeToken<List<Item>>() {}.type
            val itemsList: List<Item> = Gson().fromJson(json, type)

            val headerData = HeaderData(
                title = getString(R.string.app_name),
                imageUrl = R.drawable.march
            )

            val proList: RecyclerView = findViewById(R.id.productList)
            proList.layoutManager = LinearLayoutManager(this)
            proList.adapter = ProductAdapter(headerData, itemsList)

            Log.d("HomePage", "Кэш успешно загружен.")
        } else {
            Log.d("HomePage", "Кэш не найден, загрузка невозможна.")
        }
    }


    private fun checkFirebaseForUpdates() {
        val prefs = getSharedPreferences("my_prefs", MODE_PRIVATE)
        val oldHash = prefs.getInt("items_hash", 0)

        Firebase.firestore.collection("itemsList")
            .get()
            .addOnSuccessListener { result ->
                val itemsList = mutableListOf<Item>()

                for (document in result) {
                    val charack = document.get("charack") as? Map<*, *> ?: continue
                    val desc = charack["desc"] as? String ?: ""
                    val name = charack["name"] as? String ?: ""
                    val text = charack["text"] as? String ?: ""

                    val count = (document.getLong("count") ?: 0).toInt()
                    val id = (document.getLong("id") ?: 0).toInt()
                    val image = document.getString("image") ?: ""
                    val price = (document.getLong("price") ?: 0).toInt()

                    val item = Item(
                        charack = Item.Charack(desc = desc, name = name, text = text),
                        count = count,
                        id = id,
                        image = image,
                        price = price
                    )

                    itemsList.add(item)
                }

                val gson = Gson()
                val newJson = gson.toJson(itemsList)
                val newHash = newJson.hashCode()

                if (newHash != oldHash) {
                    prefs.edit()
                        .putString("items_map", newJson)
                        .putInt("items_hash", newHash)
                        .apply()

                    val headerData = HeaderData(
                        title = getString(R.string.app_name),
                        imageUrl = R.drawable.march
                    )

                    val proList: RecyclerView = findViewById(R.id.productList)
                    proList.layoutManager = LinearLayoutManager(this)
                    proList.adapter = ProductAdapter(headerData, itemsList)

                    Log.d("HomePage", "Данные обновлены из Firebase")
                } else {
                    Log.d("HomePage", "Данные в Firebase не изменились")
                }
            }
            .addOnFailureListener { e ->
                Log.e("HomePage", "Ошибка загрузки из Firebase: ${e.localizedMessage}")
                Toast.makeText(this, "Не удалось обновить товары", Toast.LENGTH_SHORT).show()
            }
    }


    override fun onDestroy() {
        super.onDestroy()
        // Отключаем слушатель при уничтожении активности
        listenerRegistration?.remove()
    }
}