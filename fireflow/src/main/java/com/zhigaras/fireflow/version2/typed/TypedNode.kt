package com.zhigaras.fireflow.version2.typed

import com.zhigaras.fireflow.version2.Node
import kotlinx.coroutines.flow.Flow

interface TypedNode<out T: Any>: Node {
    suspend fun fetch(): T?
    fun observe(): Flow<T>
}