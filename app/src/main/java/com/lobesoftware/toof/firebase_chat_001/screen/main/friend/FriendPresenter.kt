package com.lobesoftware.toof.firebase_chat_001.screen.main.friend

import com.lobesoftware.toof.firebase_chat_001.data.model.User
import com.lobesoftware.toof.firebase_chat_001.repositories.UserRepositoryImpl
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class FriendPresenter : FriendContract.Presenter {

    private var mView: FriendContract.View? = null
    private lateinit var mUserRepository: UserRepositoryImpl
    private val mCompositeDisposable = CompositeDisposable()

    override fun fetchFriendRequest() {
        handleCheckCurrentUser {
            mView?.let { view ->
                val disposable = mUserRepository.fetchRequestFriend(it)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ user ->
                        view.onFetchFriendRequestSuccess(user)
                    }, {
                        //No need
                    })
                mCompositeDisposable.add(disposable)
            }
        }
    }

    override fun fetchFriend() {
        handleCheckCurrentUser {
            mView?.let { view ->
                val disposable = mUserRepository.fetchFriend(it)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ user ->
                        view.onFetchFriendSuccess(user)
                    }, {
                        //No need
                    })
                mCompositeDisposable.add(disposable)
            }
        }
    }

    override fun acceptFriend(user: User) {
        handleCheckCurrentUser { id ->
            mView?.let {
                val disposable = mUserRepository.acceptFriend(id, user)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        //No need
                    }, {
                        //No need
                    })
                mCompositeDisposable.add(disposable)
            }
        }
    }

    override fun rejectFriend(user: User) {
        handleCheckCurrentUser { id ->
            mView?.let {
                val disposable = mUserRepository.rejectFriend(id, user)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        //No need
                    }, {
                        //No need
                    })
                mCompositeDisposable.add(disposable)
            }
        }
    }

    override fun setView(view: FriendContract.View) {
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

    private fun handleCheckCurrentUser(f: (id: String) -> Unit) {
        mView?.let { view ->
            val disposable = mUserRepository.getCurrentUser()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ currentUser ->
                    currentUser.id?.let { currentId ->
                        f(currentId)
                    }
                }, {
                    view.onCheckCurrentUserFail()
                })
            mCompositeDisposable.add(disposable)
        }
    }
}
