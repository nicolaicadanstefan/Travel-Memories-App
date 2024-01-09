package com.stefandev.travelmemories

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.stefandev.travelmemories.data.Memory
import com.stefandev.travelmemories.data.MemoryAdapter
import com.stefandev.travelmemories.data.MemoryViewModel
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var memoryViewModel: MemoryViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var memoryAdapter: MemoryAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recycler_view_memories)
        recyclerView.layoutManager = LinearLayoutManager(this)
        memoryAdapter = MemoryAdapter(emptyList()) // Initialize with an empty list
        recyclerView.adapter = memoryAdapter

        memoryViewModel = ViewModelProvider(this)[MemoryViewModel::class.java]

        // Observe the LiveData from the ViewModel
        memoryViewModel.readAllData.observe(this) { memories ->
            // Update the RecyclerView adapter's data
            memoryAdapter.memories = memories
            memoryAdapter.notifyDataSetChanged()
        }

        memoryViewModel = ViewModelProvider(this)[MemoryViewModel::class.java]
        val addButton: ImageButton = findViewById(R.id.button_add_memory)
        addButton.setOnClickListener {
            showAddMemoryDialog() // display add memory dialog page
        }
    }

    private fun showAddMemoryDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_memory, null)
        val customDialog = AlertDialog.Builder(this, R.style.CustomDialogStyle)
            .setView(dialogView)
            .create()

        // Set dialog width and height to match the content of the Edit Text views
        customDialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        dialogView.findViewById<Button>(R.id.buttonSaveMemory).setOnClickListener {
            val memoryName = dialogView.findViewById<EditText>(R.id.editMemoryName).text.toString()
            val date = dialogView.findViewById<EditText>(R.id.editDate).text.toString()
            val location = dialogView.findViewById<EditText>(R.id.editLocation).text.toString()
            val details = dialogView.findViewById<EditText>(R.id.editDetails).text.toString()
            val favorite = dialogView.findViewById<CheckBox>(R.id.checkBoxFavorite).isChecked

            if (validateMemoryInfo(memoryName, date, location)) {
                saveMemory(memoryName, date, location, details, favorite)
                customDialog.dismiss()
            }
        }
        customDialog.show()
    }

    private fun saveMemory(
        memoryName: String,
        dateString: String,
        location: String,
        details: String,
        favorite: Boolean
    ) {
        // Convert dateString to Date object, handle exceptions
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date: Date = try {
            dateFormat.parse(dateString) ?: Date()
        } catch (e: ParseException) {
            Date() // Use current date if parsing fails
        }

        val newMemory = Memory(0, memoryName, date, location, details, favorite)
        memoryViewModel.addMemory(newMemory) {
            showToast("Your travel memory was successfully added!")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun validateMemoryInfo(
        memoryName: String,
        dateString: String,
        location: String
    ): Boolean {
        if (memoryName.isEmpty() && dateString.isEmpty() && location.isEmpty()) {
            showToast("Please complete the form for adding a new memory!")
            return false
        } else if (memoryName.isEmpty()) {
            showToast("Please insert a name for the travel memory!")
            return false
        } else if (dateString.isEmpty()) {
            showToast("Please insert a date!")
            return false
        } else if (location.isEmpty()) {
            showToast("Please insert a location!")
            return false
        }
        return true
    }
}