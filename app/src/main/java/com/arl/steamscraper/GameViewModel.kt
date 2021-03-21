package com.arl.steamscraper

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.arl.steamscraper.data.entity.Game
import kotlinx.coroutines.launch

class GameViewModel(private val repository: GameRepository) : ViewModel() {

    val gameList: LiveData<List<Game>> = repository.getAllGames

    fun insert(game: Game) = viewModelScope.launch { repository.insert(game) }
    fun update(game: Game) = viewModelScope.launch { repository.update(game) }
    fun delete(game: Game) = viewModelScope.launch { repository.delete(game) }
    fun deleteAllGames()   = viewModelScope.launch { repository.deleteAllGames() }
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