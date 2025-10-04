package com.example.safetalk

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

/**
 * RecyclerView Adapter for displaying a list of journal entries.
 * Uses ListAdapter with DiffUtil for efficient updates.
 */
class JournalAdapter :
    ListAdapter<Journal, JournalAdapter.JournalViewHolder>(JournalDiffCallback()) {

    /**
     * ViewHolder class that holds references to UI elements in each item view.
     */
    class JournalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewText: TextView = itemView.findViewById(R.id.textViewText)
        val textViewCity: TextView = itemView.findViewById(R.id.textViewCity)
        val textViewTime: TextView = itemView.findViewById(R.id.textViewTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JournalViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_journal, parent, false)
        return JournalViewHolder(view)
    }

    override fun onBindViewHolder(holder: JournalViewHolder, position: Int) {
        val journal = getItem(position)
        holder.textViewText.text = journal.text
        holder.textViewCity.text = "📍 ${journal.city}"
        holder.textViewTime.text = journal.timestamp
    }
}

/**
 * DiffUtil callback for efficiently updating journal list items.
 */
class JournalDiffCallback : DiffUtil.ItemCallback<Journal>() {
    override fun areItemsTheSame(oldItem: Journal, newItem: Journal): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Journal, newItem: Journal): Boolean {
        return oldItem == newItem
    }
}