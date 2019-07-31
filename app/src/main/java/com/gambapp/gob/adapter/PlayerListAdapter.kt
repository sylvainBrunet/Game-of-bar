package com.gambapp.gob.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import com.gambapp.gob.R
import com.gambapp.gob.helpers.RealmDbHelper
import com.gambapp.gob.model.Realm.RealmPlayer
import io.realm.RealmRecyclerViewAdapter
import io.realm.RealmResults


internal class PlayerListAdapter(data: RealmResults<RealmPlayer>) :
        RealmRecyclerViewAdapter<RealmPlayer, PlayerListAdapter.MyViewHolder>(data, true) {


    init {

        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.player_list_items, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val obj = getItem(position)



        holder.name?.text = obj?.player_name
    }

    fun removeAt(position: Int) {
        val obj = getItem(position)
        obj.let {
            val playerItem = RealmDbHelper().getByKey<RealmPlayer>("player_name", obj!!.player_name!!)!!
            RealmDbHelper().delete<RealmPlayer>(playerItem)

            notifyItemRemoved(position)
            notifyDataSetChanged()
        }

    }


    internal inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var viewBackground: RelativeLayout? = view.findViewById(R.id.view_background)
        var viewForeground: RelativeLayout? = view.findViewById(R.id.view_foreground)
        var id: TextView? = null
        var name: TextView? = null

        init {


            name = view.findViewById(R.id.playerFromRecycler)
        }

    }
}
