package com.zhigaras.fireflow.version2

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.zhigaras.fireflow.version2.typed.TypedNode
import com.zhigaras.fireflow.version2.typed.CollectionNode
import com.zhigaras.fireflow.version2.typed.ObjectNode
import kotlinx.coroutines.tasks.await

abstract class BaseNode(
    protected val ref: DatabaseReference,
) : Node {
    protected suspend fun fetchSnapshot(): DataSnapshot = ref.get().await()

    override fun child(path: String): Node {
        return NodeImpl(ref.child(path))
    }

    override fun <T : Any> asTyped(clazz: Class<T>): TypedNode<T> {
        return ObjectNode(ref, clazz)
    }

    override fun <T : Any> asTypedMap(clazz: Class<T>): TypedNode<Map<String, T>> {
        return CollectionNode(ref, clazz)
    }
}
