package ru.medyannikov.coinmainer.ui.main

import android.app.AlertDialog
import android.graphics.drawable.AnimationDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.support.v4.content.ContextCompat
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import butterknife.BindView
import butterknife.OnTouch
import org.jetbrains.anko.find
import ru.medyannikov.coinmainer.R
import ru.medyannikov.coinmainer.ui.base.BaseActivity
import java.util.*
import android.view.animation.LinearInterpolator
import android.widget.Chronometer
import android.widget.TextView
import org.jetbrains.anko.alert
import org.jetbrains.anko.onChronometerTick
import org.jetbrains.anko.toast


class MainActivity : BaseActivity() {

  @BindView(R.id.android_image)
  lateinit var imageView: ImageView

  @BindView(R.id.textDescription)
  lateinit var description: TextView

  @BindView(R.id.scoreNum)
  lateinit var score: TextView

  @BindView(R.id.timeNum)
  lateinit var chronometer: Chronometer

  val rootView: ViewGroup by lazy { find<FrameLayout>(R.id.root_main) }
  val easterRandom = Random()
  var countScore = 0
  val mediaPlayerMusic: MediaPlayer by lazy { MediaPlayer.create(this, R.raw.main_music) }


  override fun getLayout() = R.layout.a_main

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    initViews()
    initTimer()
  }

  override fun onStart() {
    super.onStart()
    initMusic()
  }

  private var mediaLength: Int = 0

  override fun onPause() {
    if (mediaPlayerMusic.isPlaying) {
      mediaPlayerMusic.pause()
      mediaLength = mediaPlayerMusic.currentPosition
    }
    chronometer.post { chronometer.stop() }
    super.onPause()
  }

  override fun onResume() {
    super.onResume()
    mediaPlayerMusic.seekTo(mediaLength)
    mediaPlayerMusic.start()
    chronometer.post { chronometer.start() }
  }

  override fun onDestroy() {
    super.onDestroy()
    mediaPlayerMusic.stop()
    mediaPlayerMusic.release()
  }

  override fun onBackPressed() {
    alert(R.string.exit_question, R.string.exit,
        {
          cancellable(false)
          positiveButton(R.string.yes) { finish() }
          negativeButton(R.string.no) { cancel() }
        }).show()
  }

  private fun initMusic() {
    mediaPlayerMusic.isLooping = true
    mediaPlayerMusic.start()
  }

  private fun initViews() {
    updateScore(0)
  }

  @OnTouch(R.id.android_image)
  fun onLogoTouched(v: View, e: MotionEvent): Boolean {
    if (e.actionMasked == MotionEvent.ACTION_DOWN) {
      v.rotationY = MathUtils.lerp(MathUtils.clamp(e.x / v.width, 0f, 1f), - 2f, 2f)
      v.rotationX = MathUtils.lerp(MathUtils.clamp(e.y / v.height, 0f, 1f), 22f, 20f)
      v.scaleX = 1f
      v.scaleY = 1f
      spawnCoin(v.left + e.x.toInt(), v.top + e.y.toInt())
      v.animate().scaleX(1f).scaleY(1f).rotationX(0f).rotationY(0f).duration = 200
    }
    return true
  }

  private fun spawnCoin(x: Int, y: Int) {
    val imageView = ImageView(this)
    val drawable = ContextCompat.getDrawable(this, R.drawable.coin_animation) as AnimationDrawable
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
            rootView.removeView(imageView)
          }
    }
    updateScore(1)
  }

  private fun updateScore(i: Int) {
    countScore += i
    score.text = String.format("%09d", countScore)
    checkScore()
  }

  private fun initTimer() {
    chronometer.base = SystemClock.elapsedRealtime()
    chronometer.post { chronometer.start() }
  }

  private fun checkScore() {
    when (countScore) {
      10 -> spawnText(R.string.ten_points)
      100 -> spawnText(R.string.hundred_points)
      300 -> spawnText(R.string.three_hundred_points)
      400 -> spawnText(R.string.four_hundred_points)
      600 -> spawnText(R.string.six_hundred_points)
      999 -> spawnText(R.string.ninenine_hundred_points)
      2000 -> spawnText(R.string.two_thousand_points)
      3000 -> spawnText(R.string.three_thousand_points)
      5000 -> spawnText(R.string.five_thousand_points)
      6000 -> spawnText(R.string.six_thousand_points)
      10000 -> spawnText(R.string.ten_thousand_points)
    }
  }

  private fun spawnText(text: Int) {
    description.setText(text)
    description.visibility = View.VISIBLE
    description
        .animate()
        .alpha(0f)
        .setDuration(3000)
        .setInterpolator(LinearInterpolator())
        .withEndAction {
          description.alpha = 1f
          description.visibility = View.GONE
        }
  }
}
