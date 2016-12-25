package ru.medyannikov.coinmainer.ui.base

abstract class BasePresenter<V : BaseView> {

  private var view: V? = null

  fun attachView(view: V) {
    this.view = view
  }


  open fun detachView() {
    view = null
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