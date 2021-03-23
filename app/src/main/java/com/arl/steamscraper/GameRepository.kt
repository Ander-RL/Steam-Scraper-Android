package com.arl.steamscraper

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.arl.steamscraper.data.dao.GameDao
import com.arl.steamscraper.data.entity.Game
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class GameRepository(private val gameDao: GameDao) {

    // Room executes all queries on a separate thread.
    val getAllGames: LiveData<List<Game>> = gameDao.getAllGames()

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    @WorkerThread
    suspend fun insert(game: Game) {
        withContext(Dispatchers.IO) {
            gameDao.insert(game)
        }
    }

    @WorkerThread
    suspend fun update(game: Game) {
        withContext(Dispatchers.IO) {
            gameDao.update(game)
        }
    }

    @WorkerThread
    suspend fun delete(game: Game) {
        withContext(Dispatchers.IO) {
            gameDao.delete(game)
        }
    }

    @WorkerThread
    suspend fun deleteAllGames() {
        withContext(Dispatchers.IO) {
            gameDao.deleteAllGames()
        }
    }

    @WorkerThread
    suspend fun getAllGames(): LiveData<List<Game>> {
        return withContext(Dispatchers.IO) {
            gameDao.getAllGames()
        }
    }
}