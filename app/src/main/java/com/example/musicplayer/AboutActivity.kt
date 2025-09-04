package com.example.musicplayer

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import com.example.musicplayer.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {

    lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
    //    setTheme(MainActivity.currentThemeNav[MainActivity.themeIndex])
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "About"
        binding.aboutText.text = aboutText()
    }

    private fun aboutText(): String{
        return "Developed by: Madeline Yamada" +
        "\n\nProvide me some feedback to improve the app experience"
    }

    private fun applyTheme() {
        val themeColor = ContextCompat.getColor(this, MainActivity.getColorFromIndex(MainActivity.themeIndex))
        supportActionBar?.setBackgroundDrawable(themeColor.toDrawable())
    }

    override fun onResume() {
        super.onResume()
        applyTheme()
    }


}