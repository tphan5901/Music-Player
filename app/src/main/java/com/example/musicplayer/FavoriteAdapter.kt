package com.example.musicplayer

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayer.MainActivity.Companion.getColorFromIndex
import com.example.musicplayer.databinding.FavoriteViewBinding

class FavoriteAdapter(private val context: Context, private var musicList: ArrayList<Music>) : RecyclerView.Adapter<FavoriteAdapter.MyHolder>() {
    class MyHolder(binding: FavoriteViewBinding) : RecyclerView.ViewHolder(binding.root){
        val image = binding.songImgFV
        val name = binding.songNameFV
        val root = binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(FavoriteViewBinding.inflate(LayoutInflater.from(context), parent, false))
    }


    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        // ✅ Apply theme color to song text
        val themeColor = ContextCompat.getColor(context, getColorFromIndex(MainActivity.themeIndex))
        holder.name.setTextColor(themeColor)

        holder.name.text = musicList[position].title
        Glide.with(context)
            .load(musicList[position].artUri)
            .apply(RequestOptions().placeholder(R.drawable.ganyu).centerCrop())
            .into(holder.image)
        holder.root.setOnClickListener{
            val intent = Intent(context, PlayerActivity::class.java)
            intent.putExtra("index", position)
            intent.putExtra("class", "FavoriteAdapter")
            ContextCompat.startActivity(context, intent, null)

        }
    }

    //return number of items in musicList[]
    override fun getItemCount(): Int {
        return musicList.size
    }


}