package org.magicalbitcoin.androidapp.wallet

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_wallet_settings.*
import org.bitcoindevkit.bdkjni.Lib
import org.bitcoindevkit.bdkjni.Types.Network
import org.bitcoindevkit.bdkjni.Types.WalletConstructor
import org.magicalbitcoin.androidapp.MainActivity
import org.magicalbitcoin.androidapp.R
import org.magicalbitcoin.androidapp.key.KeyListViewModel
import org.magicalbitcoin.androidapp.key.NamedExtendedKeys


private const val TAG = "wallet.SettingsFragment"

class SettingsFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private val walletListViewModel: WalletListViewModel by activityViewModels()
    private val keyListViewModel: KeyListViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_wallet_settings, container, false)

        // update view when models change

        walletListViewModel.selectedWallet.observe(
            viewLifecycleOwner,
            Observer<IdWallet> { wallet ->
                // update UI
                input_name.setText(wallet?.walletConstructor?.name)
                text_descriptor.setText(wallet?.walletConstructor?.descriptor)
            })

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // handle add button click
        add_update_button.setOnClickListener {

            // get input values
            val name = input_name.text.toString()
            val descriptor = text_descriptor.text.toString()
            val network = Network.valueOf(getString(R.string.app_network))
            val electrumUrl = getString(R.string.app_electrum_url)

            // update set of wallet names
            walletListViewModel.addWallet(
                IdWallet(
                    WalletConstructor(
                        name,
                        network,
                        "",
                        descriptor,
                        "",
                        electrumUrl,
                        ""
                    )
                )
            )

            findNavController().popBackStack()
        }

        // setup spinner
        val keySpinnerAdapter = ArrayAdapter<NamedExtendedKeys>(
            requireContext(),
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            wallet_key_spinner.adapter = adapter
        }

        walletListViewModel.selectedWallet.observe(
            viewLifecycleOwner,
            Observer<IdWallet> { wallet ->
                // update UI

            })

        keyListViewModel.keyList.observe(
            viewLifecycleOwner,
            Observer<List<NamedExtendedKeys>> { keyList ->
                // update spinner
                keySpinnerAdapter.clear()
                keySpinnerAdapter.addAll(keyList)
            })

        wallet_key_spinner.onItemSelectedListener = this
    }

    override fun onResume() {
        super.onResume()
        val mainActivity: MainActivity = activity as MainActivity
        val selectedWallet = walletListViewModel.selectedWallet.value

        if (arguments?.getBoolean("isNew") == true) {
            add_update_button.text = getText(R.string.add)
            text_descriptor.isEnabled = true

            // clear input values
            walletListViewModel.selectedWallet.value = null

            // update action bar title
            mainActivity.supportActionBar?.title = getString(R.string.title_add_wallet)
            mainActivity.supportActionBar?.subtitle = null

        } else {
            add_update_button.text = getText(R.string.update)
            text_descriptor.isEnabled = false

            // update action bar title
            mainActivity.supportActionBar?.title = getString(R.string.title_wallet)
            mainActivity.supportActionBar?.subtitle = getString(R.string.subtitle_wallet, selectedWallet?.walletConstructor?.name?:"", selectedWallet?.id?:"")
        }

        mainActivity.hideNav()

        keyListViewModel.reloadKeyList()
        if (keyListViewModel.keyList.value?.isEmpty() != false) {

            val builder: AlertDialog.Builder? = activity?.let {
                AlertDialog.Builder(it)
            }
            builder?.setMessage(R.string.no_key_dialog_message)?.setTitle(R.string.no_key_dialog_title)
                ?.setPositiveButton(R.string.no_key_ok,
                    DialogInterface.OnClickListener { dialog, id ->
                        val isNewArg = bundleOf("isNew" to true)
                        findNavController().navigate(R.id.action_wallet_setup_to_key, isNewArg)
                    })
                ?.setNegativeButton(R.string.no_key_cancel,
                    DialogInterface.OnClickListener { dialog, id ->
                        findNavController().popBackStack()
                    })

            val dialog: AlertDialog? = builder?.create()
            dialog?.show()
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        val key = parent?.getItemAtPosition(position) as NamedExtendedKeys
        walletListViewModel.selectedKey.value = key
        text_descriptor.text = getString(R.string.single_key_descriptor, key.extendedKeys.ext_priv_key)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}


