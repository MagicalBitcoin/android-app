package org.magicalbitcoin.androidapp

import android.app.Application
import org.bitcoindevkit.bdkjni.Lib

class MBApplication : Application() {
    companion object {
        init {
            Lib.load()
        }
    }
}