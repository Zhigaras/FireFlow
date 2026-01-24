package com.zhigaras.fireflow.version2

import com.google.firebase.database.FirebaseDatabase
import com.zhigaras.fireflow.version2.typed.TypedNode

interface Node {
    fun child(path: String): Node
    fun <T : Any> asTyped(clazz: Class<T>): TypedNode<T>
    fun <T : Any> asTypedMap(clazz: Class<T>): TypedNode<Map<String, T>>

    companion object {
        fun root(): Node = NodeImpl(FirebaseDatabase.getInstance().reference)
    }
}
