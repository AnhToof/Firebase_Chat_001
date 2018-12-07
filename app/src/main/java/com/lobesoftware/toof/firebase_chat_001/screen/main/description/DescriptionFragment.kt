package com.lobesoftware.toof.firebase_chat_001.screen.main.description

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*

import com.lobesoftware.toof.firebase_chat_001.R
import com.lobesoftware.toof.firebase_chat_001.data.model.Group
import com.lobesoftware.toof.firebase_chat_001.screen.main.MainActivity
import kotlinx.android.synthetic.main.fragment_description.view.*

class DescriptionFragment : Fragment() {

    private lateinit var mView: View
    private lateinit var mNavigator: DescriptionNavigator
    private var mGroup: Group? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        mView = inflater.inflate(R.layout.fragment_description, container, false)
        initViews()
        return mView
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val item = menu.findItem(R.id.action_add)
        item.isVisible = false
        super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            mNavigator.backToChatDetailScreen()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initViews() {
        (activity as? MainActivity)?.let {
            arguments?.let { args ->
                mGroup = args.getParcelable(ARGUMENT_GROUP)
                mNavigator = DescriptionNavigatorImpl(it)
            }
            it.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        mGroup?.let { group ->
            mView.text_description_content.text = group.description
            (activity as? MainActivity)?.let {
                it.supportActionBar?.title = group.title
            }
        }
    }

    companion object {
        private const val ARGUMENT_GROUP = "group"

        fun getInstance(group: Group): DescriptionFragment {
            val args = Bundle()
            args.putParcelable(ARGUMENT_GROUP, group)
            val fragment = DescriptionFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
