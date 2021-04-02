package com.arl.steamscraper

import android.content.Intent
import android.util.Log
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.arl.steamscraper.data.GameRepository
import com.arl.steamscraper.data.entity.Price
import com.arl.steamscraper.data.entity.relations.GameAndPrice
import com.arl.steamscraper.rds.JsonSteamParser
import kotlinx.coroutines.*
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList

class PriceService : LifecycleService() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        Log.d("PriceService", "PriceService --> onStartCommand")

        val repository = (application as GameScraperApplication).repository
        val gameAndPrice: LiveData<List<GameAndPrice>> = repository.getAllGamesAndPrices
        var games = arrayListOf<GameAndPrice>()

        gameAndPrice.observe(this, Observer {

            games = it as ArrayList<GameAndPrice>

            for (element in games) {
                Log.d("PriceService", element.game.toString())
            }

            MainScope().launch { insertPrice(games, repository) }

        })

        return super.onStartCommand(intent, flags, startId)
    }

    private suspend fun insertPrice(gameList: List<GameAndPrice>, repository: GameRepository) {
        withContext(Dispatchers.IO) {
            var counter = 0
            for (game in gameList) {
                Log.d("PriceService", game.listPrice.toString())
                val parser = parseUrl(game.game.gameUrl)
                val price = Price(
                    0,
                    Integer.valueOf(parser.getAppId()),
                    parser.getInitialPrice(),
                    parser.getFinalPrice(),
                    parser.getDiscount(),
                    getDateString()
                )

                if (game.listPrice.last().date != getDateString()) {
                    repository.insert(price)
                }

                if (game.listPrice.last().currentPrice < game.listPrice.last().originalPrice) {
                    val gameName = game.game.name
                    val gamePrice = game.listPrice.last().currentPrice
                    val gameDiscount = game.listPrice.last().discount

                    val notificationHelper = NotificationHelper(applicationContext, "$gameName  ($gamePrice€ - $gameDiscount%)", counter)
                    val nb = notificationHelper.getNotificationChannel()
                    notificationHelper.getManager().notify(counter, nb.build())
                }
                counter++
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

        Log.d("onCreate", urlParsed)

        val api = getNetworkRequest(urlParsed)
        return JsonSteamParser(urlParsed, api)
    }

    private suspend fun getNetworkRequest(url: String): String {
        return withContext(Dispatchers.IO) {
            Log.d("onCreate", "Current thread = " + Thread.currentThread().name)
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
}