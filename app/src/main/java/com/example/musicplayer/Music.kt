package com.example.musicplayer

import android.media.MediaMetadataRetriever
import java.util.concurrent.TimeUnit

data class Music(val id:String, val title:String, val album:String, val artist:String, val duration: Long = 0, val path: String,
    val artUri:String) {

    fun formatDuration(duration: Long):String{
        val minutes = TimeUnit.MINUTES.convert(duration, TimeUnit.MILLISECONDS)
        val seconds = (TimeUnit.SECONDS.convert(duration, TimeUnit.MILLISECONDS) -
                minutes * TimeUnit.SECONDS.convert(1, TimeUnit.MINUTES))
        return String.format("%02d:%02d", minutes, seconds)
    }

    //retrieve file art
    fun getImgArt(path: String): ByteArray? {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(path)
        return retriever.embeddedPicture
    }
}


fun setSongPosition(increment: Boolean){
    //if repeat function isnt on
    if(!PlayerActivity.repeat) {
        //musicListPA array new position updates to current song position
        if (increment) {
            if (PlayerActivity.musicListPA.size - 1 == PlayerActivity.songPosition)
                //song starts at 0:00
                PlayerActivity.songPosition = 0
            else ++PlayerActivity.songPosition
        } else {
            if (0 == PlayerActivity.songPosition)
                PlayerActivity.songPosition = PlayerActivity.musicListPA.size - 1
            else --PlayerActivity.songPosition
        }
    }

}
