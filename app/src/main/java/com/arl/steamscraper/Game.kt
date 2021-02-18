package com.arl.steamscraper

import android.graphics.drawable.Drawable

data class Game(val imageResource: Drawable, val gameName: String, val gamePlatform: String,
                val priceOriginal: String, val priceDiscount: String)
