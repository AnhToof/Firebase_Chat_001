package com.lobesoftware.toof.firebase_chat_001.screen.main.add_member

import com.lobesoftware.toof.firebase_chat_001.data.model.User
import com.lobesoftware.toof.firebase_chat_001.repositories.UserRepository
import com.lobesoftware.toof.firebase_chat_001.utils.Constant
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class AddMemberPresenter(
    view: AddMemberContract.View,
    userRepository: UserRepository
) : AddMemberContract.Presenter {

    private var mView: AddMemberContract.View? = view
    private val mUserRepository = userRepository
    private val mCompositeDisposable = CompositeDisposable()

    override fun fetchMembers() {
        handleCheckCurrentUser { view, id ->
            val disposable = mUserRepository.fetchFriend(id)
                .flatMap {
                    mUserRepository.fetchUserById(it)
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    when (it.action) {
                        Constant.ACTION_ADD -> {
                            view.onMemberAdded(it)
                        }
                        Constant.ACTION_REMOVE -> {
                            view.onMemberRemoved(it)
                        }
                        Constant.ACTION_CHANGE -> {
                            view.onMemberChanged(it)
                        }
                    }
                }, {
                    view.onFetchFail()
                })
            mCompositeDisposable.add(disposable)
        }
    }

    override fun filterMembers(searchText: String, members: ArrayList<User>) {
        mView?.let { view ->
            val disposable = Observable.just(members)
                .flatMapIterable { it }
                .filter {
                    it.fullName.toString().toLowerCase().contains(searchText.toLowerCase())
                }
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    view.onFilterMemberSuccess(it)
                }, {
                    view.onFetchFail()
                })
            mCompositeDisposable.add(disposable)
        }
    }

    override fun setView(view: AddMemberContract.View) {
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

    private fun handleCheckCurrentUser(function: (view: AddMemberContract.View, id: String) -> Unit) {
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
