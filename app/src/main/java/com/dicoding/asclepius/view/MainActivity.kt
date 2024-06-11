package com.dicoding.asclepius.view


import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.get
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ActivityMainBinding
import com.dicoding.asclepius.view.history.HistoryActivity
import com.dicoding.asclepius.view.result.ResultActivity
import com.yalantis.ucrop.UCrop
import java.security.MessageDigest
import java.util.UUID

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var currentImageUri: Uri? = null

    private val galleryPicker = registerForActivityResult(ActivityResultContracts.PickVisualMedia()){image ->
        if(image != null){
            val imgName = generateHashedFileName()
            UCrop.of(image,Uri.fromFile(cacheDir.resolve(imgName))).start(this)
            currentImageUri = image;
            showImage()
        } else {
            showToast(getString(R.string.no_image_hint))
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

        binding.galleryButton.setOnClickListener {
            startGallery()
        }
        binding.analyzeButton.setOnClickListener{
            analyzeImage()
        }

        toggleButtonState()



    }

    private fun startGallery() {
        galleryPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun showImage() {
        currentImageUri?.let {
            binding.previewImageView.setImageURI(it);
        }
        toggleButtonState()
    }

    private fun analyzeImage() {
        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra(ResultActivity.EXTRA_IMAGE_URI, currentImageUri.toString())
        startActivity(intent)
    }


    private fun generateHashedFileName(): String {
        val uniqueString = UUID.randomUUID().toString() + System.currentTimeMillis()
        val messageDigest = MessageDigest.getInstance("SHA-256")
        val hashBytes = messageDigest.digest(uniqueString.toByteArray())
        val hashString = hashBytes.joinToString(""){
            "%02x".format(it)
        }
        return "croppedImage_$hashString.jpg"
    }
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun toggleButtonState(){
        binding.analyzeButton.isEnabled = currentImageUri != null
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "onActivityResult")
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            currentImageUri = UCrop.getOutput(data!!)
            showImage()
        } else if (resultCode == UCrop.RESULT_ERROR) {
            val errorMessage = UCrop.getError(data!!)?.message.toString()
            showToast(errorMessage)
            Log.e(TAG, errorMessage)
        }
    }


    companion object {
        private const val TAG = "MainActivity"
    }
}