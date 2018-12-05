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

    fun fetchConversationsInformation(currentId: String, group: Group, action: String): Observable<Group>

    fun fetchUserGroupById(group: Group): Observable<Group>

    fun createGroup(currentId: String, group: Group): Completable

    fun fetchUsersInGroup(currentId: String, group: Group): Single<List<User>>
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

    override fun fetchConversationsInformation(currentId: String, group: Group, action: String): Observable<Group> {
        return Observable.create { emitter ->
            mDatabase.child(Constant.KeyDatabase.Group.GROUP)
                .addChildEventListener(object : ChildEventListener {
                    override fun onCancelled(dataSnapshot: DatabaseError) {
                        emitter.onError(dataSnapshot.toException())
                    }

                    override fun onChildMoved(dataSnapshot: DataSnapshot, p1: String?) {
                    }

                    override fun onChildChanged(dataSnapshot: DataSnapshot, p1: String?) {
                        if (dataSnapshot.key == group.id) {
                            dataSnapshot.getValue(Group::class.java)?.let { changedGroup ->
                                changedGroup.action = Constant.ACTION_CHANGE
                                emitter.onNext(changedGroup)
                            }
                        }
                    }

                    override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                        if (dataSnapshot.key == group.id) {
                            dataSnapshot.getValue(Group::class.java)?.let { addedGroup ->
                                when (action) {
                                    Constant.ACTION_ADD -> {
                                        addedGroup.action = action
                                        if (addedGroup.type == GroupType.PRIVATE.value) {
                                            addedGroup.members.remove(currentId)
                                        }
                                    }
                                    Constant.ACTION_REMOVE -> {
                                        addedGroup.action = action
                                        TODO("Add later")
                                    }
                                }
                                emitter.onNext(addedGroup)
                            }
                        }
                    }

                    override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.key == group.id) {
                            dataSnapshot.getValue(Group::class.java)?.let { removedGroup ->
                                removedGroup.action = Constant.ACTION_REMOVE
                                emitter.onNext(removedGroup)
                            }
                        }
                    }
                })
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

    override fun fetchUserGroupById(group: Group): Observable<Group> {
        return Observable.create { emitter ->
            if (group.type == GroupType.GROUP.value) {
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
                            if (dataSnapshot.key == group.members.keys.first()) {
                                dataSnapshot.getValue(User::class.java)?.let { changedUser ->
                                    group.title = changedUser.fullName
                                    group.action = Constant.ACTION_CHANGE
                                    emitter.onNext(group)
                                }
                            }
                        }

                        override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                            if (dataSnapshot.key == group.members.keys.first()) {
                                dataSnapshot.getValue(User::class.java)?.let { addedUser ->
                                    group.title = addedUser.fullName
                                    group.action = Constant.ACTION_ADD
                                    emitter.onNext(group)
                                }
                            }
                        }

                        override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                            TODO("Remove related user (Ex: friend, group,....)")
                        }
                    })
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
                                    users.add(user)
                                }
                            }
                        }
                        emitter.onSuccess(users)
                    }
                })
        }
    }
}
