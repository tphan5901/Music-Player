package com.example.musicplayer

import android.media.MediaMetadataRetriever
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

data class Music(val id:String, val title:String, val album:String, val artist:String, val duration: Long = 0, val path: String,
    val artUri:String) {

    //retrieve file art
    fun getImgArt(path: String): ByteArray? {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(path)
        return retriever.embeddedPicture
    }

}

    //Playlist Data model
    class Playlist{
        lateinit var name: String
        lateinit var playlist: ArrayList<Music>
        lateinit var createdOn: String
        var imageUri: String = ""
    }


    class MusicPlaylist{
        var ref: ArrayList<Playlist> = ArrayList()
    }


    fun formatDuration(duration: Long):String{
        val minutes = TimeUnit.MINUTES.convert(duration, TimeUnit.MILLISECONDS)
        val seconds = (TimeUnit.SECONDS.convert(duration, TimeUnit.MILLISECONDS) -
                minutes * TimeUnit.SECONDS.convert(1, TimeUnit.MINUTES))
        return String.format("%02d:%02d", minutes, seconds)
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

    fun exitApplication(){
        if(PlayerActivity.musicService != null){
            PlayerActivity.musicService!!.audioManager.abandonAudioFocus { PlayerActivity.musicService }
            PlayerActivity.musicService!!.stopForeground(true)
            PlayerActivity.musicService!!.mediaPlayer!!.release()
            PlayerActivity.musicService = null }
        exitProcess(1)
    }

    fun favoriteChecker(id: String): Int{
        PlayerActivity.isFavorite = false
        FavoriteActivity.favoriteSongs.forEachIndexed{ index, music ->
            if(id == music.id){
                PlayerActivity.isFavorite = true
                return index
            }

        }
        return -1
    }


    fun checkPlaylist(playlist: ArrayList<Music>): ArrayList<Music> {
        playlist.forEachIndexed { index, music ->
            val file = File(music.path)
            if(!file.exists())
                playlist.removeAt(index)
        }
        return playlist
    }

