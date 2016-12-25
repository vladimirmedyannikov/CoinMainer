package ru.medyannikov.coinmainer.ui.main

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.support.v4.content.ContextCompat
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.BounceInterpolator
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.Chronometer
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import com.squareup.seismic.ShakeDetector
import com.squareup.seismic.ShakeDetector.SENSITIVITY_LIGHT
import org.jetbrains.anko.*
import ru.medyannikov.coinmainer.R
import ru.medyannikov.coinmainer.service.MusicService
import ru.medyannikov.coinmainer.ui.base.BaseActivity
import ru.medyannikov.coinmainer.ui.main.views.GameTextView
import ru.medyannikov.coinmainer.ui.menu.MainMenu.Companion.GAME_MODE_EXTRAS
import ru.medyannikov.coinmainer.ui.menu.MainMenu.GAME_MODE
import ru.medyannikov.coinmainer.ui.utils.hide
import ru.medyannikov.coinmainer.ui.utils.show
import java.util.*


class MainActivity : BaseActivity() {

  @BindView(R.id.android_image)
  lateinit var imageView: ImageView

  @BindView(R.id.android_image2)
  lateinit var imageViewBottom: ImageView

  @BindView(R.id.textDescription)
  lateinit var description: TextView

  @BindView(R.id.scoreNum)
  lateinit var score: TextView

  @BindView(R.id.shake_text)
  lateinit var shakeText: TextView

  @BindView(R.id.timeNum)
  lateinit var chronometer: Chronometer

  val rootView: ViewGroup by lazy { find<FrameLayout>(R.id.animation_main) }
  val easterRandom = Random()
  var countScore = 0

  override fun getLayout() = R.layout.a_main

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    initViews()
    initTimer()
  }

  private val shakeDetector: ShakeDetector by lazy {
    ShakeDetector(ShakeDetector.Listener {
      spawn(shakeText, easterRandom.nextInt(shakeText.width).toFloat(),
          easterRandom.nextInt(shakeText.height).toFloat())
    })
  }

  override fun onStart() {
    super.onStart()
    initMusic()
    if (gameMode == GAME_MODE.SHAKE) {
      shakeDetector.setSensitivity(SENSITIVITY_LIGHT)
      shakeDetector.start(sensorManager)
      imageViewBottom.hide()
      imageView.hide()
      shakeText.show()
    } else {
      imageViewBottom.show()
      imageView.show()
      imageView.onTouch { view, event ->
        if (event.actionMasked == MotionEvent.ACTION_DOWN) {
          spawn(view, event.x, event.y)
        }
        true
      }
      shakeText.hide()
    }
  }

  override fun onPause() {
    stopService(Intent(this, MusicService::class.java))
    shakeDetector.stop()
    chronometer.post { chronometer.stop() }
    super.onPause()
  }

  override fun onResume() {
    super.onResume()
    chronometer.post { chronometer.start() }
  }

  override fun onDestroy() {
    super.onDestroy()
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
    startService<MusicService>()
  }

  private var gameMode: GAME_MODE = GAME_MODE.CLASSIC

  private fun initViews() {
    updateScore(0)
    gameMode = intent.extras.getSerializable(GAME_MODE_EXTRAS) as GAME_MODE
  }

  fun spawn(v: View, x: Float, y: Float) {
    v.rotationY = MathUtils.lerp(MathUtils.clamp(x / v.width, 0f, 1f), - 8f, 8f)
    v.rotationX = MathUtils.lerp(MathUtils.clamp(y / v.height, 0f, 1f), 22f, 22f)
    v.scaleX = 1f
    v.scaleY = 1f
    spawnCoin(v.left + x.toInt(), v.top + y.toInt())
    v.animate().scaleX(1f).scaleY(1f).rotationX(0f).rotationY(0f).duration = 200
    rootView.scaleY = 1.05f
    rootView.scaleX = 1.05f
    rootView.animate().scaleX(1f).scaleY(1f).setDuration(150).setInterpolator(BounceInterpolator())
        .withEndAction {}
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
            if (! isFinishing) {
              showScore(imageView.x.toInt(), imageView.y.toInt())
              rootView.removeView(imageView)
            }
          }
    }
    updateScore(1)
  }

  fun showScore(x: Int, y: Int) {
    val textScore = GameTextView(this, null)
    textScore.text = "+1"
    val scale = resources.displayMetrics.density
    textScore.layoutParams = FrameLayout.LayoutParams((32 * scale).toInt(), (32 * scale).toInt())
    rootView.addView(textScore)
    textScore.visibility = View.INVISIBLE

    Handler().post {
      textScore.visibility = View.VISIBLE
      textScore.x = x.toFloat()
      textScore.y = y.toFloat()
      textScore.animate()
          .setDuration(500)
          .yBy(- 25 * scale)
          .setInterpolator(LinearInterpolator())
          .withEndAction { rootView.removeView(textScore) }
    }
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
          if (! isFinishing) {
            description.alpha = 1f
            description.visibility = View.GONE
          }
        }
  }
}
