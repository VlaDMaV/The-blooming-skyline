package com.example.thebloomingskyline

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.RadioGroup
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.json.JSONObject
import org.json.JSONTokener

class Profile : AppCompatActivity() {
    private lateinit var sharedPreferences: android.content.SharedPreferences

    // Объявляем список всех View элементов формы
    private val authViews by lazy {
        listOf(
            R.id.button,
            R.id.button2,
            R.id.NameAddGAD,
            R.id.LastNameAddGAD,
            
            R.id.radioGroupGender,
            R.id.PhoneAddGAD,
            R.id.CardAddGAD,
            R.id.ButtonForSave,
            R.id.FirstNameGAD,
            R.id.LastNameGAD,

            R.id.GenderGAD,
            R.id.PhoneGAD,
            R.id.CardGAD,
            R.id.NameGAD,
            R.id.Name2GAD,
            R.id.button_hp,
            R.id.textViewProf
        )
    }

    @SuppressLint("MissingInflatedId")
    override fun onResume() {
        super.onResume()
        refreshData()
    }

    private fun refreshData() {
        val text = findViewById<TextView>(R.id.textViewProf)
        val savedEmail = sharedPreferences.getString("user_email", null)
        if (savedEmail != null) {
            text.text = "Профиль, $savedEmail!"
        }
    }

    private fun showAuthorizedUI(isAuthorized: Boolean) {
        // Сначала скрываем все элементы
        authViews.forEach { viewId ->
            findViewById<View>(viewId).visibility = View.GONE
        }

        // Затем показываем нужные элементы
        if (isAuthorized) {
            // Элементы для авторизованных пользователей
            listOf(
                R.id.button, R.id.NameAddGAD, R.id.LastNameAddGAD,
                 R.id.radioGroupGender, R.id.PhoneAddGAD,
                R.id.CardAddGAD, R.id.ButtonForSave,
                R.id.FirstNameGAD, R.id.LastNameGAD,
                R.id.GenderGAD, R.id.PhoneGAD, R.id.CardGAD,
                R.id.NameGAD, R.id.Name2GAD, R.id.textViewProf
            ).forEach { viewId ->
                findViewById<View>(viewId).visibility = View.VISIBLE
            }
        } else {
            // Элементы для неавторизованных пользователей
            listOf(R.id.button_hp, R.id.button2, R.id.textViewProf).forEach { viewId ->
                findViewById<View>(viewId).visibility = View.VISIBLE
            }
        }

        // Устанавливаем начальный текст
        findViewById<TextView>(R.id.textViewProf).apply {
            text = if (isAuthorized) "Профиль" else "Добро пожаловать"
            visibility = View.VISIBLE
        }
    }

    private fun loadUsersDictionary(): MutableMap<String, HashMap<String, String>> {
        val json = sharedPreferences.getString("user_dict", null) ?: return mutableMapOf()
        return try {
            val jsonMap = JSONObject(JSONTokener(json))
            val result = mutableMapOf<String, HashMap<String, String>>()

            for (email in jsonMap.keys()) {
                val userData = jsonMap.getJSONObject(email)
                val userMap = hashMapOf<String, String>()

                for (key in userData.keys()) {
                    userMap[key] = userData.getString(key)
                }

                result[email] = userMap
            }

            result
        } catch (e: Exception) {
            mutableMapOf()
        }
    }

    private fun saveUsersDictionary(usersMap: Map<String, HashMap<String, String>>) {
        try {
            val mainJsonObject = JSONObject()

            for ((email, userData) in usersMap) {
                val userJsonObject = JSONObject()
                for ((key, value) in userData) {
                    userJsonObject.put(key, value)
                }
                mainJsonObject.put(email, userJsonObject)
            }

            sharedPreferences.edit()
                .putString("user_dict", mainJsonObject.toString())
                .apply()
        } catch (e: Exception) {
            Log.e("SaveUsers", "Error saving users dictionary", e)
        }
    }

    private fun saveUserData() {
        val savedEmail = sharedPreferences.getString("user_email", null)
        if (savedEmail == null) return

        val firstName = findViewById<TextView>(R.id.NameAddGAD).text.toString().trim()
        val lastName = findViewById<TextView>(R.id.LastNameAddGAD).text.toString().trim()

        val number = findViewById<TextView>(R.id.PhoneAddGAD).text.toString().trim()
        val cardNumber = findViewById<TextView>(R.id.CardAddGAD).text.toString().trim()
        val gender = when (findViewById<RadioGroup>(R.id.radioGroupGender).checkedRadioButtonId) {
            R.id.radioMale -> "Male"
            R.id.radioFemale -> "Female"
            else -> "Unknown"
        }

        val usersMap = loadUsersDictionary()
        val userData = hashMapOf(
            "first_name" to firstName,
            "last_name" to lastName,

            "gender" to gender,
            "number" to number,
            "cardnumber" to cardNumber
        )

        usersMap[savedEmail] = userData
        saveUsersDictionary(usersMap)
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)
        sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val savedEmail = sharedPreferences.getString("user_email", null)
        showAuthorizedUI(savedEmail != null)

        findViewById<TextView>(R.id.textViewProf).text = "Профиль"

        findViewById<Button>(R.id.button).setOnClickListener {
            sharedPreferences.edit().clear().apply()
            findViewById<TextView>(R.id.textViewProf).text = "Профиль"
        }

        findViewById<Button>(R.id.button2).setOnClickListener {
            startActivity(Intent(this, RegActivity::class.java))
        }

        findViewById<Button>(R.id.button_hp).setOnClickListener {
            startActivity(Intent(this, AuthActivity::class.java))
        }

        findViewById<Button>(R.id.ButtonForSave).setOnClickListener {
            saveUserData()
        }
    }
}