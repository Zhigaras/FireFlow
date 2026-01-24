package com.zhigaras.fireflow.version2.typed

import com.google.firebase.database.DatabaseReference
import com.zhigaras.fireflow.version2.BaseNode

abstract class BaseTypedNode<T : Any>(
    ref: DatabaseReference,
) : BaseNode(ref), TypedNode<T>
