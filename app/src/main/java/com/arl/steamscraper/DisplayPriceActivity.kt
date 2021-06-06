package com.arl.steamscraper

import android.os.Bundle
import android.util.Log
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arl.steamscraper.data.entity.Game
import com.arl.steamscraper.data.entity.Price
import com.arl.steamscraper.data.entity.relations.GameAndPrice
import com.arl.steamscraper.rvAdapters.RVAdapter
import com.arl.steamscraper.rvAdapters.RVpriceAdapter
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.util.ArrayList

class DisplayPriceActivity : AppCompatActivity()  {

    lateinit var adView: AdView
    lateinit var applicationScope: CoroutineScope

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_price)

        applicationScope = MainScope()
        val appContext = applicationContext

        applicationScope.launch {
            MobileAds.initialize(appContext) {}
            adView = findViewById(R.id.adViewBanner)
            val adRequest = AdRequest.Builder().build()
            adView.loadAd(adRequest)
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview_pricehistory)
        recyclerView.setHasFixedSize(true)
        val adapter = RVpriceAdapter(applicationContext)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        val repository = (applicationContext as GameScraperApplication).repository
        val appId = Integer.valueOf(intent.getStringExtra(MainActivity.TAG_MAIN).toString())

        var game: Game? = null
        var priceList = arrayListOf<Price>()

        MainScope().launch {
            val gameAndPrice = repository.getAllGamesAndPricesList() as ArrayList<GameAndPrice>
            for (games: GameAndPrice in gameAndPrice) {
                if (games.game.appId == appId) {
                    game = games.game
                    priceList = games.listPrice as ArrayList<Price>
                }
            }
            adapter.setData(priceList)
        }
    }
}