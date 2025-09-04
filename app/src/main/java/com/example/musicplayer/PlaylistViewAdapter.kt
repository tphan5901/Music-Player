package com.example.musicplayer

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayer.databinding.PlaylistViewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class PlaylistViewAdapter(private val context: Context, private var playlistList: ArrayList<Playlist>,
    private val onEditRequested: (position: Int) -> Unit) :
    RecyclerView.Adapter<PlaylistViewAdapter.MyHolder>() {

    class MyHolder(binding: PlaylistViewBinding) : RecyclerView.ViewHolder(binding.root) {
        val image = binding.playlistImg
        val name = binding.playlistName
        val root = binding.root
        val delete = binding.playlistDeleteBtn
        val edit = binding.playlistEditBtn
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(PlaylistViewBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val playlist = playlistList[position]


        // Apply theme
        val themeColor = ContextCompat.getColor(context, MainActivity.getColorFromIndex(MainActivity.themeIndex))
        holder.name.setTextColor(themeColor)

        holder.name.text = playlist.name
        holder.name.isSelected = true

        holder.delete.setOnClickListener {
            val builder = MaterialAlertDialogBuilder(context)
            builder.setTitle(playlist.name)
                .setMessage("Do you want to delete this playlist?")
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton("Yes") { dialog, _ ->
                    PlaylistActivity.musicPlaylist.ref.removeAt(position)
                    refreshPlaylist()
                    dialog.dismiss()
                }
            val customDialog = builder.create()
            customDialog.show()
            customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
            customDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED)
        }

        // Edit
        holder.edit.setOnClickListener {
            onEditRequested(position) // call back to PlaylistActivity
        }

        // Open details
        holder.root.setOnClickListener {
            val intent = Intent(context, PlaylistDetails::class.java)
            intent.putExtra("index", position)
            ContextCompat.startActivity(context, intent, null)
        }

        // Load image
        val imageToLoad = if (playlist.imageUri.isNotEmpty()) playlist.imageUri else R.drawable.ei_icon

        Glide.with(context)
            .load(imageToLoad)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.ei_icon)
                    .centerCrop()
                    .skipMemoryCache(true) //loads new image
//line of code below causes bug, when new image is selected for playlist, it causes it not to be saved into playlist object
// in long term memory so if you go to another activity or restart app, it wont be saved & loaded from saved object in memory
            //        .diskCacheStrategy(DiskCacheStrategy.NONE) // prevents caching old images
            )
            .into(holder.image)


    }


    override fun getItemCount(): Int {
        return playlistList.size
    }

    fun refreshPlaylist() {
        playlistList = ArrayList()
        playlistList.addAll(PlaylistActivity.musicPlaylist.ref)
        notifyDataSetChanged()
    }

}
