package com.zhigaras.fireflow.version2.typed

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference

class ObjectNode<T : Any>(
    ref: DatabaseReference,
    private val clazz: Class<T>,
) : BaseTypedNode<T>(ref), TypedNode.Object<T> {

    override fun DataSnapshot.map(): T? {
        return getValue(clazz)
    }

    override suspend fun update(transform: (T) -> T) {
        val current = fetchSnapshot().map() ?: clazz.getDeclaredConstructor().newInstance()
        val updated = transform(current)
        post(updated)
    }
}
