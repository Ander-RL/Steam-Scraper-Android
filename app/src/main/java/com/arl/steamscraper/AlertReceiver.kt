package com.arl.steamscraper

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class AlertReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("PriceService", "AlertReceiver --> onReceive")
        if (intent != null) {
            if(intent.extras?.containsKey("daily_check") == true){
                Log.d("PriceService", "AlertReceiver --> startService")
                val intentService = Intent(context, PriceService::class.java)
                intentService.action = ".PriceService"
                context?.startService(intentService)
            }
        }
    }
}