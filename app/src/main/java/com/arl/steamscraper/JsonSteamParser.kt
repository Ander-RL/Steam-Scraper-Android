package com.arl.steamscraper

import android.util.Log
import java.net.URL


class JsonSteamParser(val url: String, val api: String) {

    val patternName = "\"name\":"
    val patternQuotes = "\""
    var windows = false
    var mac = false
    var linux = false

    //private val api = URL(url).readText()
    private val platForms = api.substring(api.indexOf("\"platforms\":"), api.indexOf(",\"categories"))
    private val prices = api.substring(api.indexOf("\"initial\":"), api.indexOf(",\"initial_formatted\":"))

    fun getInitialPrice(): Double {
        val initialPrice = ((prices.substring(prices.indexOf("\"initial\":"), prices.indexOf(",\"final\":")))
            .replace("\"initial\":","")).toDouble() / 100
        Log.d("JsonSteamParser", "Initial price = $initialPrice")
        return initialPrice
    }

    fun getFinalPrice(): Double{
        val finalPrice =
            ((prices.substring(prices.indexOf("\"final\":"),prices.indexOf(",\"discount_percent\":")))
                .replace("\"final\":", "")).toDouble() / 100
        Log.d("JsonSteamParser", "Final price = $finalPrice")
        return finalPrice
    }

    fun getDiscount(): Int{
        val discount = (prices.substring(prices.indexOf("\"discount_percent\":"), prices.length))
            .replace("\"discount_percent\":","").toInt()
        Log.d("JsonSteamParser", "Discount = $discount")
        return discount
    }

    fun getImageUrl(): String {
        val image = api.substring(api.indexOf("\"header_image\":"), api.indexOf(",\"website\":"))
            .replace("\"header_image\":", "")
            .replace("\"", "")
        Log.d("JsonSteamParser", "Image = $image")
        return image
    }

    fun isWindows(): Boolean {
        if (platForms.contains("\"windows\":true")) {
            windows = true
        }
        Log.d("JsonSteamParser", "Windows = $windows")
        return windows
    }

    fun isMac(): Boolean {
        if (platForms.contains("\"mac\":true")) {
            mac = true
        }
        Log.d("JsonSteamParser", "Mac = $mac")
        return mac
    }

    fun isLinux(): Boolean {
        if (platForms.contains("\"linux\":true")) {
            linux = true
        }
        Log.d("JsonSteamParser", "Linux = $linux")
        return linux
    }

    fun getName(): String {
        var name = (api.substring(api.indexOf("\"name\":"), api.indexOf(",\"steam_appid\"")))
        name = (name.replace(patternName, "")).replace(patternQuotes, "")
        Log.d("JsonSteamParser", "Name = $name")
        return name
    }

    fun getAppId(): String {
        val appid = url.substring(url.indexOf("=") + 1)
        Log.d("JsonSteamParser", "Appid = $appid")
        return appid
    }
}