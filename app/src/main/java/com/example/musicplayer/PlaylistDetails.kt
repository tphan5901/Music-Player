package com.example.musicplayer

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.core.net.toUri
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayer.databinding.ActivityPlaylistDetailsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.GsonBuilder


class PlaylistDetails : AppCompatActivity() {

    lateinit var binding: ActivityPlaylistDetailsBinding

    lateinit var adapter: MusicAdapter

    companion object{
        var currentPlaylistPos: Int = -1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

    //    setTheme(MainActivity.currentTheme[MainActivity.themeIndex])
        binding = ActivityPlaylistDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        currentPlaylistPos = intent.extras?.get("index") as Int
        try{
            PlaylistActivity.musicPlaylist.ref[currentPlaylistPos].playlist =
                checkPlaylist(playlist = PlaylistActivity.musicPlaylist.ref[currentPlaylistPos].playlist)
        } catch (e: Exception) {

        }
        binding.playlistDetailsRV.setItemViewCacheSize(10)
        binding.playlistDetailsRV.layoutManager = LinearLayoutManager(this)
        binding.playlistDetailsRV.setHasFixedSize(true)

        adapter = MusicAdapter(this, PlaylistActivity.musicPlaylist.ref[currentPlaylistPos].playlist, playlistDetails = true)
        binding.playlistDetailsRV.adapter = adapter
        binding.backBtnPD.setOnClickListener { finish() }
        binding.shuffleBtnPD.setOnClickListener{
            val intent = Intent(this, PlayerActivity::class.java)
            intent.putExtra("index", 0)
            intent.putExtra("class", "PlaylistDetailsShuffle")
            startActivity(intent)
        }
        binding.addBtnPD.setOnClickListener {
            startActivity(Intent(this, SelectionActivity::class.java))
        }
        binding.removeAllPD.setOnClickListener {
            val builder = MaterialAlertDialogBuilder(this)
            builder.setTitle("Remove")
                .setMessage("Do you want to remove all songs?")
                .setNegativeButton("No"){ dialog, _->
                    dialog.dismiss()
                }
                .setPositiveButton( "Yes"){ dialog, _ ->
                    PlaylistActivity.musicPlaylist.ref[currentPlaylistPos].playlist.clear()
                    adapter.refreshPlaylist()
                    dialog.dismiss()
                }
            val customDialog = builder.create()
            customDialog.show()
            customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
            customDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED)

        }

        val itemTouchHelper = ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition

                // Reset swipe immediately so the item stays visible
                adapter.notifyItemChanged(position)

                AlertDialog.Builder(this@PlaylistDetails)
                    .setTitle("Delete Song")
                    .setMessage("Do you want to delete this song?")
                    .setPositiveButton("Yes") { _, _ ->
                        // Delete from playlist + refresh
                        adapter.deleteItem(position)
                        PlaylistActivity.musicPlaylist.ref[currentPlaylistPos].playlist = adapter.musicList
                    }
                    .setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }
        })
        itemTouchHelper.attachToRecyclerView(binding.playlistDetailsRV)

    }


    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()

        val themeColor = ContextCompat.getColor(this, MainActivity.getColorFromIndex(MainActivity.themeIndex))

        // 🔹 Apply theme color
        binding.addBtnPD.compoundDrawables[1]?.setTint(themeColor)
        binding.removeAllPD.compoundDrawables[1]?.setTint(themeColor)
        binding.shuffleBtnPD.iconTint = ColorStateList.valueOf(themeColor)
        binding.backBtnPD.setColorFilter(themeColor)
        binding.playlistNamePD.setTextColor(themeColor)
        binding.moreInfoPD.setTextColor(themeColor)
        binding.shuffleBtnPD.strokeColor =
            ContextCompat.getColorStateList(this, MainActivity.getColorFromIndex(MainActivity.themeIndex))
        binding.shuffleBtnPD.icon?.setTint(themeColor)


        val sharedPref = getSharedPreferences("BG_IMAGE", MODE_PRIVATE)
        val bgUri = sharedPref.getString("bg_uri", null)

        if (bgUri != null && bgUri.isNotEmpty()) {
            Glide.with(this)
                .load(bgUri.toUri())
                .apply(
                    RequestOptions()
                        .placeholder(R.drawable.clorinde_icon)
                        .centerCrop()
                )
                .into(binding.bgImage)
        } else {
            binding.bgImage.setImageResource(R.drawable.clorinde_icon)
        }

        binding.playlistNamePD.text = PlaylistActivity.musicPlaylist.ref[currentPlaylistPos].name
        binding.moreInfoPD.text = "Total Songs: ${adapter.itemCount} \n\n" +
                "Created On: ${PlaylistActivity.musicPlaylist.ref[currentPlaylistPos].createdOn}"
        if(adapter.itemCount > 0)
        {
            Glide.with(this)
                .load(PlaylistActivity.musicPlaylist.ref[currentPlaylistPos].playlist[0].artUri)
                .apply(RequestOptions().placeholder(R.drawable.reiko).centerCrop())
                .into(binding.playlistImgPD)
            binding.shuffleBtnPD.visibility = View.VISIBLE
        }
        adapter.notifyDataSetChanged()
        //for storing favorite songs using shared preferences
        getSharedPreferences("FAVOURITES", MODE_PRIVATE).edit {
            val jsonStringPlaylist = GsonBuilder().create().toJson(PlaylistActivity.musicPlaylist)
            putString("MusicPlaylist", jsonStringPlaylist)
        }
    }

}