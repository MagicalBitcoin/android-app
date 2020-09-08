package org.magicalbitcoin.androidapp.wallet

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class WalletSendViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is withdraw Fragment"
    }
    val text: LiveData<String> = _text
}