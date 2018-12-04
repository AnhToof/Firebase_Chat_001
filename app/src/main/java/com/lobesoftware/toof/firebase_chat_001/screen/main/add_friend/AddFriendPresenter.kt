package com.lobesoftware.toof.firebase_chat_001.screen.main.add_friend

import com.lobesoftware.toof.firebase_chat_001.repositories.UserRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class AddFriendPresenter(
    view: AddFriendContact.View,
    userRepository: UserRepository
) : AddFriendContact.Presenter {

    private var mView: AddFriendContact.View? = view
    private val mUserRepository = userRepository
    private val mCompositeDisposable = CompositeDisposable()

    override fun searchUserByEmail(email: String) {
        handleCheckCurrentUser { view, id ->
            val disposable = mUserRepository.searchUserByEmail(id, email)
                .flatMap {
                    mUserRepository.checkFriend(id, it)
                }
                .flatMap {
                    mUserRepository.checkRequestedFriend(id, it)
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    view.onSearchUserSuccess(it, it.action)
                }, {
                    view.onSearchUserFail(it)
                })
            mCompositeDisposable.add(disposable)
        }

    }

    override fun requestFriend(friendId: String) {
        handleCheckCurrentUser { view, id ->
            val disposable = mUserRepository.requestFriend(id, friendId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    view.onRequestSuccess()
                }, {
                    view.onRequestFail()
                })
            mCompositeDisposable.add(disposable)
        }
    }

    override fun setView(view: AddFriendContact.View) {
    }

    override fun onStart() {
    }

    override fun onStop() {
        mCompositeDisposable.clear()
    }

    override fun onDestroy() {
        mView = null
    }

    private fun handleCheckCurrentUser(function: (view: AddFriendContact.View, id: String) -> Unit) {
        mView?.let { view ->
            val disposable = mUserRepository.getCurrentUser()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ currentUser ->
                    currentUser.id?.let { id ->
                        function(view, id)
                    }
                }, {
                    view.onCheckCurrentUserFail()
                })
            mCompositeDisposable.add(disposable)
        }
    }
}
