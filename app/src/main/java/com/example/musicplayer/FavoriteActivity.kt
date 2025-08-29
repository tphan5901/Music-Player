package com.example.musicplayer

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.musicplayer.databinding.ActivityFavoriteBinding

class FavoriteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFavoriteBinding
    private lateinit var adapter: FavoriteAdapter

    companion object {
        var favoriteSongs: ArrayList<Music> = ArrayList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //removes header bar
        supportActionBar?.hide()
        //bind the back button eventListener
        val tempList = ArrayList<String>()
        tempList.add("Song 1")
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

}

