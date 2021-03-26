package com.arl.steamscraper.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.arl.steamscraper.data.dao.GameDao
import com.arl.steamscraper.data.entity.Game
import com.arl.steamscraper.data.entity.Price
import com.arl.steamscraper.data.entity.relations.GameAndPrice
import kotlinx.coroutines.CoroutineScope

@Database(entities = [Game::class, Price::class], version = 1, exportSchema = false)
abstract class GameDataBase : RoomDatabase() {

    abstract val gameDao: GameDao

    companion object {
        // The value of a volatile variable will never be cached,
        // and all writes and reads will be done to and from the main memory
        @Volatile
        private var INSTANCE: GameDataBase? = null

        fun getInstance(context: Context): GameDataBase { // this = context
            // Wrapping the code to get the database into synchronized means that only one thread
            // of execution at a time can enter this block of code,
            // which makes sure the database only gets initialized once.
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        GameDataBase::class.java,
                        "game_database"
                    ).fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}