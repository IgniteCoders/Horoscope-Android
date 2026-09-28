package com.ignite.horoscope.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ignite.horoscope.R
import com.ignite.horoscope.data.Horoscope
import com.ignite.horoscope.utils.SessionManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.progressindicator.LinearProgressIndicator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection


class DetailActivity : AppCompatActivity() {

    lateinit var nameTextView: TextView
    lateinit var datesTextView: TextView
    lateinit var iconImageView: ImageView
    lateinit var horoscopeLuckTextView: TextView
    lateinit var periodNavigationView: BottomNavigationView
    lateinit var progressIndicator: LinearProgressIndicator

    lateinit var horoscope: Horoscope
    lateinit var session: SessionManager
    lateinit var favoriteMenu: MenuItem
    var isFavorite = false

    var horoscopeLuckText = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        session = SessionManager(this)

        nameTextView = findViewById(R.id.nameTextView)
        datesTextView = findViewById(R.id.datesTextView)
        iconImageView = findViewById(R.id.iconImageView)
        horoscopeLuckTextView = findViewById(R.id.horoscopeLuckTextView)
        progressIndicator = findViewById(R.id.progressIndicator)
        periodNavigationView = findViewById(R.id.periodNavigationView)
        periodNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_daily -> {
                    getHoroscopeLuck("daily")
                    true
                }
                R.id.action_weekly -> {
                    getHoroscopeLuck("weekly")
                    true
                }
                R.id.action_monthly -> {
                    getHoroscopeLuck("monthly")
                    true
                }
                else -> false
            }
        }

        val id = intent.getStringExtra("HOROSCOPE_ID")!!

        isFavorite = session.isFavorite(id)

        horoscope = Horoscope.getById(id)

        nameTextView.setText(horoscope.name)
        datesTextView.setText(horoscope.dates)
        iconImageView.setImageResource(horoscope.sign)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle(horoscope.name)
        supportActionBar?.setSubtitle(horoscope.dates)

        getHoroscopeLuck()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_detail_menu, menu)

        favoriteMenu = menu.findItem(R.id.action_favorite)
        setFavoriteMenu()

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_favorite -> {
                setFavorite()
                true
            }
            R.id.action_share -> {
                share()
                true
            }
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun setFavoriteMenu() {
        if (isFavorite) {
            favoriteMenu.setIcon(R.drawable.ic_favorite_selected)
        } else {
            favoriteMenu.setIcon(R.drawable.ic_favorite)
        }
    }

    fun setFavorite() {
        isFavorite = !isFavorite
        if (isFavorite) {
            session.setFavorite(horoscope.id)
        } else {
            session.setFavorite("")
        }
        setFavoriteMenu()
    }

    fun share() {
        if (horoscopeLuckText.isNotEmpty()) {
            val sendIntent = Intent()
            sendIntent.setAction(Intent.ACTION_SEND)
            sendIntent.putExtra(Intent.EXTRA_TEXT, horoscopeLuckText)
            sendIntent.setType("text/plain")

            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }
    }

    fun getHoroscopeLuck(period: String = "daily") {
        progressIndicator.show()
        horoscopeLuckTextView.setText(R.string.horoscope_luck_loading)
        CoroutineScope(Dispatchers.IO).launch {
            val url = URL("https://freehoroscopeapi.com/api/v1/get-horoscope/$period?sign=${horoscope.id}&day=TODAY")
            // HTTP Connexion
            val connection = url.openConnection() as HttpsURLConnection
            // Method
            connection.setRequestMethod("GET")

            try {
                // Response code
                val responseCode = connection.getResponseCode()

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Read the response
                    val response = readStream(connection.inputStream)

                    val jsonResponse = JSONObject(response)
                    val result = jsonResponse.getJSONObject("data").getString("horoscope")

                    CoroutineScope(Dispatchers.Main).launch {
                        horoscopeLuckText = result // Save result to share later
                        horoscopeLuckTextView.text = result
                        progressIndicator.hide()
                    }

                    Log.i("API", result)
                } else {
                    Log.e("API", "Server response: $responseCode")
                }
            } catch (e: Exception) {
                Log.e("API", "Error", e)
            } finally {
                connection.disconnect()
            }
        }
    }

    fun readStream (input: InputStream) : String {
        val reader = BufferedReader(InputStreamReader(input))
        val response = StringBuffer()
        var inputLine: String? = null

        while ((reader.readLine().also { inputLine = it }) != null) {
            response.append(inputLine)
        }
        reader.close()
        return response.toString()
    }
}