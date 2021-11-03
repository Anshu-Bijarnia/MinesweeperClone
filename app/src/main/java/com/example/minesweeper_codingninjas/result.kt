package com.example.minesweeper_codingninjas

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class result : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        // Values from gameplay
        var intent = getIntent ()
        var showName = intent.getStringExtra("player_name")
        var showResult = intent.getStringExtra("result")

        // Showing the result on screen
        val show_result = findViewById<TextView>(R.id.result_text)
        show_result.text = showResult

        // Try again button to return to home screen
        val restart = findViewById<Button>(R.id.restart_button)
        restart.setOnClickListener {
            Toast.makeText(this,"Thankyou for playing",Toast.LENGTH_SHORT).show()
            val intent = Intent (this,MainActivity::class.java).apply{}
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
        finishAffinity()
    }
}