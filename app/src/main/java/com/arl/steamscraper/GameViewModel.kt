package com.arl.steamscraper

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.arl.steamscraper.data.GameRepository
import com.arl.steamscraper.data.entity.Game
import com.arl.steamscraper.data.entity.Price
import com.arl.steamscraper.data.entity.relations.GameAndPrice
import kotlinx.coroutines.launch

class GameViewModel(private val repository: GameRepository) : ViewModel() {

    val gameList: LiveData<List<Game>> = repository.getAllGames
    val priceList: LiveData<List<Price>>  = repository.getAllPrices
    val gamesAndPricesList: LiveData<List<GameAndPrice>>  = repository.getAllGamesAndPrices

    fun insert(game: Game) = viewModelScope.launch { repository.insert(game) }
    fun insert(price: Price) = viewModelScope.launch { repository.insert(price) }
    fun insertGameAndPrice(game: Game, priceList: List<Price>) = viewModelScope.launch { repository.insertGameAndPrice(game, priceList) }
    fun update(game: Game) = viewModelScope.launch { repository.update(game) }
    fun delete(game: Game) = viewModelScope.launch { repository.delete(game) }
    fun deleteAllGames()   = viewModelScope.launch { repository.deleteAllGames() }
    //fun getAllGames() = viewModelScope.launch { repository.getAllGames() }
    fun getAllGamesAndPrices(appId: Int): LiveData<List<GameAndPrice>> = repository.getAllGamesAndPrices(appId)
}

class GameViewModelFactory(private val repository: GameRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GameViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}