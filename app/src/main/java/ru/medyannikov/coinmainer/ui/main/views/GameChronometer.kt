package ru.medyannikov.coinmainer.ui.main.views

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.widget.Chronometer
import android.widget.TextView


class GameChronometer (context: Context, attributeSet: AttributeSet) : Chronometer(context, attributeSet) {

  init {
    val face = Typeface.createFromAsset(context.assets,
        "zekton rg.ttf")
    typeface = face
  }
}