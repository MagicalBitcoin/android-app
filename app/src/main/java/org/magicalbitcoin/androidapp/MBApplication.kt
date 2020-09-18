package org.magicalbitcoin.androidapp

import android.app.Application
import org.bitcoindevkit.library.Lib

class MBApplication : Application() {
    companion object {
        init {
            Lib.load()
        }
    }
}