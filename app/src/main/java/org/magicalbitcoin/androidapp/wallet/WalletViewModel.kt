package org.magicalbitcoin.androidapp.wallet

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.magicalbitcoin.androidapp.MBApplication

class WalletViewModel(application: Application) : AndroidViewModel(application) {

    private val _balance = MutableLiveData<String>().apply {
        val app = application as MBApplication
        // TODO value = app.getBalance()
    }
    val balance: LiveData<String> = _balance
}