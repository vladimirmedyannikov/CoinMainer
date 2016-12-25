package ru.medyannikov.coinmainer.ui.menu

import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.TextView
import butterknife.BindView
import org.jetbrains.anko.alert
import org.jetbrains.anko.onClick
import ru.medyannikov.coinmainer.R
import ru.medyannikov.coinmainer.ui.base.BaseActivity
import ru.medyannikov.coinmainer.ui.menu.MainMenu.GAME_MODE.CLASSIC
import ru.medyannikov.coinmainer.ui.menu.MainMenu.GAME_MODE.SHAKE
import ru.medyannikov.coinmainer.ui.utils.startGame

class MainMenu : BaseActivity() {

  @BindView(R.id.classic_mainer)
  lateinit var classicMode: TextView

  @BindView(R.id.shake_mainer)
  lateinit var shakeMode: TextView

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    classicMode.onClick {
      startGame(CLASSIC)
    }
    shakeMode.onClick {
      startGame(SHAKE)
    }
    val animRotate = AnimationUtils.loadAnimation(this, R.anim.rotate)
    val animShake = AnimationUtils.loadAnimation(this, R.anim.rotate)
    classicMode.startAnimation(animRotate)
    animShake.startOffset = 100
    shakeMode.startAnimation(animShake)
  }

  override fun onBackPressed() {
    alert(R.string.exit_question, R.string.exit,
        {
          cancellable(false)
          positiveButton(R.string.yes) { finish() }
          negativeButton(R.string.no) { cancel() }
        }).show()
  }

  override fun getLayout(): Int = R.layout.a_menu

  companion object {
    val GAME_MODE_EXTRAS = "GAME_MODE"
  }

  enum class GAME_MODE {
    CLASSIC, SHAKE
  }
}