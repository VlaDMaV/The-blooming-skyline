package com.example.thebloomingskyline

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RegActivity : AppCompatActivity() {
    override fun onResume() {
        super.onResume()
        val sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE)
        val savedEmail = sharedPreferences.getString("user_email", null)
        if (savedEmail != null) {
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_reg)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val userEmail: EditText = findViewById(R.id.user_email)
        val userPass: EditText = findViewById(R.id.user_pass)
        val button: Button = findViewById(R.id.button_reg)
        val userPassRepeat: EditText = findViewById(R.id.user_pass_repeat)
        val button2: TextView = findViewById(R.id.sign_up)

        button.setOnClickListener {
            val email = userEmail.text.toString().trim()
            val pass = userPass.text.toString().trim()
            val passRepeat = userPassRepeat.text.toString().trim()

            if (email.isNotEmpty() && pass.isNotEmpty() && passRepeat.isNotEmpty()) {
                if (pass != passRepeat) {
                    Toast.makeText(this, "Пароли не совпадают!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                } else {
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Получаем текущего пользователя
                            val userAuthEmail = FirebaseAuth.getInstance().currentUser

                            // Отправляем письмо с подтверждением
                            userAuthEmail?.sendEmailVerification()?.addOnCompleteListener { verificationTask ->
                                if (verificationTask.isSuccessful) {
                                    Toast.makeText(this, "Письмо отправлено на почту $email", Toast.LENGTH_SHORT).show()

                                    // Перенаправляем пользователя
                                    startActivity(Intent(this, AuthActivity::class.java))
                                } else {
                                    Toast.makeText(this, "Ошибка отправки письма: ${verificationTask.exception?.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            Toast.makeText(this, "Ошибка регистрации: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Заполните все поля!", Toast.LENGTH_SHORT).show()
            }
        }

        button2.setOnClickListener {
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
        }
    }
}