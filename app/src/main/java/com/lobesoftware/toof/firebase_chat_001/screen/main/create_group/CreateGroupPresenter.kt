package com.lobesoftware.toof.firebase_chat_001.screen.main.create_group

import com.lobesoftware.toof.firebase_chat_001.data.model.Group
import com.lobesoftware.toof.firebase_chat_001.repositories.UserRepository
import com.lobesoftware.toof.firebase_chat_001.utils.validator.Validator
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class CreateGroupPresenter : CreateGroupContact.Presenter {

    private var mView: CreateGroupContact.View? = null
    private lateinit var mUserRepository: UserRepository
    private lateinit var mValidator: Validator
    private val mCompositeDisposable = CompositeDisposable()

    override fun createGroup(group: Group) {
        mView?.let { view ->
            val errorMessageValidate = validate(group)
            errorMessageValidate?.let {
                view.onInputDataInValid(errorMessageValidate)
                return
            }
            handleValidateAndCheckCurrentUser { id ->
                view.showProgressDialog()
                val disposable = mUserRepository.createGroup(id, group)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doAfterTerminate {
                        view.hideProgressDialog()
                    }
                    .subscribe({
                        view.onCreateGroupSuccess()
                    }, {
                        view.onCheckCurrentUserFail()
                    })
                mCompositeDisposable.add(disposable)
            }
        }
    }

    override fun setView(view: CreateGroupContact.View) {
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

    fun setUserRepository(userRepository: UserRepository) {
        mUserRepository = userRepository
    }

    fun setValidator(validator: Validator) {
        mValidator = validator
    }

    private fun handleValidateAndCheckCurrentUser(function: (id: String) -> Unit) {
        mView?.let { view ->
            val disposable = mUserRepository.getCurrentUser()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ currentUser ->
                    currentUser.id?.let { currentId ->
                        function(currentId)
                    }
                }, {
                    view.onCheckCurrentUserFail()
                })
            mCompositeDisposable.add(disposable)
        }
    }

    private fun validate(group: Group): String? {
        val inputTitleError = mValidator.validateGroupTitle(group.title ?: "")
        inputTitleError?.let {
            return it
        }
        return null
    }
}
