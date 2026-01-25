package com.zhigaras.fireflow.node

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.zhigaras.fireflow.node.typed.TypedNode
import com.zhigaras.fireflow.node.typed.CollectionNode
import com.zhigaras.fireflow.node.typed.ObjectNode
import kotlinx.coroutines.tasks.await
import java.lang.RuntimeException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

abstract class BaseNode(
    protected val ref: DatabaseReference,
) : Node {
    protected suspend fun fetchSnapshot(): DataSnapshot = ref.get().await()

    override fun child(path: String): Node {
        return NodeImpl(ref.child(path))
    }

    override fun <T : Any> asTyped(clazz: Class<T>): TypedNode.Object<T> {
        return ObjectNode(ref, clazz)
    }

    override fun <T : Any> asTypedCollectionOf(clazz: Class<T>): TypedNode.Collection<T> {
        return CollectionNode(ref, clazz)
    }

    override suspend fun post(obj: Any?) {
        ref.setValue(obj).await()
    }

    override suspend fun postWithIdGenerating(obj: Any): String = suspendCoroutine { cont ->
        ref.push().setValue(obj) { error, ref ->
            error?.let { cont.resumeWithException(error.toException()) }
                ?: ref.key?.let { key -> cont.resume(key) }
                ?: cont.resumeWithException(RuntimeException("Generated id is null"))
        }
    }
}
