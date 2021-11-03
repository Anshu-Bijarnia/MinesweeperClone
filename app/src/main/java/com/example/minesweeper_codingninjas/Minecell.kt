package com.example.minesweeper_codingninjas

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageButton
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton


class Minecell: AppCompatButton {
    constructor(context: Context?) : super(context!!)
    constructor(context: Context?,attrs: AttributeSet?,defStyleAttr:Int):super (
        context!!,
        attrs,
        defStyleAttr
    )
    var value = 0;
    var isRevelead = false
    var isMarked = false
    var isMine = false
}