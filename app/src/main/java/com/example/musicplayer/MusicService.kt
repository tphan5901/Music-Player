package com.example.musicplayer

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.os.Looper
import android.support.v4.media.session.MediaSessionCompat
import android.text.format.DateUtils
import androidx.core.app.NotificationCompat
import com.example.musicplayer.PlayerActivity.Companion.binding
import com.example.musicplayer.PlayerActivity.Companion.musicListPA
import com.example.musicplayer.PlayerActivity.Companion.musicService
import com.example.musicplayer.PlayerActivity.Companion.songPosition
//import androidx.media.app.NotificationCompat

class MusicService: Service() {
    private var myBinder = MyBinder()
    var mediaPlayer: MediaPlayer? = null
    private lateinit var mediaSession : MediaSessionCompat
    private lateinit var runnable: Runnable


    override fun onBind(intent: Intent?): IBinder{
        mediaSession = MediaSessionCompat(baseContext, "My music")
        return myBinder
    }

    inner class MyBinder:Binder(){
        fun currentService(): MusicService{
            return this@MusicService
        }
    }


    fun showNotification(playPauseIcon: Int){
        val prevIntent = Intent(baseContext, NotificationReceiver::class.java).setAction(ApplicationClass.PREVIOUS)
        val playIntent = Intent(baseContext, NotificationReceiver::class.java).setAction(ApplicationClass.PLAY)
        val nextIntent = Intent(baseContext, NotificationReceiver::class.java).setAction(ApplicationClass.NEXT)
        val exitIntent = Intent(baseContext, NotificationReceiver::class.java).setAction(ApplicationClass.EXIT)

        val prevPendingIntent = PendingIntent.getBroadcast(
            baseContext, 0, prevIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val playPendingIntent = PendingIntent.getBroadcast(
            baseContext, 0, playIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val nextPendingIntent = PendingIntent.getBroadcast(
            baseContext, 0, nextIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val exitPendingIntent = PendingIntent.getBroadcast(
            baseContext, 0, exitIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val music = PlayerActivity.musicListPA[PlayerActivity.songPosition]
        val imgArt = music.getImgArt(music.path)
        val image = if (imgArt != null) {
            BitmapFactory.decodeByteArray(imgArt, 0, imgArt.size)
        } else {
            BitmapFactory.decodeResource(resources, R.drawable.pyra_splash_screen)
        }


        val notification = NotificationCompat.Builder(baseContext, ApplicationClass.CHANNEL_ID)
            .setContentTitle(PlayerActivity.musicListPA[PlayerActivity.songPosition].title)
            .setContentText(PlayerActivity.musicListPA[PlayerActivity.songPosition].artist)
            .setSmallIcon(playPauseIcon)
            .setLargeIcon(image)
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(mediaSession.sessionToken))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .addAction(R.drawable.previous_icon, "Previous", prevPendingIntent)
            .addAction(R.drawable.play_icon, "Play", playPendingIntent)
            .addAction(R.drawable.next_icon, "Next", nextPendingIntent)
            .addAction(R.drawable.exit_icon, "Exit", exitPendingIntent)
            .build()
        startForeground(13, notification)
    }


    fun createMediaPlayer(){
        try{
            if (musicService!!.mediaPlayer == null) musicService!!.mediaPlayer = MediaPlayer()
            musicService!!.mediaPlayer!!.reset()
            musicService!!.mediaPlayer!!.setDataSource(musicListPA[songPosition].path)
            musicService!!.mediaPlayer!!.prepare()

            binding.playPauseBtnPA.setImageResource(R.drawable.pause_icon)
            musicService!!.showNotification(R.drawable.pause_icon)

            binding.tvSeekBarStart.text = DateUtils.formatElapsedTime(mediaPlayer!!.currentPosition.toLong() / 1000)
            binding.tvSeekBarEnd.text = DateUtils.formatElapsedTime(mediaPlayer!!.currentPosition.toLong() / 1000)

            binding.seekBarPA.max = musicService!!.mediaPlayer!!.duration
        }catch(e:Exception){ return}
    }


    fun seekBarSetup(){
        runnable = Runnable {
            PlayerActivity.binding.tvSeekBarStart.text = DateUtils.formatElapsedTime(mediaPlayer!!.currentPosition.toLong() / 1000)
            PlayerActivity.binding.seekBarPA.progress = mediaPlayer!!.currentPosition
            android.os.Handler(Looper.getMainLooper()).postDelayed(runnable, 200)

        }
        android.os.Handler(Looper.getMainLooper()).postDelayed(runnable, 0)
    }



}

