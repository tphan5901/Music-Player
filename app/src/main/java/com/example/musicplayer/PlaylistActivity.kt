package com.example.musicplayer

import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayer.MainActivity.Companion.getColorFromIndex
import com.example.musicplayer.databinding.ActivityPlaylistBinding
import com.example.musicplayer.databinding.AddPlaylistDialogBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat

class PlaylistActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlaylistBinding
    private lateinit var adapter: PlaylistViewAdapter
    // for editing images
    private lateinit var pickImageLauncher: ActivityResultLauncher<String>
    private var tempEditingPlaylist: Playlist? = null
    private var tempEditingPosition: Int = -1

    companion object {
        var musicPlaylist: MusicPlaylist = MusicPlaylist()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlaylistBinding.inflate(layoutInflater)
        supportActionBar?.hide()
        setContentView(binding.root)

        // Image picker
        pickImageLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                uri?.let {
                    tempEditingPlaylist?.imageUri = uri.toString() // store image as string in Playlist
                    adapter.notifyItemChanged(tempEditingPosition)
                }
            }

        // RecyclerView setup
        binding.playlistRV.setHasFixedSize(true)
        binding.playlistRV.setItemViewCacheSize(13)
        binding.playlistRV.layoutManager = GridLayoutManager(this, 2)

        // Adapter with edit callback
        adapter = PlaylistViewAdapter(this, musicPlaylist.ref) { position ->
            showEditDialog(position) // pass only Int
        }

        binding.playlistRV.adapter = adapter
        binding.backBtnPLA.setOnClickListener { finish() }
        binding.addPlaylistBtn.setOnClickListener { customAlertDialog() }
    }

    private fun customAlertDialog() {
        val customDialog =
            LayoutInflater.from(this@PlaylistActivity)
                .inflate(R.layout.add_playlist_dialog, binding.root, false)
        val builder = MaterialAlertDialogBuilder(this)
        val binder = AddPlaylistDialogBinding.bind(customDialog)
        builder.setView(customDialog)
            .setTitle("Playlist Form")
            .setPositiveButton("Add") { dialog, _ ->
                val playlistName = binder.playlistName.text
                if (playlistName != null && playlistName.isNotEmpty()) {
                    addPlaylist(playlistName.toString())
                }
                dialog.dismiss()
            }.show()
    }

    private fun addPlaylist(name: String) {
        val playlistExists = musicPlaylist.ref.any { it.name == name }
        if (playlistExists) {
            Toast.makeText(this, "Playlist Exists", Toast.LENGTH_SHORT).show()
            return
        }

        val tempPlaylist = Playlist().apply {
            this.name = name
            this.playlist = ArrayList()
            this.createdOn = SimpleDateFormat("dd MMM yyyy", java.util.Locale.ENGLISH)
                .format(java.util.Calendar.getInstance().time)
            this.imageUri = ""
        }


        musicPlaylist.ref.add(tempPlaylist)
        adapter.refreshPlaylist()
    }


    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()

        val themeColor = ContextCompat.getColor(this, MainActivity.getColorFromIndex(MainActivity.themeIndex))

        // Apply theme color
        binding.backBtnPLA.setColorFilter(themeColor)
        binding.addPlaylistBtn.strokeColor =
            ContextCompat.getColorStateList(this, getColorFromIndex(MainActivity.themeIndex))
        binding.addPlaylistBtn.icon?.setTint(themeColor)
        binding.addPlaylistBtn.iconTint = ColorStateList.valueOf(themeColor)

        val sharedPref = getSharedPreferences("BG_IMAGE", MODE_PRIVATE)
        val bgUri = sharedPref.getString("bg_uri", null)

        if (bgUri != null && bgUri.isNotEmpty()) {
            Glide.with(this)
                .load(bgUri.toUri())
                .apply(
                    RequestOptions()
                        .placeholder(R.drawable.pyra_splash_screen)
                        .centerCrop()
                )
                .into(binding.bgImage)
        } else {
            binding.bgImage.setImageResource(R.drawable.pyra_splash_screen)
        }

    }


    fun showEditDialog(position: Int) {
        val playlist = musicPlaylist.ref[position] // always get latest object
        tempEditingPlaylist = playlist
        tempEditingPosition = position

        val dialogView = layoutInflater.inflate(R.layout.edit_playlist, null)
        val editText = dialogView.findViewById<EditText>(R.id.editPlaylistTitle)
        val changeImgBtn = dialogView.findViewById<Button>(R.id.changeImageBtn)

        // Pre-fill with current data
        editText.setText(playlist.name)

        val alertDialog = AlertDialog.Builder(this)
            .setTitle("Edit Playlist")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val newTitle = editText.text.toString()
                if (newTitle.isNotEmpty()) {
                    playlist.name = newTitle
                    adapter.notifyItemChanged(position)
                }
            }
            .setNegativeButton("Cancel", null)
            .create()

        changeImgBtn.setOnClickListener {
            tempEditingPlaylist = playlist
            tempEditingPosition = position
            pickImageLauncher.launch("image/*")
        }
        alertDialog.show()
    }

}
