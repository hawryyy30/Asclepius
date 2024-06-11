package com.dicoding.asclepius.view.history

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.asclepius.R
import com.dicoding.asclepius.data.local.ClassificationVerdict
import com.dicoding.asclepius.databinding.ActivityHistoryBinding
import com.dicoding.asclepius.util.ViewModelFactory

class HistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryBinding
    private val viewModel: HistoryViewModel by viewModels<HistoryViewModel> {
        ViewModelFactory.fetchInstance(this)
    }
    private val adapter = HistoryAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val layoutManager = LinearLayoutManager(this)
        binding.rvHistory.layoutManager = layoutManager
        binding.rvHistory.adapter = adapter

        viewModel.results.observe(this) { results ->
            adapter.submitList(results)

            if (results.isNotEmpty()) {
                binding.emptyState.visibility = View.GONE
                binding.noHistoryText.visibility = View.GONE
            } else {
                binding.emptyState.visibility = View.VISIBLE
                binding.noHistoryText.visibility = View.VISIBLE
            }
        }
    }
}