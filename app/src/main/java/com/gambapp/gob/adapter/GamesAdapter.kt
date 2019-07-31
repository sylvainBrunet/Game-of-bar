package com.gambapp.gob.adapter


import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.gambapp.gob.GameDetailActivity
import com.gambapp.gob.R
import com.gambapp.gob.R.layout.games_list_items
import com.gambapp.gob.model.Realm.RealmOwnedGame

class GamesAdapter(private val games: ArrayList<RealmOwnedGame>) : RecyclerView.Adapter<GamesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(games_list_items, parent, false)
        return ViewHolder(v, parent.context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(games[position])
    }

    override fun getItemCount(): Int {
        return games.size
    }

    class ViewHolder(view: View, context: Context) : RecyclerView.ViewHolder(view) {
        private val cont = context
        fun bindItems(game: RealmOwnedGame?) {
            val imageView: ImageView = itemView.findViewById(R.id.imageDetails)
            val gameName: TextView = itemView.findViewById(R.id.gameName)
            if (game != null) {
                val img = cont.resources.getIdentifier("img_${game.game!!.id}", "drawable", cont.packageName)
                imageView.setImageBitmap(BitmapFactory.decodeResource(cont.resources, img))
                gameName.text = game.game!!.title
            }

            //set the onclick listener for the singlt list item
            imageView.setOnClickListener {
                val intent = Intent(cont, GameDetailActivity::class.java)
                intent.putExtra("game_id", game!!.game!!.id)
                startActivity(cont, intent, null)
            }
        }

    }
}