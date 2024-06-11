package com.dicoding.asclepius.view.result

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import com.dicoding.asclepius.databinding.ActivityResultBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper
import com.dicoding.asclepius.util.ViewModelFactory
import androidx.activity.viewModels
import com.dicoding.asclepius.R
import com.dicoding.asclepius.data.local.ClassificationVerdict
import org.tensorflow.lite.task.vision.classifier.Classifications
import java.text.NumberFormat

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding
    private lateinit var imageClassifierHelper: ImageClassifierHelper
    private val viewModel: ResultViewModel by viewModels<ResultViewModel> {
        ViewModelFactory.fetchInstance(this)
    }

    private var verdict: ClassificationVerdict? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intent.getStringExtra(EXTRA_IMAGE_URI)?.let { imageUriString ->
            val imageUri = Uri.parse(imageUriString)
            binding.resultImage.setImageURI(imageUri)
            initializeImageClassifier(imageUri)
            imageClassifierHelper.classifyStaticImage(imageUri)
            binding.saveBtn.setOnClickListener {
                verdict?.let { viewModel.insert(it) }
                finish()
            }
        } ?: run {
            verdict = if (Build.VERSION.SDK_INT >= 33) {
                intent.getParcelableExtra(EXTRA_RESULT, ClassificationVerdict::class.java)
            } else {
                @Suppress("DEPRECATION") intent.getParcelableExtra(EXTRA_RESULT)
            }

            verdict?.let { verdict ->
                displayResult(Uri.parse(verdict.imageUri), verdict.label, verdict.confidence)
                binding.saveBtn.setOnClickListener {
                    viewModel.delete(verdict)
                    finish()
                }
            }
        }
    }

    private fun initializeImageClassifier(imageUri: Uri) {
        imageClassifierHelper = ImageClassifierHelper(
            context = this,
            classifierListener = object : ImageClassifierHelper.ClassifierListener {
                override fun onError(error: String) {
                    showToast(error)
                }

                override fun onResults(results: List<Classifications>?) {
                    results?.takeIf { it.isNotEmpty() && it[0].categories.isNotEmpty() }
                        ?.let { resultList ->
                            resultList[0].categories.maxByOrNull { it.score }?.let { category ->
                                runOnUiThread {
                                    displayResult(imageUri, category.label, category.score)
                                    verdict = ClassificationVerdict(
                                        time = System.currentTimeMillis(),
                                        imageUri = imageUri.toString(),
                                        label = category.label,
                                        confidence = category.score
                                    )
                                }
                            }
                        } ?: runOnUiThread {
                        binding.confidenceScore.text = "-"
                    }
                }
            }
        )
    }

    @SuppressLint("SetTextI18n")
    private fun displayResult(imageUri: Uri, label: String, confidenceScore: Float) {
        binding.resultImage.setImageURI(imageUri)
        binding.confidenceScore.text =
            "${NumberFormat.getPercentInstance().format(confidenceScore)} $label"
        val progress = (confidenceScore * 100).toInt()

        Log.d("ResultActivity", "Label: $label, Progress: $progress")

        if (label.contains("cancer", ignoreCase = true)) {
            binding.resultMessage.text = getString(R.string.severe_desc)
        } else {
            binding.resultMessage.text = getString(R.string.low_desc)
        }
        binding.circularProgress.progress = progress
    }

    private fun showToast(message: String) {
        Toast.makeText(this@ResultActivity, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val EXTRA_IMAGE_URI = "extra_image_uri"
        const val EXTRA_RESULT = "extra_result"
    }
}
