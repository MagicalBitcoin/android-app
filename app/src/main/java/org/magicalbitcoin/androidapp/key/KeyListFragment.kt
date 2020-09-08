package org.magicalbitcoin.androidapp.key

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
import kotlinx.android.synthetic.main.fragment_key_list.*
import org.bitcoindevkit.bdkjni.Lib
import org.bitcoindevkit.bdkjni.Types.Network
import org.magicalbitcoin.androidapp.MainActivity
import org.magicalbitcoin.androidapp.R

class KeyListFragment : Fragment(), KeyListAdapter.KeyGesture {

    private var keyListAdapter = KeyListAdapter()
    private val keyListViewModel: KeyListViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        // setup view components
        val view = inflater.inflate(R.layout.fragment_key_list, container, false)
        keyListAdapter.apply {
            listener = this@KeyListFragment
            keyList = ArrayList()
        }

        view.findViewById<RecyclerView>(R.id.key_list).apply {
            layoutManager = LinearLayoutManager(view.context)
            adapter = keyListAdapter
        }

        val fab = view.findViewById<FloatingActionButton>(R.id.add_key_fab)
        fab.setOnClickListener {
            val isNewArg = bundleOf("isNew" to true)
            findNavController().navigate(R.id.action_key_list_to_key, isNewArg)
        }

        // update view when models change

        keyListViewModel.keyList.observe(
            viewLifecycleOwner,
            Observer<List<NamedExtendedKeys>> { keyList ->
                // update UI
                keyListAdapter.keyList.clear()
                keyListAdapter.keyList.addAll(keyList)
            })

        return view
    }

    override fun onResume() {
        super.onResume()
        val mainActivity: MainActivity = activity as MainActivity
        mainActivity.showNav()
        key_list.requestFocus()
        keyListViewModel.reloadKeyList()
        mainActivity.supportActionBar?.title = getString(R.string.title_key_list)
        mainActivity.supportActionBar?.subtitle = null
    }

    override fun onItemClick(key: NamedExtendedKeys) {
        keyListViewModel.selectedKey.value = key
        val isNewArg = bundleOf("isNew" to false)
        findNavController().navigate(R.id.action_key_list_to_key, isNewArg)
    }

    override fun onItemLongClick(key: NamedExtendedKeys) {
        TODO("Not yet implemented, should open wallet fragment")
    }
}


class KeyListAdapter() : RecyclerView.Adapter<KeyHolder>() {

    var keyList: ArrayList<NamedExtendedKeys> = ArrayList()
    var listener: KeyGesture? = null

    override fun getItemCount(): Int {
        return keyList.size
    }

    override fun onBindViewHolder(holder: KeyHolder, position: Int) {
        val key = keyList[position]
        holder.update(key)
        holder.itemView.setOnLongClickListener {
            listener?.onItemLongClick(key)
            return@setOnLongClickListener true
        }
        holder.itemView.setOnClickListener { listener?.onItemClick(key) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KeyHolder {
        val item = LayoutInflater.from(parent.context).inflate(R.layout.item_key, parent, false)
        return KeyHolder(item)
    }

    interface KeyGesture {
        fun onItemClick(key: NamedExtendedKeys)
        fun onItemLongClick(key: NamedExtendedKeys)
    }
}

class KeyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val identicon = itemView.findViewById<GithubIdenticonView>(R.id.key_identicon)
    private val nameTextView = itemView.findViewById<TextView>(R.id.item_key_name)
    private val idTextView = itemView.findViewById<TextView>(R.id.item_key_id)

    fun update(key: NamedExtendedKeys) {
        nameTextView.text = key.name
        idTextView.text = key.id
        identicon.hash = key.id.hashCode()
    }
}
