package ru.medyannikov.coinmainer.ui.base

import rx.Subscription
import rx.subscriptions.CompositeSubscription

abstract class BasePresenter<V : BaseView> {

  private var view: V? = null
  private val subscriptions by lazy { CompositeSubscription() }

  fun attachView(view: V) {
    this.view = view
  }

  fun addSubscription(subscription: Subscription) {
    subscriptions.add(subscription)
  }

  open fun detachView() {
    view = null
    subscriptions.clear()
  }

  open fun showError(ex: Throwable) {
    doIfViewReady {
      hideProgress()
      showError("Error")
    }
  }

  protected fun doIfViewReady(f: V.() -> Unit) {
    view?.apply {
      if (isReady()) {
        f.invoke(this)
      }
    }
  }
}