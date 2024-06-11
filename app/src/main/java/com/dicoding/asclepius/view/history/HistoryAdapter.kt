package com.dicoding.asclepius.view.history

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.asclepius.data.local.ClassificationVerdict
import com.dicoding.asclepius.databinding.HistoryCardBinding
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

class HistoryAdapter :
    ListAdapter<ClassificationVerdict, HistoryAdapter.ViewHolder>(DIFF_CALLBACK) {
    inner class ViewHolder(private val binding: HistoryCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(verdict: ClassificationVerdict) {
            binding.imageViewHistory.setImageURI(Uri.parse(verdict.imageUri))
            binding.tvConfidenceScore.text =
                NumberFormat.getPercentInstance().format(verdict.confidence)
            binding.tvVerdictLabel.text = verdict.label
            val dateFormat = SimpleDateFormat("EEE, dd MM yyyy, HH:mm", Locale.getDefault())
            binding.tvTime.text = dateFormat.format(verdict.time)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = HistoryCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val verdict = getItem(position)
        holder.bind(verdict)

    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ClassificationVerdict>() {
            override fun areItemsTheSame(
                prevItem: ClassificationVerdict,
                nextItem: ClassificationVerdict
            ): Boolean {
                return prevItem == nextItem
            }

            override fun areContentsTheSame(
                prevItem: ClassificationVerdict,
                nextItem: ClassificationVerdict
            ): Boolean {
                return prevItem == nextItem
            }
        }
    }
}
