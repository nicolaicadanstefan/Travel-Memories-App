package com.stefandev.travelmemories.data

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.stefandev.travelmemories.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MemoryAdapter(var memories: List<Memory>) : RecyclerView.Adapter<MemoryAdapter.MemoryViewHolder>() {

    class MemoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewName: TextView = view.findViewById(R.id.text_view_memory_name)
        val textViewDate: TextView = view.findViewById(R.id.text_view_date)
        val textViewLocation: TextView = view.findViewById(R.id.text_view_location)
        val textViewDetails: TextView = view.findViewById(R.id.text_view_details)
        // Add ImageView if handling images
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoryViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.memory_item, parent, false)
        return MemoryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MemoryViewHolder, position: Int) {
        val currentItem = memories[position]
        holder.textViewName.text = currentItem.memoryName
        holder.textViewDate.text = formatDate(currentItem.date) // Format date
        holder.textViewLocation.text = currentItem.location
        holder.textViewDetails.text = currentItem.details
        // Bind image if necessary
    }

    override fun getItemCount() = memories.size

    private fun formatDate(date: Date): String {
        // Format the date as needed
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(date)
    }
}