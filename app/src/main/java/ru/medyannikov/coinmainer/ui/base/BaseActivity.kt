package ru.medyannikov.coinmainer.ui.base

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import butterknife.ButterKnife
import butterknife.Unbinder

abstract class BaseActivity : AppCompatActivity() {

  private var unbinder: Unbinder? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    injectDependencies()
    super.onCreate(savedInstanceState)
    setContentView(getLayout())
    unbinder = ButterKnife.bind(this)
    setupToolbar()
  }

  private fun setupToolbar() {
    /*setSupportActionBar(toolbar)
    supportActionBar?.apply {
      title = ""
      setDisplayHomeAsUpEnabled(true)
      setHomeButtonEnabled(true)
    }*/
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      android.R.id.home -> {
        onBackPressed()
        return true
      }
    }
    return super.onOptionsItemSelected(item)
  }

  override fun onDestroy() {
    super.onDestroy()
    unbinder?.unbind()
    getPresenter()?.detachView()
  }

  protected abstract fun getLayout(): Int

  protected open fun isLogoEnabled() = true

  protected open fun getPresenter(): BasePresenter<*>? = null

  protected open fun injectDependencies() {}
}