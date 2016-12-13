package ru.medyannikov.coinmainer.ui.base

import android.content.Context
import android.support.annotation.StringRes

interface BaseView {

  fun showProgress() {}
  fun hideProgress()  {}
  fun isReady(): Boolean
  fun showError(message: String) {}
  fun context(): Context
}

fun BaseView.stringRes(@StringRes resId: Int): String = context().getString(resId)