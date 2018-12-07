package com.lobesoftware.toof.firebase_chat_001.repositories

import android.net.Uri
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.lobesoftware.toof.firebase_chat_001.data.model.Group
import com.lobesoftware.toof.firebase_chat_001.data.model.Message
import com.lobesoftware.toof.firebase_chat_001.utils.Constant
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

interface MessageRepository {

    fun fetchMessages(currentId: String, groupId: String): Observable<Message>

    fun sendMessage(currentId: String, group: Group, message: Message): Completable

    fun uploadImage(uri: Uri): Single<Uri>
}

class MessageRepositoryImpl : MessageRepository {

    private val mDatabase = FirebaseDatabase.getInstance().reference
    private val mFirebaseStorage = FirebaseStorage.getInstance().reference

    enum class GroupType(val value: Boolean) {
        PRIVATE(false),
        GROUP(true)
    }

    override fun fetchMessages(currentId: String, groupId: String): Observable<Message> {
        return Observable.create { emitter ->
            mDatabase.child(Constant.KeyDatabase.Message.MESSAGES).child(groupId)
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

    override fun sendMessage(currentId: String, group: Group, message: Message): Completable {
        return Completable.create { emitter ->
            group.id?.let {id ->
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
            uri.lastPathSegment?.let {lastPathSegment ->
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
