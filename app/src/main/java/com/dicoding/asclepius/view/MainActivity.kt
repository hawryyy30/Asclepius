package com.dicoding.asclepius.view

import android.Manifest
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.get
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var currentImageUri: Uri? = null

    private val galleryPicker = registerForActivityResult(ActivityResultContracts.PickVisualMedia()){image ->
        if(image != null){

        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.appBarLayout[0].setOnClickListener{
            showToast(getString(R.string.madeby))
        }
        binding.topAppBar.menu.findItem(R.id.btn_history).setOnMenuItemClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
            true
        }
        binding.topAppBar.menu.findItem(R.id.btn_about).setOnMenuItemClickListener {
            val intent = Intent(this, AboutActivity::class.java)
            startActivity(intent)
            true
        }


    }

    private fun startGallery() {

    }

    private fun showImage() {
        // TODO: Menampilkan gambar sesuai Gallery yang dipilih.
    }

    private fun analyzeImage() {
        // TODO: Menganalisa gambar yang berhasil ditampilkan.
    }

    private fun moveToResult() {
        val intent = Intent(this, ResultActivity::class.java)
        startActivity(intent)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}