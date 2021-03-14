package com.arl.steamscraper

import com.google.gson.annotations.SerializedName

data class JsonSteamObject(@SerializedName("1282730") val steamObject: SteamData) {

    data class SteamData(@SerializedName("data") val gameSteam: GameSteam) {

        data class GameSteam(val type: String, val name: String, val steam_appid: Int,
                             val required_age: Int, val detailed_description: String,
                             val supported_languages: String)
    }

}