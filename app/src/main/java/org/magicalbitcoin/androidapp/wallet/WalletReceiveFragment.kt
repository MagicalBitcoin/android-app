package org.magicalbitcoin.androidapp.wallet

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import org.magicalbitcoin.androidapp.R

class WalletReceiveFragment : Fragment() {

    private lateinit var depositViewModel: DepositViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        depositViewModel =
            ViewModelProvider(this).get(DepositViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_wallet_receive, container, false)
        val textView: TextView = root.findViewById(R.id.text_deposit)
        depositViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })

        val address = depositViewModel.text.value

        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Deposit Address")
        sharingIntent.putExtra(Intent.EXTRA_TEXT, address)

        val shareButton: Button = root.findViewById(R.id.share_button)
        shareButton.setOnClickListener(View.OnClickListener {
            startActivity(
                Intent.createChooser(
                    sharingIntent,
                    "Share via"
                )
            )
        })

        return root
    }
}
