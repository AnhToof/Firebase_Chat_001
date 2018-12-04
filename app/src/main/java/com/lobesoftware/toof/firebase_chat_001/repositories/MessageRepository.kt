package com.lobesoftware.toof.firebase_chat_001.repositories

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.lobesoftware.toof.firebase_chat_001.data.model.Message
import com.lobesoftware.toof.firebase_chat_001.utils.Constant
import io.reactivex.Completable
import io.reactivex.Observable

interface MessageRepository {

    fun fetchMessages(currentId: String, groupId: String): Observable<Message>

    fun sendMessage(currentId: String, groupId: String, message: Message): Completable
}

class MessageRepositoryImpl : MessageRepository {

    private val mDatabase = FirebaseDatabase.getInstance().reference

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

    override fun sendMessage(currentId: String, groupId: String, message: Message): Completable {
        return Completable.create { emitter ->
            mDatabase.child(Constant.KeyDatabase.Message.MESSAGES).child(groupId).push().key?.let { messageId ->
                message.id = messageId
                message.from_user = currentId
                mDatabase.child(Constant.KeyDatabase.Message.MESSAGES).child(groupId).child(messageId).setValue(message)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            emitter.onComplete()
                            return@addOnCompleteListener
                        }
                        it.exception?.let { exception ->
                            emitter.onError(exception)
                            return@addOnCompleteListener
                        }
                        emitter.onError(NullPointerException())
                    }
            }
        }
    }
}
