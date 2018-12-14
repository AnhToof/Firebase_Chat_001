package com.lobesoftware.toof.firebase_chat_001.screen.main.chat_detail

import android.net.Uri
import com.lobesoftware.toof.firebase_chat_001.data.model.Group
import com.lobesoftware.toof.firebase_chat_001.data.model.Message
import com.lobesoftware.toof.firebase_chat_001.data.model.User
import com.lobesoftware.toof.firebase_chat_001.repositories.GroupRepository
import com.lobesoftware.toof.firebase_chat_001.repositories.MessageRepository
import com.lobesoftware.toof.firebase_chat_001.repositories.UserRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class ChatDetailPresenter(
    view: ChatDetailContract.View,
    userRepository: UserRepository,
    groupRepository: GroupRepository,
    messageRepository: MessageRepository
) : ChatDetailContract.Presenter {

    private var mView: ChatDetailContract.View? = view
    private val mUserRepository = userRepository
    private val mMessageRepository = messageRepository
    private val mGroupRepository = groupRepository
    private val mCompositeDisposable = CompositeDisposable()

    override fun fetchGroupInformation(groupId: String) {
        handleCheckCurrentUser { view, id ->
            val disposable = mGroupRepository.fetchConversationsInformation(id, groupId, null)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    view.onFetchGroupInformationSuccess(it)
                }, {
                    view.onFetchFail(it)
                })
            mCompositeDisposable.add(disposable)
        }
    }

    override fun getCurrentUserId() {
        handleCheckCurrentUser { view, id ->
            view.onGetCurrentUserIdSuccess(id)
        }
    }

    override fun fetchLastMessage(groupId: String, users: List<User>) {
        handleCheckCurrentUser { view, id ->
            val disposable = mMessageRepository.fetchLastMessage(id, groupId)
                .flatMapSingle {
                    mMessageRepository.fetchUserWithMessage(it, users)
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    view.onMessageAdded(it)
                }, {
                    view.onFetchFail(it)
                })
            mCompositeDisposable.add(disposable)
        }
    }

    override fun fetchPreviousMessages(groupId: String, lastMessageId: String, users: List<User>) {
        handleCheckCurrentUser { view, id ->
            val disposable = mMessageRepository.fetchPreviousMessage(id, groupId, lastMessageId)
                .toObservable()
                .flatMapIterable {
                    it
                }
                .flatMapSingle {
                    mMessageRepository.fetchUserWithMessage(it, users)
                }
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    view.onFetchPreviousMessagesSuccess(it)
                }, {
                    view.onFetchFail(it)
                })
            mCompositeDisposable.add(disposable)
        }
    }

    override fun fetchNextMessages(groupId: String, firstMessageId: String, users: List<User>) {
        handleCheckCurrentUser { view, id ->
            val disposable = mMessageRepository.fetchNextMessage(id, groupId, firstMessageId)
                .toObservable()
                .flatMapIterable {
                    it
                }
                .flatMapSingle {
                    mMessageRepository.fetchUserWithMessage(it, users)
                }
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    view.onFetchNextMessagesSuccess(it)
                }, {
                    view.onFetchFail(it)
                })
            mCompositeDisposable.add(disposable)
        }
    }

    override fun sendMessage(group: Group, message: Message) {
        handleCheckCurrentUser { view, id ->
            if (group.id == null) {
                view.onFetchFail(NullPointerException())
            } else {
                val disposable = mMessageRepository.sendMessage(id, group, message)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                    }, {
                        view.onFetchFail(it)
                    })
                mCompositeDisposable.add(disposable)
            }
        }
    }

    override fun leaveGroup(group: Group) {
        handleCheckCurrentUser { view, id ->
            view.showProgressDialog()
            val groupId = group.id
            if (groupId == null) {
                view.onFetchFail(NullPointerException())
                view.hideProgressDialog()
            } else {
                val disposable = mGroupRepository.leaveGroup(id, group)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doAfterTerminate {
                        view.hideProgressDialog()
                    }
                    .subscribe({
                        view.onLeaveGroupSuccess()
                    }, {
                        view.onFetchFail(it)
                    })
                mCompositeDisposable.add(disposable)
            }
        }
    }

    override fun uploadImage(uri: Uri) {
        handleCheckCurrentUser { view, _ ->
            view.showProgressDialog()
            val disposable = mMessageRepository.uploadImage(uri)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterTerminate {
                    view.hideProgressDialog()
                }
                .subscribe({
                    view.onUploadSuccess(it)
                }, {
                    view.onUploadFail(it)
                })
            mCompositeDisposable.add(disposable)
        }
    }

    override fun setView(view: ChatDetailContract.View) {
    }

    override fun onStart() {
    }

    override fun onStop() {
    }

    override fun onDestroy() {
        mCompositeDisposable.clear()
        mView = null
    }

    private fun handleCheckCurrentUser(function: (view: ChatDetailContract.View, id: String) -> Unit) {
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
