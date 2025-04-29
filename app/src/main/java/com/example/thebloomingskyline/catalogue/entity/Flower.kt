package com.example.thebloomingskyline.catalogue.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "flowers",
    indices = [Index(value = ["name"], unique = true)]
)
data class Flower(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String,
    val count: Int,
    val price: Double,
    val category: String,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "image_url") val imageUrl: String? = null
) {
    val isLowStock: Boolean
        get() = count < 5
}