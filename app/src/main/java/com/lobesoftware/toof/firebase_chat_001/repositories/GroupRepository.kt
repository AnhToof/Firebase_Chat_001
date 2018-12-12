package com.lobesoftware.toof.firebase_chat_001.repositories

import com.google.firebase.database.*
import com.lobesoftware.toof.firebase_chat_001.data.model.Group
import com.lobesoftware.toof.firebase_chat_001.data.model.User
import com.lobesoftware.toof.firebase_chat_001.utils.Constant
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

interface GroupRepository {

    fun fetchConversations(currentId: String): Observable<Group>

    fun fetchConversationsInformation(currentId: String, groupId: String?, action: String?): Observable<Group>

    fun fetchUserGroupById(currentId: String, group: Group): Observable<Group>

    fun createGroup(currentId: String, group: Group): Completable

    fun updateGroup(currentId: String, group: Group): Completable

    fun fetchUsersInGroup(currentId: String, group: Group): Single<List<User>>

    fun leaveGroup(currentId: String, group: Group): Completable

    fun fetchGroupWithFriendInformation(currentId: String, friendId: String): Single<Group>
}

class GroupRepositoryImpl : GroupRepository {

    private val mDatabase = FirebaseDatabase.getInstance().reference

    enum class GroupType(val value: Boolean) {
        PRIVATE(false),
        GROUP(true)
    }

    override fun fetchConversations(currentId: String): Observable<Group> {
        return Observable.create { emitter ->
            mDatabase.child(Constant.KeyDatabase.User.USER).child(currentId).child(Constant.KeyDatabase.User.GROUP)
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
                            emitter.onNext(Group(id = it, action = Constant.ACTION_ADD))
                        }
                    }

