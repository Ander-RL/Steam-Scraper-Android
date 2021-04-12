package com.arl.steamscraper.receiver

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.arl.steamscraper.PriceCheckWorker
import java.util.*

class AlertReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("AlertReceiver", "Daily check")
        if (intent != null) {
            if(intent.extras?.containsKey("daily_check") == true){
                val checkPriceWorkRequest: WorkRequest = OneTimeWorkRequestBuilder<PriceCheckWorker>().build()
                if (context != null) {
                    WorkManager.getInstance(context).enqueue(checkPriceWorkRequest)
                }
            }
        }

        if(intent?.action.equals("android.intent.action.BOOT_COMPLETED")){
            Log.d("AlertReceiver", "Boot Complete Daily check")
            val intento = Intent(context, AlertReceiver::class.java)
            intento.putExtra("daily_check", "daily_check")

            val alarmManager =
                context?.getSystemService(Context.ALARM_SERVICE) as? AlarmManager

            val pendingIntent = PendingIntent.getBroadcast(
                context, 0,
                intento, PendingIntent.FLAG_UPDATE_CURRENT
            )

            if (alarmManager != null && pendingIntent != null) {
                alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent)
            }
        }
    }
}