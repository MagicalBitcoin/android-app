package org.magicalbitcoin.androidapp.wallet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.fragment_wallet.*
import kotlinx.android.synthetic.main.fragment_wallet_settings.*
import kotlinx.android.synthetic.main.fragment_wallet_settings.input_name
import org.bitcoindevkit.bdkjni.Lib
import org.bitcoindevkit.bdkjni.Types.WalletConstructor
import org.jetbrains.anko.doAsync
import org.magicalbitcoin.androidapp.MainActivity
import org.magicalbitcoin.androidapp.R

class WalletFragment : Fragment() {

    private val walletListViewModel: WalletListViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        val view = inflater.inflate(R.layout.fragment_wallet, container, false)

        // update view when models change

        walletListViewModel.selectedWallet.observe(
            viewLifecycleOwner,
            Observer<IdWallet> { wallet ->
                // update UI
                doAsync { refresh() }
            })

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swipe_refresh.setOnRefreshListener { doAsync { refresh() } }
    }

    override fun onResume() {
        super.onResume()
        val mainActivity: MainActivity = activity as MainActivity
        mainActivity.showNav()

        // refresh
        doAsync { refresh() }

        // update action bar title
        val selectedWallet = walletListViewModel.selectedWallet.value
        mainActivity.supportActionBar?.title = getString(R.string.title_wallet)
        mainActivity.supportActionBar?.subtitle = getString(
            R.string.subtitle_wallet,
            selectedWallet?.walletConstructor?.name ?: "",
            selectedWallet?.id ?: ""
        )
    }

    fun refresh() {
        val selectedWallet = walletListViewModel.selectedWallet.value?.walletConstructor
        if (!swipe_refresh.isRefreshing && selectedWallet != null) {
            swipe_refresh.isRefreshing = true
            val walletPtr = Lib().constructor(selectedWallet)
            Lib().sync(walletPtr)
            val sats = Lib().get_balance(walletPtr)
            val transactions = Lib().list_transactions(walletPtr)
            Lib().destructor(walletPtr)
            activity?.runOnUiThread {
                wallet_balance.text = sats.toString()
                swipe_refresh.isRefreshing = false
            }
        }
    }
}
