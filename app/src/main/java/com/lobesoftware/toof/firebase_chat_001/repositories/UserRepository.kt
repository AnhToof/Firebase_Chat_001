package com.lobesoftware.toof.firebase_chat_001.repositories

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lobesoftware.toof.firebase_chat_001.data.model.User
import com.lobesoftware.toof.firebase_chat_001.utils.Constant
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.Single
import io.reactivex.SingleEmitter

interface UserRepository {

    fun loginWithEmailAndPassword(email: String, password: String): Single<User>

    fun register(user: User, password: String): Single<User>

    fun getCurrentUser(): Single<User>

    fun fetchUserInformation(): Observable<User>

    fun signOut()
}

class UserRepositoryImpl : UserRepository {

    private val mFirebaseAuth = FirebaseAuth.getInstance()
    private val mDatabase = FirebaseDatabase.getInstance().reference

    override fun loginWithEmailAndPassword(email: String, password: String): Single<User> {
        return Single.create { emitter ->
            mFirebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (!it.isSuccessful) {
                        return@addOnCompleteListener
                    }
                    it.result?.user?.let { user ->
                        handleAuthSuccess(user.uid, emitter)
                    }
                }
                .addOnFailureListener {
                    emitter.onError(it)
                }
        }
    }

    override fun register(user: User, password: String): Single<User> {
        return Single.create { emitter ->
            mFirebaseAuth.createUserWithEmailAndPassword(user.email ?: "", password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        saveUserToFirebaseDatabase(user)
                        emitter.onSuccess(user)
                    } else {
                        it.exception?.let { exception ->
                            emitter.onError(exception)
                        }
                    }
                }
                .addOnFailureListener {
                    emitter.onError(it)
                }
        }
    }

    override fun getCurrentUser(): Single<User> {
        return Single.create {
            val user = mFirebaseAuth.currentUser
            if (user == null) {
                it.onError(NullPointerException())
            } else {
                handleAuthSuccess(user.uid, it)
            }
        }
    }

    override fun fetchUserInformation(): Observable<User> {
        return Observable.create {
            val user = mFirebaseAuth.currentUser
            if (user == null) {
                it.onError(NullPointerException())
            } else {
                handleFetchInformationSuccess(user.uid, it)
            }
        }
    }

    override fun signOut() {
        mFirebaseAuth.currentUser?.let {
            mFirebaseAuth.signOut()
        }
    }

    private fun saveUserToFirebaseDatabase(user: User) {
        user.id = mFirebaseAuth.currentUser?.uid ?: ""
        user.id?.let {
            val ref = mDatabase.child(Constant.KeyDatabase.User.USER).child(it)
            ref.setValue(user)
        }
    }

    private fun handleAuthSuccess(id: String, emitter: SingleEmitter<User>) {
        mDatabase.child(Constant.KeyDatabase.User.USER).child(id)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(dataSnapshot: DatabaseError) {
                    emitter.onError(dataSnapshot.toException())
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    dataSnapshot.getValue(User::class.java)?.let {
                        emitter.onSuccess(it)
                    }
                }
            })
    }

    private fun handleFetchInformationSuccess(id: String, emitter: ObservableEmitter<User>) {
        mDatabase.child(Constant.KeyDatabase.User.USER).child(id)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(dataSnapshot: DatabaseError) {
                    emitter.onError(dataSnapshot.toException())
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    dataSnapshot.getValue(User::class.java)?.let {
                        emitter.onNext(it)
                    }
                }
            })
    }
}
