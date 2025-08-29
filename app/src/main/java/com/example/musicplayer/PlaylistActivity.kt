package com.example.musicplayer

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.musicplayer.databinding.ActivityPlaylistBinding
import com.example.musicplayer.databinding.AddPlaylistDialogBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat


class PlaylistActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlaylistBinding
    private lateinit var adapter: PlaylistViewAdapter

    companion object{
        var musicPlaylist: MusicPlaylist = MusicPlaylist()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlaylistBinding.inflate(layoutInflater)
        //removes header bar
        supportActionBar?.hide()
        setContentView(binding.root)
        val tempList = ArrayList<String>()
        binding.playlistRV.setHasFixedSize(true)
        binding.playlistRV.setItemViewCacheSize(13)
        binding.playlistRV.layoutManager = GridLayoutManager(this, 2)
        adapter = PlaylistViewAdapter(this, playlistList = musicPlaylist.ref)
        binding.playlistRV.adapter = adapter
        binding.backBtnPLA.setOnClickListener{ finish() }
        binding.addPlaylistBtn.setOnClickListener{ customAlertDialog() }
    }

    private fun customAlertDialog(){
        val customDialog = LayoutInflater.from(this@PlaylistActivity).inflate(R.layout.add_playlist_dialog, binding.root, false)
        val builder = MaterialAlertDialogBuilder(this)
        val binder = AddPlaylistDialogBinding.bind(customDialog)
        builder.setView(customDialog)
            .setTitle("Playlist Form")
            .setPositiveButton( "Add"){dialog, _ ->
                val playlistName = binder.playlistName.text
                val createdBy = binder.yourName.text
                if(playlistName != null && createdBy != null){
                    if(playlistName.isNotEmpty() && createdBy.isNotEmpty()){
                        addPlaylist(playlistName.toString(), createdBy.toString())
                    }
                }
                dialog.dismiss()
            }.show()
    }

    private fun addPlaylist(name: String, createdBy: String){
        var playlistExists = false
        for(i in musicPlaylist.ref){
            if(name.equals(i.name)){
                playlistExists = true
                break
            }
        }
        if(playlistExists) Toast.makeText(this, "Playlist Exist", Toast.LENGTH_SHORT).show()
        else{
            val tempPlaylist = Playlist()
            tempPlaylist.name = name
            tempPlaylist.playlist = ArrayList()
            tempPlaylist.createdBy = createdBy
            val calendar = java.util.Calendar.getInstance().time
            val sdf = SimpleDateFormat("dd MMM yyyy", java.util.Locale.ENGLISH)
            tempPlaylist.createdOn = sdf.format(calendar)
            musicPlaylist.ref.add(tempPlaylist)
            adapter.refreshPlaylist()
            //    tempPlaylist.createdOn
        }
    }

}

