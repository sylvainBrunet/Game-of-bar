package com.gambapp.gob

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.text.Html
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.gambapp.gob.adapter.PlayerListAdapter
import com.gambapp.gob.helpers.RealmDbHelper
import com.gambapp.gob.helpers.RecyclerItemTouchHelper
import com.gambapp.gob.manager.EventManager
import com.gambapp.gob.model.Realm.RealmGame
import com.gambapp.gob.model.Realm.RealmPlayer
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_game_detail.*
import android.text.method.ScrollingMovementMethod
import com.gambapp.gob.services.AnalyticsService


class GameDetailActivity : AppCompatActivity(), RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {
    val realm: Realm = Realm.getDefaultInstance()
    lateinit var game:RealmGame

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int, position: Int) {
        if (viewHolder is PlayerListAdapter.MyViewHolder) {

            val adapter = playerList.adapter as PlayerListAdapter
            adapter.removeAt(viewHolder.adapterPosition)
            adapter.notifyDataSetChanged()
            setupPlayerList()
        }
    }

    private val playerName = RealmDbHelper().getAll<RealmPlayer>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_detail)
        val game_id = intent.getIntExtra("game_id", 1)
        game = RealmDbHelper().get<RealmGame>(game_id.toLong())!!
        title = game!!.title

        val first = "Faire glisser pour "
        val next = "<font color='#EE0000'>supprimer un joueurs</font>"
        textView4.text = Html.fromHtml(first + next)
        EventManager().OnGame(game_id.toString(), this)
        setupGameDetail(game)

        playerList.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        playerList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        val itemTouchHelperCallback = RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this)
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(playerList)
        editPlayer.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                setupPlayer()
                return@OnKeyListener true
            }
            false
        })
        buttonAddPlayer.setOnClickListener {
            setupPlayer()
        }

        buttonLetsDrink.setOnClickListener {
            if (playerName.count() == 0) {
                val builder = AlertDialog.Builder(this, R.style.AlertDialogStyle)
                builder.setMessage("Il faut au minimum un joueur pour lancer la partie")
                // Note the block
                builder.setPositiveButton("Ok") { _, _ ->

                }
                builder.setNegativeButton("Annuler") { _, _ ->
                }
                val dialog: AlertDialog = builder.create()
                dialog.show()
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLUE)

            } else if(playerName.count() == 1){
                val builder = AlertDialog.Builder(this, R.style.AlertDialogStyle)
                builder.setMessage("Tu veut vraiment jouer tout seul alcoolique ? :)")
                // Note the block
                builder.setPositiveButton("Ok") { _, _ ->
                    redirect()
                }
                builder.setNegativeButton("Annuler") { _, _ ->
                }
                val dialog: AlertDialog = builder.create()
                dialog.show()
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLUE)
            }
                else{
                redirect()
                }
            }
    }

    private fun setupPlayer() {
        val text = editPlayer.text
        val alreadyExist = RealmDbHelper().checkIfExist<RealmPlayer>("player_name",text.toString())
        if (alreadyExist == null){
            if (text != null && text.length >= 3) {
                val playerItem = RealmPlayer()
                playerItem.player_name = editPlayer.text.toString()
                RealmDbHelper().add<RealmPlayer>(playerItem)
                setupPlayerList()
                editPlayer.text = null
                val inputManager: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputManager.hideSoftInputFromWindow(currentFocus.windowToken, InputMethodManager.SHOW_FORCED)
            }
        }



    }

    private fun setupGameDetail(game: RealmGame) {

        textDescriptionDetails.text = game.description
        textDescriptionDetails.movementMethod = ScrollingMovementMethod()
        val img = this.resources.getIdentifier("img_${game.id}", "drawable", this.packageName)
        imageDetails.setImageBitmap(BitmapFactory.decodeResource(this.resources, img))
        setupPlayerList()
    }

    private fun setupPlayerList() {

        playerList.adapter = PlayerListAdapter(playerName)
    }
    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    private fun redirect(){
        val game_id = intent.getIntExtra("game_id", 1)
        val intentQuestion = Intent(this, GameActivity::class.java)
        intentQuestion.putExtra("game_id", game_id)

        val intent = Intent(this, CardGameActivity::class.java)
        intent.putExtra("game_id", game_id)

        val intentDice = Intent(this, DiceGameActivity::class.java)
        intentDice.putExtra("game_id", game_id)

        val intentBarJack = Intent(this, BarJackActivity::class.java)
        intentBarJack.putExtra("game_id", game_id)
        val intentTuPrefere = Intent(this, PrefereActivity::class.java)
        intentTuPrefere.putExtra("game_id", game_id)

        val analyticsManager = AnalyticsService.instance
        analyticsManager.setDate(game, playerName.count(), 0)
        analyticsManager.eventStart()

        when (game_id) {
            1 -> startActivity(intentQuestion)
            2 -> startActivity(intent)
            3 -> startActivity(intent)
            4 -> startActivity(intentDice)
            5 -> startActivity(intent)
            6 -> startActivity(intentQuestion)
            7 -> startActivity(intentQuestion)
            8 -> startActivity(intentBarJack)
            9 -> startActivity(intentTuPrefere)
            else -> { // Note the block
            }
        }
    }
}

