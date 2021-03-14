package com.arl.steamscraper

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface JsonSteamApi {
    // 1282730
    @GET("?appids=1282730")
    fun getGameSteam(): Call<JsonSteamObject>

}