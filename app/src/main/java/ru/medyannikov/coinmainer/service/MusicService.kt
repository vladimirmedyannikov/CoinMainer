package ru.medyannikov.coinmainer.service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import ru.medyannikov.coinmainer.R


class MusicService : Service() {

  private var mediaPlayerMusic: MediaPlayer? = null

  override fun onBind(intent: Intent?): IBinder? = null

  override fun onCreate() {
    super.onCreate()
    mediaPlayerMusic = MediaPlayer.create(this, R.raw.main_music)
    mediaPlayerMusic?.isLooping = true
  }

  override fun onDestroy() {
    mediaPlayerMusic?.stop()
    super.onDestroy()
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    mediaPlayerMusic?.start()
    return super.onStartCommand(intent, flags, startId)
  }
}