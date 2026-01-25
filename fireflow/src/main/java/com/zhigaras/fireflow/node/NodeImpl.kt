package com.zhigaras.fireflow.node

import com.google.firebase.database.DatabaseReference

class NodeImpl(
    ref: DatabaseReference,
) : BaseNode(ref), Node, RootNode
