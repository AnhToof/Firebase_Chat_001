package com.lobesoftware.toof.firebase_chat_001.repositories

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.lobesoftware.toof.firebase_chat_001.data.model.User
import com.lobesoftware.toof.firebase_chat_001.utils.Constant
import io.reactivex.*


interface UserRepository {

    fun loginWithEmailAndPassword(email: String, password: String): Single<User>

    fun register(user: User, password: String): Single<User>

    fun getCurrentUser(): Single<User>

    fun fetchUserInformation(): Observable<User>

    fun signOut()

    fun fetchFriend(currentId: String): Observable<User>

    fun fetchRequestFriend(currentId: String): Observable<User>

    fun acceptFriend(currentId: String, user: User): Completable

    fun rejectFriend(currentId: String, user: User): Completable
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

    override fun fetchFriend(currentId: String): Observable<User> {
        return Observable.create {
            handleFetchFriend(currentId, it)
        }
    }

    override fun fetchRequestFriend(currentId: String): Observable<User> {
        return Observable.create {
            handleFetchFriendRequest(currentId, it)
        }
    }

    override fun acceptFriend(currentId: String, user: User): Completable {
        return Completable.create { emitter ->
            user.id?.let { id ->
                val childUpdates = HashMap<String, Any?>()
                childUpdates["/${Constant.KeyDatabase.Friend.FRIEND_REQUEST}/$currentId/$id"] = null
                childUpdates["/${Constant.KeyDatabase.Friend.FRIEND_REQUEST}/$id/$currentId"] = null
                childUpdates["/${Constant.KeyDatabase.Friend.FRIENDSHIP}/$currentId/$id"] = true
                childUpdates["/${Constant.KeyDatabase.Friend.FRIENDSHIP}/$id/$currentId"] = true
                mDatabase.updateChildren(childUpdates)
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

    override fun rejectFriend(currentId: String, user: User): Completable {
        return Completable.create { emitter ->
            user.id?.let { id ->
                val childUpdates = HashMap<String, Any?>()
                childUpdates["/${Constant.KeyDatabase.Friend.FRIEND_REQUEST}/$currentId/$id"] = null
                childUpdates["/${Constant.KeyDatabase.Friend.FRIEND_REQUEST}/$id/$currentId"] = null
                mDatabase.updateChildren(childUpdates)
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

    private fun handleFetchFriend(id: String, emitter: ObservableEmitter<User>) {
        mDatabase.child(Constant.KeyDatabase.Friend.FRIENDSHIP).child(id)
            .addChildEventListener(object : ChildEventListener {
                override fun onCancelled(dataSnapshot: DatabaseError) {
                    emitter.onError(dataSnapshot.toException())
                }

                override fun onChildMoved(dataSnapshot: DataSnapshot, p1: String?) {
                }

                override fun onChildChanged(dataSnapshot: DataSnapshot, p1: String?) {
                }

                override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                    dataSnapshot.key?.let {
                        handleFetchUserById(it, emitter, Constant.ACTION_ADD)
                    }
                }

                override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                    dataSnapshot.key?.let {
                        handleFetchUserById(it, emitter, Constant.ACTION_REMOVE)
                    }
                }
            })
    }

    private fun handleFetchFriendRequest(id: String, emitter: ObservableEmitter<User>) {
        mDatabase.child(Constant.KeyDatabase.Friend.FRIEND_REQUEST).child(id)
            .addChildEventListener(object : ChildEventListener {
                override fun onCancelled(dataSnapshot: DatabaseError) {
                    emitter.onError(dataSnapshot.toException())
                }

                override fun onChildMoved(dataSnapshot: DataSnapshot, p1: String?) {
                }

                override fun onChildChanged(dataSnapshot: DataSnapshot, p1: String?) {
                }

                override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                    dataSnapshot.key?.let {
                        if (dataSnapshot.getValue(String::class.java) == Constant.KeyDatabase.Friend.TYPE_REQUEST_RECEIVED) {
                            handleFetchUserById(it, emitter, Constant.ACTION_ADD)
                        }
                    }
                }

                override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                    dataSnapshot.key?.let {
                        handleFetchUserById(it, emitter, Constant.ACTION_REMOVE)
                    }
                }
            })
    }

    private fun handleFetchUserById(id: String, emitter: ObservableEmitter<User>, action: String) {
        var tmpAction = action
        mDatabase.child(Constant.KeyDatabase.User.USER).child(id)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(dataSnapshot: DatabaseError) {
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    dataSnapshot.getValue(User::class.java)?.let {
                        tmpAction = when (tmpAction) {
                            Constant.ACTION_ADD -> {
                                it.action = Constant.ACTION_ADD
                                emitter.onNext(it)
                                ""
                            }
                            Constant.ACTION_REMOVE -> {
                                it.action = Constant.ACTION_REMOVE
                                emitter.onNext(it)
                                ""
                            }
                            else -> {
                                it.action = Constant.ACTION_CHANGE
                                emitter.onNext(it)
                                ""
                            }
                        }
                    }
                }
            })
    }
}
