package org.magicalbitcoin.androidapp.key

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_key.*
import org.bitcoindevkit.bdkjni.Lib
import org.bitcoindevkit.bdkjni.Types.ExtendedKeys
import org.bitcoindevkit.bdkjni.Types.Network
import org.magicalbitcoin.androidapp.MainActivity
import org.magicalbitcoin.androidapp.R

class KeyFragment : Fragment() {

    private val keyListViewModel: KeyListViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_key, container, false)

        // update view when models change

        keyListViewModel.selectedKey.observe(viewLifecycleOwner, Observer<NamedExtendedKeys> { key ->
            // update UI
            input_key_name.setText(key?.name)
            key_id.text = key?.id
            key_priv.text = key?.extendedKeys?.ext_priv_key
            key_pub.text = key?.extendedKeys?.ext_pub_key
        })

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // handle add button click
        add_update_key_button.setOnClickListener {

            // get input values
            val name = input_key_name.text.toString()
            val mnemonic = keyListViewModel.selectedKey.value?.extendedKeys?.mnemonic?:""
            val extPrivKey = keyListViewModel.selectedKey.value?.extendedKeys?.ext_priv_key?:""
            val extPubKey = keyListViewModel.selectedKey.value?.extendedKeys?.ext_pub_key?:""

            val extKeys = ExtendedKeys(mnemonic, extPrivKey, extPubKey)

            // update set of wallet names
            keyListViewModel.addKey(NamedExtendedKeys(name, extKeys))

            findNavController().popBackStack()
        }
    }

    override fun onResume() {
        super.onResume()
        val mainActivity: MainActivity = activity as MainActivity

        if (arguments?.getBoolean("isNew") == true) {
            val network = Network.valueOf(getString(R.string.app_network))
            val newKeys = Lib().generate_extended_key(network, 24)
            keyListViewModel.selectedKey.value = NamedExtendedKeys("", newKeys)
            add_update_key_button.text = getText(R.string.add)
            // update action bar title
            val selectedKey = keyListViewModel.selectedKey.value
            mainActivity.supportActionBar?.title = getString(R.string.title_add_key)
            mainActivity.supportActionBar?.subtitle = null
        } else {
            add_update_key_button.text = getText(R.string.update)
            // update action bar title
            val selectedKey = keyListViewModel.selectedKey.value
            mainActivity.supportActionBar?.title = getString(R.string.title_key)
            mainActivity.supportActionBar?.subtitle = getString(R.string.subtitle_key, selectedKey?.name?:"", selectedKey?.id?:"")
        }

        mainActivity.hideNav()
    }
}
