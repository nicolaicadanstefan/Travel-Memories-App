package com.stefandev.travelmemories

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import com.stefandev.travelmemories.data.Memory
import com.stefandev.travelmemories.data.MemoryViewModel
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var memoryViewModel: MemoryViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_main)
        val addButton: ImageButton = findViewById(R.id.button_add_memory)
        addButton.setOnClickListener{
            showAddMemoryDialog() // display add memory dialog page
        }
    }

    private fun showAddMemoryDialog(){
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_memory, null)
        val customDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialogView.findViewById<Button>(R.id.buttonSaveMemory).setOnClickListener {
            val memoryName = dialogView.findViewById<EditText>(R.id.editMemoryName).text.toString()
            val date = dialogView.findViewById<EditText>(R.id.editDate).text.toString()
            val location = dialogView.findViewById<EditText>(R.id.editLocation).text.toString()
            val favorite = dialogView.findViewById<CheckBox>(R.id.checkBoxFavorite).isChecked

            saveMemory(memoryName, date, location, favorite)
            customDialog.dismiss()
        }
        customDialog.show()
    }

    private fun saveMemory(memoryName: String, dateString: String, location: String, favorite: Boolean) {
        // Convert dateString to Date object, handle exceptions
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date: Date = try {
            dateFormat.parse(dateString) ?: Date()
        } catch (e: ParseException) {
            Date() // Use current date if parsing fails
        }

        val newMemory = Memory(0, memoryName, date, location, favorite)
        memoryViewModel.addMemory(newMemory) // Using View Model to save in the database
    }
}