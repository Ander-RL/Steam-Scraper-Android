package com.arl.steamscraper.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_table")
data class Game(
    @PrimaryKey(autoGenerate = false)
    val appId: Int,
    val name: String,
    val initialPrice: Double,
    val finalPrice: Double,
    val discount: Int,
    val imageUrl: String,
    val isWindows: Boolean,
    val isMac: Boolean,
    val isLinux: Boolean,
    val gameUrl: String
)
