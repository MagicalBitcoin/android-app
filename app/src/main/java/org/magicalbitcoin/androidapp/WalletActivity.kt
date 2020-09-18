package org.magicalbitcoin.androidapp

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_wallet.*

class WalletActivity : AppCompatActivity() {

    var name: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallet)

        add_button.setOnClickListener(View.OnClickListener { confirm() })

        name = intent.getStringExtra("name")
        if (name == null) {
            title = getString(R.string.create_new_wallet)
            return
        }
        val pref = getSharedPreferences(name, Context.MODE_PRIVATE)
        input_name.setText(pref.getString("name", ""))
        input_descriptor.setText(pref.getString("descriptor", ""))
        input_network.setText(pref.getString("network", ""))
        input_url.setText(pref.getString("url", ""))
        add_button.visibility = View.GONE
    }

    private fun confirm() {
        if (input_name.text!!.isEmpty() || input_descriptor.text!!.isEmpty() ||
            input_network.text!!.isEmpty() || input_url.text!!.isEmpty()) {
            Toast.makeText(this, getString(R.string.fill_all_fields_before_continue), Toast.LENGTH_LONG).show()
            return
        }

        val names = getSharedPreferences(packageName, Context.MODE_PRIVATE).getStringSet(
            "wallets",
            HashSet<String>()
        )
        names?.let {
            names.add(name)
            getSharedPreferences(packageName, Context.MODE_PRIVATE)
                .edit().putStringSet("wallets", names).apply()
        }

        getSharedPreferences(name, Context.MODE_PRIVATE).edit()
            .putString("name", input_name.text.toString())
            .putString("descriptor", input_descriptor.text.toString())
            .putString("network", input_network.text.toString())
            .putString("path", filesDir.toString())
            .putString("url", input_url.text.toString())
            .apply()
        finish()
    }
}
