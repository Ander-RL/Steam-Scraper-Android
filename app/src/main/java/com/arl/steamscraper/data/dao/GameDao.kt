package com.arl.steamscraper.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.arl.steamscraper.data.entity.Game

@Dao
interface GameDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(game: Game)

    @Update
    fun update(game: Game)

    @Delete
    fun delete(game: Game)

    @Query("DELETE FROM game_table")
    fun deleteAllGames()

    @Query("SELECT * FROM game_table ORDER BY appId DESC")
    fun getAllGames(): LiveData<List<Game>>
}