package org.magicalbitcoin.androidapp.wallet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.lelloman.identicon.view.GithubIdenticonView
import kotlinx.android.synthetic.main.fragment_wallet_list.*
import org.bitcoindevkit.bdkjni.Types.WalletConstructor
import org.magicalbitcoin.androidapp.MainActivity
import org.magicalbitcoin.androidapp.R

class WalletListFragment : Fragment(), WalletListAdapter.WalletGesture {

    private var walletsAdapter = WalletListAdapter()
    private val walletListViewModel: WalletListViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        // setup view components
        val view = inflater.inflate(R.layout.fragment_wallet_list, container, false)
        walletsAdapter.apply {
            listener = this@WalletListFragment
            walletList = ArrayList()
        }

        view.findViewById<RecyclerView>(R.id.wallet_list).apply {
            layoutManager = LinearLayoutManager(view.context)
            adapter = walletsAdapter
        }

        val fab = view.findViewById<FloatingActionButton>(R.id.add_wallet_fab)
        fab.setOnClickListener {
            val isNewArg = bundleOf("isNew" to true)
            findNavController().navigate(R.id.action_wallet_list_to_setup, isNewArg)
        }

        // update view when models change

        walletListViewModel.walletList.observe(viewLifecycleOwner, Observer<List<IdWallet>>{ wallets ->
            // update UI
            walletsAdapter.walletList.clear()
            walletsAdapter.walletList.addAll(wallets)
        })

        return view
    }

    override fun onResume() {
        super.onResume()
        val mainActivity: MainActivity = activity as MainActivity
        mainActivity.showNav()
        wallet_list.requestFocus()
        walletListViewModel.reloadWalletList()
        mainActivity.supportActionBar?.title = getString(R.string.title_wallet_list)
        mainActivity.supportActionBar?.subtitle = null
    }

    override fun onItemClick(wallet: IdWallet) {
        walletListViewModel.selectedWallet.value = wallet
        val isNewArg = bundleOf("isNew" to false)
        findNavController().navigate(R.id.action_wallet_list_to_wallet, isNewArg)
    }

    override fun onItemLongClick(wallet: IdWallet) {
        TODO("Not yet implemented, should open wallet fragment")
    }
}


class WalletListAdapter() : RecyclerView.Adapter<WalletHolder>(){

    var walletList: ArrayList<IdWallet> = ArrayList()
    var listener: WalletGesture? = null

    override fun getItemCount():Int{
        return walletList.size
    }

    override fun onBindViewHolder(holder: WalletHolder, position: Int) {
        val wallet = walletList[position]
        holder.update(wallet)
        holder.itemView.setOnLongClickListener {
            listener?.onItemLongClick(wallet)
            return@setOnLongClickListener true
        }
        holder.itemView.setOnClickListener { listener?.onItemClick(wallet) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WalletHolder {
        val item = LayoutInflater.from(parent.context).inflate(R.layout.item_wallet, parent, false)
        return WalletHolder(item)
    }

    interface WalletGesture {
        fun onItemClick(wallet: IdWallet)
        fun onItemLongClick(wallet: IdWallet)
    }
}

class WalletHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    private val identicon = itemView.findViewById<GithubIdenticonView>(R.id.wallet_identicon)
    private val nameTextView = itemView.findViewById<TextView>(R.id.wallet_name)
    private val statusTextView = itemView.findViewById<TextView>(R.id.wallet_status)

    fun update(wallet: IdWallet) {
        nameTextView.text = wallet.walletConstructor.name
        statusTextView.text = wallet.id
        identicon.hash = wallet.id.hashCode()
    }
}
