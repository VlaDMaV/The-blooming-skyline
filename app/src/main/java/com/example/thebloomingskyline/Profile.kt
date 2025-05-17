package com.example.thebloomingskyline

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Profile : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onResume() {
        super.onResume()
        refreshData() // Ваш метод для обновления данных
    }

    private fun refreshData() {
        val text: TextView = findViewById(R.id.textViewProf)
            val sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE)
        val savedEmail = sharedPreferences.getString("user_email", null)
        if (savedEmail != null) {
            text.text = "Профиль, $savedEmail!"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val text: TextView = findViewById(R.id.textViewProf)

        val button: Button = findViewById(R.id.button2)
        val button2: Button = findViewById(R.id.button_hp)
        val butAnReg: Button = findViewById(R.id.button)
        val buttonOrders: Button = findViewById(R.id.button_orders)

        text.text = "Профиль"
        
        val sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE)
        val savedEmail = sharedPreferences.getString("user_email", null)

        butAnReg.setOnClickListener {
            sharedPreferences.edit().clear().apply()
            if (savedEmail != null) {
                text.text = "Профиль"
            }
        }

        button.setOnClickListener {
            val intent = Intent(this, RegActivity::class.java)
            startActivity(intent)
        }

        button2.setOnClickListener {
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
        }

        buttonOrders.setOnClickListener {
            val intent = Intent(this, OrdersActivity::class.java)
            startActivity(intent)
        }

    }
}