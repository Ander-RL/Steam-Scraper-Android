package com.arl.steamscraper

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arl.steamscraper.data.entity.Game
import com.arl.steamscraper.rds.JsonSteamParser
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.*
import java.net.URL


class MainActivity : AppCompatActivity() {

    private val gameViewModel: GameViewModel by viewModels { GameViewModelFactory((application as GameScraperApplication).repository) }
    private var gameUrl: String = ""
    private var appid: String = ""
    private var name: String = ""
    private var initialPrice: Double = 0.0
    private var finalPrice: Double = 0.0
    private var discount: Int = 0
    private var imageUrl: String = ""
    private var isWindows: Boolean = false
    private var isMac: Boolean = false
    private var isLinux: Boolean = false
    private var games: List<Game> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fab: FloatingActionButton = findViewById(R.id.btn_fab)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val adapter = RVAdapter()

        gameViewModel.gameList.observe(this, Observer { adapter.setData(it) })

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        fab.setOnClickListener { btn: View ->
            // 1. Instantiate an >AlertDialog.Builder with its constructor
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)

            val editTextDialog: EditText = EditText(this)
            editTextDialog.hint = "Enter a game URL"

            builder.setView(editTextDialog)

            // 2. Chain together various setter methods to set the dialog characteristics
            builder.setTitle("Add a Game")
                .apply {
                    setPositiveButton(
                        "Add",
                        DialogInterface.OnClickListener { dialog, id ->
                            gameUrl = editTextDialog.text.toString()
                            parseUrl(gameUrl)
                            dialog.dismiss()
                            Toast.makeText(applicationContext, gameUrl, Toast.LENGTH_SHORT).show()
                        })
                    setNegativeButton(
                        "Cancel",
                        DialogInterface.OnClickListener { dialog, id -> dialog.dismiss() })
                }

            // 3. Get the AlertDialog from create()
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }

        val url1: String = "https://store.steampowered.com/api/appdetails/?appids=1282730"
        val url2: String = "https://store.steampowered.com/api/appdetails/?appids=983970"
        val url3: String = "https://store.steampowered.com/api/appdetails/?appids=1222730"
        val url4: String = "https://store.steampowered.com/api/appdetails/?appids=1128920"
        val url5: String = "https://store.steampowered.com/api/appdetails/?appids=578650"
        val url6: String = "https://store.steampowered.com/api/appdetails/?appids=552500"

        val list: List<String> = listOf(url1, url2, url3, url4, url5, url6)

        for (url: String in list) {
            //parseUrl(url, tvPrueba)
        }
    }

    private fun parseUrl(url: String) {
        MainScope().launch {
            val parser = getSteamParser(url)

            appid = parser.getAppId()
            name = parser.getName()
            initialPrice = parser.getInitialPrice()
            finalPrice = parser.getFinalPrice()
            discount = parser.getDiscount()
            imageUrl = parser.getImageUrl()
            isWindows = parser.isWindows()
            isMac = parser.isMac()
            isLinux = parser.isLinux()

            insertGame()

            Log.d("onCreate", "\n" + "------------------------------------------" + "\n")
            Log.d("onCreate", "$appid \n")
            Log.d("onCreate", "$name \n")
            Log.d("onCreate", "$initialPrice \n")
            Log.d("onCreate", "$finalPrice \n")
            Log.d("onCreate", "$discount \n")
            Log.d("onCreate", "$imageUrl \n")
            Log.d("onCreate", "$isWindows \n")
            Log.d("onCreate", "$isMac \n")
            Log.d("onCreate", "$isLinux \n")
            Log.d("onCreate", "\n" + "------------------------------------------" + "\n")
        }
    }

    private suspend fun getSteamParser(url: String): JsonSteamParser {
        val api = getNetworkRequest(url)
        return JsonSteamParser(url, api)
    }

    private suspend fun getNetworkRequest(url: String): String {
        return withContext(Dispatchers.IO) {
            Log.d("onCreate", "Current thread = " + Thread.currentThread().name)
            URL(url).readText()
        }
    }

    private fun insertGame() {

        val game: Game = Game(Integer.valueOf(appid), name, initialPrice, finalPrice, discount, imageUrl, isWindows, isMac, isLinux, gameUrl)
        gameViewModel.insert(game)

    }
}