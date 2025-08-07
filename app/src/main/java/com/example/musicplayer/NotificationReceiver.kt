package com.example.musicplayer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayer.PlayerActivity.Companion.binding
import com.example.musicplayer.PlayerActivity.Companion.musicListPA
import com.example.musicplayer.PlayerActivity.Companion.songPosition


class NotificationReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?){
        when(intent?.action){
                ApplicationClass.PREVIOUS -> prevNextSong(increment = false, context = context!!)
                ApplicationClass.PLAY -> if(PlayerActivity.isPlaying) pauseMusic() else playMusic()
                ApplicationClass.NEXT ->  prevNextSong(increment = true, context = context!!)
                ApplicationClass.EXIT -> {
                    exitApplication()
                }
            }
    }

    private fun playMusic(){
        PlayerActivity.isPlaying = true
        PlayerActivity.musicService!!.mediaPlayer!!.start()
        PlayerActivity.musicService!!.showNotification(R.drawable.pause_icon)
        PlayerActivity.binding.playPauseBtnPA.setImageResource(R.drawable.pause_icon)
    }

    private fun pauseMusic(){
        PlayerActivity.isPlaying = false
        PlayerActivity.musicService!!.mediaPlayer!!.pause()
        PlayerActivity.musicService!!.showNotification(R.drawable.play_icon)
        PlayerActivity.binding.playPauseBtnPA.setImageResource(R.drawable.play_icon)
    }

    private fun prevNextSong(increment: Boolean, context: Context){
        setSongPosition(increment = increment)
        PlayerActivity.musicService!!.createMediaPlayer()
        Glide.with(context)
            .load(musicListPA[songPosition].artUri)
            .apply(RequestOptions().placeholder(R.drawable.pyra_splash_screen).centerCrop())
            .into(binding.songImgPA)
        PlayerActivity.binding.songNamePA.text = musicListPA[songPosition].title
        playMusic()
    }

}

