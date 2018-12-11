package com.lobesoftware.toof.firebase_chat_001.screen.main.user_detail

import com.lobesoftware.toof.firebase_chat_001.data.model.User
import com.lobesoftware.toof.firebase_chat_001.repositories.GroupRepository
import com.lobesoftware.toof.firebase_chat_001.repositories.UserRepository
import com.lobesoftware.toof.firebase_chat_001.utils.Constant
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class UserDetailPresenter(
    view: UserDetailContract.View,
    userRepository: UserRepository,
    groupRepository: GroupRepository
) : UserDetailContract.Presenter {

    private var mView: UserDetailContract.View? = view
    private val mUserRepository = userRepository
    private val mGroupRepository = groupRepository
    private val mCompositeDisposable = CompositeDisposable()

    override fun fetchFriendState(user: User) {
        handleCheckCurrentUser { view, id ->
            val disposable = mUserRepository.checkFriend(id, user)
                .flatMap {
                    mUserRepository.checkRequestedFriend(id, user)
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    when (it.action) {
                        Constant.ACTION_FRIEND -> {
                            view.onAlreadyFriend()
                        }
                        Constant.ACTION_SENT -> {
                            view.onRequestedFriend()
                        }
                        Constant.ACTION_RECEIVED -> {
                            view.onReceivedFriendRequest()
                        }
                        else -> {
                            view.onNotFriend()
                        }
                    }
                }, {
                    view.onFetchFail(it)
                })
            mCompositeDisposable.add(disposable)
        }
    }

    override fun addFriend(friendId: String) {
        handleCheckCurrentUser { view, id ->
            val disposable = mUserRepository.requestFriend(id, friendId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    view.onRequestedFriend()
                }, {
                    view.onFetchFail(it)
                })
            mCompositeDisposable.add(disposable)
        }
    }

    override fun acceptFriend(user: User) {
        handleCheckCurrentUser { view, id ->
            val disposable = mUserRepository.acceptFriend(id, user)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    view.onAlreadyFriend()
                }, {
                    view.onFetchFail(it)
                })
            mCompositeDisposable.add(disposable)
        }
    }

    override fun unFriend(friendId: String) {
        handleCheckCurrentUser { view, id ->
            val disposable = mGroupRepository.fetchGroupWithFriendInformation(id, friendId)
                .flatMap {
                    it.id?.let { groupId ->
                        mUserRepository.unFriend(id, friendId, groupId)
                    }
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    view.onNotFriend()
                }, {
                    view.onFetchFail(it)
                })
            mCompositeDisposable.add(disposable)
        }
    }

    override fun fetchGroupWithFriendInformation(user: User) {
        handleCheckCurrentUser { view, id ->
            user.id?.let { friendId ->
                val disposable = mGroupRepository.fetchGroupWithFriendInformation(id, friendId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        it.title = user.fullName
                        view.onFetchGroupWithFriendSuccess(it)
                    }, {
                        view.onFetchFail(it)
                    })
                mCompositeDisposable.add(disposable)
            }
        }
    }

    override fun setView(view: UserDetailContract.View) {
    }

    override fun onStart() {
    }

    override fun onStop() {
        mCompositeDisposable.clear()
    }

    override fun onDestroy() {
        mView = null
    }

    private fun handleCheckCurrentUser(function: (view: UserDetailContract.View, id: String) -> Unit) {
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
