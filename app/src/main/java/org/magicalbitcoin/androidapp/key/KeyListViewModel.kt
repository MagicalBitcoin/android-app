package org.magicalbitcoin.androidapp.key

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import org.bitcoindevkit.bdkjni.Types.ExtendedKeys

class KeyListViewModel(private val app: Application) : AndroidViewModel(app) {

    val keyList: MutableLiveData<List<NamedExtendedKeys>> by lazy {
        MutableLiveData<List<NamedExtendedKeys>>().also {
            loadKeyList()
        }
    }

    val selectedKey: MutableLiveData<NamedExtendedKeys> = MutableLiveData()

    fun reloadKeyList() {
        keyList.value = loadKeyList()
    }

    fun addKey(keys: NamedExtendedKeys) {
        val ids = mutableSetOf<String>()
        ids.addAll(loadKeyIds()!!.toList())
        ids.add(keys.id)
        app.getSharedPreferences(app.packageName, Context.MODE_PRIVATE).edit()
            .putStringSet("keys", ids)
            .apply()

        app.getSharedPreferences(keys.id, Context.MODE_PRIVATE).edit()
            .putString("id", keys.id)
            .putString("name", keys.name)
            .putString("mnemonic", keys.extendedKeys.mnemonic)
            .putString("extPrivKey", keys.extendedKeys.ext_priv_key)
            .putString("extPubKey", keys.extendedKeys.ext_pub_key)
            .apply()
    }

    private fun loadKeyIds(): MutableSet<String>? {
        return app.getSharedPreferences(app.packageName, Context.MODE_PRIVATE)
            .getStringSet("keys", HashSet<String>())
    }

    private fun loadKeyList(): List<NamedExtendedKeys>? {
        val ids = loadKeyIds()
        return ids?.map {
            val pref = app.getSharedPreferences(it, Context.MODE_PRIVATE)
            val extendedKeys = ExtendedKeys(
                pref.getString("mnemonic", "").toString(),
                pref.getString("extPrivKey", "").toString(),
                pref.getString("extPubKey", "").toString()
            )
            NamedExtendedKeys(
                pref.getString("name", "").toString(),
                extendedKeys
            )
        }
    }
}

data class NamedExtendedKeys(
    val name: String,
    val extendedKeys: ExtendedKeys,
    val id: String = extendedKeys.ext_pub_key.substring(extendedKeys.ext_pub_key.length-5, extendedKeys.ext_pub_key.length)
) {
    override fun toString(): String = name
}