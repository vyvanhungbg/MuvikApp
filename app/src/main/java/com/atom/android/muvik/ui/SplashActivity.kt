package com.atom.android.muvik.ui

import android.content.Intent
import android.os.Build
import android.view.Window
import android.view.WindowManager
import com.atom.android.muvik.base.BaseActivity
import com.atom.android.muvik.databinding.ActivitySplashBinding
import com.atom.android.muvik.ui.main.MainActivity
import java.util.*
import kotlin.concurrent.schedule


class SplashActivity : BaseActivity<ActivitySplashBinding>(ActivitySplashBinding::inflate) {

    override fun initView() {

        val window: Window = window
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        Timer().schedule(DELAY_TIME) {
            this@SplashActivity.runOnUiThread {
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                finish()
            }
        }
    }

    override fun initData() {

    }

    override fun initEvent() {

    }

    companion object {
        const val DELAY_TIME = 1000L

    }

}
