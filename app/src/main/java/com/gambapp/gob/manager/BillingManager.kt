package com.gambapp.gob.manager

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import com.android.billingclient.api.*
import com.gambapp.gob.MainActivity
import com.gambapp.gob.R
import com.gambapp.gob.adapter.GamesAdapter
import com.gambapp.gob.helpers.RealmDbHelper
import com.gambapp.gob.helpers.ToastHelper
import com.gambapp.gob.model.Realm.RealmOwnedGame

class BillingManager(val context: Context) : PurchasesUpdatedListener {

    override fun onPurchasesUpdated(responseCode: Int, purchases: MutableList<Purchase>?) {
        this.billingResponse(responseCode)
            if (purchases != null){
                val gameId: Int? = purchases[0].sku.takeLast(1).toIntOrNull()
                if (gameId != null){
                    RealmManager(context).addOwnedGame(gameId, "BOUGHT",null)
                    reloadGame()
                }


        }

    }


    fun setupBillingClient(): BillingClient {
        var billingClient = BillingClient
                .newBuilder(context)
                .setListener(this)
                .build()

        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(@BillingClient.BillingResponse billingResponseCode: Int) {
                if (billingResponseCode == BillingClient.BillingResponse.OK) {
                    println("BILLING | startConnection | RESULT OK")

                } else {
                    println("BILLING | startConnection | RESULT: $billingResponseCode")
                }
            }

            override fun onBillingServiceDisconnected() {
                println("BILLING | onBillingServiceDisconnected | DISCONNECTED")
            }
        })
        val skuList = ArrayList<String>()
        skuList.add("game.id.1")
        skuList.add("game.id.2")
        skuList.add("game.id.3")
        skuList.add("game.id.4")
        skuList.add("game.id.5")
        skuList.add("game.id.6")
        skuList.add("game.id.7")
        skuList.add("game.id.8")
        skuList.add("game.id.9")


        val params = SkuDetailsParams.newBuilder()
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)
        billingClient.querySkuDetailsAsync(params.build(), { responseCode, skuDetailsList ->
        })
        return billingClient
    }
    fun reloadGame(){
        val maniActivity = context as MainActivity
        val rv_games_list = maniActivity.findViewById<View>(R.id.rv_games_list) as RecyclerView
        val games = RealmDbHelper().getAllSorted<RealmOwnedGame>("position")
        rv_games_list.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rv_games_list.adapter = GamesAdapter(games)
        rv_games_list.adapter.notifyDataSetChanged()
    }
    fun buyParams(itemId: Int): BillingFlowParams {

        val gameId : String = itemId.toString()
        val game = "game.id."+gameId
        Log.d("Billing id", game)
        val fake ="android.test.purchased"
        return BillingFlowParams.newBuilder()
                .setSku(game)
                .setType(BillingClient.SkuType.INAPP) // SkuType.SUB for subscription
                .build()
    }

    private fun billingResponse(responseCode: Int) {
        var response = ""
        when (responseCode) {
            -1 -> response = "Erreur inconnu"
            0 -> response = "Le jeu a bien été achetée !"
            1 -> response = "Achat annulé"
            2 -> response = "Problème de connexion internet"
            3 -> response = "Version non supporté par votre téléphone"
            4 -> response = "Le produit n'est pas disponible à l'achat"
            6 -> response = "Fatal error"
            7 -> response = "Vous avez déjà acheté cet items"
            8 -> response = "Vous devez posséder cet item pour l'utiliser"
        }
        ToastHelper().shortToast(context, response)
    }
}