package com.zhigaras.fireflow.version2.typed

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference

class CollectionNode<T : Any>(
    ref: DatabaseReference,
    private val clazz: Class<T>,
) : BaseTypedNode<Map<String, T>>(ref) {

    override fun DataSnapshot.map(): Map<String, T> {
        return children.mapNotNull { child ->
            val key = child.key
            val value = child.getValue(clazz)
            if (key != null && value != null) key to value
            else null
        }.toMap()
    }
}
