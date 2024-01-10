package com.stefandev.travelmemories.data

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.stefandev.travelmemories.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MemoryAdapter(var memories: List<Memory>) :
    RecyclerView.Adapter<MemoryAdapter.MemoryViewHolder>() {
    var memoriesFull: List<Memory> = ArrayList(memories)

    class MemoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewName: TextView = view.findViewById(R.id.text_view_memory_name)
        val textViewDate: TextView = view.findViewById(R.id.text_view_date)
        val textViewLocation: TextView = view.findViewById(R.id.text_view_location)
        val textViewDetails: TextView = view.findViewById(R.id.text_view_details)
        val imageViewFavorite: ImageView = view.findViewById(R.id.image_view_favorite)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoryViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.memory_item, parent, false)
        return MemoryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MemoryViewHolder, position: Int) {
        val currentItem = memories[position]
        holder.textViewName.text = currentItem.memoryName
        holder.textViewDate.text = formatDate(currentItem.date) // Format date
        holder.textViewLocation.text = currentItem.location
        holder.textViewDetails.text = currentItem.details

        // Set visibility of star icon based on favorite status
        if (currentItem.favorite) {
            holder.imageViewFavorite.visibility = View.VISIBLE
        } else {
            holder.imageViewFavorite.visibility = View.GONE
        }
    }

    override fun getItemCount() = memories.size

    private fun formatDate(date: Date): String {
        // Format the date as needed
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return dateFormat.format(date)
    }

    fun filter(query: String) {
        memories = if (query.isEmpty()) {
            memoriesFull
        } else {
            val filteredList = ArrayList<Memory>()
            for (memory in memoriesFull) {
                if (memory.memoryName.lowercase(Locale.getDefault())
                        .contains(query.lowercase(Locale.getDefault()))
                ) {
                    filteredList.add(memory)
                }
            }
            filteredList
        }
        notifyDataSetChanged()
    }
}