package com.zhigaras.fireflow.version2.typed

import com.zhigaras.fireflow.version2.Node
import kotlinx.coroutines.flow.Flow

interface TypedNode<T : Any> : Node {
    suspend fun fetch(): T?
    fun observe(): Flow<T>

    interface Object<T : Any> : TypedNode<T> {
        suspend fun update(transform: (T) -> T)
    }

    interface Collection<T : Any> : TypedNode<Map<String, T>>
}