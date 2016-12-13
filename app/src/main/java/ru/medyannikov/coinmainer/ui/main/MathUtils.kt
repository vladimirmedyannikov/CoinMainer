package ru.medyannikov.coinmainer.ui.main

import android.graphics.Color

object MathUtils {

  fun lerp(t: Float, from: Float, to: Float): Float {
    return from + (to - from) * t
  }

  fun lerp(t: Float, vararg numbers: Float): Float {
    val d = t % 1
    val tFloor = Math.floor(t.toDouble()).toInt()

    if (d == 0f) {
      return numbers[tFloor]
    } else {
      return lerp(d, numbers[tFloor], numbers[tFloor + 1])
    }
  }

  fun lerpColor(t: Float, from: Int, to: Int): Int {
    return Color.argb(
        lerp(t, Color.alpha(from).toFloat(), Color.alpha(to).toFloat()).toInt(),
        lerp(t, Color.red(from).toFloat(), Color.red(to).toFloat()).toInt(),
        lerp(t, Color.green(from).toFloat(), Color.green(to).toFloat()).toInt(),
        lerp(t, Color.blue(from).toFloat(), Color.blue(to).toFloat()).toInt()
    )
  }

  fun clamp(value: Float, min: Float, max: Float): Float {
    if (value < min) {
      return min
    } else if (value > max) {
      return max
    } else {
      return value
    }
  }
}