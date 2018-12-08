package com.lobesoftware.toof.firebase_chat_001.screen.main.description

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        mView = inflater.inflate(R.layout.fragment_description, container, false)
        initViews()
        handleEvents()
        return mView
    }

    private fun initViews() {
        (activity as? MainActivity)?.let {
            arguments?.let { args ->
                mGroup = args.getParcelable(ARGUMENT_GROUP)
                mNavigator = DescriptionNavigatorImpl(it)
            }
        }
        mGroup?.let { group ->
            mView.text_description_content.text = group.description
            (activity as? MainActivity)?.let {
                mView.toolbar.title = group.title
            }
        }
    }

    private fun handleEvents() {
        mView.toolbar.setNavigationOnClickListener {
            mNavigator.backToChatDetailScreen()
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
