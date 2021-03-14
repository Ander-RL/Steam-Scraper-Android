package com.arl.steamscraper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import com.arl.steamscraper.JsonSteamObject.SteamData.GameSteam

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_prueba)

        val tv_prueba: TextView = findViewById(R.id.tv_prueba)

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://store.steampowered.com/api/appdetails/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val jsonSteamApi: JsonSteamApi = retrofit.create(JsonSteamApi::class.java)

        val call = jsonSteamApi.getGameSteam()

        call.enqueue(object: Callback<JsonSteamObject> {

            override fun onResponse(call: Call<JsonSteamObject>, response: Response<JsonSteamObject>) {
                if(!response.isSuccessful) {
                    tv_prueba.text = response.code().toString()
                } else {

                    val data: JsonSteamObject.SteamData? = response.body()?.steamObject
                    Log.d("onResponse", response.toString())
                    Log.d("onResponse", response.body().toString())
                    Log.d("onResponse", response.body()?.steamObject.toString())
                    Log.d("onResponse", response.body()?.steamObject?.gameSteam.toString())

                    if(data != null){
                        val game: GameSteam = data.gameSteam
                        var content = ""
                        content += "type = " + game.type + "\n"
                        content += "name = " + game.name + "\n"
                        content += "steam_appid = " + game.steam_appid + "\n"
                        content += "required_age = " + game.required_age + "\n"
                        content += "detailed_description = " + game.detailed_description + "\n"
                        content += "supported_languages = " + game.supported_languages + "\n"

                        tv_prueba.append(content)
                    }

                    /*if (games != null) {
                        for (game: GameSteam in games){
                            var content = ""
                            content += "type = " + game.type + "\n"
                            content += "name = " + game.name + "\n"
                            content += "steam_appid = " + game.steam_appid + "\n"
                            content += "required_age = " + game.required_age + "\n"
                            content += "detailed_description = " + game.detailed_description + "\n"
                            content += "supported_languages = " + game.supported_languages + "\n"

                            tv_prueba.append(content)
                        }
                    }*/
                }
            }

            override fun onFailure(call: Call<JsonSteamObject>, t: Throwable) {
                tv_prueba.text = "onFailure: " + t.message
                Log.d("onFailure", "onFailure: " + t.message)
            }
        })

    }
}