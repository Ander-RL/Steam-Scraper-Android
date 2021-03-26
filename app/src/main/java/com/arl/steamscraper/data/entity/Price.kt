package com.arl.steamscraper.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey

@Entity(tableName = "price_table", foreignKeys = [
    ForeignKey(
        entity = Game::class,
        parentColumns = ["appId"],
        childColumns = ["appId"],
        onDelete = CASCADE
    )
])
data class Price(
    @PrimaryKey(autoGenerate = true)
    val idPrice: Int = 0,
    var appId: Int,
    val originalPrice: Double,
    val currentPrice: Double,
    val discount: Int
)