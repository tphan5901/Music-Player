package com.example.musicplayer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import kotlin.system.exitProcess


class NotificationReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?){
        when(intent?.action){
                ApplicationClass.PREVIOUS -> Toast.makeText(context, "Previous Clicked", Toast.LENGTH_SHORT).show()
                ApplicationClass.PLAY -> if(PlayerActivity.isPlaying) pauseMusic() else playMusic()
                ApplicationClass.NEXT -> Toast.makeText(context, "Next Clicked", Toast.LENGTH_SHORT).show()
                ApplicationClass.EXIT ->{
                    PlayerActivity.musicService!!.stopForeground(true)
                    PlayerActivity.musicService = null
                    exitProcess(1)
                }

            }
    }

    private fun playMusic(){
        PlayerActivity.isPlaying = true
        PlayerActivity.musicService!!.mediaPlayer!!.start()
        PlayerActivity.musicService!!.showNotification(R.drawable.pause_icon)
    //    PlayerActivity.binding.playPauseBtnPA.setImageResource(R.drawable.pause_icon)
    }

    private fun pauseMusic(){
        PlayerActivity.isPlaying = false
        PlayerActivity.musicService!!.mediaPlayer!!.pause()
        PlayerActivity.musicService!!.showNotification(R.drawable.play_icon)
        //PlayerActivity.binding.playPauseBtnPA.setImageResource(R.drawable.play_icon)
    }
}

