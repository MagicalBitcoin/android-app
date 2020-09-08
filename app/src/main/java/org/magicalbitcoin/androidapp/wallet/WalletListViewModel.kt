package org.magicalbitcoin.androidapp.wallet

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import org.bitcoindevkit.bdkjni.Types.Network
import org.bitcoindevkit.bdkjni.Types.WalletConstructor
import org.magicalbitcoin.androidapp.key.NamedExtendedKeys
import java.security.MessageDigest

class WalletListViewModel(private val app: Application) : AndroidViewModel(app) {

    val walletList: MutableLiveData<List<IdWallet>> by lazy {
        MutableLiveData<List<IdWallet>>().also {
            loadWalletList()
        }
    }

    val selectedWallet: MutableLiveData<IdWallet> = MutableLiveData()
    val selectedKey: MutableLiveData<NamedExtendedKeys> = MutableLiveData()

    fun reloadWalletList() {
        walletList.value = loadWalletList()
    }

    fun addWallet(idWallet: IdWallet) {
        val ids = mutableSetOf<String>()
        ids.addAll(loadWalletIds()!!.toList())
        ids.add(idWallet.id)
        app.getSharedPreferences(app.packageName, Context.MODE_PRIVATE).edit()
            .putStringSet("wallets", ids)
            .apply()

        app.getSharedPreferences(idWallet.id, Context.MODE_PRIVATE).edit()
            .putString("id", idWallet.id)
            .putString("name", idWallet.walletConstructor.name)
            .putString("descriptor", idWallet.walletConstructor.descriptor)
            .putString("network", idWallet.walletConstructor.network.toString())
            .putString("url", idWallet.walletConstructor.electrum_url)
            .apply()
    }

    private fun loadWalletIds(): MutableSet<String>? {
        return app.getSharedPreferences(app.packageName, Context.MODE_PRIVATE)
            .getStringSet("wallets", HashSet<String>())
    }

    private fun loadWalletList(): List<IdWallet>? {
        val names = loadWalletIds()
        return names?.map {
            val pref = app.getSharedPreferences(it, Context.MODE_PRIVATE)
            IdWallet(WalletConstructor(
                pref.getString("name", "").toString(),
                Network.testnet,
                pref.getString("path", app.filesDir.toString()).toString(),
                pref.getString("descriptor", "").toString(),
                pref.getString("change_descriptor", null),
                pref.getString("url", "").toString(),
                null
            ))
        }
    }
}

class IdWallet(walletConstructor: WalletConstructor) {
    val id: String
    val walletConstructor: WalletConstructor = walletConstructor

    init {
        val digest = MessageDigest.getInstance("SHA-256")
        val bytes = digest.digest(walletConstructor.descriptor.toByteArray(Charsets.UTF_8))
        val hash = bytes.fold("", { str, it -> str + "%02x".format(it) });
        id = hash.substring(hash.length - 5, hash.length)
    }
}