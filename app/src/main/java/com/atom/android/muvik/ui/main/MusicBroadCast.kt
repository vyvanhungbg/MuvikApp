package com.atom.android.muvik.ui.main

import android.content.*
import android.os.IBinder
import com.atom.android.muvik.utils.Constant

class MusicBroadCast : BroadcastReceiver() {

    var musicService: MusicService? = null

    override fun onReceive(context: Context?, intent: Intent?) {
        val actionFlag = intent?.getStringExtra(Constant.EXTRA_ACTION_FLAG)

        val intentToService = Intent(context, MusicService::class.java)
        intentToService.putExtra(Constant.EXTRA_ACTION_FLAG, actionFlag)
        val serviceConnection: ServiceConnection = object : ServiceConnection {
            override fun onServiceConnected(p0: ComponentName?, iBinder: IBinder?) {
                val mBinder: MusicService.MusicBinder = iBinder as MusicService.MusicBinder
                musicService = mBinder.getService()
                musicService?.handleActionMusic(actionFlag)
            }

            override fun onServiceDisconnected(p0: ComponentName?) {
            }

        }
        context?.applicationContext?.bindService(
            intentToService,
            serviceConnection,
            Context.BIND_AUTO_CREATE
        )
    }
}
