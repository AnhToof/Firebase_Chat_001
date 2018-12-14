package com.lobesoftware.toof.firebase_chat_001.screen.main.show_image

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.lobesoftware.toof.firebase_chat_001.R
import com.lobesoftware.toof.firebase_chat_001.extension.loadUrl
import com.lobesoftware.toof.firebase_chat_001.screen.main.MainActivity
import kotlinx.android.synthetic.main.fragment_show_image.view.*

class ShowImageFragment : Fragment() {

    private lateinit var mView: View
    private lateinit var mNavigator: ShowImageNavigator
    private var mImageUrl: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.fragment_show_image, container, false)
        (activity as? MainActivity)?.let {
            mNavigator = ShowImageNavigatorImpl(it)
        }
        setUpData()
        handleEvents()
        return mView
    }

    private fun setUpData() {
        arguments?.let { args ->
            mImageUrl = args.getString(ARGUMENT_IMAGE)
        }
        mImageUrl?.let {
            mView.image_view.loadUrl(it)
        }
    }

    private fun handleEvents() {
        mView.toolbar.setNavigationOnClickListener {
            mNavigator.backToPreviousScreen()
        }
    }

    companion object {
        private const val ARGUMENT_IMAGE = "image"
        fun getInstance(imageUrl: String): ShowImageFragment {
            val args = Bundle()
            args.putString(ARGUMENT_IMAGE, imageUrl)
            val fragment = ShowImageFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
