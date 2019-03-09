package com.example.utente10.galileo.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.app.TaskStackBuilder
import com.example.utente10.galileo.BeaconMapActivity
import com.example.utente10.galileo.ContentsActivity
import com.example.utente10.galileo.R
import com.example.utente10.galileo.bean.Landmark
import com.example.utente10.galileo.bean.Macroarea

private val CHANNEL_ID = "NotificationChannel"
private var notificationId = 0

fun sendNotificationForLandmark(context: Context, landmark: Landmark) {

    // Creating an Intent for the activity to start
    //activity to start on notification click
    val intent = Intent(context, ContentsActivity::class.java)
    intent.putExtra("landmarkLabel", landmark.beacon?.label)
    // Create the TaskStackBuilder
    val pendingIntent: PendingIntent? = TaskStackBuilder.create(context).run {
        // Add the intent, which inflates the back stack
        addNextIntentWithParentStack(intent)
        //TODO: creare stack per le activity che dovrebbero essere aperte all'apertura della notifica
        // Get the PendingIntent containing the entire back stack
        getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    val mBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(landmark.name)
            .setContentText(landmark.description)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setVisibility(Notification.VISIBILITY_PUBLIC)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
    with(NotificationManagerCompat.from(context)) {
        // notificationId is a unique int for each notification that you must define
        notify(notificationId, mBuilder.build())
    }
}

fun sendNotificationForMacroarea(context: Context, macroarea: Macroarea) {

    // Creating an Intent for the activity to start
    //activity to start on notification click
    val intent = Intent(context, BeaconMapActivity::class.java)
    intent.putExtra("macroarea", macroarea.name)
    // Create the TaskStackBuilder
    val pendingIntent: PendingIntent? = TaskStackBuilder.create(context).run {
        // Add the intent, which inflates the back stack
        addNextIntentWithParentStack(intent)
        //TODO: creare stack per le activity che dovrebbero essere aperte all'apertura della notifica
        // Get the PendingIntent containing the entire back stack
        getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    val mBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(macroarea.name)
            .setContentText(macroarea.description)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setVisibility(Notification.VISIBILITY_PUBLIC)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
    with(NotificationManagerCompat.from(context)) {
        // notificationId is a unique int for each notification that you must define
        notify(notificationId, mBuilder.build())
    }
}

//creating notification channel for android>=8.0
fun createNotificationChannel(context: Context) {
    // Create the NotificationChannel, but only on API 26+ because
    // the NotificationChannel class is new and not in the support library
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = context.getString(R.string.landmark_channel)
        val descriptionText = context.getString(R.string.channel_description)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        // Register the channel with the system
        val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}