package com.lobesoftware.toof.firebase_chat_001.screen.main.profile

import com.lobesoftware.toof.firebase_chat_001.repositories.UserRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class ProfilePresenter(
    view: ProfileContract.View,
    userRepository: UserRepository
) : ProfileContract.Presenter {

    private var mView: ProfileContract.View? = view
    private val mUserRepository = userRepository
    private val mCompositeDisposable = CompositeDisposable()

    override fun setView(view: ProfileContract.View) {
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

    override fun fetchInformation() {
        mView?.let { view ->
            val disposable = mUserRepository.fetchUserInformation()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    view.onFetchInformationSuccess(it)
                }, {
                    view.onFetchFail(it)
                })
            mCompositeDisposable.add(disposable)
        }
    }

    override fun signOut() {
        mView?.let {
            mUserRepository.signOut()
            it.onSignOutSuccess()
        }
    }
}
