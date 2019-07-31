package com.gambapp.gob.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.gambapp.gob.R


class BarJackPlayerAdapter(val locationList: ArrayList<Map.Entry<String, Int>>): RecyclerView.Adapter<BarJackPlayerAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: BarJackPlayerAdapter.ViewHolder, position: Int) {
        val location = locationList[position]
        holder.locationName?.text = location.key
        holder.locationAddress?.text = location.value.toString()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BarJackPlayerAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.bar_jack_player_list_items, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return locationList.size
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val locationName = itemView.findViewById<TextView>(R.id.namePrefere)
        val locationAddress = itemView.findViewById<TextView>(R.id.scorePrefere)
    }
}
