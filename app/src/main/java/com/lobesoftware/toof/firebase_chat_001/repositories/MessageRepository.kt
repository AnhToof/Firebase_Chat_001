package com.lobesoftware.toof.firebase_chat_001.repositories

import android.net.Uri
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.lobesoftware.toof.firebase_chat_001.data.model.Group
import com.lobesoftware.toof.firebase_chat_001.data.model.Message
import com.lobesoftware.toof.firebase_chat_001.data.model.User
import com.lobesoftware.toof.firebase_chat_001.utils.Constant
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

interface MessageRepository {

    fun fetchLastMessage(currentId: String, groupId: String): Observable<Message>

    fun fetchPreviousMessage(currentId: String, groupId: String, fromMessageId: String): Single<List<Message>>

    fun fetchNextMessage(currentId: String, groupId: String, fromMessageId: String): Single<List<Message>>

    fun sendMessage(currentId: String, group: Group, message: Message): Completable

    fun uploadImage(uri: Uri): Single<Uri>

    fun fetchUserWithMessage(message: Message, users: List<User>): Single<Message>
}

class MessageRepositoryImpl : MessageRepository {

    private val mDatabase = FirebaseDatabase.getInstance().reference
    private val mFirebaseStorage = FirebaseStorage.getInstance().reference

    enum class GroupType(val value: Boolean) {
        PRIVATE(false),
        GROUP(true)
    }

    override fun fetchLastMessage(currentId: String, groupId: String): Observable<Message> {
        return Observable.create { emitter ->
            mDatabase.child(Constant.KeyDatabase.Message.MESSAGES).child(groupId)
                .limitToLast(Constant.LIMIT_LAST_MESSAGE)
                .addChildEventListener(object : ChildEventListener {
                    override fun onCancelled(dataSnapshot: DatabaseError) {
                        emitter.onError(dataSnapshot.toException())
                    }

                    override fun onChildMoved(dataSnapshot: DataSnapshot, p1: String?) {
                    }

                    override fun onChildChanged(dataSnapshot: DataSnapshot, p1: String?) {
                    }

                    override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                        dataSnapshot.getValue(Message::class.java)?.let { message ->
                            message.action = Constant.ACTION_ADD
                            emitter.onNext(message)
                        }
                    }

                    override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                    }
                })
        }
    }

    override fun fetchUserWithMessage(message: Message, users: List<User>): Single<Message> {
        return Single.create { emitter ->
            message.from_user?.let {
                val userMatch = users.find { user ->
                    user.id == it
                }
                if (userMatch != null) {
                    message.user = userMatch
                    emitter.onSuccess(message)
                } else {
                    mDatabase.child(Constant.KeyDatabase.User.USER).child(it)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onCancelled(dataSnapshot: DatabaseError) {
                                emitter.onError(dataSnapshot.toException())
                            }

                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                dataSnapshot.getValue(User::class.java)?.let { user ->
                                    message.user = user
                                    emitter.onSuccess(message)
                                }
                            }
                        })
                }
            }
        }
    }

    override fun fetchPreviousMessage(
        currentId: String,
        groupId: String,
        fromMessageId: String
    ): Single<List<Message>> {
        return Single.create { emitter ->
            mDatabase.child(Constant.KeyDatabase.Message.MESSAGES).child(groupId).orderByKey().endAt(fromMessageId)
                .limitToLast(Constant.LIMIT_MESSAGES)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(dataSnapshot: DatabaseError) {
                        emitter.onError(dataSnapshot.toException())
                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val messages = mutableListOf<Message>()
                        dataSnapshot.children.forEach {
                            if (it.key != fromMessageId) {
                                it.getValue(Message::class.java)?.let { message ->
                                    messages.add(0, message)
                                }
                            }
                        }
                        emitter.onSuccess(messages)
                    }
                })
        }
    }

    override fun fetchNextMessage(currentId: String, groupId: String, fromMessageId: String): Single<List<Message>> {
        return Single.create { emitter ->
            mDatabase.child(Constant.KeyDatabase.Message.MESSAGES).child(groupId).orderByKey().startAt(fromMessageId)
                .limitToFirst(Constant.LIMIT_MESSAGES)
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(dataSnapshot: DatabaseError) {
                        emitter.onError(dataSnapshot.toException())
                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val messages = mutableListOf<Message>()
                        dataSnapshot.children.forEach {
                            if (it.key != fromMessageId) {
                                it.getValue(Message::class.java)?.let { message ->
                                    messages.add(message)
                                }
                            }
                        }
                        emitter.onSuccess(messages)
                    }
                })
        }
    }

    override fun sendMessage(currentId: String, group: Group, message: Message): Completable {
        return Completable.create { emitter ->
            group.id?.let { id ->
                mDatabase.child(Constant.KeyDatabase.Message.MESSAGES).child(id).push().key?.let { messageId ->
                    message.id = messageId
                    message.from_user = currentId
                    if (group.type == GroupType.PRIVATE.value) {
                        message.to_user = group.members.keys.first { it != currentId }
                    }
                    mDatabase.child(Constant.KeyDatabase.Message.MESSAGES).child(id).child(messageId)
                        .setValue(message)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                emitter.onComplete()
                                return@addOnCompleteListener
                            }
                            it.exception?.let { exception ->
                                emitter.onError(exception)
                                return@addOnCompleteListener
                            }
                        }
                }
            }
        }
    }

    override fun uploadImage(uri: Uri): Single<Uri> {
        return Single.create { emitter ->
            uri.lastPathSegment?.let { lastPathSegment ->
                mFirebaseStorage.child(Constant.KeyStorage.IMAGES).child(lastPathSegment).putFile(uri)
                    .addOnFailureListener {
                        emitter.onError(it)
                    }
                    .addOnSuccessListener {
                        mFirebaseStorage.child(Constant.KeyStorage.IMAGES).child(lastPathSegment).downloadUrl
                            .addOnSuccessListener { url ->
                                emitter.onSuccess(url)
                            }
                    }
            }
        }
    }
}
