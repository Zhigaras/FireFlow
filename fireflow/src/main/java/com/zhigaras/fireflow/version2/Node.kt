package com.zhigaras.fireflow.version2

import com.google.firebase.database.FirebaseDatabase
import com.zhigaras.fireflow.version2.typed.TypedNode

interface Node {
    fun child(path: String): Node
    fun <T : Any> asTyped(clazz: Class<T>): TypedNode.Object<T>
    fun <T : Any> asTypedCollectionOf(clazz: Class<T>): TypedNode.Collection<T>
    suspend fun post(obj: Any?)
    suspend fun postWithIdGenerating(obj: Any): String

    companion object {
        fun root(): Node = NodeImpl(FirebaseDatabase.getInstance().reference)
    }
}
