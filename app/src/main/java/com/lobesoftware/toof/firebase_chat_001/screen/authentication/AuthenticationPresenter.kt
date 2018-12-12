package com.lobesoftware.toof.firebase_chat_001.screen.authentication

import com.lobesoftware.toof.firebase_chat_001.repositories.UserRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class AuthenticationPresenter(
    view: AuthenticationContract.View,
    userRepository: UserRepository
) : AuthenticationContract.Presenter {

    private var mView: AuthenticationContract.View? = view
    private val mUserRepository = userRepository
    private val mCompositeDisposable = CompositeDisposable()

    override fun checkLoggedState() {
        mView?.let { view ->
            view.showDialog()
            val disposable = mUserRepository.getCurrentUser()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterTerminate {
                    view.hideDialog()
                }
                .subscribe({
                    view.onLogged()
                }, {
                    view.onUnLog()
                })
            mCompositeDisposable.add(disposable)
        }
    }

    override fun setView(view: AuthenticationContract.View) {
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
}
