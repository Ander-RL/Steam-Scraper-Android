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
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arl.steamscraper.data.entity.Game
import com.arl.steamscraper.data.entity.Price
import com.arl.steamscraper.data.entity.relations.GameAndPrice
import com.arl.steamscraper.rds.JsonSteamParser
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.*
import java.net.URL
import java.util.*


class MainActivity : AppCompatActivity() {

    private val gameViewModel: GameViewModel by viewModels { GameViewModelFactory((application as GameScraperApplication).repository) }
    private var gameUrl: String = ""
    private var appid: String = ""
    private var name: String = ""
    private var originalPrice: Double = 0.0
    private var currentPrice: Double = 0.0
    private var discount: Int = 0
    private var imageUrl: String = ""
    private var isWindows: Boolean = false
    private var isMac: Boolean = false
    private var isLinux: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fab: FloatingActionButton = findViewById(R.id.btn_fab)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val adapter = RVAdapter(applicationContext)

        val applicationScope = MainScope()

        gameViewModel.gamesAndPricesList.observe(this, Observer { adapter.setData(it) })

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        fab.setOnClickListener { btn: View ->
            // 1. Instantiate an AlertDialog.Builder with its constructor
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
                            applicationScope.launch { insertGame(parseUrl(gameUrl)) }
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

        val itemTouchHelperCallback = object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                gameViewModel.delete(adapter.gameData.get(viewHolder.absoluteAdapterPosition))
                adapter.gameData.remove(adapter.gameData.get(viewHolder.absoluteAdapterPosition))
                adapter.notifyDataSetChanged()
                Toast.makeText(applicationContext, "Game deleted", Toast.LENGTH_SHORT).show()
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        gameViewModel.gamesAndPricesList.observe(this, Observer {
            applicationScope.launch { insertPrice(it) }
        })

    }

    private suspend fun insertPrice(gameList: List<GameAndPrice>) {
        withContext(Dispatchers.IO) {
            for (game in gameList) {
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
                    gameViewModel.insert(price)
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

    private fun insertGame(parser: JsonSteamParser) {

        appid = parser.getAppId()
        name = parser.getName()
        originalPrice = parser.getInitialPrice()
        currentPrice = parser.getFinalPrice()
        discount = parser.getDiscount()
        imageUrl = parser.getImageUrl()
        isWindows = parser.isWindows()
        isMac = parser.isMac()
        isLinux = parser.isLinux()

        Log.d("onCreate", "\n" + "------------------------------------------" + "\n")
        Log.d("onCreate", "$appid \n")
        Log.d("onCreate", "$name \n")
        Log.d("onCreate", "$originalPrice \n")
        Log.d("onCreate", "$currentPrice \n")
        Log.d("onCreate", "$discount \n")
        Log.d("onCreate", "$imageUrl \n")
        Log.d("onCreate", "$isWindows \n")
        Log.d("onCreate", "$isMac \n")
        Log.d("onCreate", "$isLinux \n")
        Log.d("onCreate", "\n" + "------------------------------------------" + "\n")

        val date = getDateString()
        val game: Game =
            Game(Integer.valueOf(appid), name, imageUrl, isWindows, isMac, isLinux, gameUrl)
        val price: Price =
            Price(0, Integer.valueOf(appid), originalPrice, currentPrice, discount, date)

        Log.d("onCreate", "date = ${getDateString()}")

        gameViewModel.insert(game)
        gameViewModel.insert(price)

    }

    private fun getDateString(): String {

        val c: Calendar = Calendar.getInstance()
        val day = c.get(Calendar.DAY_OF_MONTH)
        val month = c.get(Calendar.MONTH)
        val year = c.get(Calendar.YEAR)

        return "$day/$month/$year"
    }
}