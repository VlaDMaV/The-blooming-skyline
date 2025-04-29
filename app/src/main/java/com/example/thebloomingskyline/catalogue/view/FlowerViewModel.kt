package com.example.thebloomingskyline.catalogue.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.thebloomingskyline.catalogue.config.FlowerDatabase

import com.example.thebloomingskyline.catalogue.entity.Flower
import com.example.thebloomingskyline.catalogue.repository.FlowerRepository
import kotlinx.coroutines.launch

class FlowerViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: FlowerRepository

    init {
        val flowerDao = FlowerDatabase.getDatabase(application).flowerDao()
        repository = FlowerRepository(flowerDao)
    }

    // LiveData для наблюдения
    private val _currentCategory = MutableLiveData<String>()
    val currentCategory: LiveData<String> = _currentCategory

    private val _operationStatus = MutableLiveData<OperationStatus>()
    val operationStatus: LiveData<OperationStatus> = _operationStatus

    // Цветы по текущей категории
    val flowersByCategory: LiveData<List<Flower>> = currentCategory.switchMap { category ->
        repository.getFlowersByCategory(category)
    }

    // Все категории
    val allCategories: LiveData<List<String>> = repository.getAllCategories()

    // Поисковые результаты
    private val _searchQuery = MutableLiveData<String>()
    val searchResults: LiveData<List<Flower>> = _searchQuery.switchMap { query ->
        if (query.isBlank()) {
            flowersByCategory
        } else {
            repository.searchFlowers(query)
        }
    }

    // Цветы с низким запасом
    val lowStockFlowers: LiveData<List<Flower>> = repository.getLowStockFlowers()

    // Самые дорогие цветы
    val mostExpensiveFlowers: LiveData<List<Flower>> = repository.getMostExpensiveFlowers()

    // Установка текущей категории
    fun setCategory(category: String) {
        _currentCategory.value = category
    }

    // Операции с цветами
    fun addFlower(flower: Flower) {
        viewModelScope.launch {
            try {
                repository.insertFlower(flower)
                _operationStatus.value = OperationStatus.SUCCESS("Цветок добавлен")
            } catch (e: Exception) {
                _operationStatus.value = OperationStatus.ERROR(e.message ?: "Ошибка добавления")
            }
        }
    }

    fun getFlowersByCategory(category: String): LiveData<List<Flower>> {
        return repository.getFlowersByCategory(category)
    }

    fun updateFlower(flower: Flower) {
        viewModelScope.launch {
            try {
                repository.updateFlower(flower)
                _operationStatus.value = OperationStatus.SUCCESS("Изменения сохранены")
            } catch (e: Exception) {
                _operationStatus.value = OperationStatus.ERROR(e.message ?: "Ошибка обновления")
            }
        }
    }

    fun deleteFlower(flower: Flower) {
        viewModelScope.launch {
            try {
                repository.deleteFlower(flower)
                _operationStatus.value = OperationStatus.SUCCESS("Цветок удален")
            } catch (e: Exception) {
                _operationStatus.value = OperationStatus.ERROR(e.message ?: "Ошибка удаления")
            }
        }
    }

    // Поиск
    fun searchFlowers(query: String) {
        _searchQuery.value = query
    }

    // Получение цветка по ID
    fun getFlowerById(id: Int): LiveData<Flower> {
        return repository.getFlowerById(id)
    }

    // Статистика
    suspend fun getFlowerCountByCategory(category: String): Int {
        return repository.getFlowerCountByCategory(category)
    }

    // Пакетные операции
    fun importSampleFlowers() {
        viewModelScope.launch {
            try {
                val sampleFlowers = listOf(
                    Flower(name = "Роза", description = "Красная роза", count = 10, price = 150.0, category = "Розы"),
                    Flower(name = "Тюльпан", description = "Желтый тюльпан", count = 15, price = 75.0, category = "Тюльпаны"),
                    // ... другие примеры
                )
                repository.insertAll(sampleFlowers)
                _operationStatus.value = OperationStatus.SUCCESS("Примеры цветов добавлены")
            } catch (e: Exception) {
                _operationStatus.value = OperationStatus.ERROR(e.message ?: "Ошибка импорта")
            }
        }
    }

    // Состояния операций
    sealed class OperationStatus {
        data class SUCCESS(val message: String) : OperationStatus()
        data class ERROR(val error: String) : OperationStatus()
        object LOADING : OperationStatus()
    }

    fun addSampleRoses() {
        viewModelScope.launch {
            val sampleFlowers = listOf(
                // Гвоздики
                Flower(
                    id = 0,
                    name = "Турецкая гвоздика",
                    description = " двулетнее или многолетнее садовое растение с яркими, ароматными цветками, собранными в плотные щитковидные соцветия",
                    count = 20,
                    price = 250.0,
                    category = "Гвоздики",
                    imageUrl = "turgv"
                ),
                Flower(
                    id = 0,
                    name = "Гвоздика садовая",
                    description = "декоративное растение с красивыми, часто ароматными цветами",
                    count = 15,
                    price = 280.0,
                    category = "Гвоздики",
                    imageUrl = "sadgv"
                ),
                Flower(
                    id = 0,
                    name = "Гвоздика китайская",
                    description = "невысокое однолетнее или многолетнее растение с изящными цветками, часто используемое в бордюрах, клумбах и контейнерном озеленении",
                    count = 5,
                    price = 350.0,
                    category = "Гвоздики",
                    imageUrl = "kitgv"
                ),
                // Розы
                Flower(
                    id = 0,
                    name = "Красная роза",
                    description = "Классическая роза алого цвета, символ любви",
                    count = 20,
                    price = 250.0,
                    category = "Весенние новинки",
                    imageUrl = "redrose"
                ),
                Flower(
                    id = 0,
                    name = "Белая роза",
                    description = "Чистая белая роза, символ невинности",
                    count = 15,
                    price = 280.0,
                    category = "Весенние новинки",
                    imageUrl = "whiterose"
                ),
                Flower(
                    id = 0,
                    name = "Черная роза",
                    description = "Экзотическая темно-бордовая роза",
                    count = 5,
                    price = 350.0,
                    category = "Весенние новинки",
                    imageUrl = "blackrose"
                ),

                // Тюльпаны
                Flower(
                    id = 0,
                    name = "Красный тюльпан",
                    description = "Яркий красный тюльпан, символ страсти",
                    count = 30,
                    price = 150.0,
                    category = "Тюльпаны",
                    imageUrl = "redtul"
                ),
                Flower(
                    id = 0,
                    name = "Желтый тюльпан",
                    description = "Солнечный желтый тюльпан, символ радости",
                    count = 25,
                    price = 160.0,
                    category = "Тюльпаны",
                    imageUrl = "yellowtul"
                ),
                Flower(
                    id = 0,
                    name = "Фиолетовый тюльпан",
                    description = "Элегантный фиолетовый тюльпан",
                    count = 18,
                    price = 180.0,
                    category = "Тюльпаны",
                    imageUrl = "purpletul"
                ),

                // Лилии
                Flower(
                    id = 0,
                    name = "Белая лилия",
                    description = "Чистая белая лилия, символ благородства",
                    count = 12,
                    price = 320.0,
                    category = "Лилии",
                    imageUrl = "whitel"
                ),
                Flower(
                    id = 0,
                    name = "Розовая лилия",
                    description = "Нежная розовая лилия",
                    count = 10,
                    price = 340.0,
                    category = "Лилии",
                    imageUrl = "pinkl"
                ),
                Flower(
                    id = 0,
                    name = "Тигровая лилия",
                    description = "Экзотическая оранжевая лилия с пятнами",
                    count = 8,
                    price = 360.0,
                    category = "Лилии",
                    imageUrl = "tigerl"
                ),

                // Орхидеи
                Flower(
                    id = 0,
                    name = "Белая орхидея",
                    description = "Изысканная белая орхидея",
                    count = 7,
                    price = 450.0,
                    category = "Орхидеи",
                    imageUrl = "whiteo"
                ),
                Flower(
                    id = 0,
                    name = "Фиолетовая орхидея",
                    description = "Королевская фиолетовая орхидея",
                    count = 5,
                    price = 480.0,
                    category = "Орхидеи",
                    imageUrl = "purpleo"
                ),
                Flower(
                    id = 0,
                    name = "Розовая орхидея",
                    description = "Нежная розовая орхидея",
                    count = 6,
                    price = 470.0,
                    category = "Орхидеи",
                    imageUrl = "pinko"
                ),

                // Пионы
                Flower(
                    id = 0,
                    name = "Розовый пион",
                    description = "Пышный розовый пион, символ богатства",
                    count = 15,
                    price = 380.0,
                    category = "Пионы",
                    imageUrl = "pinkp"
                ),
                Flower(
                    id = 0,
                    name = "Белый пион",
                    description = "Нежный белый пион",
                    count = 12,
                    price = 390.0,
                    category = "Пионы",
                    imageUrl = "whitep"
                ),
                Flower(
                    id = 0,
                    name = "Красный пион",
                    description = "Яркий красный пион",
                    count = 10,
                    price = 400.0,
                    category = "Пионы",
                    imageUrl = "redp"
                ),

                // Хризантемы
                Flower(
                    id = 0,
                    name = "Белая хризантема",
                    description = "Свежая белая хризантема",
                    count = 20,
                    price = 220.0,
                    category = "Хризантемы",
                    imageUrl = "https://example.com/white_chrysanthemum.jpg"
                ),
                Flower(
                    id = 0,
                    name = "Желтая хризантема",
                    description = "Солнечная желтая хризантема",
                    count = 22,
                    price = 210.0,
                    category = "Хризантемы",
                    imageUrl = "https://example.com/yellow_chrysanthemum.jpg"
                )
            )
            sampleFlowers.forEach { repository.insertFlower(it) }
        }
    }
}