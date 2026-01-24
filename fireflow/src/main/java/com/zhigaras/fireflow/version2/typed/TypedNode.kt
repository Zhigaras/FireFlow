package com.zhigaras.fireflow.version2.typed

import com.zhigaras.fireflow.version2.Node

interface TypedNode<out T: Any>: Node {
    suspend fun fetch(): T?
}