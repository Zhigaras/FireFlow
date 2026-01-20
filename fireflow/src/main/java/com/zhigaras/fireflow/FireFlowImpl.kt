package com.zhigaras.fireflow

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import com.zhigaras.fireflow.mapper.FireFlowExceptionMapper
import com.zhigaras.fireflow.model.FireFlowException
import com.zhigaras.fireflow.model.Data
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.tasks.await
import java.lang.RuntimeException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

internal class FireFlowImpl(databaseProvider: DatabaseProvider) : FireFlow {

    private val reference = databaseProvider.provide()
    private val exceptionMapper = FireFlowExceptionMapper()

    override suspend fun postWithIdGenerating(obj: Any?, vararg children: String): String {
        return suspendCoroutine { cont ->
            makeReference(*children).push().setValue(obj) { error, ref ->
                error?.let { cont.resumeWithException(error.toException()) }
                    ?: ref.key?.let { key -> cont.resume(key) }
                    ?: cont.resumeWithException(RuntimeException("Generated id is null"))
            }
        }
    }

    override suspend fun <T : Any> getDataSnapshot(
        clazz: Class<T>,
        vararg children: String
    ): T? = getDataSnapshotImpl({ getValue(clazz) }, *children)

    override suspend fun <T : Any> getDataSnapshot(
        clazz: GenericTypeIndicator<T>,
        vararg children: String
    ): T? = getDataSnapshotImpl({ getValue(clazz) }, *children)

    private suspend fun <T : Any> getDataSnapshotImpl(
        getMethod: DataSnapshot.() -> T?,
        vararg children: String
    ) = suspendCoroutine { cont ->
        makeReference(*children).get().addOnSuccessListener { snapshot ->
            try {
                snapshot.getMethod()?.let { cont.resume(it) }
                    ?: cont.resume(null)
            } catch (e: Exception) {
                cont.resumeWithException(e)
            }
        }.addOnFailureListener {
            cont.resumeWithException(it)
        }.addOnCanceledListener {
            cont.resumeWithException(RuntimeException("Canceled"))
        }
    }

    override suspend fun post(obj: Any?, vararg children: String) {
        makeReference(*children).setValue(obj).await()
    }

    override fun <T : Any> subscribe(
        clazz: Class<T>,
        vararg children: String
    ): Flow<Data<T>> = subscribeImpl(*children) { snapshot ->
        snapshot.getValue(clazz)
            ?.let { trySend(Data.Parsed(it)) }
            ?: trySend(Data.Raw(snapshot.value.toString()))
    }

    override fun <T : Any> subscribeIgnoreUnparsed(
        clazz: Class<T>,
        vararg children: String
    ): Flow<T> = subscribeImpl(*children) { snapshot ->
        snapshot.getValue(clazz)?.let(::trySend)
    }

    private inline fun <T : Any> subscribeImpl(
        vararg children: String,
        crossinline handleData: SendChannel<T>.(DataSnapshot) -> Unit
    ) = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    handleData(snapshot)
                } catch (e: Exception) {
                    close(e)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                close(exceptionMapper.mapFromDatabaseError(error.code))
            }
        }
        val ref = makeReference(*children)
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }.retry {
        delay(RETRY_SUBSCRIBE_DELAY)
        it is FireFlowException.NonFatal
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

    private companion object {
        const val RETRY_SUBSCRIBE_DELAY = 3000L
    }
}
