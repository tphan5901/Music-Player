package com.example.musicplayer

import android.media.MediaMetadataRetriever
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

// Music Data Model
data class Music(
    val id: String,
    val title: String,
    val album: String,
    val artist: String,
    val duration: Long = 0,
    val path: String,
    val artUri: String
) {
    // Retrieve album art for this music
    fun getImgArt(): ByteArray? {
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(path)
            retriever.embeddedPicture
        } catch (e: Exception) {
            null
        } finally {
            retriever.release()
        }
    }
}


// Playlist Data Model
class Playlist(
    var name: String,
    var playlist: ArrayList<Music>,
    var createdOn: String,
    var imageUri: String = ""
)

/* old data model
class Playlist(
    string: String,
    arrayList: ArrayList<Any?>,
    string1: String,
    arrayList1: ArrayList<Any?>
) {
    lateinit var name: String
    lateinit var playlist: ArrayList<Music>
    lateinit var createdOn: String
    var imageUri: String = ""
}
*/


// MusicPlaylist Data Model
class MusicPlaylist {
    var ref: ArrayList<Playlist> = ArrayList()
}

// Format duration from milliseconds to mm:ss
fun formatDuration(duration: Long): String {
    val minutes = TimeUnit.MINUTES.convert(duration, TimeUnit.MILLISECONDS)
    val seconds = (TimeUnit.SECONDS.convert(duration, TimeUnit.MILLISECONDS) -
            minutes * TimeUnit.SECONDS.convert(1, TimeUnit.MINUTES))
    return String.format("%02d:%02d", minutes, seconds)
}

// Update song position in playlist
fun setSongPosition(increment: Boolean) {
    if (!PlayerActivity.repeat) {
        if (increment) {
            if (PlayerActivity.musicListPA.size - 1 == PlayerActivity.songPosition)
                PlayerActivity.songPosition = 0
            else ++PlayerActivity.songPosition
        } else {
            if (0 == PlayerActivity.songPosition)
                PlayerActivity.songPosition = PlayerActivity.musicListPA.size - 1
            else --PlayerActivity.songPosition
        }
    }
}

// Exit the app safely, releasing resources
fun exitApplication() {
    PlayerActivity.musicService?.let { service ->
        service.audioManager.abandonAudioFocus { service }
        service.stopForeground(true)
        service.mediaPlayer?.release()
        PlayerActivity.musicService = null
    }
    exitProcess(1)
}

// Check if a song is in favorites
fun favoriteChecker(id: String): Int {
    PlayerActivity.isFavorite = false
    FavoriteActivity.favoriteSongs.forEachIndexed { index, music ->
        if (id == music.id) {
            PlayerActivity.isFavorite = true
            return index
        }
    }
    return -1
}

// Remove missing songs from playlist
fun checkPlaylist(playlist: ArrayList<Music>): ArrayList<Music> {
    val iterator = playlist.iterator()
    while (iterator.hasNext()) {
        val music = iterator.next()
        val file = File(music.path)
        if (!file.exists()) iterator.remove()
    }
    return playlist
}

// Applies theme to about & feedback activity
fun AppCompatActivity.applyTheme(themeIndex: Int) {
    val themeColor = ContextCompat.getColor(this, MainActivity.getColorFromIndex(themeIndex))
    this.supportActionBar?.setBackgroundDrawable(themeColor.toDrawable())
}