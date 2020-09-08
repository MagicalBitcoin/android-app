package org.magicalbitcoin.androidapp

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import org.bitcoindevkit.bdkjni.Types.Network
import org.bitcoindevkit.bdkjni.Types.WalletConstructor

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        getSupportActionBar()?.setDisplayShowTitleEnabled(true)

        val navView: BottomNavigationView = findViewById(R.id.bottom_nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_wallet_list,
                R.id.navigation_wallet,
                R.id.navigation_receive,
                R.id.navigation_send
            )
        )
        //setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop")
    }

    fun hideNav() {
        bottom_nav_view.visibility = View.GONE
    }

    fun showNav() {
        bottom_nav_view.visibility = View.VISIBLE
    }

    fun setWalletParams(name: String, descriptor: String, network: String, url: String) {
        val names = mutableSetOf<String>()
        names.addAll(getWalletNames()!!.toList())
        names.add(name)
        getSharedPreferences(packageName, Context.MODE_PRIVATE).edit()
            .putStringSet("wallets", names)
            .apply()

        getSharedPreferences(name, Context.MODE_PRIVATE).edit()
            .putString("name", name)
            .putString("descriptor", descriptor)
            .putString("network", network)
            .putString("url", url)
            .apply()
    }

    fun getWalletNames(): MutableSet<String>? {
        return getSharedPreferences(packageName, Context.MODE_PRIVATE)
            .getStringSet("wallets", HashSet<String>())
    }

    fun getWallets(): List<WalletConstructor>? {
        val names = getWalletNames()
        return names?.map {
            val pref = getSharedPreferences(it, Context.MODE_PRIVATE)
            WalletConstructor(
                pref.getString("name", "").toString(),
                Network.testnet,
                pref.getString("path", filesDir.toString()).toString(),
                pref.getString("descriptor", "").toString(),
                pref.getString("change_descriptor", null),
                pref.getString("url", "").toString(),
                null
            )
        }
    }
}
