package com.example.thebloomingskyline

import android.annotation.SuppressLint
import android.app.Application
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase

class AuthActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_auth)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val userEmail = findViewById<EditText>(R.id.user_email)
        val userPass = findViewById<EditText>(R.id.user_pass)
        val button = findViewById<Button>(R.id.button_reg)
        val resendButton = findViewById<Button>(R.id.button_resend)

        // Сначала скрываем кнопку повторной отправки
        resendButton.visibility = TextView.GONE

        button.setOnClickListener {
            val email = userEmail.text.toString().trim()
            val pass = userPass.text.toString().trim()

            if (email.isNotEmpty() && pass.isNotEmpty()) {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, pass).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val userAuthEmail = FirebaseAuth.getInstance().currentUser
                        if (userAuthEmail?.isEmailVerified == true) {
                            // Сохраняем email пользователя для дальнейшего работы с БД
                            val sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE)
                            sharedPreferences.edit().putString("user_email", email).apply()
                            // Пользователь подтвердил email
                            val intent = Intent(this, Profile::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            finish()
                        } else {
                            // Пользователь не подтвердил email
                            Toast.makeText(this, "Пожалуйста, подтвердите свой email", Toast.LENGTH_SHORT).show()

                            // Показываем кнопку повторной отправки
                            resendButton.visibility = TextView.VISIBLE
                        }
                    } else {
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                Toast.makeText(this, "Заполните все поля!", Toast.LENGTH_SHORT).show()
            }
        }

        // Обработчик для кнопки повторной отправки
        resendButton.setOnClickListener {
            val user = FirebaseAuth.getInstance().currentUser

            user?.sendEmailVerification()?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Письмо с подтверждением отправлено повторно", Toast.LENGTH_LONG).show()
                    resendButton.visibility = TextView.GONE // Скрываем кнопку после отправки
                } else {
                    Toast.makeText(this, "Ошибка отправки: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}