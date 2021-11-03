package com.example.minesweeper_codingninjas

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.*
import org.w3c.dom.Text

class customBoard : AppCompatActivity() {
    var cellNumber = 0;
    var flag = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_board)

        // Reference of all the textview on the custom page
        val height = findViewById<TextView>(R.id.rows_custom)
        val width = findViewById<TextView>(R.id.col_custom)
        val mines = findViewById<TextView>(R.id.mine_custom)
        // Checking if the fields are not empty
        setErrorListener(height)
        setErrorListener((width))
        setErrorListener(mines)

        // When user clicks the submit button - send the selected values to gameplay activity
        val submit = findViewById<Button>(R.id.submit)
        submit.setOnClickListener {
            if (height.text.isEmpty() || width.text.isEmpty() || mines.text.isEmpty()){
                Toast.makeText(this,"Fill all the fields", LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // Calculating the number of cells so that adequate number of mines can be decided
            if (height.text.isNotEmpty() && width.text.isNotEmpty()){
                    cellNumber = height.text.toString().toInt()*width.text.toString().toInt()
                }else {
                    cellNumber = 0
                }
            // Checking if mines are less than 1/4 of cells
            setErrorListenerMines(mines)
            if (flag != 0){
                val intent = Intent (this,gamePlay::class.java).apply {
                    putExtra("height",height.text.toString().toInt())
                    putExtra("width",width.text.toString().toInt())
                    putExtra("mines",mines.text.toString().toInt())
                }
                startActivity (intent)
            }
        }
    }

    private fun setErrorListener(editText: TextView) {
        editText.error = if(editText.text.toString().isNotEmpty()) null else "Field Cannot be Empty"
        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                editText.error = if(editText.text.toString().isNotEmpty()) null else "Field Cannot be Empty"
            }
        })
    }
    private fun setErrorListenerMines(mines:TextView){
        mines.error = if (mines.text.toString().toInt() < (cellNumber/4)) {
            flag = 1
            null
        } else {
            flag = 0
            "To many mines"
        }
        mines.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                mines.error = if (mines.text.toString().toInt() < (cellNumber/4)) {
                    flag = 1
                    null
                } else {
                    flag = 0
                    "To many mines"
                }
            }
        })
    }
}