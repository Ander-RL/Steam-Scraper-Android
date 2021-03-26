package com.arl.steamscraper.data.entity.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.arl.steamscraper.data.entity.Game
import com.arl.steamscraper.data.entity.Price

data class GameAndPrice(
    @Embedded val game: Game,
    @Relation(
        parentColumn = "appId",
        entityColumn = "idPrice"
    )
    val listPrice: List<Price> = emptyList()
)