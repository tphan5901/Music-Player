package com.example.musicplayer

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicplayer.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var musicAdapter: MusicAdapter

    companion object {
        lateinit var MusicListMA: ArrayList<Music>
        lateinit var musicListSearch : ArrayList<Music>
        var search: Boolean = false

    }


    @OptIn(ExperimentalMaterial3Api::class)
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        // Switch to the app theme BEFORE calling super.onCreate() and setContentView
        //    setTheme(R.style.Theme_MusicPlayer)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //nav drawer
        toggle = ActionBarDrawerToggle(this, binding.root, R.string.open, R.string.close)
        binding.root.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if(requestRuntimePermission()) {
            initializeLayout()
            // storing favorites data
            FavoriteActivity.favoriteSongs = ArrayList()
            val editor = getSharedPreferences("FAVORITES", MODE_PRIVATE)
            val jsonString = editor.getString("FavoriteSongs", null)
            val typeToken = object : TypeToken<ArrayList<Music>>(){}.type
            if(jsonString != null){
                val data: ArrayList<Music> = GsonBuilder().create().fromJson(jsonString, typeToken)
                FavoriteActivity.favoriteSongs.addAll(data)
            }

        }

        binding.shuffleBtn.setOnClickListener {
            val intent = Intent(this@MainActivity, PlayerActivity::class.java)
            intent.putExtra("index", 0)
            intent.putExtra("class", "MainActivity")
            startActivity(intent)
        }
        binding.favoriteBtn.setOnClickListener {
            startActivity(Intent(this@MainActivity, FavoriteActivity::class.java))
        }
        binding.playlistBtn.setOnClickListener {
            startActivity(Intent(this@MainActivity, PlaylistActivity::class.java))
        }
        binding.navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navFeedback -> Toast.makeText(baseContext, "Feedback", Toast.LENGTH_SHORT)
                    .show()

                R.id.navSettings -> Toast.makeText(baseContext, "Settings", Toast.LENGTH_SHORT)
                    .show()

                R.id.navAbout -> Toast.makeText(baseContext, "About", Toast.LENGTH_SHORT).show()
                R.id.navExit -> {
                    val builder = MaterialAlertDialogBuilder(this)
                    builder.setTitle("Exit")
                        .setMessage("Do u want to close app")
                        .setPositiveButton( "Yes"){_, _ ->
                            exitApplication()
                        }
                        .setNegativeButton("No"){ dialog, _->
                            dialog.dismiss()
                        }
                    val customDialog = builder.create()
                    customDialog.show()
                    customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
                    customDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED)
                }
            }
            true
        }

    }

    //Request External Access Permission on Runtime
    private fun requestRuntimePermission() :Boolean{
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_MEDIA_AUDIO),
                    100
                )
                return false
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    101
                )
            }
        }
        return true
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 13) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                initializeLayout()
            }
            else
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    13
                )
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item))
            return true
        return super.onOptionsItemSelected(item)
    }


    @RequiresApi(Build.VERSION_CODES.R)
    private fun initializeLayout() {
        search = false
        MusicListMA = getAllAudio()
        binding.musicRV.setHasFixedSize(true)
        binding.musicRV.setItemViewCacheSize(13)
        binding.musicRV.layoutManager = LinearLayoutManager(this@MainActivity)
        musicAdapter = MusicAdapter(this@MainActivity, MusicListMA)
        binding.musicRV.adapter = musicAdapter
        binding.totalSongs.text = "All Songs: " + musicAdapter.itemCount
    }

    @SuppressLint("Recycle")
    @RequiresApi(Build.VERSION_CODES.R)
    private fun getAllAudio(): ArrayList<Music> {
        val tempList = ArrayList<Music>()
        val selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0"
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM_ID
        )

        val cursor = this.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            MediaStore.Audio.Media.DATE_ADDED + " DESC"
        )

        if (cursor != null && cursor.moveToFirst()) {
            do {
                val titleC = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE))
                val idC = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID))
                val albumC = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM))
                val artistC = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST))
                val pathC = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))
                val durationC = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))
                val albumIdC = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID))
                // old code
                //  val uri = Uri.parse("content://media/external/audio/albumart")
                val uri = "content://media/external/audio/albumart".toUri()
                val artUriC = Uri.withAppendedPath(uri, albumIdC).toString()

                val music = Music(id = idC, title = titleC, album = albumC, artist = artistC, path = pathC, duration = durationC, artUri = artUriC)
                val file = File(pathC)
                if (file.exists()) {
                    tempList.add(music)
                }
            }
            while (cursor.moveToNext())
            cursor.close()
        }
        return tempList
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!PlayerActivity.isPlaying && PlayerActivity.musicService != null) {
            exitApplication()
        }
    }

    override fun onResume(){
        super.onResume()
        // storing favorites data
        getSharedPreferences("FAVORITES", MODE_PRIVATE).edit {
            val jsonString = GsonBuilder().create().toJson(FavoriteActivity.favoriteSongs)
            putString("FavoriteSongs", jsonString)
        }
    //old code
    //    val editor = getSharedPreferences("FAVORITES", MODE_PRIVATE).edit()
    //    val jsonString = GsonBuilder().create().toJson(FavoriteActivity.favoriteSongs)
    //    editor.putString("FavoriteSongs", jsonString)
    //    editor.apply()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_view, menu)

        val searchView = menu?.findItem(R.id.searchView)?.actionView as? SearchView

        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

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
                    musicAdapter.updateMusicList(searchList = musicListSearch)
                } else {
                    search = false
                    musicAdapter.updateMusicList(MusicListMA)
                }
                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }


}

