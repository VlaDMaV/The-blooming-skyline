package com.example.thebloomingskyline.catalogue.dao


import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.thebloomingskyline.catalogue.entity.Flower

@Dao
interface FlowerDao {
    // Основные CRUD операции
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(flower: Flower)

    @Update
    suspend fun update(flower: Flower)

    @Delete
    suspend fun delete(flower: Flower)

    // Получение данных
    @Query("SELECT * FROM flowers WHERE id = :flowerId")
    fun getFlowerById(flowerId: Int): LiveData<Flower>

    @Query("SELECT * FROM flowers WHERE category = :category ORDER BY name ASC")
    fun getFlowersByCategory(category: String): LiveData<List<Flower>>

    @Query("SELECT DISTINCT category FROM flowers ORDER BY category ASC")
    fun getAllCategories(): LiveData<List<String>>

    @Query("SELECT * FROM flowers WHERE name LIKE :searchQuery OR description LIKE :searchQuery")
    fun searchFlowers(searchQuery: String): LiveData<List<Flower>>

    // Специальные запросы
    @Query("SELECT * FROM flowers WHERE count < :threshold ORDER BY count ASC")
    fun getLowStockFlowers(threshold: Int = 5): LiveData<List<Flower>>

    @Query("SELECT * FROM flowers ORDER BY price DESC LIMIT :limit")
    fun getMostExpensiveFlowers(limit: Int = 5): LiveData<List<Flower>>

    @Query("SELECT COUNT(*) FROM flowers WHERE category = :category")
    suspend fun getFlowerCountByCategory(category: String): Int
}