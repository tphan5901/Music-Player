package com.example.musicplayer

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayer.MainActivity.Companion.getColorFromIndex
import com.example.musicplayer.databinding.ActivityFavoriteBinding

class FavoriteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFavoriteBinding
    private lateinit var adapter: FavoriteAdapter

    companion object {
        var favoriteSongs: ArrayList<Music> = ArrayList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
 //       setTheme(MainActivity.currentTheme[MainActivity.themeIndex])

        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        favoriteSongs = checkPlaylist(favoriteSongs)
        //removes header bar
        supportActionBar?.hide()
        //bind the back button eventListener
        binding.backBtnFA.setOnClickListener { finish() }
        binding.favoriteRV.setHasFixedSize(true)
        binding.favoriteRV.setItemViewCacheSize(13)
        binding.favoriteRV.layoutManager = GridLayoutManager(this, 4)
        adapter = FavoriteAdapter(this, favoriteSongs)
        binding.favoriteRV.adapter = adapter
        if(favoriteSongs.size < 1) binding.shuffleBtnFA.visibility = View.INVISIBLE
        binding.shuffleBtnFA.setOnClickListener{
            val intent = Intent(this, PlayerActivity::class.java)
            intent.putExtra("index", 0)
            intent.putExtra("class", "FavoriteShuffle")
            startActivity(intent)
        }

    }

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


    override fun onResume() {
        super.onResume()

        val themeColor = ContextCompat.getColor(this, getColorFromIndex(MainActivity.themeIndex))
        // 🔹 Apply theme color
        binding.backBtnFA.setColorFilter(themeColor)
        binding.favoriteTxt.setTextColor(themeColor)
        binding.shuffleBtnFA.strokeColor =
            ContextCompat.getColorStateList(this, getColorFromIndex(MainActivity.themeIndex))
        binding.shuffleBtnFA.icon?.setTint(themeColor)
        binding.shuffleBtnFA.iconTint = ColorStateList.valueOf(themeColor)


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
            binding.bgImage.setImageResource(R.drawable.nilou)
        }

    }



}

