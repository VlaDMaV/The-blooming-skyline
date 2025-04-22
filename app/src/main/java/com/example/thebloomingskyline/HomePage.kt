package com.example.thebloomingskyline

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import android.content.Intent
import androidx.core.view.WindowInsetsCompat
import android.widget.TextView
import android.widget.Button
import android.widget.ImageButton

class HomePage : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val text: TextView = findViewById(R.id.textView2)

        val butMenu1: ImageButton = findViewById(R.id.imageButton1)
        val butMenu2: ImageButton = findViewById(R.id.imageButton2)
        val butMenu3: ImageButton = findViewById(R.id.imageButton3)
        val butMenu4: ImageButton = findViewById(R.id.imageButton4)
        val butMenu5: ImageButton = findViewById(R.id.imageButton5)

        text.text = "Привет!"

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