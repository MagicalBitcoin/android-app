package org.magicalbitcoin.androidapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lelloman.identicon.view.GithubIdenticonView
import kotlinx.android.synthetic.main.activity_wallets.*
import org.bitcoindevkit.bdkjni.Types.Network
import org.bitcoindevkit.bdkjni.Types.WalletConstructor
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

class WalletsAdapter() : RecyclerView.Adapter<WalletHolder>(){

    var list: ArrayList<WalletConstructor> = ArrayList<WalletConstructor>()
    var listener: WalletGesture? = null

    override fun getItemCount():Int{
        return list.size
    }

    override fun onBindViewHolder(holder: WalletHolder, position: Int) {
        var wallet= list[position]
        holder.update(wallet)
        holder.itemView.setOnLongClickListener {
            listener?.onItemLongClick(wallet)
            return@setOnLongClickListener true
        }
        holder.itemView.setOnClickListener { listener?.onItemClick(wallet) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WalletHolder {
        var item = LayoutInflater.from(parent.context).inflate(R.layout.item_wallet, parent, false)
        return WalletHolder(item)
    }

    interface WalletGesture {
        fun onItemClick(wallet: WalletConstructor)
        fun onItemLongClick(wallet: WalletConstructor)
    }
}

class WalletHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    private val identicon = itemView.findViewById<GithubIdenticonView>(R.id.identicon)
    private val nameTextView = itemView.findViewById<TextView>(R.id.name)
    private val statusTextView = itemView.findViewById<TextView>(R.id.status)

    fun update(wallet: WalletConstructor) {
        nameTextView.text = wallet.name
        statusTextView.text = wallet.network.name
        identicon.hash = wallet.name.hashCode()
    }
}

class WalletsActivity : AppCompatActivity(), WalletsAdapter.WalletGesture {
    var walletsAdapter = WalletsAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallets)
        setSupportActionBar(toolbar)
        fab.setOnClickListener {
            val intent = Intent(this, WalletActivity::class.java)
            startActivity(intent)
        }
        collapsing_toolbar.title =  getString(R.string.app_name)

        walletsAdapter.apply {
            listener = this@WalletsActivity
            list = ArrayList()
        }

        findViewById<RecyclerView>(R.id.recyclerView).apply {
            layoutManager = LinearLayoutManager(this@WalletsActivity)
            adapter = walletsAdapter
        }
    }

    override fun onResume() {
        super.onResume()

        val wallets = getWallets()
        wallets?.let {
            walletsAdapter.apply {
                list.clear()
                list.addAll(wallets)
            }
        }
    }

    override fun onItemClick(wallet: WalletConstructor) {
        val intent = Intent(this, BalanceActivity::class.java)
        intent.putExtra("WalletConstructor", wallet)
        startActivity(intent)
    }

    override fun onItemLongClick(wallet: WalletConstructor) {
        val intent = Intent(this, WalletActivity::class.java)
        intent.putExtra("name", wallet.name)
        startActivity(intent)
    }

    fun getWallets(): List<WalletConstructor>? {
        val names = getSharedPreferences(packageName, Context.MODE_PRIVATE).getStringSet(
            "wallets",
            HashSet<String>()
        )
        return names?.map {
            val pref = getSharedPreferences(it, Context.MODE_PRIVATE)
            WalletConstructor(pref.getString("name", "").toString(),
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
