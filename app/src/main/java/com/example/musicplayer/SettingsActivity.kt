package com.example.musicplayer

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.example.musicplayer.databinding.ActivitySettingsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class SettingsActivity : AppCompatActivity() {

    lateinit var binding: ActivitySettingsBinding


    @SuppressLint("IntentReset")
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

            builder.setTitle("Sort by")
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
        // old code causing bug [ACTION_PICK] where bg image selected does not display in other activities.xml
        // btw this bug doesn't register as error when compiled
       // binding.selectBgImage.setOnClickListener {
       //     val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
      //      intent.type = "image/*"
     //       startActivityForResult(intent, 101) // requestCode = 101
     //   }

        // background image picker
        binding.selectBgImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
                type = "image/*"
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
            }
            startActivityForResult(intent, 101)
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
            "Software Version: ${pInfo.versionName}"
        } catch (e: Exception) {
            "Version not found"
        }
    }

    //set background image
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 && resultCode == RESULT_OK && data != null) {
            val selectedImageUri = data.data
            if (selectedImageUri != null) {
                try {
                    // persist permission so URI can be used later
                    contentResolver.takePersistableUriPermission(
                        selectedImageUri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                } catch (e: SecurityException) {
                    e.printStackTrace()
                }

                getSharedPreferences("BG_IMAGE", MODE_PRIVATE).edit {
                    putString("bg_uri", selectedImageUri.toString())
                }

                Toast.makeText(this, "Background image set!", Toast.LENGTH_SHORT).show()
            }
        }
    }


}