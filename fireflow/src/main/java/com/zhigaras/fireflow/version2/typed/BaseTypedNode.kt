package com.zhigaras.fireflow.version2.typed

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.zhigaras.fireflow.mapper.FireFlowExceptionMapper
import com.zhigaras.fireflow.model.FireFlowException
import com.zhigaras.fireflow.version2.BaseNode
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.retry

abstract class BaseTypedNode<T : Any>(
    ref: DatabaseReference,
) : BaseNode(ref), TypedNode<T> {
    private val exceptionMapper = FireFlowExceptionMapper()

    protected abstract fun DataSnapshot.map(): T?

    override suspend fun fetch(): T? {
        val snapshot = fetchSnapshot()
        return if (snapshot.exists()) snapshot.map()
        else null
    }

    override fun observe(): Flow<T> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    snapshot.map()?.let(::trySend)
                } catch (e: Exception) {
                    close(e)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                close(exceptionMapper.mapFromDatabaseError(error.code))
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }.retry {
        delay(RETRY_SUBSCRIBE_DELAY)
        it is FireFlowException.NonFatal
    }

    private companion object {
        const val RETRY_SUBSCRIBE_DELAY = 3000L
    }
}
