package org.magicalbitcoin.androidapp

import android.app.Application
import org.magicalbitcoin.wallet.Lib

class MBApplication : Application() {
    companion object {
        init {
            Lib.load()
        }
    }
}