package com.example.musicplayer

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayer.databinding.MusicViewBinding

class MusicAdapter(private val context: Context, var musicList: ArrayList<Music>, private var playlistDetails: Boolean = false,
                   private val selectionActivity: Boolean = false)
    : RecyclerView.Adapter<MusicAdapter.MyHolder>() {
    class MyHolder(binding: MusicViewBinding) : RecyclerView.ViewHolder(binding.root){
        val title = binding.songNameMV
        val album = binding.songAlbumMV
        val image = binding.imageMV
        val duration = binding.songDuration
        val root = binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicAdapter.MyHolder {
        return MyHolder(MusicViewBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    //return number of items in musicList[]
    override fun getItemCount(): Int {
        return musicList.size
    }

    fun updateMusicList(searchList: ArrayList<Music>) {
        musicList = ArrayList()
        musicList.addAll(searchList)
        notifyDataSetChanged()
    }


    //Convert duration of song file to readable format
    fun formatDuration(durationMs: Long): String {
        val totalSeconds = durationMs / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%d:%02d", minutes, seconds)
    }
    

    //display song title, song duration on main activity(screen)
    override fun onBindViewHolder(holder: MusicAdapter.MyHolder, position: Int) {
        holder.title.text = musicList[position].title
        holder.album.text = musicList[position].album
        holder.duration.text =  formatDuration(musicList[position].duration)
        val song = musicList[position]
        val themeColor = ContextCompat.getColor(context, MainActivity.getColorFromIndex(MainActivity.themeIndex))

        // Apply theme
        holder.title.text = song.title
        holder.album.text = song.album
    // old code displays raw bytes of song duration
    //    holder.duration.text = song.duration.toString()
        holder.duration.text = formatDuration(song.duration)
        holder.duration.setTextColor(themeColor)
        holder.title.setTextColor(themeColor)
        holder.album.setTextColor(themeColor)

        Glide.with(context)
            .load(musicList[position].artUri)
            .apply(RequestOptions().placeholder(R.drawable.clorinde_icon).centerCrop())
            .error(R.drawable.music_icon) // fallback image when no album art found
            .into(holder.image)
        when{
            playlistDetails -> {
                holder.root.setOnClickListener {
                    sendIntent(ref = "PlaylistDetailsAdapter", pos=position)
                }
            }
            // when song item is selected in selection activity, highlight it, else deselect it
            selectionActivity ->{
                // Highlight song if it's already in the current playlist
                val isInPlaylist = PlaylistActivity.musicPlaylist.ref[PlaylistDetails.currentPlaylistPos].playlist
                    .any { it.id == musicList[position].id }

                if (isInPlaylist) {
                    holder.root.setBackgroundColor(ContextCompat.getColor(context, R.color.black))
                } else {
                    holder.root.setBackgroundResource(0)
                }
                holder.root.setOnClickListener {
                    if(addSong(musicList[position]))
                        holder.root.setBackgroundColor(ContextCompat.getColor(context, R.color.black))
                    else
                    //    old code
                    //    holder.root.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
                        holder.root.setBackgroundResource(0)
                }
            }
            else -> {
                holder.root.setOnClickListener{
                    when{
                        MainActivity.search -> sendIntent(ref = "MusicAdapterSearch", pos = position)
                        musicList[position].id == PlayerActivity.nowPlayingId ->
                            sendIntent(ref = "NowPlaying", pos = PlayerActivity.songPosition)
                        else->sendIntent(ref="MusicAdapter", pos = position) } }
            }
        }

    }

    fun refreshPlaylist(){
        musicList = ArrayList()
        musicList = PlaylistActivity.musicPlaylist.ref[PlaylistDetails.currentPlaylistPos].playlist
        notifyDataSetChanged()
    }

    private fun sendIntent(ref: String, pos: Int){
        val intent = Intent(context, PlayerActivity::class.java)
        intent.putExtra("index", pos)
        intent.putExtra("class", ref)
        ContextCompat.startActivity(context, intent, null)
    }

    private fun addSong(song: Music): Boolean{
        PlaylistActivity.musicPlaylist.ref[PlaylistDetails.currentPlaylistPos].playlist.forEachIndexed { index, music ->
            if(song.id == music.id){
                PlaylistActivity.musicPlaylist.ref[PlaylistDetails.currentPlaylistPos].playlist.removeAt(index)
                return false
            }
        }
        PlaylistActivity.musicPlaylist.ref[PlaylistDetails.currentPlaylistPos].playlist.add(song)
        return true
    }

    fun deleteItem(position: Int) {
        musicList.removeAt(position)
        notifyItemRemoved(position)
    }




}