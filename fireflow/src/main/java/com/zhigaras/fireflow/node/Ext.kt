package com.zhigaras.fireflow.node

inline fun <reified T : Any> Node.asTyped() = asTyped(T::class.java)

inline fun <reified T : Any> Node.asTypedCollectionOf() = asTypedCollectionOf(T::class.java)
