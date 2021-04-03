package com.arl.steamscraper

import android.app.Notification
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.*
import com.arl.steamscraper.data.GameRepository
import com.arl.steamscraper.data.entity.Price
import com.arl.steamscraper.data.entity.relations.GameAndPrice
import com.arl.steamscraper.rds.JsonSteamParser
import kotlinx.coroutines.*
import java.net.URL
import java.util.*

class PriceService : LifecycleService() {

    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startMyForeground()
        } else {
            startForeground(3, Notification())
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        Log.d("PriceService", "PriceService --> onStartCommand")

        val repository = (application as GameScraperApplication).repository

        lifecycleScope.launch {
            val games = repository.getAllGamesAndPricesList() as ArrayList<GameAndPrice>
            insertPrice(games, repository)
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private suspend fun insertPrice(gameList: List<GameAndPrice>, repository: GameRepository) {

        withContext(Dispatchers.IO) {
            for ((counter, game) in gameList.withIndex()) {
                Log.d("insertPrice", game.game.toString())
                Log.d("insertPrice", game.listPrice.toString())
                val parser = parseUrl(game.game.gameUrl)
                val price = Price(
                    0,
                    Integer.valueOf(parser.getAppId()),
                    parser.getInitialPrice(),
                    parser.getFinalPrice(),
                    parser.getDiscount(),
                    getDateString()
                )
                Log.d("insertPrice", "new Price = $price")

                if (price.currentPrice <= game.listPrice.last().currentPrice) {
                    val gameName = game.game.name
                    val gamePrice = price.currentPrice
                    val gameDiscount = price.discount

                    val notificationHelper = NotificationHelper(
                        applicationContext,
                        "Game on Sale!",
                        "$gameName  ($gamePrice€ - $gameDiscount%)",
                        counter
                    )
                    val nb = notificationHelper.getNotificationChannel()
                    notificationHelper.getManager().notify(counter, nb.build())
                }

                if (game.listPrice.last().date != getDateString()) {
                    Log.d("insertPrice", "<--- Inserting new Price --->")
                    repository.insert(price)
                }
            }
        }
    }

    private suspend fun parseUrl(url: String): JsonSteamParser {
        return getSteamParser(url)
    }

    private suspend fun getSteamParser(url: String): JsonSteamParser {

        var urlParsed = url.substring(url.indexOf("/app") + 5, url.length)
        urlParsed = urlParsed.substring(0, urlParsed.indexOf("/"))

        urlParsed = "https://store.steampowered.com/api/appdetails/?appids=$urlParsed"

        Log.d("getSteamParser", urlParsed)

        val api = getNetworkRequest(urlParsed)
        return JsonSteamParser(urlParsed, api)
    }

    private suspend fun getNetworkRequest(url: String): String {
        return withContext(Dispatchers.IO) {
            Log.d("getNetworkRequest", "Current thread = " + Thread.currentThread().name)
            URL(url).readText()
        }
    }

    private fun getDateString(): String {

        val c: Calendar = Calendar.getInstance()
        val day = c.get(Calendar.DAY_OF_MONTH)
        val month = c.get(Calendar.MONTH)
        val year = c.get(Calendar.YEAR)

        return "$day/$month/$year"
    }

    private fun startMyForeground() {
        val notificationHelper = NotificationHelper(this, "Fetching data", "Updating game prices", 4)
        val nb: NotificationCompat.Builder = notificationHelper.getNotificationChannel()
        notificationHelper.getManager().notify(4, nb.build())
        startForeground(4, nb.build())
    }
}