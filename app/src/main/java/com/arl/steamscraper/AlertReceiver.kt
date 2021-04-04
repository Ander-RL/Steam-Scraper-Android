package com.arl.steamscraper

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest

class AlertReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent != null) {
            if(intent.extras?.containsKey("daily_check") == true){
                val checkPriceWorkRequest: WorkRequest = OneTimeWorkRequestBuilder<PriceCheckWorker>().build()
                if (context != null) {
                    WorkManager.getInstance(context).enqueue(checkPriceWorkRequest)
                }
            }
        }
    }
}