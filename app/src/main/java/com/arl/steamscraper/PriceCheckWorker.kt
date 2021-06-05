package com.arl.steamscraper

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.arl.steamscraper.data.GameRepository
import com.arl.steamscraper.data.entity.Price
import com.arl.steamscraper.data.entity.relations.GameAndPrice
import com.arl.steamscraper.parser.JsonSteamParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL
import java.util.*

class PriceCheckWorker(appContext: Context, workerParams: WorkerParameters): Worker(appContext, workerParams) {

    override fun doWork(): Result {

        val repository = (applicationContext as GameScraperApplication).repository

        MainScope().launch {
            val games = repository.getAllGamesAndPricesList() as ArrayList<GameAndPrice>
            insertPrice(games, repository)
        }

        return Result.success()
    }

    private suspend fun insertPrice(gameList: List<GameAndPrice>, repository: GameRepository) {

        withContext(Dispatchers.IO) {
            for ((counter, game) in gameList.withIndex()) {
                val parser = parseUrl(game.game.gameUrl)
                val price = Price(
                    0,
                    Integer.valueOf(parser.getAppId()),
                    parser.getInitialPrice(),
                    parser.getFinalPrice(),
                    parser.getDiscount(),
                    getDateString()
                )

                if (price.currentPrice < game.listPrice.last().currentPrice) {
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

        val api = getNetworkRequest(urlParsed)
        return JsonSteamParser(urlParsed, api)
    }

    private suspend fun getNetworkRequest(url: String): String {
        return withContext(Dispatchers.IO) {
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