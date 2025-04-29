package com.example.thebloomingskyline.catalogue.repository



import androidx.lifecycle.LiveData
import com.example.thebloomingskyline.catalogue.dao.FlowerDao
import com.example.thebloomingskyline.catalogue.entity.Flower
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FlowerRepository public constructor(
    private val flowerDao: FlowerDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    // Реализация Singleton
    companion object {
        @Volatile
        private var INSTANCE: FlowerRepository? = null

        fun getInstance(flowerDao: FlowerDao): FlowerRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = FlowerRepository(flowerDao)
                INSTANCE = instance
                instance
            }
        }
    }

    // Основные операции
    suspend fun insertFlower(flower: Flower) = withContext(ioDispatcher) {
        flowerDao.insert(flower)
    }

    suspend fun updateFlower(flower: Flower) = withContext(ioDispatcher) {
        flowerDao.update(flower)
    }

    suspend fun deleteFlower(flower: Flower) = withContext(ioDispatcher) {
        flowerDao.delete(flower)
    }

    // Получение данных
    fun getFlowerById(id: Int): LiveData<Flower> = flowerDao.getFlowerById(id)

    fun getFlowersByCategory(category: String): LiveData<List<Flower>> {
        return flowerDao.getFlowersByCategory(category)
    }

    fun getAllCategories(): LiveData<List<String>> = flowerDao.getAllCategories()

    // Поиск и фильтрация
    fun searchFlowers(query: String): LiveData<List<Flower>> {
        return flowerDao.searchFlowers("%$query%")
    }

    fun getLowStockFlowers(threshold: Int = 5): LiveData<List<Flower>> {
        return flowerDao.getLowStockFlowers(threshold)
    }

    fun getMostExpensiveFlowers(limit: Int = 5): LiveData<List<Flower>> {
        return flowerDao.getMostExpensiveFlowers(limit)
    }

    // Статистика
    suspend fun getFlowerCountByCategory(category: String): Int {
        return withContext(ioDispatcher) {
            flowerDao.getFlowerCountByCategory(category)
        }
    }

    // Пакетные операции
    suspend fun insertAll(flowers: List<Flower>) = withContext(ioDispatcher) {
        flowers.forEach { flowerDao.insert(it) }
    }

}