package com.zhigaras.fireflow.node

import com.zhigaras.fireflow.DatabaseProvider
import com.zhigaras.fireflow.node.typed.TypedNode

interface Node {
    fun child(path: String): Node
    fun <T : Any> asTyped(clazz: Class<T>): TypedNode.Object<T>
    fun <T : Any> asTypedCollectionOf(clazz: Class<T>): TypedNode.Collection<T>
    suspend fun post(obj: Any?)
    suspend fun postWithIdGenerating(obj: Any): String

    companion object {
        fun root(provider: DatabaseProvider = DatabaseProvider.Default()): RootNode =
            NodeImpl(provider.provide())
    }
}
