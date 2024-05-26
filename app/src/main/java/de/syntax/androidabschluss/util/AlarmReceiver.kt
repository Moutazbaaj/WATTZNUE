package de.syntax.androidabschluss.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import de.syntax.androidabschluss.R


/**
 * BroadcastReceiver responsible for handling alarm events and showing notifications for reminders.
 */
class AlarmReceiver : BroadcastReceiver() {

    /**
     * Companion object containing constants used for notification channel and intent extras.
     */
    companion object {
        const val CHANNEL_ID = "my_note_reminders"
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_TEXT = "extra_text"
    }


    /**
     * Handles the broadcast alarm event.
     *
     * @param context Context The context in which the receiver is running.
     * @param intent Intent The intent containing the alarm data.
     */
    override fun onReceive(context: Context?, intent: Intent?) {
        // Extract title and text from intent extras
        val title = intent?.getStringExtra(EXTRA_TITLE)
        val text = intent?.getStringExtra(EXTRA_TEXT)

        // Create and show the notification
        val notificationManager = context?.let {
            ContextCompat.getSystemService(
                it,
                NotificationManager::class.java
            )
        } as NotificationManager

        // Ensure notification channel creation
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "My Note Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notification channel for note reminders"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notificationId =
            System.currentTimeMillis().toInt()  // Ensure this is unique for each notification
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("You have a Reminder:")
            .setContentText(title)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setSmallIcon(R.drawable.iconapp)
            .build()

        notificationManager.notify(notificationId, notification)
    }
}


