package org.magicalbitcoin.androidapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_balance.*
import org.jetbrains.anko.doAsync
import org.bitcoindevkit.bdkjni.Lib
import org.bitcoindevkit.bdkjni.Types.WalletConstructor
import org.bitcoindevkit.bdkjni.Types.WalletPtr
import java.text.NumberFormat

class BalanceActivity : AppCompatActivity() {

    var wallet: WalletConstructor? = null
    var pointer: WalletPtr? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_balance)

        wallet = intent.getParcelableExtra<WalletConstructor>("WalletConstructor")
        toolbar.title = wallet?.name

        swipe_refresh.setOnRefreshListener {  doAsync { refresh() }}
    }

    override fun onResume() {
        super.onResume()
        swipe_refresh.isRefreshing = true
        doAsync { refresh() }
    }

    fun refresh() {
        pointer = Lib().constructor(wallet!!)
        Lib().sync(pointer!!)
        val satoshi = Lib().get_balance(pointer!!)
        runOnUiThread {
            balance.text = NumberFormat.getNumberInstance().format(satoshi)
            swipe_refresh.isRefreshing = false
        }
    }
}
