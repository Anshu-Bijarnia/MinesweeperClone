package com.example.minesweeper_codingninjas

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import org.w3c.dom.Text

class MainActivity : AppCompatActivity() {
    var level = "";
    private lateinit var sharedPreferences : SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Setting up the highscore and the last game score
        sharedPreferences = this.getSharedPreferences("time", Context.MODE_PRIVATE)
        var bestTime : TextView = findViewById(R.id.best_time)
        var best_time= sharedPreferences.getInt("Best",0)
        if (best_time == 0){
            bestTime.text = "NA"
        }else {
            bestTime.text = "High Score Time: " + ((best_time / 1000) / 60) + " m " + ((best_time / 1000) % 60) + " s"
        }
        var lastGameTime: TextView = findViewById(R.id.last_game_time)
        var last_time = sharedPreferences.getInt("Last",0)
        if (last_time == 0){
            lastGameTime.text = "NA"
        }else {
            lastGameTime.text = "Last Game Time: " + ((last_time / 1000) / 60) + " m " + ((last_time / 1000) % 60) + " s";
        }
        // If Make a custom board is clicked
        val customBoard_button  = findViewById<Button>(R.id.custom_board)
        customBoard_button.setOnClickListener {
            val intent = Intent(this@MainActivity,customBoard::class.java).apply {
            }
            startActivity(intent)
        }

        // If radio button easy is selected
        var easy : RadioButton = findViewById(R.id.easy_level)
        easy.setOnClickListener {
            level = "easy"
        }

        // If radio button medium is selected
        var medium : RadioButton = findViewById(R.id.medium_level)
        medium.setOnClickListener {
            level = "medium"
        }

        // If radio button hard is selected
        var hard : RadioButton = findViewById(R.id.hard_level)
        hard.setOnClickListener {
            level = "hard"
        }

        // If user clicks the start button
        var start : Button = findViewById(R.id.start_game)
        start.setOnClickListener {
            if (level == ""){
                Toast.makeText(this,"Please select a difficulty level or make a custom baord",Toast.LENGTH_LONG).show()
            }else {
                val intent = Intent (this,gamePlay::class.java).apply{
                    putExtra("selectedLevel",level)
                    putExtra("flag",1)
                }
                startActivity(intent)
            }
        }
        val rules : ImageView = findViewById(R.id.instruction)
        rules.setOnClickListener {
            showInstructions ()
        }
    }
    // Shows the instructions on how to play the game
    private fun showInstructions (){
        val builder : AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("INSTRUCTIONS")
        builder.setMessage("The purpose of the game is to open all the cells of the board which do not contain a bomb. You lose if you set off a bomb cell.\n" +
                "\n" +
                "Every non-bomb cell you open will tell you the total number of bombs in the eight neighboring cells. Once you are sure that a cell contains a bomb, you can long-click that cell to put a flag on it as a reminder. Once you have flagged all the bombs around an open cell, you can quickly open the remaining non-bomb cells by clicking on the cell.\n" +
                "\n" +
                "To start a new game (abandoning the current one), just click on the restart button.\n" +
                "\n" +
                "Happy mine hunting!")
        builder.setCancelable(false)
        builder.setPositiveButton("Got it!"){
            dialog, which ->
        }
        val alertDialog = builder.create()
        alertDialog.show()
    }

    override fun onBackPressed() {
        finishAffinity()
    }
}