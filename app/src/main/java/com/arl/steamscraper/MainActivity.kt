package com.arl.steamscraper

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
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
import com.arl.steamscraper.rds.JsonSteamParser
import com.arl.steamscraper.receiver.AlertReceiver
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
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

    lateinit var adView: AdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val applicationScope = MainScope()
        val context = applicationContext

        applicationScope.launch {
            MobileAds.initialize(context) {}
            adView = findViewById(R.id.adViewBanner)
            val adRequest = AdRequest.Builder().build()
            adView.loadAd(adRequest)
        }

        val fab: FloatingActionButton = findViewById(R.id.btn_fab)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        recyclerView.setHasFixedSize(true)
        val adapter = RVAdapter(applicationContext)

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
                            if (!editTextDialog.text.isEmpty()) {
                                if (!editTextDialog.text.contains("https://store.steampowered.com/app/")) {
                                    Toast.makeText(
                                        applicationContext,
                                        "Please, insert a correct URL",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    applicationScope.launch { insertGame(parseUrl(gameUrl)) }
                                    Toast.makeText(applicationContext, gameUrl, Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(
                                    applicationContext,
                                    "Please, insert a value",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            dialog.dismiss()
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

        // Click listener for each card
        adapter.setOnItemClickListener(object : RVAdapter.OnItemClickListener {
            override fun onItemClick(game: Game) {
                // Toast.makeText(applicationContext, "Listener", Toast.LENGTH_SHORT).show()
            }
        })

        // Daily price check
        startAlarm(Calendar.getInstance())
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

        val date = getDateString()
        val game: Game =
            Game(Integer.valueOf(appid), name, imageUrl, isWindows, isMac, isLinux, gameUrl)
        val price: Price =
            Price(0, Integer.valueOf(appid), originalPrice, currentPrice, discount, date)

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

    private fun startAlarm(c: Calendar) {

        val intent = Intent(applicationContext, AlertReceiver::class.java)
        intent.putExtra("daily_check", "daily_check")

        val alarmManager =
            applicationContext.getSystemService(Context.ALARM_SERVICE) as? AlarmManager

        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext, 0,
            intent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        if (alarmManager != null && pendingIntent != null) {
            c.set(Calendar.HOUR_OF_DAY, 10)
            alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                c.timeInMillis + (24 * 60 * 60 * 1000),
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
        }
    }
}