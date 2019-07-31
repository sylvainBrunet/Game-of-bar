package com.gambapp.gob.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.RelativeLayout
import android.widget.TextView
import com.gambapp.gob.R
import com.gambapp.gob.helpers.RealmDbHelper
import com.gambapp.gob.model.Realm.RealmPlayer
import io.realm.RealmRecyclerViewAdapter
import io.realm.RealmResults
import android.widget.CompoundButton
import com.gambapp.gob.manager.PrefereManager
import java.util.*
import kotlin.collections.HashMap


internal class ButtonPlayerList(data: RealmResults<RealmPlayer>, cont : Context,val manager: PrefereManager) :
        RealmRecyclerViewAdapter<RealmPlayer, ButtonPlayerList.MyViewHolder>(data, true) {
    var selectedStrings = ArrayList<String>()



    init {

        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.button_list_items, parent, false)
        return MyViewHolder(itemView,parent.context,this.manager)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val obj = getItem(position)



        holder.tv?.text = obj?.player_name
    }

    fun removeAt(position: Int) {
        val obj = getItem(position)
        obj.let {
            val playerItem = RealmDbHelper().getByKey<RealmPlayer>("player_name", obj!!.player_name)!!
            RealmDbHelper().delete<RealmPlayer>(playerItem)

            notifyItemRemoved(position)
            notifyDataSetChanged()
        }

    }


    internal inner class MyViewHolder(view: View, context: Context,manager: PrefereManager) : RecyclerView.ViewHolder(view) {
        private val cont = context

        var viewBackground: RelativeLayout? = view.findViewById(R.id.view_background)
        var viewForeground: RelativeLayout? = view.findViewById(R.id.view_foreground)
        var id: TextView? = null
        var tv: CheckBox ? = null

        init {
            val playerName = RealmDbHelper().getAll<RealmPlayer>()

            var arr : HashMap<Int,String> = HashMap()
            for (i in 0 until playerName.size) {
                arr[i] = playerName.get(i)!!.player_name

            }
            tv = view.findViewById(R.id.playerFromRecycler)
            tv!!.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    tv!!.setTextColor(context.resources.getColor(R.color.qtype12))
                    selectedStrings.add(tv!!.text.toString())
                    Log.d("tag",tv!!.text.toString())
                    manager.letsGetRandom(getKeyByValue(arr,tv!!.text)!!)

                } else {
                    selectedStrings.remove(tv!!.getText().toString())
                    tv!!.setTextColor(context.resources.getColor(R.color.white))

                }
            }


        }
        fun resetCheckBox(){
            tv!!.isChecked = false
        }

    }
}

fun <T, E> getKeyByValue(map: Map<T, E>, value: E): T? {
    for ((key, value1) in map) {
        if (Objects.equals(value, value1)) {
            return key
        }
    }
    return null
}
