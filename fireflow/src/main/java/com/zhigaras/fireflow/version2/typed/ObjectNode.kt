package com.zhigaras.fireflow.version2.typed

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference

class ObjectNode<T : Any>(
    ref: DatabaseReference,
    private val clazz: Class<T>,
) : BaseTypedNode<T>(ref) {

    override fun DataSnapshot.map(): T? {
        return getValue(clazz)
    }
}
