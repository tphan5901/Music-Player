package com.example.musicplayer

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayer.MainActivity.Companion.MusicListMA
import com.example.musicplayer.MainActivity.Companion.getColorFromIndex
import com.example.musicplayer.MainActivity.Companion.musicListSearch
import com.example.musicplayer.MainActivity.Companion.search
import com.example.musicplayer.databinding.ActivitySelectionBinding

class SelectionActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySelectionBinding
    private lateinit var adapter: MusicAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        binding = ActivitySelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.selectionRV.setItemViewCacheSize(10)
        binding.selectionRV.layoutManager = LinearLayoutManager(this)
        binding.selectionRV.setHasFixedSize(true)
        adapter = MusicAdapter(this, MainActivity.MusicListMA, selectionActivity = true)
        binding.selectionRV.adapter = adapter

        binding.backBtnSA.setOnClickListener { finish() }
        // SearchView setup
        binding.searchViewSA.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = true

            override fun onQueryTextChange(newText: String?): Boolean {
                musicListSearch = ArrayList()
                if (!newText.isNullOrEmpty()) {
                    val userInput = newText.lowercase()
                    for (song in MusicListMA) {
                        if (song.title.lowercase().contains(userInput)) {
                            musicListSearch.add(song)
                        }
                    }
                    search = true
                    adapter.updateMusicList(musicListSearch)
                } else {
                    search = false
                    adapter.updateMusicList(MusicListMA)
                }
                return true
            }
        })
    }


    override fun onResume() {
        super.onResume()
        val themeColor = ContextCompat.getColor(this, getColorFromIndex(MainActivity.themeIndex))

        // Apply theme color
        binding.backBtnSA.setColorFilter(themeColor)
        // Get the search icon inside SearchView
        val searchIcon: ImageView = binding.searchViewSA.findViewById(androidx.appcompat.R.id.search_mag_icon)
        searchIcon.setColorFilter(themeColor)

        val sharedPref = getSharedPreferences("BG_IMAGE", MODE_PRIVATE)
        val bgUri = sharedPref.getString("bg_uri", null)

        if (bgUri != null && bgUri.isNotEmpty()) {
            Glide.with(this)
                .load(bgUri.toUri())
                .apply(
                    RequestOptions()
                        .placeholder(R.drawable.yuka_kuromochi)
                        .centerCrop()
                )
                .into(binding.bgImage)
        } else {
            binding.bgImage.setImageResource(R.drawable.yuka_kuromochi)
        }
    }

}
