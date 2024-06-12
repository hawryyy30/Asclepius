package com.dicoding.asclepius.view.result

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.net.Uri
import android.os.Build
import android.view.View

import android.widget.Toast
import com.dicoding.asclepius.databinding.ActivityResultBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper
import com.dicoding.asclepius.util.ViewModelFactory
import androidx.activity.viewModels
import androidx.core.view.get
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.asclepius.Konfetti.Presets
import com.dicoding.asclepius.R
import com.dicoding.asclepius.data.local.ClassificationVerdict
import com.dicoding.asclepius.view.AboutActivity
import com.dicoding.asclepius.view.history.HistoryActivity
import org.tensorflow.lite.task.vision.classifier.Classifications
import java.text.NumberFormat

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding
    private lateinit var imageClassifierHelper: ImageClassifierHelper
    private val viewModel: ResultViewModel by viewModels<ResultViewModel> {
        ViewModelFactory.fetchInstance(this)
    }
    private val articleViewModel: ArticlesViewModel by viewModels<ArticlesViewModel> {
        ViewModelFactory.fetchInstance(this)
    }

    private var verdict: ClassificationVerdict? = null

    private val articleAdapter = ArticlesAdapter().apply {
        setHasStableIds(true)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
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

        val layoutManager = LinearLayoutManager(this)
        binding.rvArticles.layoutManager = layoutManager
        binding.rvArticles.adapter = articleAdapter
        observeArticles()

        intent.getStringExtra(EXTRA_IMAGE_URI)?.let { imageUriString ->
            val imageUri = Uri.parse(imageUriString)
            binding.resultImage.setImageURI(imageUri)
            initializeImageClassifier(imageUri)
            imageClassifierHelper.classifyStaticImage(imageUri)
            binding.saveBtn.setOnClickListener {
                showToast("Your result is saved ")
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
        imageClassifierHelper = ImageClassifierHelper(context = this,
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
            })
    }

    @SuppressLint("SetTextI18n")
    private fun displayResult(imageUri: Uri, label: String, confidenceScore: Float) {
        binding.resultImage.setImageURI(imageUri)
        binding.confidenceScore.text =
            "${NumberFormat.getPercentInstance().format(confidenceScore)} $label"
        binding.circularProgress.progress = (confidenceScore * 100).toInt().let { progress ->

            if (label == "Cancer") {
                binding.resultMessage.text = getString(R.string.severe_desc)
                progress
            } else {
                toggleKonfetti()
                binding.resultMessage.text = getString(R.string.low_desc)
                100 - progress
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this@ResultActivity, message, Toast.LENGTH_SHORT).show()
    }


    private fun observeArticles() {
        articleViewModel.articlesLiveData.observe(this) {
            articleAdapter.submitList(it)
            updateEmptyStateVisibility(it.isEmpty())
        }
        articleViewModel.isLoadingLiveData.observe(this) {

            binding.loadingBar.visibility = if (it) View.VISIBLE else View.GONE
            binding.loadingMessage.visibility = if (it) View.VISIBLE else View.GONE
        }
        articleViewModel.errorMsgLiveData.observe(this) {
            if (it.isNotEmpty()) {
                binding.emptyState.visibility = if (it.isNotEmpty()) View.GONE else View.VISIBLE
                showToast(it)
                updateEmptyStateVisibility(true)
            }
        }
    }

    fun toggleKonfetti() {
        binding.konfettiView.start(Presets.rain())
    }

    private fun updateEmptyStateVisibility(isEmpty: Boolean) {
        val isEmptyStateVisible = isEmpty || articleAdapter.itemCount == 0
        binding.emptyState.visibility = if (isEmptyStateVisible) View.VISIBLE else View.GONE
    }


    companion object {
        const val EXTRA_IMAGE_URI = "extra_image_uri"
        const val EXTRA_RESULT = "extra_result"
    }
}
