package com.arl.steamscraper.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.arl.steamscraper.data.entity.Game
import com.arl.steamscraper.data.entity.Price
import com.arl.steamscraper.data.entity.relations.GameAndPrice

@Dao
interface GameDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(game: Game)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(price: Price)

    @Insert
    fun insertGameAndPrice(game: Game, priceList: List<Price>)

    @Update
    fun update(game: Game)

    @Delete
    fun delete(game: Game)

    @Query("DELETE FROM game_table")
    fun deleteAllGames()

    @Query("SELECT * FROM game_table ORDER BY appId ASC")
    fun getAllGames(): LiveData<List<Game>>

    @Transaction
    @Query("SELECT * FROM price_table ORDER BY priceAppId ASC")
    fun getAllPrices(): LiveData<List<Price>>

    @Transaction
    @Query("SELECT * FROM game_table JOIN price_table  WHERE appId = priceAppId ORDER BY currentPrice ASC")
    //@Query("SELECT * FROM game_table ORDER BY name ASC")
    fun getAllGamesAndPrices(): LiveData<List<GameAndPrice>>

    @Transaction
    @Query("SELECT * FROM game_table WHERE appId = :appId")
    fun getAllGamesAndPrices(appId: Int): LiveData<List<GameAndPrice>>
}