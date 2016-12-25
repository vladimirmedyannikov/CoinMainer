package ru.medyannikov.coinmainer.ui.utils

import android.view.View
import org.jetbrains.anko.startActivity
import ru.medyannikov.coinmainer.ui.base.BaseActivity
import ru.medyannikov.coinmainer.ui.main.MainActivity
import ru.medyannikov.coinmainer.ui.menu.MainMenu
import ru.medyannikov.coinmainer.ui.menu.MainMenu.Companion.GAME_MODE_EXTRAS


fun BaseActivity.startGame(mode: MainMenu.GAME_MODE){
  startActivity<MainActivity>(GAME_MODE_EXTRAS to mode)
}

fun View.show() { visibility = View.VISIBLE }

fun View.hide() { visibility = View.GONE }

