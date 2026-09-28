package com.ignite.horoscope.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ignite.horoscope.R
import com.ignite.horoscope.adapters.HoroscopeAdapter
import com.ignite.horoscope.data.Horoscope
import com.ignite.horoscope.utils.search

class MainActivity : AppCompatActivity() {

    lateinit var recyclerView: RecyclerView
    lateinit var adapter: HoroscopeAdapter

    var horoscopeList: List<Horoscope> = Horoscope.getAll()

    var isGridViewEnabled = false
    lateinit var viewModeMenu: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        supportActionBar?.setTitle(R.string.activity_main_title)

        recyclerView = findViewById(R.id.recyclerView)

        setupViewMode()
    }

    override fun onResume() {
        super.onResume()

        adapter.updateItems(horoscopeList)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_main_menu, menu)

        viewModeMenu = menu.findItem(R.id.action_view_mode)
        setViewModeMenu()

        val searchView = menu.findItem(R.id.action_search).actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                horoscopeList = Horoscope.getAll().filter {
                    getString(it.name).search(newText)
                    || getString(it.dates).search(newText)
                }

                adapter.updateItems(horoscopeList)
                return true
            }
        })

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_view_mode -> {
                isGridViewEnabled = !isGridViewEnabled
                setupViewMode()
                setViewModeMenu()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupViewMode() {
        if (isGridViewEnabled) {
            adapter = HoroscopeAdapter(horoscopeList, ::onItemClickListener, R.layout.item_horoscope_grid)
            recyclerView.layoutManager = GridLayoutManager(this, 2)
        } else {
            adapter = HoroscopeAdapter(horoscopeList, ::onItemClickListener, R.layout.item_horoscope_list)
            recyclerView.layoutManager = LinearLayoutManager(this)
        }
        recyclerView.adapter = adapter
    }

    private fun setViewModeMenu() {
        if (isGridViewEnabled) {
            viewModeMenu.setIcon(R.drawable.ic_list_view)
        } else {
            viewModeMenu.setIcon(R.drawable.ic_grid_view)
        }
    }

    fun onItemClickListener(position: Int) {
        val horoscope = horoscopeList[position]
        goToDetail(horoscope)
    }

    fun goToDetail(horoscope: Horoscope) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra("HOROSCOPE_ID", horoscope.id)
        startActivity(intent)
    }
}