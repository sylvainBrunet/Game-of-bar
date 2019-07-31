package com.gambapp.gob.adapter


import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.gambapp.gob.R
import com.gambapp.gob.R.layout.games_example_list_items
import com.gambapp.gob.model.Realm.RealmPreview
import io.realm.RealmList

class GamesExampleAdapter(val games: RealmList<RealmPreview>) : RecyclerView.Adapter<GamesExampleAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(games_example_list_items, parent, false)
        return ViewHolder(v, parent.context)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(games[position])
    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return games.size
    }

    class ViewHolder(view: View, context: Context): RecyclerView.ViewHolder(view) {
        private val cont = context
        fun bindItems(game: RealmPreview?){
            val gameName: TextView = itemView.findViewById(R.id.gameName)
            if (game != null) {

                gameName.text = game.preview
            }


        }

    }
}