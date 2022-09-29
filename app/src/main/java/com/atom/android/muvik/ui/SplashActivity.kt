package com.atom.android.muvik.ui

import android.content.Intent
import android.os.Handler
import android.os.Looper
import com.atom.android.muvik.ui.main.MainActivity
import com.atom.android.muvik.R
import com.atom.android.muvik.base.BaseActivity
import com.atom.android.muvik.databinding.ActivitySplashBinding

class SplashActivity : BaseActivity<ActivitySplashBinding>(ActivitySplashBinding::inflate) {


    override fun initView() {
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, DELAY_TIME)
    }

    override fun initData() {

    }

    override fun initEvent() {

    }

    companion object {
        const val DELAY_TIME = 1200L
    }

}
