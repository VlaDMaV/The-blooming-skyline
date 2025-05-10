package com.example.thebloomingskyline

import Item
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import android.content.Intent
import androidx.core.view.WindowInsetsCompat
import android.widget.Button
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import android.util.Log
import android.widget.EditText
import android.widget.Toast

class HomePage : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")

    override fun onResume() {
        super.onResume()

        val db = Firebase.firestore
        val proList: RecyclerView = findViewById(R.id.productList)
        proList.layoutManager = LinearLayoutManager(this)

        db.collection("itemsList").get().addOnSuccessListener { querySnapshot ->
            val itemsList = mutableListOf<Item>()
            for (document in querySnapshot) {
                document.toObject(Item::class.java)?.let {
                    itemsList.add(it)
                }
            }

            // Здесь, когда itemsList уже загружен — устанавливаем адаптер
            proList.adapter = ProductAdapter(itemsList)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val addToDB: Button = findViewById(R.id.buttonAddToDB)

        addToDB.setOnClickListener {
            val db = Firebase.firestore

            val desc2 = findViewById<EditText>(R.id.desc).text.toString()
            val name2 = findViewById<EditText>(R.id.name).text.toString()
            val text2 = findViewById<EditText>(R.id.text).text.toString()

            // Для числовых полей нужна конвертация и проверка
            val count2 = try {
                findViewById<EditText>(R.id.count).text.toString().toInt()
            } catch (e: NumberFormatException) {
                Toast.makeText(this, "Некорректное количество", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val id2 = try {
                findViewById<EditText>(R.id.id).text.toString().toInt()
            } catch (e: NumberFormatException) {
                Toast.makeText(this, "Некорректный ID", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val image2 = findViewById<EditText>(R.id.image).text.toString()

            val price2 = try {
                findViewById<EditText>(R.id.price).text.toString().toInt()
            } catch (e: NumberFormatException) {
                Toast.makeText(this, "Некорректная цена", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (desc2.isEmpty() || name2.isEmpty() || text2.isEmpty() || image2.isEmpty()) {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val newItem = hashMapOf(
                "charack" to hashMapOf(
                    "desc" to desc2,
                    "name" to name2,
                    "text" to text2
                ),
                "count" to count2,
                "id" to id2,
                "image" to image2,
                "price" to price2
            )

            db.collection("itemsList")
                .add(newItem)
                .addOnSuccessListener { documentReference ->
                    Toast.makeText(this, "Товар добавлен! ID: ${documentReference.id}", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Ошибка: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                    Log.e("Firestore", "Error adding document", e)
                }
        }

        val butMenu1: ImageButton = findViewById(R.id.imageButton1)
        val butMenu2: ImageButton = findViewById(R.id.imageButton2)
        val butMenu3: ImageButton = findViewById(R.id.imageButton3)
        val butMenu4: ImageButton = findViewById(R.id.imageButton4)
        val butMenu5: ImageButton = findViewById(R.id.imageButton5)

        // Переходы по страницам меню
        butMenu1.setOnClickListener {
            val intent = Intent(this, HomePage::class.java)
            startActivity(intent)
        }

        butMenu2.setOnClickListener {
            val intent = Intent(this, Catalog::class.java)
            startActivity(intent)
        }

        butMenu3.setOnClickListener {
            val intent = Intent(this, Buket::class.java)
            startActivity(intent)
        }

        butMenu4.setOnClickListener {
            val intent = Intent(this, Basket::class.java)
            startActivity(intent)
        }

        butMenu5.setOnClickListener {
            val intent = Intent(this, Profile::class.java)
            startActivity(intent)
        }

    }
}