package com.example.thebloomingskyline

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.json.JSONObject
import org.json.JSONTokener
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Profile : AppCompatActivity() {
    private val db = Firebase.firestore

    companion object {
        private const val AUTH_REQUEST_CODE = 1001
    }

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
        val savedEmail = sharedPreferences.getString("user_email", null) ?: return

        val firstName = findViewById<EditText>(R.id.NameAddGAD).text.toString().trim()
        val lastName = findViewById<EditText>(R.id.LastNameAddGAD).text.toString().trim()
        val phone = findViewById<EditText>(R.id.PhoneAddGAD).text.toString().trim()
        val cardNumber = findViewById<EditText>(R.id.CardAddGAD).text.toString().trim()
        val gender = when (findViewById<RadioGroup>(R.id.radioGroupGender).checkedRadioButtonId) {
            R.id.radioMale -> "Male"
            R.id.radioFemale -> "Female"
            else -> "Unknown"
        }

        // Создаем объект с данными пользователя
        val user = hashMapOf(
            "first_name" to firstName,
            "last_name" to lastName,
            "phone" to phone,
            "card_number" to cardNumber,
            "gender" to gender,
            "email" to savedEmail,
            "timestamp" to System.currentTimeMillis() // Добавляем метку времени
        )

        // Сохраняем в коллекцию "infAboutUsers" в Firestore
        db.collection("infAboutUsers") // Используем ваше название коллекции
            .document(savedEmail) // Используем email как ID документа
            .set(user)
            .addOnSuccessListener {
                Toast.makeText(this, "Данные сохранены в Firestore", Toast.LENGTH_SHORT).show()

                // Дополнительно сохраняем локально (опционально)
                val usersMap = loadUsersDictionary().apply {
                    this[savedEmail] = hashMapOf(
                        "first_name" to firstName,
                        "last_name" to lastName,
                        "number" to phone,
                        "cardnumber" to cardNumber,
                        "gender" to gender
                    )
                }
                saveUsersDictionary(usersMap)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Ошибка: ${e.message}", Toast.LENGTH_SHORT).show()
            }
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

        // Если пользователь не авторизован - сразу переходим на регистрацию
        if (savedEmail == null) {
            // Если не авторизован - переходим на RegActivity
            startActivity(Intent(this, RegActivity::class.java))
            finish()
            return
        }

        // Загружаем и отображаем данные пользователя
        loadAndDisplayUserData(savedEmail)

        showAuthorizedUI(true)

        findViewById<TextView>(R.id.textViewProf).text = "Профиль"

        findViewById<Button>(R.id.button).setOnClickListener {
            clearUserData()
            recreate()
        }

        findViewById<Button>(R.id.button2).setOnClickListener {
            startActivity(Intent(this, RegActivity::class.java))
        }

        findViewById<Button>(R.id.button_hp).setOnClickListener {
            startActivityForResult(Intent(this, AuthActivity::class.java), AUTH_REQUEST_CODE)
        }

        findViewById<Button>(R.id.ButtonForSave).setOnClickListener {
            saveUserData()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AUTH_REQUEST_CODE && resultCode == RESULT_OK) {
            recreate() // Перезагружаем Activity после успешного входа
        }
    }

    private fun loadAndDisplayUserData(email: String) {
        db.collection("infAboutUsers") // Ваше название коллекции
            .document(email)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // Заполняем поля из Firestore
                    findViewById<EditText>(R.id.NameAddGAD).setText(document.getString("first_name") ?: "")
                    findViewById<EditText>(R.id.LastNameAddGAD).setText(document.getString("last_name") ?: "")
                    findViewById<EditText>(R.id.PhoneAddGAD).setText(document.getString("phone") ?: "")
                    findViewById<EditText>(R.id.CardAddGAD).setText(document.getString("card_number") ?: "")

                    // Устанавливаем пол
                    when (document.getString("gender")) {
                        "Male" -> findViewById<RadioGroup>(R.id.radioGroupGender).check(R.id.radioMale)
                        "Female" -> findViewById<RadioGroup>(R.id.radioGroupGender).check(R.id.radioFemale)
                    }
                } else {
                    loadLocalUserData(email) // Загрузка локальных данных, если Firestore пуст
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Ошибка загрузки из Firestore", Toast.LENGTH_SHORT).show()
                loadLocalUserData(email)
            }
    }

    private fun loadLocalUserData(email: String) {
        val usersMap = loadUsersDictionary()
        val userData = usersMap[email]

        userData?.let {
            findViewById<EditText>(R.id.NameAddGAD).setText(it["first_name"] ?: "")
            findViewById<EditText>(R.id.LastNameAddGAD).setText(it["last_name"] ?: "")
            findViewById<EditText>(R.id.PhoneAddGAD).setText(it["number"] ?: "")
            findViewById<EditText>(R.id.CardAddGAD).setText(it["cardnumber"] ?: "")

            val gender = it["gender"] ?: "Unknown"
            val radioGroup = findViewById<RadioGroup>(R.id.radioGroupGender)
            radioGroup.clearCheck()
            when (gender) {
                "Male" -> radioGroup.check(R.id.radioMale)
                "Female" -> radioGroup.check(R.id.radioFemale)
            }
        }
    }

    private fun clearUserData() {
        sharedPreferences.edit().clear().apply()

        // Очищаем все поля
        findViewById<EditText>(R.id.NameAddGAD).text.clear()
        findViewById<EditText>(R.id.LastNameAddGAD).text.clear()
        findViewById<EditText>(R.id.PhoneAddGAD).text.clear()
        findViewById<EditText>(R.id.CardAddGAD).text.clear()
        findViewById<RadioGroup>(R.id.radioGroupGender).clearCheck()

        // Очищаем TextView
        findViewById<TextView>(R.id.NameGAD).text = ""
        findViewById<TextView>(R.id.Name2GAD).text = ""
        findViewById<TextView>(R.id.GenderGAD).text = "Пол"
        findViewById<TextView>(R.id.PhoneGAD).text = "Телефон"
        findViewById<TextView>(R.id.CardGAD).text = "Номер карты"
    }

}