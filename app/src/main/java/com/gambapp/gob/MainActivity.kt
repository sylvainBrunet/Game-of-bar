package com.gambapp.gob

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearSnapHelper
import android.support.v7.widget.RecyclerView
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.gambapp.gob.adapter.GamesAdapter
import com.gambapp.gob.adapter.GamesExampleAdapter
import com.gambapp.gob.helpers.RealmDbHelper
import com.gambapp.gob.manager.BillingManager
import com.gambapp.gob.manager.ConfigManager
import com.gambapp.gob.manager.RealmManager
import com.gambapp.gob.model.Realm.RealmGame
import com.gambapp.gob.model.Realm.RealmOwnedGame
import com.gambapp.gob.services.ActivationCodeService
import com.gambapp.gob.services.MyCallback
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.database.*
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceId.*
import com.tapadoo.alerter.Alerter
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), View.OnClickListener, PurchasesUpdatedListener {
    private val REALM_DATABASE_VERSION: Int = BuildConfig.VERSION_CODE
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var database: DatabaseReference
    private lateinit var billingClient: BillingClient
    private var m_Text = ""
    private var shopsGame: ArrayList<Any?> = arrayListOf()
    private var shopsRealmGame: ArrayList<RealmGame?> = arrayListOf()
    private var imgs = arrayListOf<ImageView>()
    private var overlay = arrayListOf<ImageView>()
    private var gameTitleList = arrayListOf<TextView>()
    private var gamePriceList = arrayListOf<TextView>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        val configManager = ConfigManager(this)
        billingClient = BillingManager(this).setupBillingClient()
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        configManager.initConfiguration(this)
        configManager.shouldUpdateRealmDatabaseVersion(REALM_DATABASE_VERSION)

        imgs.add(imageDetails)
        imgs.add(imageDetails2)
        imgs.add(imageDetails3)
        imgs.add(imageDetails4)
        gamePriceList.add(gamePrice)
        gamePriceList.add(gamePrice2)
        gamePriceList.add(gamePrice3)
        gamePriceList.add(gamePrice4)
        gameTitleList.add(gameTitle)
        gameTitleList.add(gameTitle2)
        gameTitleList.add(gameTitle3)
        gameTitleList.add(gameTitle4)
        overlay.add(imageDetailsOverLay)
        overlay.add(imageDetails2OverLay)
        overlay.add(imageDetails3OverLay)
        overlay.add(imageDetails4OverLay)


        database = FirebaseDatabase.getInstance().reference
        database.child("shop").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val map = dataSnapshot.value as Map<*, *>
                shopsGame = ArrayList(map.values)
                for (s in shopsGame) {
                    val game = RealmDbHelper().get<RealmGame>(s.toString().toLong())
                    shopsRealmGame.add(game)
                }
                for (i in 0..3) {
                    setLock(i)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.e("update", "Failed to read app title value.", error.toException())
                Alerter.create(this@MainActivity)
                        .setTitle("Erreur !")
                        .setText("Pas de connexion internet ! ")
                        .setBackgroundColorRes(R.color.alert_default_error_background)
                        .show()
            }
        })

        getBuy()
        getGamesAdapter()
        restore.setOnClickListener {
            getBuy()
            Alerter.create(this@MainActivity)
                    .setTitle("Bravo !")
                    .setText("Les achats ont été restoré ! \uD83D\uDE0A ")
                    .setBackgroundColorRes(R.color.alerter_default_success_background)
                    .show()
        }

        unlockGame.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Rentrer votre code")
            val view = layoutInflater.inflate(R.layout.add_game_layout, null)
            val editText: EditText = view.findViewById(R.id.edittext)
            builder.setView(view)
            builder.setPositiveButton("OK") { _, _ ->
                m_Text = editText.text.toString()
                ActivationCodeService(this).findCode(object : MyCallback {
                    override fun onCallback(value: String, expirationDate: String) {
                        value.let {
                            RealmManager(applicationContext).addOwnedGame(value.toInt(), "FREE_UNLOCK", expirationDate)
                            val game = RealmDbHelper().get<RealmGame>(value.toLong())
                            if (game != null){
                                getGamesAdapter()
                                Alerter.create(this@MainActivity)
                                        .setTitle("Bravo !")
                                        .setText(game.title + " ajouté ! \uD83D\uDE0A ")
                                        .setBackgroundColorRes(R.color.alerter_default_success_background)
                                        .show()
                            }else{
                                Alerter.create(this@MainActivity)
                                        .setTitle("Dommage !")
                                        .setText("Le jeu n'est pas trouvé")
                                        .setBackgroundColorRes(R.color.alert_default_error_background)
                                        .show()
                            }

                        }
                    }
                }, m_Text)

            }

            builder.setNegativeButton("ANNULER") { dialog, _ -> dialog.cancel() }
            builder.show()
        }

        getInstance().instanceId
                .addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Log.w("FCM", "getInstanceId failed", task.exception)
                        return@OnCompleteListener
                    }

                    // Get new Instance ID token
                    val token = task.result?.token

                    Log.d("FCM", "ID : " + token)
                })
    }

    private fun getGamesAdapter(){
        val games = RealmDbHelper().getAllSorted<RealmOwnedGame>("position")
        rv_games_list.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.HORIZONTAL, false)
        rv_games_list.adapter = GamesAdapter(games)
        rv_games_list.adapter.notifyDataSetChanged()
    }

    private fun setLock(position: Int) {
        val game = this.shopsRealmGame[position]
        if (game != null){
            if (RealmManager(applicationContext).alreadyBought(game.id)) {
                setUnlock(game, position)
            } else {
                val img = resources.getIdentifier("img_${game.id}", "drawable", packageName)
                imgs[position].setImageBitmap(BitmapFactory.decodeResource(resources, img))
                gameTitleList[position].text = game.title
                gamePriceList[position].text = game.price.toString() + " €"
            }

        }


    }

    private fun setUnlock(game: RealmGame?, position: Int){
        val img = resources.getIdentifier("img_${game!!.id}", "drawable", packageName)
        imgs[position].setImageBitmap(BitmapFactory.decodeResource(resources, img))
        overlay[position].visibility = View.INVISIBLE
        gamePriceList[position].text = game.title


    }

    private fun getBuy(){
        if (billingClient.isReady){
            billingClient.queryPurchaseHistoryAsync(BillingClient.SkuType.INAPP) { responseCode, purchasesList ->
                if (responseCode == BillingClient.BillingResponse.OK && purchasesList != null) {
                    for (purchase in purchasesList) {
                        val gameId = purchase.sku.takeLast(1).toIntOrNull()
                        if (gameId != null) {
                            RealmManager(this).addOwnedGame(purchase.sku.takeLast(1).toInt(), "BOUGHT", null)
                            getGamesAdapter()
                        }


                    }
                }
            }
        }
    }

    override fun onClick(clickView: View?) {
        when (clickView) {
            imageDetailsOverLay -> {
                showBottomSheetDialog(shopsRealmGame[0]!!)
            }

            imageDetails2OverLay -> {
                showBottomSheetDialog(shopsRealmGame[1]!!)
            }

            imageDetails3OverLay -> {
                showBottomSheetDialog(shopsRealmGame[2]!!)
            }

            imageDetails4OverLay -> {
                showBottomSheetDialog(shopsRealmGame[3]!!)
            }

            imageDetails3 -> {
                val intentQuestion = Intent(this, GameDetailActivity::class.java)
                intentQuestion.putExtra("game_id", shopsRealmGame[2]!!.id)
                startActivity(intentQuestion)
            }
            imageDetails -> {
                val intentQuestion = Intent(this, GameDetailActivity::class.java)
                intentQuestion.putExtra("game_id", shopsRealmGame[0]!!.id)
                startActivity(intentQuestion)
            }
            imageDetails2 -> {
                val intentQuestion = Intent(this, GameDetailActivity::class.java)
                intentQuestion.putExtra("game_id", shopsRealmGame[1]!!.id)
                startActivity(intentQuestion)
            }
            imageDetails4 -> {
                val intentQuestion = Intent(this, GameDetailActivity::class.java)
                intentQuestion.putExtra("game_id", shopsRealmGame[3]!!.id)
                startActivity(intentQuestion)
            }
        }
    }

    private fun initView() {
        val connectivityManager = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
        if (isConnected == true){
            do {
                imageDetailsOverLay.setOnClickListener(this)
                imageDetails2OverLay.setOnClickListener(this)
                imageDetails3OverLay.setOnClickListener(this)
                imageDetails4OverLay.setOnClickListener(this)
                imageDetails3.setOnClickListener(this)
                imageDetails.setOnClickListener(this)
                imageDetails2.setOnClickListener(this)
                imageDetails4.setOnClickListener(this)
            }
            while (shopsRealmGame.isNotEmpty())



        }

    }

    private fun showBottomSheetDialog(game: RealmGame) {
        val view = layoutInflater.inflate(R.layout.bottom_sheet_layout, null)
        val titleModal: TextView = view.findViewById(R.id.titleModal)
        val descriptionModal: TextView = view.findViewById(R.id.descriptionModal)
        descriptionModal.movementMethod = ScrollingMovementMethod()

        val imageView: ImageView = view.findViewById(R.id.imageModal)
        val buyButton: Button = view.findViewById(R.id.buyButton)
        val gameType: TextView = view.findViewById(R.id.gameType)


        val img = this.resources.getIdentifier("img_${game.id}", "drawable", this.packageName)
        imageView.setImageBitmap(BitmapFactory.decodeResource(this.resources, img))
        descriptionModal.text = game.description
        titleModal.text = game.title
        when (game.type) {
            0 -> gameType.text = "Jeu de question"
            1 -> gameType.text = "Jeu de carte"
            2 -> gameType.text = "Jeu de dés"
            3 -> gameType.text = "Jeu fun"
            4 -> gameType.text = "Jeu de choix"
            else ->{gameType.text = "Jeu de question"}
        }


        val dialog = BottomSheetDialog(this)
        dialog.setContentView(view)
        val preview = RealmDbHelper().get<RealmGame>(game.id.toLong())

        buyButton.setOnClickListener {
            val responseCode = billingClient.launchBillingFlow(this, BillingManager(this).buyParams(game.id))
            if (responseCode == 0) {
                dialog.hide()
            }
        }
        val recycler: RecyclerView? = dialog.findViewById(R.id.rv_game_description)
        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(recycler)
        recycler!!.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recycler.adapter = GamesExampleAdapter(preview!!.preview)
        recycler.adapter.notifyDataSetChanged()
        dialog.show()
    }

    override fun onPurchasesUpdated(responseCode: Int, purchases: MutableList<Purchase>?) {
        println("onPurchasesUpdated: $responseCode")
    }
}