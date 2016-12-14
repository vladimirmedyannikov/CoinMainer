package ru.medyannikov.coinmainer.ui.main.views

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.widget.TextView

class GameTextView(context: Context, attributeSet: AttributeSet) : TextView(context, attributeSet) {

  init {
    val face = Typeface.createFromAsset(context.assets,
        "zekton rg.ttf")
    typeface = face
  }
}