package com.zhigaras.fireflow.node

import com.google.firebase.database.DatabaseReference

class NodeImpl(
    ref: DatabaseReference,
) : BaseNode(ref), Node {
    override fun child(path: String): Node {
        return NodeImpl(ref.child(path))
    }
}
