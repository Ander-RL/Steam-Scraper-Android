package com.arl.steamscraper

import android.app.Application
import com.arl.steamscraper.data.GameDataBase

/**
 * We want to only have one instance of the database and of the repository in our application.
 * An easy way to achieve this is by creating them as members of the Application class.
 * Like this, whenever they're needed, they will just be retrieved from the Application,
 * rather than constructed every time.
 * */

class GameScraperApplication : Application() {
    // Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts
    val database by lazy { GameDataBase.getInstance(this) }
    val repository by lazy { GameRepository(database.gameDao) }
}