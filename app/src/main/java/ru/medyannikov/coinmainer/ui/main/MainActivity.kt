package ru.medyannikov.coinmainer.ui.main

import android.graphics.drawable.AnimationDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import butterknife.OnTouch
import org.jetbrains.anko.find
import ru.medyannikov.coinmainer.R
import ru.medyannikov.coinmainer.ui.base.BaseActivity
import java.util.*


class MainActivity : BaseActivity() {

  val rootView: ViewGroup by lazy { find<FrameLayout>(R.id.root_main) }
  val easterRandom = Random()

  override fun getLayout() = R.layout.a_main

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    initViews()
  }

  private fun initViews() {

  }

  @OnTouch(R.id.android_image)
  fun onLogoTouched(v: View, e: MotionEvent): Boolean {

    if (e.actionMasked == MotionEvent.ACTION_DOWN) {
      v.rotationY = MathUtils.lerp(MathUtils.clamp(e.x / v.width, 0f, 1f), - 30f, 30f)
      v.rotationX = MathUtils.lerp(MathUtils.clamp(e.y / v.height, 0f, 1f), 30f, - 30f)
      v.scaleX = 0.95f
      v.scaleY = 0.95f
      spawnCoin(v.left + e.x.toInt(), v.top + e.y.toInt())
      v.animate().scaleX(1f).scaleY(1f).rotationX(0f).rotationY(0f).duration = 200
    }
    return true
  }

  private fun spawnCoin(x: Int, y: Int) {
    val imageView = ImageView(this)
    val drawable = ContextCompat.getDrawable(this, R.drawable.ee_coin_animation) as AnimationDrawable
    imageView.background = drawable
    imageView.visibility = View.INVISIBLE

    val scale = resources.displayMetrics.density
    imageView.layoutParams = FrameLayout.LayoutParams((32 * scale).toInt(), (32 * scale).toInt())
    rootView.addView(imageView)

    val player = MediaPlayer.create(this, R.raw.snd_ee_coin)
    player.setOnCompletionListener({ it.release() })
    player.start()

    Handler().post {
      drawable.start()
      imageView.visibility = View.VISIBLE
      imageView.x = (x - imageView.width / 2).toFloat()
      imageView.y = (y - imageView.height / 2).toFloat()
      imageView.animate()
          .setDuration(500)
          .yBy(- (easterRandom.nextInt(50) + 50) * scale)
          .xBy((easterRandom.nextInt(20) - 10) * scale)
          .setInterpolator(OvershootInterpolator())
          .withEndAction {
            //showScore(imageView.x.toInt(), imageView.y.toInt())
            rootView.removeView(imageView)
          }
    }
  }
}
