package com.example.safetalk

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * MainActivity handles speech recognition, location & weather info,
 * and saves journal entries using Room.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var viewModel: JournalViewModel
    private val weatherRepo = WeatherRepository()

    private lateinit var startButton: Button
    private lateinit var historyButton: Button
    private lateinit var resultTextView: TextView
    private lateinit var locationTextView: TextView
    private lateinit var weatherTextView: TextView

    private var recognizedText: String = ""
    private var currentCity: String = "Unknown"

    // Permissions result handler
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.all { it.value }
        if (!granted) {
            Toast.makeText(this, "Please grant all permissions", Toast.LENGTH_SHORT).show()
        } else {
            startListening()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this)[JournalViewModel::class.java]

        startButton = findViewById(R.id.startButton)
        historyButton = findViewById(R.id.historyButton)
        resultTextView = findViewById(R.id.resultTextView)
        locationTextView = findViewById(R.id.locationTextView)
        weatherTextView = findViewById(R.id.weatherTextView)

        // Start voice input only if internet is available
        startButton.setOnClickListener {
            if (!isNetworkAvailable()) {
                Toast.makeText(this, "No internet connection. Please connect to the internet.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            )
        }

        historyButton.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }
    }

    /**
     * Check if the device has an active internet connection
     */
    private fun isNetworkAvailable(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val capabilities = cm.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    /**
     * Start listening for voice input after permissions are granted
     */
    private fun startListening() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())

        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                Toast.makeText(this@MainActivity, "🎤 Listening...", Toast.LENGTH_SHORT).show()
            }

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                recognizedText = matches?.firstOrNull() ?: "Could not understand"
                resultTextView.text = recognizedText
                getLocationAndSave()
            }

            override fun onError(error: Int) {
                Toast.makeText(this@MainActivity, "Speech error: $error", Toast.LENGTH_SHORT).show()
            }

            override fun onBeginningOfSpeech() {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onRmsChanged(rmsdB: Float) {}
        })

        speechRecognizer.startListening(intent)
    }

    /**
     * Use GPS to determine city and weather, then save journal
     */
    private fun getLocationAndSave() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val geocoder = android.location.Geocoder(this, Locale.getDefault())
                    val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    currentCity = addresses?.firstOrNull()?.locality ?: "Unknown"
                    locationTextView.text = "📍 $currentCity"

                    CoroutineScope(Dispatchers.IO).launch {
                        val weather = weatherRepo.getWeather(location.latitude, location.longitude)
                        runOnUiThread {
                            weatherTextView.text = "🌤 $weather"
                        }
                    }
                }
                saveJournal()
            }
        } catch (e: SecurityException) {
            Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Store journal data into Room database
     */
    private fun saveJournal() {
        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        val journal = Journal(
            text = recognizedText,
            city = currentCity,
            timestamp = timestamp
        )
        viewModel.insert(journal)
        Toast.makeText(this, "Journal saved", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::speechRecognizer.isInitialized) {
            speechRecognizer.destroy()
        }
    }
}