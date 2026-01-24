package com.zhigaras.fireflow.version2.typed

import com.google.firebase.database.DatabaseReference

class ObjectNode<T : Any>(
    ref: DatabaseReference,
    private val clazz: Class<T>,
) : BaseTypedNode<T>(ref) {
    override suspend fun fetch(): T? {
        return fetchSnapshot().getValue(clazz)
    }
}
