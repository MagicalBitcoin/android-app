package org.magicalbitcoin.androidapp.wallet

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.magicalbitcoin.androidapp.MBApplication

private const val TAG = "DepositViewModel"

class DepositViewModel(application: Application) : AndroidViewModel(application) {

    private val _text = MutableLiveData<String>().apply {
        val app = application as MBApplication
        // TODO value = app.getDepositAddress()
        Log.d(TAG, "deposit address: $value")
    }
    val text: LiveData<String> = _text
}