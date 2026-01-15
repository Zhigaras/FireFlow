package com.zhigaras.lib

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import com.zhigaras.lib.model.ParseResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

internal class FireFlowImpl(databaseProvider: DatabaseProvider) : FireFlow {

    private val reference = databaseProvider.provide()

    override suspend fun postWithIdGenerating(obj: Any?, vararg children: String): String {
        return suspendCoroutine { cont ->
            makeReference(*children).push().setValue(obj) { error, ref ->
                error?.let { cont.resumeWithException(IllegalStateException(error.message)) }
                    ?: ref.key?.let { key -> cont.resume(key) }
                    ?: cont.resumeWithException(IllegalStateException("Generated id is null"))
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

    override fun <T : Any> subscribe(clazz: Class<T>, vararg children: String): Flow<ParseResult<T>> =
        callbackFlow {
            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.getValue(clazz)?.let { trySend(ParseResult.Success(it)) }
                        ?: trySend(ParseResult.Error(snapshot.value.toString()))
                }

                override fun onCancelled(error: DatabaseError) {
                    close(IllegalStateException(error.message))
                }
            }
            val ref = makeReference(*children)
            ref.addValueEventListener(listener)
            awaitClose { ref.removeEventListener(listener) }
        }


//    override fun <T : Any> subscribeToList(clazz: Class<T>, vararg children: String) =
//        callbackFlow<List<T>> {
//            val listener = object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    val list = mutableListOf<T>()
//                    for (child in snapshot.children) {
//                        child.getValue(clazz)?.let { list.add(it) }
//                    }
//                    trySend(list)
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    throw IllegalStateException(error.message)
//                }
//            }
//            val ref = makeReference(*children)
//            ref.addValueEventListener(listener)
//            awaitClose { ref.removeEventListener(listener) }
//        }
//
//    override fun addItemToList(item: String, vararg children: String) {
//        val ref = makeReference(*children)
//        ref.updateChildren(mapOf(item to "waiting")) // TODO: replace "waiting" away from here
//    }
//
//    override fun removeListItem(itemId: String, vararg children: String) {
//        val ref = makeReference(*children)
//        ref.updateChildren(mapOf(itemId to null))
//    }

    private fun makeReference(vararg children: String): DatabaseReference {
        var ref = reference
        children.forEach { ref = ref.child(it) }
        return ref
    }
}
