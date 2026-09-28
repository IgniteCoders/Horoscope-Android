package com.ignite.horoscope.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ignite.horoscope.R
import com.ignite.horoscope.data.Horoscope
import com.ignite.horoscope.utils.SessionManager

class HoroscopeAdapter(
    var items: List<Horoscope>,
    val onClickListener: (Int) -> Unit,
    val layout: Int
) : RecyclerView.Adapter<HoroscopeViewHolder>() {

    // Cual es la vista para los elementos
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HoroscopeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return HoroscopeViewHolder(view)
    }

    // Cuales son los datos para el elemento
    override fun onBindViewHolder(holder: HoroscopeViewHolder, position: Int) {
        val item = items[position]
        holder.render(item)
        holder.itemView.setOnClickListener {
            onClickListener(position)
        }
    }

    // Cuantos elementos se quieren listar?
    override fun getItemCount(): Int {
        return items.size
    }

    fun updateItems(items: List<Horoscope>) {
        this.items = items
        notifyDataSetChanged()
    }
}

class HoroscopeViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    val nameTextView: TextView = view.findViewById(R.id.nameTextView)
    val datesTextView: TextView = view.findViewById(R.id.datesTextView)
    val iconImageView: ImageView = view.findViewById(R.id.iconImageView)
    val favoriteImageView: ImageView = view.findViewById(R.id.favoriteImageView)

    fun render(horoscope: Horoscope) {
        nameTextView.setText(horoscope.name)
        datesTextView.setText(horoscope.dates)
        iconImageView.setImageResource(horoscope.sign)

        val session = SessionManager(itemView.context)
        if (session.isFavorite(horoscope.id)) {
            favoriteImageView.visibility = View.VISIBLE
        } else {
            favoriteImageView.visibility = View.GONE
        }
    }
}