package com.example.musicplayer

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.musicplayer.databinding.ActivitySettingsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class SettingsActivity : AppCompatActivity() {

    lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    //    setTheme(MainActivity.currentThemeNav[MainActivity.themeIndex])
        enableEdgeToEdge()

        // Initialize binding first
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Settings"
        when(MainActivity.themeIndex){
            0 -> binding.coolPinkTheme.setBackgroundColor(Color.WHITE)
            1 -> binding.coolBlueTheme.setBackgroundColor(Color.WHITE)
            2 -> binding.coolPurpleTheme.setBackgroundColor(Color.WHITE)
            3 -> binding.coolGreenTheme.setBackgroundColor(Color.WHITE)
            4 -> binding.coolBlackTheme.setBackgroundColor(Color.WHITE)
        }
        binding.coolPinkTheme.setOnClickListener { saveTheme(0) }
        binding.coolBlueTheme.setOnClickListener { saveTheme(1) }
        binding.coolPurpleTheme.setOnClickListener { saveTheme(2) }
        binding.coolGreenTheme.setOnClickListener { saveTheme(3) }
        binding.coolBlackTheme.setOnClickListener { saveTheme(4) }
        binding.versionName.text = getVersionName()
        binding.sortBtn.setOnClickListener {
            val menuList = arrayOf("Recently Added", "Song Title", "File Size")
            var currentSort = MainActivity.sortOrder
            val builder = MaterialAlertDialogBuilder(this)

            builder.setTitle("Sorting")
                .setPositiveButton( "OK"){ _, _ ->
                    val editor = getSharedPreferences("SORTING", MODE_PRIVATE).edit()
                    editor.putInt("sortOrder", currentSort)
                    editor.apply()
                }
                .setSingleChoiceItems(menuList, currentSort) { _,which  ->
                    currentSort = which
                }

            val customDialog = builder.create()
            customDialog.show()
            customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)

        }
    }

    private fun saveTheme(index: Int){
        if(MainActivity.themeIndex != index){
            val editor = getSharedPreferences("THEMES", MODE_PRIVATE).edit()
            editor.putInt("themeIndex", index)
            val builder = MaterialAlertDialogBuilder(this)
            builder.setTitle("Apply Theme")
                .setMessage("Do u want to apply this theme?")
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

    private fun getVersionName(): String {
        return try {
            val pInfo = packageManager.getPackageInfo(packageName, 0)
            "Version Name: ${pInfo.versionName}"
        } catch (e: Exception) {
            "Version not found"
        }
    }


}