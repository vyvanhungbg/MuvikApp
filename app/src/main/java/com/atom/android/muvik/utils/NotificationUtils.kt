package com.atom.android.muvik.utils.extension

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import com.atom.android.muvik.App
import com.atom.android.muvik.R
import com.atom.android.muvik.data.model.Song
import com.atom.android.muvik.ui.main.MainActivity
import com.atom.android.muvik.ui.main.MusicBroadCast
import com.atom.android.muvik.utils.Constant
import java.io.ByteArrayInputStream
import java.io.InputStream
import kotlin.random.Random

private const val MEDIA_SESSION_TAG = "tag"


fun getPendingIntent(context: Context, action: String): PendingIntent {
    Intent(context, MusicBroadCast::class.java).also {
        it.putExtra(Constant.EXTRA_ACTION_FLAG, action)
        it.action = action
        return PendingIntent.getBroadcast(
            context.applicationContext,
            Random.nextInt(),
            it,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }
}


fun createNotificationSong(
    context: Context,
    song: Song?,
    isPlaying: Boolean?
): Notification {
    val mediaSessionCompat = MediaSessionCompat(context, MEDIA_SESSION_TAG)
    var bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.image_loading_item)
    song?.image?.let {
        val byteArray: InputStream = ByteArrayInputStream(song.image)
        bitmap = BitmapFactory.decodeStream(byteArray)
    }

    val intent = Intent(context, MainActivity::class.java).also {
        it.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
    }
    val intentBack: PendingIntent =
        PendingIntent.getActivity(
            context,
            Random.nextInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

    return NotificationCompat.Builder(context, App.CHANNEL_ID).apply {
        setContentTitle(song?.name)
        setContentText(song?.artist)
        setSmallIcon(R.drawable.ic_logo_music_24dp)
        setLargeIcon(bitmap)
        setContentIntent(intentBack)
        setOngoing(true)
        addAction(
            R.drawable.ic_previous_24dp,
            context.getString(R.string.title_notification_action_previous),
            getPendingIntent(context, Constant.ACTION_PREVIOUS)
        )
        if (isPlaying == true) {
            addAction(
                R.drawable.ic_pause_24dp,
                context.getString(R.string.title_notification_action_pause),
                getPendingIntent(context, Constant.ACTION_PAUSE)
            )
        } else {
            addAction(
                R.drawable.ic_play_white_24dp,
                context.getString(R.string.title_notification_action_resume),
                getPendingIntent(context, Constant.ACTION_RESUME)
            )
        }
        addAction(
            R.drawable.ic_next_24dp,
            context.getString(R.string.title_notification_action_next),
            getPendingIntent(context, Constant.ACTION_NEXT)
        )

        setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        setStyle(
            androidx.media.app.NotificationCompat.MediaStyle().run {

                setShowActionsInCompactView(1)

                setMediaSession(mediaSessionCompat.sessionToken)
            }
        )
    }.build()
}