                    override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                        dataSnapshot.key?.let {
                            emitter.onNext(Group(id = it, action = Constant.ACTION_REMOVE))
                        }
                    }
                })
        }
    }

    override fun fetchConversationsInformation(
        currentId: String,
        groupId: String?,
        action: String?
    ): Observable<Group> {
        return Observable.create { emitter ->
            if (action == Constant.ACTION_REMOVE) {
                emitter.onNext(Group(id = groupId, action = action))
            } else {
                mDatabase.child(Constant.KeyDatabase.Group.GROUP)
                    .addChildEventListener(object : ChildEventListener {
                        override fun onCancelled(dataSnapshot: DatabaseError) {
                            emitter.onError(dataSnapshot.toException())
                        }

                        override fun onChildMoved(dataSnapshot: DataSnapshot, p1: String?) {
                        }

                        override fun onChildChanged(dataSnapshot: DataSnapshot, p1: String?) {
                            if (dataSnapshot.key == groupId) {
                                dataSnapshot.getValue(Group::class.java)?.let { changedGroup ->
                                    changedGroup.action = Constant.ACTION_CHANGE
                                    emitter.onNext(changedGroup)
                                }
                            }
                        }

                        override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                            if (dataSnapshot.key == groupId) {
                                dataSnapshot.getValue(Group::class.java)?.let { addedGroup ->
                                    addedGroup.action = action
                                    emitter.onNext(addedGroup)
                                }
                            }
                        }

                        override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                            if (dataSnapshot.key == groupId) {
                                dataSnapshot.getValue(Group::class.java)?.let { removedGroup ->
                                    removedGroup.action = Constant.ACTION_REMOVE
                                    emitter.onNext(removedGroup)
                                }
                            }
                        }
                    })
            }
        }
    }

    override fun createGroup(currentId: String, group: Group): Completable {
        return Completable.create { emitter ->
            val childUpdates = HashMap<String, Any?>()
            mDatabase.child(Constant.KeyDatabase.Group.GROUP).push().key?.let { groupId ->
                group.id = groupId
                childUpdates["/${Constant.KeyDatabase.Group.GROUP}/$groupId"] = group
                val rootUser = "/${Constant.KeyDatabase.User.USER}"
                group.members[currentId] = true
                group.members.forEach {
                    childUpdates["/$rootUser/${it.key}/${Constant.KeyDatabase.User.GROUP}/$groupId"] = it.value
                }
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

    override fun updateGroup(currentId: String, group: Group): Completable {
        return Completable.create { emitter ->
            val childUpdates = HashMap<String, Any?>()
            childUpdates["/${Constant.KeyDatabase.Group.GROUP}/${group.id}"] = group
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

    override fun fetchUserGroupById(currentId: String, group: Group): Observable<Group> {
        return Observable.create { emitter ->
            if (group.type == GroupType.GROUP.value) {
                emitter.onNext(group)
            } else {
                if (group.action == Constant.ACTION_REMOVE) {
                    emitter.onNext(group)
                } else {
                    mDatabase.child(Constant.KeyDatabase.User.USER)
                        .addChildEventListener(object : ChildEventListener {
                            override fun onCancelled(dataSnapshot: DatabaseError) {
                                emitter.onError(dataSnapshot.toException())
                            }

                            override fun onChildMoved(dataSnapshot: DataSnapshot, p1: String?) {
                            }

                            override fun onChildChanged(dataSnapshot: DataSnapshot, p1: String?) {
                                val userId = group.members.keys.first {
                                    it != currentId
                                }
                                if (dataSnapshot.key == userId) {
                                    dataSnapshot.getValue(User::class.java)?.let { changedUser ->
                                        group.title = changedUser.fullName
                                        group.action = Constant.ACTION_CHANGE
                                        emitter.onNext(group)
                                    }
                                }
                            }

                            override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                                val userId = group.members.keys.first {
                                    it != currentId
                                }
                                if (dataSnapshot.key == userId) {
                                    dataSnapshot.getValue(User::class.java)?.let { addedUser ->
                                        group.title = addedUser.fullName
                                        group.action = Constant.ACTION_ADD
                                        emitter.onNext(group)
                                    }
                                }
                            }

                            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                                val userId = group.members.keys.first {
                                    it != currentId
                                }
                                if (dataSnapshot.key == userId) {
                                    dataSnapshot.getValue(User::class.java)?.let { removedUser ->
                                        group.title = removedUser.fullName
                                        group.action = Constant.ACTION_REMOVE
                                        emitter.onNext(group)
                                    }
                                }
                            }
                        })
                }
            }
        }
    }

    override fun fetchUsersInGroup(currentId: String, group: Group): Single<List<User>> {
        return Single.create { emitter ->
            mDatabase.child(Constant.KeyDatabase.User.USER)
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(dataSnapshot: DatabaseError) {
                        emitter.onError(dataSnapshot.toException())
                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val users = ArrayList<User>()
                        for (snap in dataSnapshot.children) {
                            snap.getValue(User::class.java)?.let { user ->
                                if (group.members.containsKey(user.id)) {
                                    user.action = Constant.ACTION_ADD
                                    users.add(user)
                                }
                            }
                        }
                        emitter.onSuccess(users)
                    }
                })
        }
    }

    override fun leaveGroup(currentId: String, group: Group): Completable {
        return Completable.create { emitter ->
            val childUpdates = HashMap<String, Any?>()
            childUpdates["/${Constant.KeyDatabase.User.USER}/$currentId/${Constant.KeyDatabase.User.GROUP}/${group.id}"] =
                    null
            if (group.members.keys.size == 1) {
                childUpdates["${Constant.KeyDatabase.Group.GROUP}/${group.id}"] = null
                childUpdates["/${Constant.KeyDatabase.Message.MESSAGES}/${group.id}"] = null
            } else {
                if (group.members.getValue(currentId)) {
                    childUpdates["${Constant.KeyDatabase.Group.GROUP}/${group.id}"] = null
                    childUpdates["/${Constant.KeyDatabase.Message.MESSAGES}/${group.id}"] = null
                    group.members.forEach {
                        if (it.key != currentId) {
                            childUpdates["/${Constant.KeyDatabase.User.USER}/${it.key}/${Constant.KeyDatabase.User.GROUP}/${group.id}"] =
                                    null
                        }
                    }
                } else {
                    childUpdates["${Constant.KeyDatabase.Group.GROUP}/${group.id}/${Constant.KeyDatabase.Group.MEMBER}/$currentId"] =
                            null
                }
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

    override fun fetchGroupWithFriendInformation(currentId: String, friendId: String): Single<Group> {
        return Single.create { emitter ->
            mDatabase.child(Constant.KeyDatabase.Group.GROUP)
                .addChildEventListener(object : ChildEventListener {
                    override fun onCancelled(dataSnapshot: DatabaseError) {
                        emitter.onError(dataSnapshot.toException())
                    }

                    override fun onChildMoved(dataSnapshot: DataSnapshot, p1: String?) {
                    }

                    override fun onChildChanged(dataSnapshot: DataSnapshot, p1: String?) {
                    }

                    override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                        dataSnapshot.getValue(Group::class.java)?.let { group ->
                            if (group.type == GroupType.PRIVATE.value) {
                                if (group.members.keys.containsAll(listOf(currentId, friendId))) {
                                    emitter.onSuccess(group)
                                }
                            }
                        }
                    }

                    override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                    }
                })
        }
    }
}
