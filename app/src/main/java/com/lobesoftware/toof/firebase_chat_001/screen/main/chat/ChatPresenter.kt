package com.lobesoftware.toof.firebase_chat_001.screen.main.chat

import com.lobesoftware.toof.firebase_chat_001.data.model.Group
import com.lobesoftware.toof.firebase_chat_001.repositories.GroupRepository
import com.lobesoftware.toof.firebase_chat_001.repositories.UserRepository
import com.lobesoftware.toof.firebase_chat_001.utils.Constant
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class ChatPresenter(
    view: ChatContract.View,
    userRepository: UserRepository,
    groupRepository: GroupRepository
) : ChatContract.Presenter {

    private var mView: ChatContract.View? = view
    private val mUserRepository = userRepository
    private val mGroupRepository = groupRepository
    private val mCompositeDisposable = CompositeDisposable()

    override fun fetchConversations() {
        handleCheckCurrentUser { view, id ->
            val disposable = mGroupRepository.fetchConversations(id)
                .flatMap {
                    it.action?.let { action ->
                        mGroupRepository.fetchConversationsInformation(id, it, action)
                    }
                }
                .flatMap {
                    mGroupRepository.fetchUserGroupById(it)
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ group ->
                    group.members[id] = true
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
