package com.example.minesweeper_codingninjas

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.content.Context
import android.content.SharedPreferences
import android.content.Intent
import android.os.Build
import android.os.SystemClock
import android.util.Log
import kotlin.random.Random
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import java.util.*


class gamePlay : AppCompatActivity() {
    private lateinit var chronometer: Chronometer
    var flaggedMines = 0;
    var fastestTime = "NA"
    var lastGameTime = "NA"
    var status = Status.ONGOING
    private var isPlay = false
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_play)
        chronometer = findViewById(R.id.timer)
        // When the game is started
        if (!isPlay){
            chronometer.base = SystemClock.elapsedRealtime()
            //chronometer.start()
            Toast.makeText(this,"Game Starts",Toast.LENGTH_SHORT).show()
            isPlay = true
        }

        // If the flag == 2 that means the user has chosen the custom board and we will neglect the level chosen on the main page
        val intent = intent
        var flag = intent.getIntExtra("flag", 2)

        // If flag == 1 - setup the board according to the level chosen by the user
        if (flag == 1){
            var level = intent.getStringExtra("selectedLevel")
            if (level.equals("easy")){
                setUpBoard (8,8,12)
            }else if (level.equals("medium")){
                setUpBoard (12,12,24)
            }else if (level.equals("hard")){
                setUpBoard (16,16,30)
            }
        }else {
            var row = intent.getIntExtra("height",0)
            var col = intent.getIntExtra("width",0)
            var mine = intent.getIntExtra("mines",0)
            setUpBoard (row,col,mine)
        }

        // Restarting the game
        val restart : ImageView = findViewById(R.id.restart)
        restart.setOnClickListener {
            gameRestart()
        }
    }

    // This function will setup the board according to the level or custom size
    @RequiresApi(Build.VERSION_CODES.M)
    private fun setUpBoard (row:Int, col:Int, mine:Int){
        // Setting up the total number of mines left
        val mines_left = findViewById<TextView>(R.id.mines_left)
        mines_left.text = mine.toString()

        // Array of buttons to find the position of a particular button
        val cellBoard = Array(row){Array(col){Minecell(this)} }
        var counter = 1
        var isFirstClick = true

        // Setting up layout parameters for future use
        val params1 = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            0
        )
        val params2 = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT
        )

        // Setting up Linear Layout
        for (i in 0 until row){
            val linearLayout  = LinearLayout (this)
            linearLayout.orientation = LinearLayout.HORIZONTAL
            linearLayout.layoutParams = params1
            params1.weight = 1.0F
            for (j in 0 until col) {
                val button = Minecell (this)
                cellBoard[i][j] = button
                button.id = counter
                button.textSize = 18.0F
                button.layoutParams = params2
                params2.weight = 1.0F
                button.setBackgroundResource(R.drawable.pink)
                button.setOnClickListener {
                    // Checking if the first click is already done
                    if (isFirstClick){
                        isFirstClick = false
                        // Setting up mines in the board
                        setMines (i,j,mine,cellBoard,row,col)
                        // Start Timer
                        startTimer ()
                    }
                    // when a cell is clicked - reveal its value
                    revealIt(i,j,cellBoard,row,col,mine)
                    display (cellBoard)
                }
                button.setOnLongClickListener {
                    if (isFirstClick){
                        Toast.makeText(this,"You cannot use flag as your first move, Try Again!!",Toast.LENGTH_LONG).show()
                    }else {
                        // When a cell is long clicked - flag it
                        flagIt(i, j, cellBoard, row, col, mine)
                        display(cellBoard)
                    }
                    return@setOnLongClickListener true
                }
                linearLayout.addView(button)
                counter ++
            }
            var board : LinearLayout = findViewById(R.id.board)
            board.addView(linearLayout)
        }
    }
    // Timer function
    private fun startTimer(){
        chronometer = findViewById(R.id.timer)
        chronometer.base = SystemClock.elapsedRealtime()
        chronometer.start()
    }
    // Function to set random mines when user first clicks on the board
    private fun setMines (row:Int,col:Int,mine:Int,cellBoard:Array<Array<Minecell>>,rowSize:Int,colSize:Int){
        // Generate random coordinates to setup mine
        var mineCount = mine
        var i = 1
        while (i<=mineCount){
            var r = (Random(System.nanoTime()).nextInt(0,rowSize))
            var c = (Random(System.nanoTime()).nextInt(0,colSize))
            if (r == row || cellBoard[r][c].isMine){
                continue
            }
            cellBoard[r][c].isMine = true
            cellBoard[r][c].value = MINE
            updateNeighbours (r,c,cellBoard,rowSize,colSize)
            i++
        }
    }

    // Update the neighbours after setting mine
    private fun updateNeighbours (row:Int, column:Int, cellBoard: Array<Array<Minecell>>, rowSize: Int, colSize: Int){
        for (i in movement){
            for (j in movement){
                if (((row+i) in 0 until rowSize) && ((column+j) in 0 until colSize) && cellBoard[row+i][column+j].value != -1 ){
                    cellBoard[row+i][column+j].value ++ ;
                }
            }
        }
    }

    // /* Move function */
    // when a button is clicked it reveals the value of that button
    private fun revealIt(x:Int,y:Int,cellBoard: Array<Array<Minecell>>,rowSize: Int,colSize: Int,mine: Int):Boolean{
        if (cellBoard[x][y].isMarked || cellBoard[x][y].isRevelead){
            return false
        }
        if (cellBoard[x][y].value == MINE){
            status = Status.LOST
            updateScore()
            return true
        }else if (cellBoard[x][y].value >0){
            cellBoard[x][y].isRevelead = true
            checkStatus(cellBoard,rowSize,colSize)
            return true
        }else if (cellBoard[x][y].value == 0){
            handleZero(x,y,cellBoard,rowSize,colSize)
            checkStatus(cellBoard,rowSize, colSize)
        }
        return false
    }
    // When a button is long pressed it is flagged
    private fun flagIt (x:Int,y:Int,cellBoard: Array<Array<Minecell>>,rowSize: Int,colSize: Int,mine: Int):Boolean{
        if (cellBoard[x][y].isRevelead){
            return false
        }else if (cellBoard[x][y].isMarked){
            flaggedMines--
            cellBoard[x][y].setBackgroundResource(R.drawable.pink)
            cellBoard[x][y].isMarked = false
            checkStatus(cellBoard,rowSize,colSize)
        }else {
            if (flaggedMines == mine){
                Toast.makeText(this,"You cannot mark more than $mine mines",Toast.LENGTH_LONG).show()
                return false
            }
            flaggedMines ++
            cellBoard[x][y].isMarked = true
            checkStatus(cellBoard, rowSize, colSize)
        }
        var finalMineCount = mine-flaggedMines
        var mines_left = findViewById<TextView>(R.id.mines_left)
        mines_left.text = finalMineCount.toString()
        return true
    }

    // Handles when board[x][y] == 0
    private val xDir = intArrayOf(-1,-1,0,1,1,1,0,-1)
    private val yDir = intArrayOf(0,1,1,1,0,-1,-1,-1)
    private fun handleZero (x:Int,y:Int,cellBoard: Array<Array<Minecell>>,rowSize: Int,colSize: Int){
        cellBoard[x][y].isRevelead = true
        for (i in 0..7){
            var xstep = x+xDir[i]
            var ystep = y+yDir[i]
            if ((xstep<0 || xstep>=rowSize) || (ystep<0 || ystep >= colSize)){
                continue
            }
            if (cellBoard[xstep][ystep].value >0 && !cellBoard[xstep][ystep].isMarked){
                cellBoard[xstep][ystep].isRevelead = true
            }else if (!cellBoard[xstep][ystep].isRevelead && !cellBoard[xstep][ystep].isMarked && cellBoard[xstep][ystep].value == 0){
                handleZero(xstep,ystep,cellBoard,rowSize, colSize)
            }
        }
    }

    // To update the status to Ongoing or Won
    private fun checkStatus (cellBoard: Array<Array<Minecell>>, rowSize: Int, colSize: Int){
        var flag1 = 0
        var flag2 = 0
        for (i in 0 until rowSize){
            for (j in 0 until colSize){
                if (cellBoard[i][j].value == MINE && !cellBoard[i][j].isMarked){
                    flag1 = 1
                }
                if (cellBoard[i][j].value != MINE && !cellBoard[i][j].isRevelead){
                    flag2 = 1
                }
            }
        }
        if (flag1 == 0 || flag2 == 0){
            status = Status.WON
        }else {
            status = Status.ONGOING
        }
        if (status == Status.WON){
            updateScore ()
        }
    }

    // To restart the game using the icon
    private fun gameRestart (){
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Alert!")
        builder.setMessage("Do you want to restart the game ? ")
        builder.setCancelable(false)
        builder.setNegativeButton("No"){
            dialog,which ->
        }
        builder.setPositiveButton("Yes"){
            dialog,which ->
            val intent = getIntent()
            finish()
            startActivity (intent)
        }
        val alertDialog = builder.create()
        alertDialog.show()
    }
    // When back button is pressed in between game
    override fun onBackPressed() {
        val builder :AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Game is still going on!!")
        builder.setMessage("Are you sure you wanna exit the game ?")
        builder.setCancelable(false)
        builder.setNegativeButton("No"){
            dialog,which ->
        }
        builder.setPositiveButton("Yes"){
            dialog,which ->
            updateScore ()
            toMainActivity()
            finish()
            super.onBackPressed()
        }
        val alertDialog = builder.create()
        alertDialog.show()
    }


    // It will display the function according to the game status
    @RequiresApi(Build.VERSION_CODES.M)
    private fun display (cellBoard: Array<Array<Minecell>>) {
        cellBoard.forEach {
            row -> row.forEach {
                if (it.isRevelead){
                    setNumberImage (it)
                }else if (it.isMarked){
                    it.setBackgroundResource(R.drawable.flag)
                }else if (!it.isMarked){
                    it.setBackgroundResource(R.drawable.pink)
                }else if (status == Status.LOST && it.value == MINE){
                    val restart = findViewById<ImageView>(R.id.restart)
                    restart.setImageResource(R.drawable.sad_face)
                    it.setBackgroundResource(R.drawable.bomb)
                }
            // To show that mine is not present here but it is marked
            if (status == Status.LOST && it.isMarked && !it.isMine){
                it.setBackgroundResource(R.drawable.crossed_flag)
            }else if (status == Status.WON && it.value == MINE){
                it.setBackgroundResource(R.drawable.flag)
                val restart = findViewById<ImageView>(R.id.restart)
                restart.setImageResource(R.drawable.happy_face)
            }else {
                it.text = " "
            }
            }
        }
    }

    // Saving Chronometer State - This function is used to update and store highscore and lastgame time and show the result page
    private fun updateScore(){
        chronometer.stop()

        // Getting elapsed time from chronometer
        val elapsedTime = SystemClock.elapsedRealtime() - chronometer.base
        val lastTime = elapsedTime.toInt()

        // Setting up highscore
        val sharedPreferences : SharedPreferences = this.getSharedPreferences("time",Context.MODE_PRIVATE)
        var highScore = sharedPreferences.getInt("Best",0)

        // Comparing high score if the last game's status is won
        if (status == Status.WON){
            if (highScore == 0){
                highScore = lastTime
            }else if (highScore != 0 && lastTime < highScore ){
                highScore = lastTime
            }
            with (sharedPreferences.edit()){
                putInt ("Best",highScore)
                putInt ("Last",lastTime)
                commit()
            }
            val intent = Intent(this,result::class.java).apply{
                putExtra("result","Congratulations \n YOU WON")
            }
            startActivity (intent)
        }else if (status == Status.LOST){
            with (sharedPreferences.edit()){
                putInt ("Best",highScore)
                putInt ("Last",lastTime)
                commit()
            }
            val intent = Intent(this,result::class.java).apply {
                putExtra("result","You Lost\nTry Again")
            }
            startActivity(intent)
        }
    }

    // This will carry data to store highscore and last game time on getting back to main activity
    private fun toMainActivity (){
        Log.d("MainActivity","inside to main"+fastestTime+" "+lastGameTime)
        val intent = Intent (this@gamePlay,MainActivity::class.java)
        intent.putExtra("highScore",fastestTime)
        intent.putExtra("lastTime",lastGameTime)
        startActivity (intent)
    }
    // Sets the image of the cell according to the value of the cell
    @RequiresApi(Build.VERSION_CODES.M)
    private fun setNumberImage(button:Minecell){
        if (button.value == 0) button.setBackgroundColor(getColor(R.color.white))
        if (button.value == 1) button.setBackgroundResource(R.drawable.one)
        if (button.value == 2) button.setBackgroundResource(R.drawable.two)
        if (button.value == 3) button.setBackgroundResource(R.drawable.three)
        if (button.value == 4) button.setBackgroundResource(R.drawable.four)
        if (button.value == 5) button.setBackgroundResource(R.drawable.five)
        if (button.value == 6) button.setBackgroundResource(R.drawable.six)
        if (button.value == 7) button.setBackgroundResource(R.drawable.seven)
        if (button.value == 8) button.setBackgroundResource(R.drawable.eight)
    }
    companion object{
        const val  MINE = -1
        val movement = intArrayOf(-1,0,1)
    }
}
enum class Status {
    WON,
    ONGOING,
    LOST
}