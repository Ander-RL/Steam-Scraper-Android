package com.arl.steamscraper

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.arl.steamscraper.data.dao.GameDao
import com.arl.steamscraper.data.entity.Game
import com.arl.steamscraper.data.entity.Price
import com.arl.steamscraper.data.entity.relations.GameAndPrice
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class GameRepository(private val gameDao: GameDao) {

    // Room executes all queries on a separate thread.
    val getAllGames: LiveData<List<Game>> = gameDao.getAllGames()
    val getAllPrices: LiveData<List<Price>>  = gameDao.getAllPrices()
    val getAllGamesAndPrices: LiveData<List<GameAndPrice>>  = gameDao.getAllGamesAndPrices()

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
    suspend fun insert(price: Price) {
        withContext(Dispatchers.IO) {
            gameDao.insert(price)
        }
    }

    @WorkerThread
    suspend fun insertGameAndPrice(game: Game, priceList: List<Price>) {
        withContext(Dispatchers.IO) {
            gameDao.insertGameAndPrice(game, priceList)
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

    /*@WorkerThread
    suspend fun getAllGames(): List<Game> {
        return withContext(Dispatchers.IO) {
            gameDao.getAllGames()
        }
    }*/

    fun getAllGamesAndPrices(appId: Int): LiveData<List<GameAndPrice>> {
        return gameDao.getAllGamesAndPrices(appId)
    }
}