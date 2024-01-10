package com.stefandev.travelmemories

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.location.Location
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.stefandev.travelmemories.data.Memory
import com.stefandev.travelmemories.data.MemoryAdapter
import com.stefandev.travelmemories.data.MemoryViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.Manifest
import com.google.gson.Gson
import android.location.Geocoder
import kotlinx.coroutines.Dispatchers as Dispatchers1

class MainActivity : AppCompatActivity() {
    private lateinit var memoryViewModel: MemoryViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var memoryAdapter: MemoryAdapter
    private var showOnlyFavorites = false
    private lateinit var locationManager: LocationManager
    private val locationPermissionCode = 100

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
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
            memoryAdapter.memoriesFull = ArrayList(memories)
            memoryAdapter.notifyDataSetChanged()

            // Update Memory Count
            val memoryCount = memories.size
            findViewById<TextView>(R.id.tv_memory_count).text = "Memories: $memoryCount"
        }
        // Add divider
        val dividerItemDecoration = DividerItemDecoration(
            recyclerView.context, (recyclerView.layoutManager as LinearLayoutManager).orientation
        )
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.divider)!!)
        recyclerView.addItemDecoration(dividerItemDecoration)

        memoryViewModel = ViewModelProvider(this)[MemoryViewModel::class.java]
        val addButton: ImageButton = findViewById(R.id.button_add_memory)
        addButton.setOnClickListener {
            showAddMemoryDialog() // display add memory dialog page
        }
        setupSearchView()

        val fab: FloatingActionButton = findViewById(R.id.fab_show_favorites)
        fab.setOnClickListener {
            showOnlyFavorites = !showOnlyFavorites
            filterMemories()
        }

        val fabDeleteAll: FloatingActionButton = findViewById(R.id.fab_delete_all)
        fabDeleteAll.setOnClickListener {
            deleteAllMemories()
        }

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                locationPermissionCode
            )
        } else {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                5000,
                5f
            ) { location ->
                updateLocationAndWeather(location)
            }
        }
    }

    private fun showAddMemoryDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_memory, null)
        val customDialog =
            AlertDialog.Builder(this, R.style.CustomDialogStyle).setView(dialogView).create()

        // Set dialog width and height to match the content of the Edit Text views
        customDialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
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
        memoryName: String, dateString: String, location: String, details: String, favorite: Boolean
    ) {
        // Convert dateString to Date object, handle exceptions
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
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
        memoryName: String, dateString: String, location: String
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

    private fun setupSearchView() {
        val searchView: SearchView = findViewById(R.id.search_view)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                memoryAdapter.filter(newText.orEmpty())
                return true
            }
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun filterMemories() {
        if (showOnlyFavorites) {
            val favoriteMemories = memoryAdapter.memoriesFull.filter { it.favorite }
            memoryAdapter.memories = favoriteMemories
        } else {
            memoryAdapter.memories = memoryAdapter.memoriesFull
        }
        memoryAdapter.notifyDataSetChanged()
    }

    private fun deleteAllMemories() {
        memoryViewModel.deleteAllMemories() {
            showToast("All memories deleted!")
        }
    }

    fun fetchWeatherData(lat: Double, lon: Double) {
        val apiKey = "bc3b076209d35792d75831dd4eded1df"
        val urlString =
            "https://api.openweathermap.org/data/2.5/weather?lat=$lat&lon=$lon&appid=$apiKey"

        CoroutineScope(Dispatchers1.IO).launch {
            val url = URL(urlString)
            with(url.openConnection() as HttpURLConnection) {
                requestMethod = "GET"
                inputStream.bufferedReader().use {
                    val response = it.readText()
                    val weatherInfo = parseWeatherResponse(response)
                    withContext(Dispatchers1.Main) {
                        findViewById<TextView>(R.id.tv_weather).text = weatherInfo
                    }
                }
            }
        }
    }

    private fun parseWeatherResponse(response: String): String {
        // Assuming the response contains temperature data in JSON format
        val gson = Gson()
        val weatherResponse = gson.fromJson(response, WeatherResponse::class.java)
        val temperatureInCelsius = weatherResponse.main.temp - 273.15 // Convert Kelvin to Celsius
        return "${weatherResponse.weather[0].description}, ${
            String.format(
                "%.2f",
                temperatureInCelsius
            )
        }°C"
    }

    private fun updateLocationAndWeather(location: Location) {
        CoroutineScope(Dispatchers1.IO).launch {
            val geocoder = Geocoder(this@MainActivity, Locale.getDefault())
            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            if (addresses != null) {
                if (addresses.isNotEmpty()) {
                    val city = addresses[0]?.locality // Get the city name
                    val country = addresses[0]?.countryName // Get the country name
                    val locationText = if (city != null && country != null) {
                        "$city, $country"
                    } else city ?: (country ?: "")
                    withContext(Dispatchers1.Main) {
                        findViewById<TextView>(R.id.tv_current_location).text = locationText
                    }
                }
            }
        }

        // Fetch and update weather data
        fetchWeatherData(location.latitude, location.longitude)
    }

    // Handle the result of location permission request
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == locationPermissionCode) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Check again if permission is granted
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    // Permission granted, fetch location
                    locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        5000,
                        5f
                    ) { location ->
                        updateLocationAndWeather(location)
                    }
                }
            } else {
                // Permission denied, handle the case
                showToast("Location permission denied")
            }
        }
    }
}

data class WeatherResponse(val weather: Array<Weather>, val main: Main) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WeatherResponse

        if (!weather.contentEquals(other.weather)) return false
        return main == other.main
    }

    override fun hashCode(): Int {
        var result = weather.contentHashCode()
        result = 31 * result + main.hashCode()
        return result
    }
}

data class Weather(val description: String)
data class Main(val temp: Double)