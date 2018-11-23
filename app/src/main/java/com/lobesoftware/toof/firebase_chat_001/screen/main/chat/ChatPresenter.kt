package com.lobesoftware.toof.firebase_chat_001.screen.main.chat

import com.lobesoftware.toof.firebase_chat_001.data.model.Group
import com.lobesoftware.toof.firebase_chat_001.repositories.UserRepositoryImpl
import com.lobesoftware.toof.firebase_chat_001.utils.Constant
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class ChatPresenter : ChatContract.Presenter {

    private var mView: ChatContract.View? = null
    private lateinit var mUserRepository: UserRepositoryImpl
    private val mCompositeDisposable = CompositeDisposable()

    override fun fetchConversations() {
        handleCheckCurrentUser { view, id ->
            val disposable = mUserRepository.fetchConversations(id)
                .flatMap {
                    it.action?.let { action ->
                        mUserRepository.fetchConversationsInformation(id, it, action)
                    }
                }
                .flatMap {
                    mUserRepository.fetchUserGroupById(it)
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ group ->
                    when (group.action) {
                        Constant.ACTION_ADD -> {
                            view.onConversationAdded(group)
                        }
                        Constant.ACTION_REMOVE -> {
                            view.onConversationRemoved(group)
                        }
                        Constant.ACTION_CHANGE -> {
                            view.onConversationChanged(group)
                        }
                    }
                }, {
                    view.onFetchFail()
                })
            mCompositeDisposable.add(disposable)
        }
    }

    override fun filterConversation(searchText: String, conversations: ArrayList<Group>) {
        mView?.let { view ->
            val disposable = Observable.just(conversations)
                .flatMapIterable { it }
                .filter {
                    it.title.toString().toLowerCase().contains(searchText.toLowerCase())
                }
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    view.onFilterConversationSuccess(it)
                }, {
                    view.onFetchFail()
                })
            mCompositeDisposable.add(disposable)
        }
    }

    override fun setView(view: ChatContract.View) {
        mView = view
    }

    override fun onStart() {
    }

    override fun onStop() {
        mCompositeDisposable.clear()
    }

    override fun onDestroy() {
        mView = null
    }

    fun setUserRepository(userRepository: UserRepositoryImpl) {
        mUserRepository = userRepository
    }

    private fun handleCheckCurrentUser(function: (view: ChatContract.View, id: String) -> Unit) {
        mView?.let { view ->
            val disposable = mUserRepository.getCurrentUser()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ currentUser ->
                    currentUser.id?.let { currentId ->
                        function(view, currentId)
                    }
                }, {
                    view.onCheckCurrentUserFail()
                })
            mCompositeDisposable.add(disposable)
        }
    }
}
