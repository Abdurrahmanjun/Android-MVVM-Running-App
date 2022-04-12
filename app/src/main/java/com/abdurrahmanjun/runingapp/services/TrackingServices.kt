package com.abdurrahmanjun.runingapp.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import com.abdurrahmanjun.runingapp.R
import com.abdurrahmanjun.runingapp.others.Constants.ACTION_PAUSE_SERVICE
import com.abdurrahmanjun.runingapp.others.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import com.abdurrahmanjun.runingapp.others.Constants.ACTION_START_OR_RESUME_SERVICE
import com.abdurrahmanjun.runingapp.others.Constants.ACTION_STOP_SERVICE
import com.abdurrahmanjun.runingapp.others.Constants.NOTIFICATION_CHANNEL_ID
import com.abdurrahmanjun.runingapp.others.Constants.NOTIFICATION_CHANNEL_NAME
import com.abdurrahmanjun.runingapp.others.Constants.NOTIFICATION_ID
import com.abdurrahmanjun.runingapp.ui.MainActivity
import timber.log.Timber

class TrackingServices : LifecycleService() {

    var isFirstRun = true

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_START_OR_RESUME_SERVICE -> {
                    if(isFirstRun) {
                        startForegroundSerivce()
                        isFirstRun = false
                    } else {
                        Timber.d("Resume Service ...")
                    }
                }
                ACTION_PAUSE_SERVICE -> {
                    Timber.d("Service Pause!!")
                }
                ACTION_STOP_SERVICE -> {
                    Timber.d("Service Stop!!")
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startForegroundSerivce() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_directions_run_black_24dp)
            .setContentTitle("Running App")
            .setContentText("00:00:00")
            .setContentIntent(getMainActivityPendingIntent())

        startForeground(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun getMainActivityPendingIntent() = PendingIntent.getActivity(
        this,
        0,
        Intent(this, MainActivity::class.java).also {
            it.action = ACTION_SHOW_TRACKING_FRAGMENT
        },
        FLAG_UPDATE_CURRENT
    )

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }

}