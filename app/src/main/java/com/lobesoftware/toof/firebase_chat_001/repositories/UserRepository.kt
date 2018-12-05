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

    fun fetchMembers(currentId: String): Observable<User>

    fun fetchUserById(user: User): Observable<User>

    fun searchUserByEmail(currentId: String, email: String): Single<User?>

    fun requestFriend(currentId: String, friendId: String): Completable

    fun checkFriend(currentId: String, user: User): Single<User>

    fun checkRequestedFriend(currentId: String, user: User): Single<User>
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
        return Observable.create { emitter ->
            mDatabase.child(Constant.KeyDatabase.Friend.FRIENDSHIP).child(currentId)
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
                            val user = User(id = it, action = Constant.ACTION_ADD)
                            emitter.onNext(user)
                        }
                    }

                    override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                        dataSnapshot.key?.let {
                            val user = User(id = it, action = Constant.ACTION_REMOVE)
                            emitter.onNext(user)
                        }
                    }
                })
        }
    }

    override fun fetchRequestFriend(currentId: String): Observable<User> {
        return Observable.create { emitter ->
            mDatabase.child(Constant.KeyDatabase.Friend.FRIEND_REQUEST).child(currentId)
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
                                val user = User(id = it, action = Constant.ACTION_ADD)
                                emitter.onNext(user)
                            }
                        }
                    }

                    override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                        dataSnapshot.key?.let {
                            val user = User(id = it, action = Constant.ACTION_REMOVE)
                            emitter.onNext(user)
                        }
                    }
                })
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
                mDatabase.child(Constant.KeyDatabase.Group.GROUP).push().key?.let {
                    childUpdates["/${Constant.KeyDatabase.Group.GROUP}/$it/${Constant.KeyDatabase.Group.ID}"] = it
                    childUpdates["/${Constant.KeyDatabase.Group.GROUP}/$it/${Constant.KeyDatabase.Group.TYPE}"] = false
                    childUpdates["/${Constant.KeyDatabase.Group.GROUP}/$it/${Constant.KeyDatabase.Group.MEMBER}/$id"] =
                            true
                    childUpdates["/${Constant.KeyDatabase.Group.GROUP}/$it/${Constant.KeyDatabase.Group.MEMBER}/$currentId"] =
                            true
                    childUpdates["/${Constant.KeyDatabase.User.USER}/$currentId/${Constant.KeyDatabase.User.GROUP}/$it"] =
                            true
                    childUpdates["/${Constant.KeyDatabase.User.USER}/$id/${Constant.KeyDatabase.User.GROUP}/$it"] = true
                }
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

    override fun fetchMembers(currentId: String): Observable<User> {
        return Observable.create { emitter ->
            mDatabase.child(Constant.KeyDatabase.User.USER)
                .addChildEventListener(object : ChildEventListener {
                    override fun onCancelled(dataSnapshot: DatabaseError) {
                        emitter.onError(dataSnapshot.toException())
                    }

                    override fun onChildMoved(dataSnapshot: DataSnapshot, p1: String?) {
                    }

                    override fun onChildChanged(dataSnapshot: DataSnapshot, p1: String?) {
                    }

                    override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                        if (dataSnapshot.key != currentId) {
                            dataSnapshot.getValue(User::class.java)?.let { user ->
                                user.action = Constant.ACTION_ADD
                                emitter.onNext(user)
                            }
                        }
                    }

                    override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.key != currentId) {
                            dataSnapshot.getValue(User::class.java)?.let { user ->
                                user.action = Constant.ACTION_ADD
                                emitter.onNext(user)
                            }

                        }
                    }
                })
        }
    }

    override fun fetchUserById(user: User): Observable<User> {
        return Observable.create { emitter ->
            mDatabase.child(Constant.KeyDatabase.User.USER)
                .addChildEventListener(object : ChildEventListener {
                    override fun onCancelled(dataSnapshot: DatabaseError) {
                        emitter.onError(dataSnapshot.toException())
                    }

                    override fun onChildMoved(dataSnapshot: DataSnapshot, p1: String?) {
                    }

                    override fun onChildChanged(dataSnapshot: DataSnapshot, p1: String?) {
                        dataSnapshot.getValue(User::class.java)?.let { fetchUser ->
                            if (fetchUser.id == user.id) {
                                fetchUser.action = Constant.ACTION_CHANGE
                                emitter.onNext(fetchUser)
                            }
                        }
                    }

                    override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                        dataSnapshot.getValue(User::class.java)?.let { fetchUser ->
                            if (fetchUser.id == user.id) {
                                fetchUser.action = Constant.ACTION_ADD
                                emitter.onNext(fetchUser)
                            }
                        }
                    }

                    override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                    }
                })
        }
    }

    override fun searchUserByEmail(currentId: String, email: String): Single<User?> {
        return Single.create { emitter ->
            mDatabase.child(Constant.KeyDatabase.User.USER)
                .orderByChild(Constant.KeyDatabase.User.EMAIL)
                .equalTo(email)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(dataSnapshot: DatabaseError) {
                        emitter.onError(dataSnapshot.toException())
                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            dataSnapshot.children.first().getValue(User::class.java)?.let {
                                emitter.onSuccess(it)
                            }
                        } else {
                            emitter.onError(NullPointerException())
                        }
                    }
                })
        }
    }

    override fun requestFriend(currentId: String, friendId: String): Completable {
        return Completable.create { emitter ->
            val childUpdates = HashMap<String, Any?>()
            childUpdates["/${Constant.KeyDatabase.Friend.FRIEND_REQUEST}/$currentId/$friendId"] =
                    Constant.KeyDatabase.Friend.TYPE_REQUEST_SENT
            childUpdates["/${Constant.KeyDatabase.Friend.FRIEND_REQUEST}/$friendId/$currentId"] =
                    Constant.KeyDatabase.Friend.TYPE_REQUEST_RECEIVED
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

    override fun checkFriend(currentId: String, user: User): Single<User> {
        return Single.create { emitter ->
            val userId = user.id
            if (userId == null) {
                emitter.onError(NullPointerException())
            } else {
                mDatabase.child(Constant.KeyDatabase.Friend.FRIENDSHIP).child(currentId).child(userId)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(dataSnapshot: DatabaseError) {
                            emitter.onError(dataSnapshot.toException())
                        }

                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if (dataSnapshot.key == user.id && dataSnapshot.value == true) {
                                user.action = Constant.ACTION_FRIEND
                            }
                            emitter.onSuccess(user)
                        }
                    })
            }
        }
    }

    override fun checkRequestedFriend(currentId: String, user: User): Single<User> {
        return Single.create { emitter ->
            if (user.action == Constant.ACTION_FRIEND) {
                emitter.onSuccess(user)
            } else {
                val userId = user.id
                if (userId == null) {
                    emitter.onError(NullPointerException())
                } else {
                    mDatabase.child(Constant.KeyDatabase.Friend.FRIEND_REQUEST).child(currentId).child(userId)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onCancelled(dataSnapshot: DatabaseError) {
                                emitter.onError(dataSnapshot.toException())
                            }

                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                user.action = Constant.ACTION_ADD
                                dataSnapshot.getValue(String::class.java)?.let {
                                    if (it == Constant.KeyDatabase.Friend.TYPE_REQUEST_SENT) {
                                        user.action = Constant.ACTION_SENT
                                    } else {
                                        user.action = Constant.ACTION_RECEIVED
                                    }
                                }
                                emitter.onSuccess(user)
                            }
                        })
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
}
