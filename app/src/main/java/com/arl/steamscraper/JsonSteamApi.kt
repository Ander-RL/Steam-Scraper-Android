package com.arl.steamscraper

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface JsonSteamApi {
    // 1282730
    @GET("?appids={id}")
    fun getGameSteam(@Path("id") appId: String): Call<JsonSteamObject>

}