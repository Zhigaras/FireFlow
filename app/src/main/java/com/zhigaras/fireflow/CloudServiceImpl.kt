package com.zhigaras.fireflow

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class CloudServiceImpl(databaseProvider: DatabaseProvider) : CloudService {

    private val reference = databaseProvider.provide()

    override suspend fun postWithIdGenerating(obj: Any?, vararg children: String): String {
        return suspendCoroutine { cont ->
            makeReference(*children).push().setValue(obj) { error, ref ->
                error?.let { cont.resumeWithException(IllegalStateException(error.message)) }
                    ?: cont.resume(ref.key!!)
            }
        }
    }

    override suspend fun <T : Any> getDataSnapshot(
        clazz: Class<T>,
        vararg children: String
    ): T = getDataSnapshotImpl({ getValue(clazz) }, *children)

    override suspend fun <T : Any> getDataSnapshot(
        clazz: GenericTypeIndicator<T>,
        vararg children: String
    ): T = getDataSnapshotImpl({ getValue(clazz) }, *children)

    private suspend fun <T : Any> getDataSnapshotImpl(
        getMethod: DataSnapshot.() -> T?,
        vararg children: String
    ) = suspendCoroutine { cont ->
        makeReference(*children).get().addOnSuccessListener { snapshot ->
            snapshot.getMethod()?.let { cont.resume(it) }
                ?: cont.resumeWithException(IllegalStateException("Problem with deserialization"))
        }.addOnFailureListener {
            cont.resumeWithException(IllegalStateException("Problem with data"))
        }.addOnCanceledListener {
            cont.resumeWithException(IllegalStateException("Canceled"))
        }
    }

    override fun post(obj: Any?, vararg children: String) {
        makeReference(*children).setValue(obj)
    }

    override fun <T : Any> subscribe(clazz: Class<T>, vararg children: String) =
        callbackFlow {
            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.getValue(clazz)?.let { trySend(it) }
                        ?: Log.w(TAG, "Data can not be deserialized to object of type ${clazz.name}. Value: ${snapshot.value}")
                }

                override fun onCancelled(error: DatabaseError) {
                    throw IllegalStateException(error.message)
                }
            }
            val ref = makeReference(*children)
            ref.addValueEventListener(listener)
            awaitClose { ref.removeEventListener(listener) }
        }


    override fun <T : Any> subscribeToList(clazz: Class<T>, vararg children: String) =
        callbackFlow<List<T>> {
            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<T>()
                    for (child in snapshot.children) {
                        child.getValue(clazz)?.let { list.add(it) }
                    }
                    trySend(list)
                }

                override fun onCancelled(error: DatabaseError) {
                    throw IllegalStateException(error.message)
                }
            }
            val ref = makeReference(*children)
            ref.addValueEventListener(listener)
            awaitClose { ref.removeEventListener(listener) }
        }

    override fun addItemToList(item: String, vararg children: String) {
        val ref = makeReference(*children)
        ref.updateChildren(mapOf(item to "waiting")) // TODO: replace "waiting" away from here
    }

    override fun removeListItem(itemId: String, vararg children: String) {
        val ref = makeReference(*children)
        ref.updateChildren(mapOf(itemId to null))
    }

    private fun makeReference(vararg children: String): DatabaseReference {
        var ref = reference
        children.forEach { ref = ref.child(it) }
        return ref
    }

    companion object {
        private const val TAG = "CloudServiceImpl"
    }
}